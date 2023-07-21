package fr.robotv2.bukkit.listeners.quest;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.events.QuestDoneEvent;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.util.ColorUtil;
import fr.robotv2.bukkit.util.PlaceholderUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestDoneListener implements Listener {

    private final RTQBukkitPlugin plugin;

    public QuestDoneListener(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    private void sendCongratulationTitle(Player player, Quest quest) {

        final int fadeIn = this.plugin.getConfig().getInt("cosmetics.title.fade-in", 10);
        final int stay = this.plugin.getConfig().getInt("cosmetics.title.stay", 20);
        final int fadeOut = this.plugin.getConfig().getInt("cosmetics.title.fade-out", 10);

        String title = this.plugin.getConfig().getString("cosmetics.title.title");
        String subtitle = this.plugin.getConfig().getString("cosmetics.title.subtitle");

        title = PlaceholderUtil.PLAYER_PLACEHOLDER.parse(player, title);
        title = PlaceholderUtil.QUEST_PLACEHOLDER.parse(quest, title);
        title = ColorUtil.color(title);

        subtitle = PlaceholderUtil.PLAYER_PLACEHOLDER.parse(player, subtitle);
        subtitle = PlaceholderUtil.QUEST_PLACEHOLDER.parse(quest, subtitle);
        subtitle = ColorUtil.color(subtitle);

        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    @EventHandler
    public void onQuestDone(QuestDoneEvent event) {

        final Player player = event.getPlayer();
        final Quest quest = event.getQuest();

        if(this.plugin.getConfig().getBoolean("cosmetics.title.enabled")) {
            this.sendCongratulationTitle(player, quest);
        }
    }
}
