package fr.robotv2.bukkit.listeners.quest;

import fr.robotv2.bukkit.events.QuestIncrementEvent;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.util.ColorUtil;
import fr.robotv2.bukkit.util.ProgressUtil;
import fr.robotv2.common.data.impl.ActiveQuest;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestIncrementListener implements Listener {

    @EventHandler
    public void onIncrement(QuestIncrementEvent event) {
        final Quest quest = event.getQuest();
        if(quest == null) return;

        final ActiveQuest activeQuest = event.getActiveQuest();

        String progressMessage =
                quest.getName() +
                " &8| " +
                "&7[" + ProgressUtil.getProcessBar(activeQuest.getProgress(), quest.getRequiredAmount()) + "&7]" +
                " &8| " +
                "&7" + activeQuest.getProgress() + " &8/ &7" + quest.getRequiredAmount()
                ;

        progressMessage = ColorUtil.color(progressMessage);

        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(progressMessage));
    }
}
