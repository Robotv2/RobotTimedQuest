package fr.robotv2.bukkit.listeners.player;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.events.ActualPlayerMoveEvent;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class PlayerMoveListener extends QuestProgressionEnhancer<Location> {

    public PlayerMoveListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onMove(ActualPlayerMoveEvent event) {
        final Player player = event.getPlayer();
        this.incrementProgression(player, QuestType.LOCATION, player.getLocation(), event, 1);
    }
}
