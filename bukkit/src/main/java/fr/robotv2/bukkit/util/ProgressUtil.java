package fr.robotv2.bukkit.util;

import org.bukkit.ChatColor;

public class ProgressUtil {

    // UTILITY CLASS

    private ProgressUtil() { }

    public static String getProcessBar(int amount, int required, int barNumber) {
        final StringBuilder builder = new StringBuilder();

        final int greenBarNumber = amount * barNumber / required;
        final int grayBarNumber = barNumber - greenBarNumber;

        for (int i = 0; i < greenBarNumber; i++) {
            builder.append(ChatColor.GREEN + "|");
        }

        for (int i = 0; i < grayBarNumber; i++) {
            builder.append(ChatColor.GRAY + "|");
        }

        return builder.toString();
    }

    public static String getProcessBar(int amount, int required) {
        return getProcessBar(amount, required, 20);
    }
}
