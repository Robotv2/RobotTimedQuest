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

    PLACEHOLDER_API("PlaceholderAPI", PlaceholderAPIHook.class, PlaceholderAPIHook::new),
    VAULT("Vault", VaultHook.class, VaultHook::new),
    ITEM_ADDER("ItemAdder", ItemAdderHook.class, ItemAdderHook::new),
    ORAXEN("Oraxen", OraxenHook.class, OraxenHook::new),
    MYTHIC_MOB("MythicMob", MythicMobHook.class, MythicMobHook::new),
    ELITE_MOB("EliteMobs", EliteMobHook.class, EliteMobHook::new),
    PYRO_FISHING_PRO("PyroFishingPro", PyroFishProHook.class, PyroFishProHook::new),
    ;

    private final Supplier<Hook> hookSupplier;
    private final Class<? extends Hook> hookClass;

    private final String pluginName;
    private Hook hook;

    Hooks(String pluginName, Class<? extends Hook> hookClass, Supplier<Hook> hookSupplier) {
        this.pluginName = pluginName;
        this.hookClass = hookClass;
        this.hookSupplier = hookSupplier;
    }

    public static void loadHooks(JavaPlugin plugin) {
        Stream.of(Hooks.values()).filter(Hooks::isPluginEnabled)
                .forEach(pluginHook -> {
                    RTQBukkitPlugin.getInstance().debug("Loading hook for " + pluginHook.pluginName);
                    pluginHook.load(plugin);
                });
    }

    public static <T extends Hook> T getHookInstance(Hooks hooks) {
        return getHookInstance(hooks, (Class<T>) hooks.hookClass);
    }

    public static <T extends Hook> T getHookInstance(Hooks hooks, Class<T> clazz) {
        return clazz.cast(hooks.hook);
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
