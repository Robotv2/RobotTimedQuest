package fr.robotv2.bukkit.hook;

import fr.robotv2.bukkit.hook.placeholderapi.PlaceholderAPIHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public enum Hooks {

    PLACEHOLDER_API("PlaceholderAPI"),
    VAULT("Vault"),
    ITEM_ADDER("ItemAdder"),
    ORAXEN("Oraxen"),
    MYTHIC_MOB("MythicMob"),
    ;

    private final String pluginName;
    private boolean initialized = false;

    Hooks(String pluginName) {
        this.pluginName = pluginName;
    }

    public static void loadHooks(JavaPlugin plugin) {

        if(Hooks.PLACEHOLDER_API.isPluginEnabled() && PlaceholderAPIHook.initialize()) {
            plugin.getLogger().info("HOOK - PlaceholderAPI successfully hooked into the plugin.");
            Hooks.PLACEHOLDER_API.setInitialized();
        }

        if(Hooks.VAULT.isPluginEnabled() && VaultHook.initialize(plugin)) {
            plugin.getLogger().info("HOOK - Vault has been successfully hooked into the plugin.");
            Hooks.VAULT.setInitialized();
        }

        if(Hooks.ITEM_ADDER.isPluginEnabled()) {
            plugin.getLogger().info("HOOK - Item Adder has been successfully hooked into this plugin.");
            Hooks.ITEM_ADDER.setInitialized();
        }

        if(Hooks.ORAXEN.isPluginEnabled()) {
            plugin.getLogger().info("HOOK - Oraxen has been successfully hooked into this plugin.");
            Hooks.ORAXEN.setInitialized();
        }

        if(Hooks.MYTHIC_MOB.isPluginEnabled() && MythicMobHook.initialize(plugin)) {
            plugin.getLogger().info("HOOK - MythicMob has been successfully hooked into this plugin.");
            Hooks.MYTHIC_MOB.setInitialized();
        }
    }

    public boolean isPluginEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled(this.pluginName);
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    private void setInitialized() {
        this.initialized = true;
    }
}
