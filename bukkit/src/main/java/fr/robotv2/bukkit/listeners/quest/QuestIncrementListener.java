package fr.robotv2.bukkit.listeners.quest;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.events.QuestIncrementEvent;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.util.ColorUtil;
import fr.robotv2.bukkit.util.PlaceholderUtil;
import fr.robotv2.common.data.impl.ActiveQuest;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestIncrementListener implements Listener {

    private final RTQBukkitPlugin plugin;

    public QuestIncrementListener(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onIncrement(QuestIncrementEvent event) {

        final ActiveQuest activeQuest = event.getActiveQuest();
        final Quest quest = event.getQuest();

        if(quest == null
                || !plugin.getConfig().getBoolean("cosmetics.actionbar.enabled")) {
            return;
        }

        String progressMessage = this.plugin.getConfig().getString("cosmetics.actionbar.progression_message", "%quest_name% &8| %quest_progressbar% &8| &7%quest_progress% &8/ &7%quest_required%");

        progressMessage = PlaceholderUtil.QUEST_PLACEHOLDER.parse(quest, progressMessage);
        progressMessage = PlaceholderUtil.ACTIVE_QUEST_PLACEHOLDER.parse(activeQuest, progressMessage);
        progressMessage = PlaceholderUtil.ACTIVE_QUEST_RELATIONAL_PLACEHOLDER.parse(quest, activeQuest, progressMessage);
        progressMessage = ColorUtil.color(progressMessage);

        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(progressMessage));
    }
}
