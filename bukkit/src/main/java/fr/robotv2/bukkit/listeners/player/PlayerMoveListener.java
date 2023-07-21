package fr.robotv2.bukkit.listeners.player;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMoveListener extends QuestProgressionEnhancer<Location> {

    private final static int COUNT_RESET_THRESHOLD = 5;
    private final Map<UUID, Integer> moveCounts = new HashMap<>();

    public PlayerMoveListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

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

        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        this.moveCounts.put(uuid, this.moveCounts.getOrDefault(uuid, 0) + 1);

        if (this.moveCounts.get(uuid) >= COUNT_RESET_THRESHOLD) {
            this.incrementProgression(player, QuestType.LOCATION, player.getLocation(), event, 1);
            this.moveCounts.put(uuid, 0);
        }
    }
}
