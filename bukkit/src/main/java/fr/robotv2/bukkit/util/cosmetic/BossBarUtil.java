package fr.robotv2.bukkit.util.cosmetic;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarUtil {

    // UTILITY CLASS

    private BossBarUtil() { }

    private static final Map<UUID, BossBar> bars = new HashMap<>();
    private static final Map<UUID, BukkitTask> tasks = new HashMap<>();

    @NotNull
    public static BossBar getOrCreate(Player player) {
        final UUID playerUUID = player.getUniqueId();
        if(!bars.containsKey(playerUUID)) {
            final BossBar bossBar = Bukkit.createBossBar(null, BarColor.WHITE, BarStyle.SOLID);
            bossBar.setProgress(1);
            bars.put(playerUUID, bossBar);
        }
        return bars.get(playerUUID);
    }

    public static void hide(Player player) {
        getOrCreate(player).removeAll();
    }

    public static void show(Player player) {
        getOrCreate(player).addPlayer(player);
    }

    public static void showFor(Player player, int seconds) {

        final UUID uuid = player.getUniqueId();

        if(tasks.containsKey(uuid)) {
            tasks.get(uuid).cancel();
            tasks.remove(uuid);
        }

        show(player);

        if(seconds <= 0) {
            return;
        }

        final BukkitTask task = Bukkit.getScheduler().runTaskLater(RTQBukkitPlugin.getInstance(), () -> hide(player), 20L * seconds);
        tasks.put(uuid, task);
    }
}
