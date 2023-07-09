package fr.robotv2.bukkit.util;

import org.bukkit.ChatColor;

public class ColorUtil {

    // UTILITY CLASS

    private ColorUtil() { }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
