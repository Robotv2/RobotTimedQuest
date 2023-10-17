package fr.robotv2.bukkit.listeners.quest;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.events.quest.BulkQuestDoneEvent;
import fr.robotv2.bukkit.util.StringListProcessor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class QuestBulkDoneListener implements Listener {

    private final RTQBukkitPlugin plugin;

    public QuestBulkDoneListener(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBulkDoneEvent(BulkQuestDoneEvent event) {

        final Player player = event.getPlayer();
        final String resetId = event.getResetId();
        final List<String> rewards = plugin.getConfig().getStringList("quest-bulk-done." + resetId);

        if(!rewards.isEmpty()) {
            new StringListProcessor().process(player, rewards);
        }
    }
}
