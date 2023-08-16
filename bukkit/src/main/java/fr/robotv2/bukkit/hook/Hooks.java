package fr.robotv2.bukkit.hook;

import fr.robotv2.bukkit.hook.placeholderapi.PlaceholderAPIHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Hooks {

    private Hooks() { }

    public static void loadHooks(JavaPlugin plugin) {

        if(Hooks.isPlaceholderAPIEnabled() && PlaceholderAPIHook.initialize()) {
            plugin.getLogger().info("HOOK - PlaceholderAPI successfully hooked into the plugin.");
        }

        if(Hooks.isVaultEnabled() && VaultHook.initialize(plugin)) {
            plugin.getLogger().info("HOOK - Vault successfully hooked into the plugin.");
        }

        if(Hooks.isItemAdderEnabled() && ItemAdderHook.initialize(plugin)) {
            plugin.getLogger().info("HOOK - Item Adder successfully hooked into this plugin.");
        }

        if(Hooks.isOraxenEnabled() && OraxenHook.initialize(plugin)) {
            plugin.getLogger().info("HOOK - Oraxen successfully hooked into this plugin.");
        }
    }

    public static boolean isVaultEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("Vault") && VaultHook.isInitialized();
    }

    public static boolean isItemAdderEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("ItemAdder");
    }

    public static boolean isOraxenEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("Oraxen");
    }

    public static boolean isPlaceholderAPIEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }
}
