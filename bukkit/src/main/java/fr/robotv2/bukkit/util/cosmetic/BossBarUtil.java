package fr.robotv2.bukkit.util.cosmetic;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarUtil {

    // UTILITY CLASS

    private BossBarUtil() { }

    private static final Map<UUID, BossBar> bars = new HashMap<>();

    @NotNull
    public static BossBar getCurrent(Player player) {
        final UUID playerUUID = player.getUniqueId();
        if(!bars.containsKey(playerUUID)) {
            final BossBar bossBar = Bukkit.createBossBar(null, BarColor.WHITE, BarStyle.SOLID);

            bossBar.addPlayer(player);
            bossBar.setProgress(1);

            bars.put(playerUUID, bossBar);
        }
        return bars.get(playerUUID);
    }

    public static void removeBar(Player player) {
        getCurrent(player).removeAll();
    }
}
