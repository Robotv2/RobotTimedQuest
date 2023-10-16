package fr.robotv2.bukkit.hook;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.hook.mythicmob.MythicMobHook;
import fr.robotv2.bukkit.hook.placeholderapi.PlaceholderAPIHook;
import fr.robotv2.bukkit.hook.pyrofishpro.PyroFishProHook;
import fr.robotv2.bukkit.hook.vault.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public enum Hooks {

    PLACEHOLDER_API("PlaceholderAPI"),
    VAULT("Vault"),
    ITEM_ADDER("ItemAdder"),
    ORAXEN("Oraxen"),
    MYTHIC_MOB("MythicMob"),
    ELITE_MOB("EliteMobs"),
    PYRO_FISHING_PRO("PyroFishingPro"),
    ;

    private final String pluginName;
    private boolean initialized = false;

    Hooks(String pluginName) {
        this.pluginName = pluginName;
    }

    public static void loadHooks(JavaPlugin plugin) {

        if(Hooks.PLACEHOLDER_API.isPluginEnabled() && PlaceholderAPIHook.initialize()) {
            Hooks.PLACEHOLDER_API.setInitialized();
        }

        if(Hooks.VAULT.isPluginEnabled() && VaultHook.initialize(plugin)) {
            Hooks.VAULT.setInitialized();
        }

        if(Hooks.ITEM_ADDER.isPluginEnabled()) {
            Hooks.ITEM_ADDER.setInitialized();
        }

        if(Hooks.ORAXEN.isPluginEnabled()) {
            Hooks.ORAXEN.setInitialized();
        }

        if(Hooks.MYTHIC_MOB.isPluginEnabled() && MythicMobHook.initialize(plugin)) {
            Hooks.MYTHIC_MOB.setInitialized();
        }

        if(Hooks.ELITE_MOB.isPluginEnabled()) {
            Hooks.ELITE_MOB.setInitialized();
        }

        if(Hooks.PYRO_FISHING_PRO.isPluginEnabled() && PyroFishProHook.initialize(plugin)) {
            Hooks.PYRO_FISHING_PRO.setInitialized();
        }
    }

    public boolean isPluginEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled(this.pluginName);
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    private void setInitialized() {
        RTQBukkitPlugin.getPluginLogger().info("HOOK - " + pluginName + " has been successfully hooked into this plugin.");
        this.initialized = true;
    }
}
