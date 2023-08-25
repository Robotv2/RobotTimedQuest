package fr.robotv2.bukkit.listeners;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.events.quest.QuestDoneEvent;
import fr.robotv2.bukkit.events.quest.QuestIncrementEvent;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.util.StringListProcessor;
import fr.robotv2.common.data.impl.ActiveQuest;
import fr.robotv2.common.data.impl.QuestPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class QuestProgressionEnhancer<T> implements Listener {

    private final RTQBukkitPlugin plugin;

    protected QuestProgressionEnhancer(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    public RTQBukkitPlugin getPlugin() {
        return this.plugin;
    }

    public GlitchChecker getGlitchChecker() {
        return this.plugin.getGlitchChecker();
    }

    public boolean allConditionsMatch(List<Condition> conditions, Player player, Event event, QuestType type) {
        return conditions.stream()
                .filter(condition -> condition.referencedType().contains(type))
                .allMatch(condition -> condition.matchCondition(player, type, event));
    }

    public boolean incrementProgression(@NotNull Player player, @NotNull QuestType type, @Nullable T target) {
        return this.incrementProgression(player, type, target, null, 1);
    }

    public boolean incrementProgression(@NotNull Player player, @NotNull QuestType type, @Nullable T target, @Nullable Event event) {
        return this.incrementProgression(player, type, target, event, 1);
    }

    public boolean incrementProgression(@NotNull Player player, @NotNull QuestType type,
                                     @Nullable T target, @Nullable Event event, int amount) {

        if(type != QuestType.LOCATION) {
            plugin.debug(type.name() + " has been triggered by " + player.getName() + ".");
        }

        if(amount == 0) {
            return false;
        }

        final QuestPlayer questPlayer = QuestPlayer.getQuestPlayer(player.getUniqueId());

        if(questPlayer == null) {
            return false;
        }

        int questAffected = 0;

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

            questAffected++;
            this.incrementQuest(quest, activeQuest, player, amount);
        }

        return questAffected != 0;
    }

    public boolean incrementProgressionFor(Player player, ActiveQuest activeQuest, T target, Event event, int amount) {

        if(activeQuest.hasEnded() || activeQuest.isDone()) {
            return false;
        }

        final QuestPlayer questPlayer = QuestPlayer.getQuestPlayer(player.getUniqueId());
        final Quest quest = this.plugin.getQuestManager().fromId(activeQuest.getQuestId());

        if(questPlayer == null || !questPlayer.hasQuest(activeQuest.getQuestId())) {
            return false;
        }

        if(quest == null || !quest.isTarget(target)) {
            return false;
        }

        if(!quest.getConditions().isEmpty() // Check if conditions are empty.
                && !this.allConditionsMatch(quest.getConditions(), player, event, quest.getType())) // Does all the conditions are met ?
        {
            return false;
        }

        this.incrementQuest(quest, activeQuest, player, amount);
        return true;
    }

    public void incrementQuest(Quest quest, ActiveQuest activeQuest, Player player, int amount) {

        activeQuest.incrementProgress(amount);
        Bukkit.getPluginManager().callEvent(new QuestIncrementEvent(activeQuest, amount));

        if(activeQuest.getProgress() >= quest.getRequiredAmount()) {

            activeQuest.setDone(true);
            new StringListProcessor().process(player, quest);

            Bukkit.getPluginManager().callEvent(new QuestDoneEvent(activeQuest));
        }
    }
}
