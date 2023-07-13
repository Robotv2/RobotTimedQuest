package fr.robotv2.bukkit.listeners;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.events.QuestDoneEvent;
import fr.robotv2.bukkit.events.QuestIncrementEvent;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.util.StringListProcessor;
import fr.robotv2.common.data.impl.ActiveQuest;
import fr.robotv2.common.data.impl.QuestPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.util.List;

public abstract class QuestProgressionEnhancer<T> implements Listener {

    private final RTQBukkitPlugin plugin;

    protected QuestProgressionEnhancer(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    public GlitchChecker getGlitchChecker() {
        return this.plugin.getGlitchChecker();
    }

    public boolean allConditionsMatch(List<Condition> conditions, Player player, Event event, QuestType type) {
        for(Condition condition : conditions) {
            if(condition.referencedType().contains(type) && !condition.matchCondition(player, event)) {
                return false;
            }
        }
        return true;
    }

    public void incrementProgression(Player player, QuestType type, T target, Event event, int amount) {

        if(type != QuestType.LOCATION) {
            plugin.debug(type.name() + " has been triggered by " + player.getName() + ".");
        }

        if(amount <= 0) {
            return;
        }

        final QuestPlayer questPlayer = QuestPlayer.getQuestPlayer(player.getUniqueId());

        if(questPlayer == null) {
            return;
        }

        for (ActiveQuest activeQuest : questPlayer.getActiveQuests()) {

            if(activeQuest.isDone() || activeQuest.hasEnded()) {
                continue;
            }

            final Quest quest = plugin.getQuestManager().fromId(activeQuest.getQuestId());

            if (quest == null // Quest doesn't exist ?? (shouldn't happen)
                    || quest.getType() != type // Quest type doesn't match. (another action)
                    || !quest.isTarget(target)) // The target isn't targeted by this quest.
            {
                continue;
            }

            if(!quest.getConditions().isEmpty() // Check if conditions are empty.
                    && !this.allConditionsMatch(quest.getConditions(), player, event, type)) // Does all the conditions are met ?
            {
                continue;
            }

            activeQuest.incrementProgress(amount);
            Bukkit.getPluginManager().callEvent(new QuestIncrementEvent(activeQuest, amount));

            if(activeQuest.getProgress() >= quest.getRequiredAmount()) {

                activeQuest.setDone(true);
                new StringListProcessor().process(player, quest);

                Bukkit.getPluginManager().callEvent(new QuestDoneEvent(activeQuest));
            }
        }
    }
}
