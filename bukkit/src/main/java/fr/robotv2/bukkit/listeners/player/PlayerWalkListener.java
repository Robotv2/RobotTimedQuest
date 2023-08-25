package fr.robotv2.bukkit.listeners.player;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerWalkListener extends QuestProgressionEnhancer<Void> {

    private final Map<UUID, Integer> walkCount = new ConcurrentHashMap<>();

    public PlayerWalkListener(RTQBukkitPlugin plugin) {
        super(plugin);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(
                () -> Bukkit.getScheduler().runTask(plugin, this::updatePlayers),
                5,
                TimeUnit.SECONDS
        );
    }

    private void updatePlayers() {

        for(Map.Entry<UUID, Integer> entry : walkCount.entrySet()) {
            final Player player = Bukkit.getPlayer(entry.getKey());
            if(player == null || !player.isOnline()) continue;
            this.incrementProgression(player, QuestType.WALK, null, null, entry.getValue());
        }

        this.walkCount.clear();
    }

    @EventHandler
    public void onWalk(PlayerStatisticIncrementEvent event) {

        if(event.getStatistic() != Statistic.WALK_ONE_CM) {
            return;
        }

        final UUID uuid = event.getPlayer().getUniqueId();
        final int current = walkCount.getOrDefault(uuid, 0);
        walkCount.put(uuid, current + (event.getNewValue() - event.getPreviousValue()));
    }
}
