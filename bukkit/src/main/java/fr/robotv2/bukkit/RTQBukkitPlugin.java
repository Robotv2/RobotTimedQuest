package fr.robotv2.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class RTQBukkitPlugin extends JavaPlugin {

    public static RTQBukkitPlugin getInstance() {
        return JavaPlugin.getPlugin(RTQBukkitPlugin.class);
    }

    @Override
    public void onEnable() {
        getLogger().warning("I honestly don't know what to do");
    }

    @Override
    public void onDisable() {
        getLogger().warning("The plugin is disabled !");
    }

    public void onReload() {

    }
}
