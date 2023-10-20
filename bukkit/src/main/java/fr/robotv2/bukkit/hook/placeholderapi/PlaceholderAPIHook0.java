package fr.robotv2.bukkit.hook.placeholderapi;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.hook.placeholderapi.expansion.ClipPlaceholder;
import fr.robotv2.placeholderannotation.PlaceholderAnnotationProcessor;
import fr.robotv2.placeholderannotation.util.PAPDebug;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPIHook0 {

    public static boolean initializePAP() {
        PAPDebug.debugEnabled(false);
        final PlaceholderAnnotationProcessor processor = PlaceholderAnnotationProcessor.create();
        final ClipPlaceholder clipPlaceholder = new ClipPlaceholder(RTQBukkitPlugin.getInstance(), processor);
        processor.registerExpansion(clipPlaceholder);
        return clipPlaceholder.register();
    }

    public static String parsePlaceholders(OfflinePlayer offlinePlayer, String input) {
        return PlaceholderAPI.setPlaceholders(offlinePlayer, input);
    }

}
