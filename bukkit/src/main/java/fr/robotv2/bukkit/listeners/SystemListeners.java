package fr.robotv2.bukkit.listeners;

import fr.mrmicky.fastinv.FastInv;
import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.events.ActualPlayerMoveEvent;
import fr.robotv2.bukkit.events.PlayerWalkEvent;
import fr.robotv2.bukkit.util.Options;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SystemListeners implements Listener {

    private final Map<UUID, Integer> walkMeter = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> moveCounts = new HashMap<>();

    public SystemListeners() {
        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(
                this::updatePlayersCount,
                2,
                2,
                TimeUnit.SECONDS
        );
    }

    // event handler for : ActualPlayerMoveEvent
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInventoryClickFixFastInv(InventoryClickEvent event) {
        if(event.getInventory().getHolder() instanceof FastInv && event.getClickedInventory() != null && event.isCancelled()) {
            if(!(event.getClickedInventory().getHolder() instanceof FastInv)) {
                event.setCancelled(false);
            }
        }
    }

    private int totalWalkMeter(Player player) {
        return player.getStatistic(Statistic.WALK_ONE_CM) + player.getStatistic(Statistic.SPRINT_ONE_CM);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.walkMeter.put(event.getPlayer().getUniqueId(), this.totalWalkMeter(event.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.walkMeter.remove(event.getPlayer().getUniqueId());
    }

    private void updatePlayersCount() {
        for(Player player : Bukkit.getOnlinePlayers()) {

            final int before = this.walkMeter.get(player.getUniqueId());
            final int now = this.totalWalkMeter(player) / 100;

            walkMeter.put(player.getUniqueId(), now);

            if((now - before) > 1) {
                Bukkit.getScheduler().runTask(RTQBukkitPlugin.getInstance(), () -> Bukkit.getPluginManager().callEvent(new PlayerWalkEvent(player, before, now)));
            }
        }
    }
}
