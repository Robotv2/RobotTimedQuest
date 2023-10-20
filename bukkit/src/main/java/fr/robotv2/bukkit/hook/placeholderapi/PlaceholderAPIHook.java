package fr.robotv2.bukkit.hook.placeholderapi;

import fr.robotv2.bukkit.hook.Hook;
import fr.robotv2.bukkit.hook.Hooks;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

public class PlaceholderAPIHook implements Hook {

    public static String parsePlaceholders(OfflinePlayer offlinePlayer, String input) {
        return Hooks.PLACEHOLDER_API.isPluginEnabled() ? PlaceholderAPIHook0.parsePlaceholders(offlinePlayer, input) : input;
    }

    @Override
    public boolean initialize(JavaPlugin plugin) {
        if(!Hooks.PLACEHOLDER_API.isPluginEnabled()) return false;
        return PlaceholderAPIHook0.initializePAP();
    }

    @Override
    public void loadConditions() { }
}
