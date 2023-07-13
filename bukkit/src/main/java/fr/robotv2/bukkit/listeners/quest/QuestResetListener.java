package fr.robotv2.bukkit.listeners.quest;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.events.DelayQuestResetEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestResetListener implements Listener {

    private final RTQBukkitPlugin plugin;

    public QuestResetListener(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onReset(DelayQuestResetEvent event) {
        plugin.debug("-----");
        plugin.debug("Reset Triggered ! " + event.getResetId());
        plugin.debug("-----");
    }
}
