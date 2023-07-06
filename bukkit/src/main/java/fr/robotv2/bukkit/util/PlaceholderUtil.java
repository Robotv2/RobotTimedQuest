package fr.robotv2.bukkit.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlaceholderUtil {

    private PlaceholderUtil() { }

    public static String parsePlaceholders(OfflinePlayer offlinePlayer, String input) {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") ? PlaceholderAPI.setPlaceholders(offlinePlayer, input) : input;
    }
}
