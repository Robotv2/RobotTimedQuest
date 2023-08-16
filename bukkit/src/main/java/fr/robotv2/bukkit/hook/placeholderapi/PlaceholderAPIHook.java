package fr.robotv2.bukkit.hook.placeholderapi;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.placeholderannotation.PlaceholderAnnotationProcessor;
import fr.robotv2.placeholderannotation.util.PAPDebug;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

public class PlaceholderAPIHook {

    public static boolean initialize() {
        PAPDebug.debugEnabled(true);
        final PlaceholderAnnotationProcessor processor = PlaceholderAnnotationProcessor.create();
        final ClipPlaceholder clipPlaceholder = new ClipPlaceholder(RTQBukkitPlugin.getInstance(), processor);
        processor.registerExpansion(clipPlaceholder);
        return clipPlaceholder.register();
    }

    public static String parsePlaceholders(OfflinePlayer offlinePlayer, String input) {
        return PlaceholderAPI.setPlaceholders(offlinePlayer, input);
    }
}
