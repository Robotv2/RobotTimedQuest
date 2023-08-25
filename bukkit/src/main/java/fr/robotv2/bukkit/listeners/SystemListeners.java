package fr.robotv2.bukkit.listeners;

import fr.robotv2.bukkit.events.ActualPlayerMoveEvent;
import fr.robotv2.bukkit.util.Options;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SystemListeners implements Listener {

    private final Map<UUID, Integer> moveCounts = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        final Location from = event.getFrom();
        final Location to = event.getTo();

        if(to == null) {
            return;
        }

        final double xFrom = from.getX();
        final double yFrom = from.getY();
        final double zFrom = from.getZ();

        final double xTo = to.getX();
        final double yTo = to.getY();
        final double zTo = to.getZ();

        if(xFrom == xTo && yFrom == yTo && zFrom == zTo) {
            // player only move their head.
            return;
        }

        final UUID uuid = event.getPlayer().getUniqueId();

        this.moveCounts.put(uuid, this.moveCounts.getOrDefault(uuid, 0) + 1);

        if (this.moveCounts.get(uuid) >= Options.PLAYER_MOVE_EVENT_THRESHOLD) {

            final ActualPlayerMoveEvent actualPlayerMoveEvent = new ActualPlayerMoveEvent(event.getPlayer(), event.getFrom(), event.getTo());
            Bukkit.getPluginManager().callEvent(actualPlayerMoveEvent);

            if(actualPlayerMoveEvent.isCancelled()) {
                event.setCancelled(true);
            }

            this.moveCounts.put(uuid, 0);
        }
    }
}
