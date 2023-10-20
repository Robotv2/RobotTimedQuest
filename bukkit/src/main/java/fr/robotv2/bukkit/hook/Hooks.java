package fr.robotv2.bukkit.hook;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.hook.elitemob.EliteMobHook;
import fr.robotv2.bukkit.hook.itemadder.ItemAdderHook;
import fr.robotv2.bukkit.hook.mythicmob.MythicMobHook;
import fr.robotv2.bukkit.hook.oraxen.OraxenHook;
import fr.robotv2.bukkit.hook.placeholderapi.PlaceholderAPIHook;
import fr.robotv2.bukkit.hook.pyrofishpro.PyroFishProHook;
import fr.robotv2.bukkit.hook.vault.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Stream;

public enum Hooks {

    VAULT("Vault", VaultHook::new),
    ITEM_ADDER("ItemAdder", ItemAdderHook::new),
    ORAXEN("Oraxen", OraxenHook::new),
    MYTHIC_MOB("MythicMob", MythicMobHook::new),
    ELITE_MOB("EliteMobs", EliteMobHook::new),
    PYRO_FISHING_PRO("PyroFishingPro", PyroFishProHook::new),
    PLACEHOLDER_API("PlaceholderAPI", PlaceholderAPIHook::new),
    ;

    private final Supplier<Hook> hookSupplier;

    private final String pluginName;
    private Hook hook;

    Hooks(String pluginName, Supplier<Hook> hookSupplier) {
        this.pluginName = pluginName;
        this.hookSupplier = hookSupplier;
    }

    public static void loadHooks(JavaPlugin plugin) {
        Stream.of(Hooks.values()).filter(pluginHook -> {
            RTQBukkitPlugin.getInstance().debug("Is plugin %s enabled ? %s", pluginHook.pluginName, String.valueOf(pluginHook.isPluginEnabled()));
            return pluginHook.isPluginEnabled();
                })
                .forEach(pluginHook -> {
                    RTQBukkitPlugin.getInstance().debug("Loading hook for " + pluginHook.pluginName);
                    pluginHook.load(plugin);
                });
    }

    public boolean isPluginEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled(this.pluginName);
    }

    public boolean isInitialized() {
        return this.hook != null;
    }

    private void load(JavaPlugin plugin) {

        try {
            final Hook supplied = hookSupplier.get();
            if (supplied.initialize(plugin)) {
                setInitialized(supplied);
            }
        } catch (Exception exception) {
            plugin.getLogger().log(Level.WARNING, "An error occurred while hooking into: " + pluginName, exception);
        }
    }

    private void setInitialized(Hook hook) {
        RTQBukkitPlugin.getPluginLogger().info("HOOK - " + pluginName + " has been successfully hooked into this plugin.");
        this.hook = hook;
        hook.loadConditions();
    }
}
