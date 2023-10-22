package fr.robotv2.bukkit.listeners.player;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import fr.robotv2.common.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Duration;
import java.util.Deque;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerOnlineListener extends QuestProgressionEnhancer<Void> {

    private final AtomicBoolean inProgress = new AtomicBoolean(false);

    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private final Deque<Pair<UUID, Long>> queue = new ConcurrentLinkedDeque<>();

    public PlayerOnlineListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    private void prepareNext(Player player) {

        final long triggerTime = System.currentTimeMillis() + Duration.ofMinutes(1).toMillis();
        final Pair<UUID, Long> pair = new Pair<>(player.getUniqueId(), triggerTime);
        this.queue.addLast(pair);
    }

    private void scheduleNext() {

        final Pair<UUID, Long> pair = queue.pollFirst();

        if(pair == null) {
            inProgress.set(false);
            return;
        }

        final long delay = pair.snd - System.currentTimeMillis();

        service.schedule(() -> {

            final UUID uuid = pair.fst;
            final Player player = Bukkit.getPlayer(uuid);

            if(player != null && player.isOnline()) {
                Bukkit.getScheduler().runTask(getPlugin(), () -> incrementProgression(player, QuestType.STAY_ONLINE, null));
                prepareNext(player);
            }

            scheduleNext();
        }, delay, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        prepareNext(event.getPlayer());

        if(!inProgress.getAndSet(true)) {
            scheduleNext();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        final Iterator<Pair<UUID, Long>> iterator = queue.iterator();
        final UUID playerUUID = event.getPlayer().getUniqueId();

        while(iterator.hasNext()) {
            final Pair<UUID, Long> next = iterator.next();
            if(Objects.equals(next.fst, playerUUID)) {
                queue.remove(next);
            }
        }
    }
}
