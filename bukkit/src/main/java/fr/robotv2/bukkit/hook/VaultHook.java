package fr.robotv2.bukkit.hook;

import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultHook {

    private static Economy eco;

    private VaultHook() { }

    public static boolean initialize(JavaPlugin plugin) {

        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        final RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            return false;
        }

        eco = rsp.getProvider();
        return true;
    }

    public static boolean hasEnough(OfflinePlayer offlinePlayer, Double value) {
        Validate.notNull(eco, "economy is null. Is Vault installed ?");
        return eco.has(offlinePlayer, value);
    }

    public static Double getBalance(OfflinePlayer offlinePlayer) {
        Validate.notNull(eco, "economy is null. Is Vault installed ?");
        return eco.getBalance(offlinePlayer);
    }

    public static void setBalance(OfflinePlayer offlinePlayer, Double value) {
        Validate.notNull(eco, "economy is null. Is Vault installed ?");
        VaultHook.giveMoney(offlinePlayer, value - VaultHook.getBalance(offlinePlayer));
    }

    public static void giveMoney(OfflinePlayer offlinePlayer, Double value) {
        Validate.notNull(eco, "economy is null. Is Vault installed ?");
        eco.depositPlayer(offlinePlayer, value);
    }

    public static void takeMoney(OfflinePlayer offlinePlayer, Double value) {
        Validate.notNull(eco, "economy is null. Is Vault installed ?");
        eco.withdrawPlayer(offlinePlayer, value);
    }
}
