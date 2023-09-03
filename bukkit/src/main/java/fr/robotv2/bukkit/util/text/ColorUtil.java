package fr.robotv2.bukkit.util.text;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {

    // UTILITY CLASS

    private ColorUtil() { }

    private final static Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})");

    public static String color(String text) {
        text = ChatColor.translateAlternateColorCodes('&', text);
        text = hexColor(text);
        return text;
    }

    public static String hexColor(String text) {

        if(!text.contains("#")) {
            return text;
        }

        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer(text.length());

        if(!matcher.find()) {
            return text;
        }

        matcher.reset();

        while (matcher.find()) {
            String colorCode = matcher.group();
            ChatColor color = ChatColor.of(colorCode);
            matcher.appendReplacement(buffer, color.toString());
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
