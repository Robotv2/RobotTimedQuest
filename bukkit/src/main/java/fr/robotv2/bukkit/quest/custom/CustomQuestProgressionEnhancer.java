package fr.robotv2.bukkit.quest.custom;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.events.quest.QuestDoneEvent;
import fr.robotv2.bukkit.events.quest.QuestIncrementEvent;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.util.StringListProcessor;
import fr.robotv2.common.data.impl.ActiveQuest;
import fr.robotv2.common.data.impl.QuestPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CustomQuestProgressionEnhancer<T> {

    final RTQBukkitPlugin RTQ_PLUGIN = JavaPlugin.getPlugin(RTQBukkitPlugin.class);

    public boolean trigger(Player player, String name, T target) {
        return this.trigger(player, name, target, 1);
    }

    public boolean trigger(Player player, String name, T target, int amount) {
        return this.trigger(player, name, target, null, amount);
    }

    public boolean trigger(Player player, String name, T target, Event event, int amount) {

        final CustomType customType = RTQ_PLUGIN.getCustomTypeManager().getCustomType(name);

        if(customType == null) {
            return false;
        }

        final QuestPlayer questPlayer = QuestPlayer.getQuestPlayer(player.getUniqueId());

        if(questPlayer == null) {
            return false;
        }

        boolean result = false;

        for(ActiveQuest activeQuest : questPlayer.getActiveQuests()) {

            if(activeQuest.hasEnded() || activeQuest.isDone()) {
                continue;
            }

            final Quest quest = RTQ_PLUGIN.getQuestManager().fromId(activeQuest.getQuestId());

            if(quest == null || quest.getType() != QuestType.CUSTOM || !quest.getCustomType().equalsIgnoreCase(name)) {
                continue;
            }

            if(!quest.isTarget(target)) {
                continue;
            }

            if(!quest.getConditions().isEmpty()
                    && quest.getConditions().stream().anyMatch(condition -> !condition.matchCondition(player, QuestType.CUSTOM, event))) {
                continue;
            }

            result = true;

            activeQuest.incrementProgress(amount);
            Bukkit.getPluginManager().callEvent(new QuestIncrementEvent(activeQuest, amount));

            if(activeQuest.getProgress() >= quest.getRequiredAmount()) {

                activeQuest.setDone(true);
                new StringListProcessor().process(player, quest);
                Bukkit.getPluginManager().callEvent(new QuestDoneEvent(activeQuest));
            }
        }

        return result;
    }
}
