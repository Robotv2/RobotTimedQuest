package fr.robotv2.bukkit.ui;

import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.FastInvManager;
import fr.mrmicky.fastinv.InventoryScheme;
import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.util.BukkitFuture;
import fr.robotv2.bukkit.util.text.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GuiHandler {

    private final RTQBukkitPlugin plugin;
    private final GuiHelper guiHelper;

    public GuiHandler(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
        this.guiHelper = new GuiHelper(plugin);
        FastInvManager.register(plugin);
    }

    public CompletableFuture<FastInv> getGuiOf(Player player) {

        final ConfigurationSection section = plugin.getGuiFile().getConfiguration().getConfigurationSection("quest-gui");

        if(section == null) {
            throw new NullPointerException("quest-gui");
        }

        final int row = section.getInt("row", 5);
        final String name = section.getString("name", InventoryType.CHEST.getDefaultTitle());

        final FastInv fastInv = new FastInv(row * 9, ColorUtil.color(name));

        final List<String> masks = section.getStringList("pattern");
        final InventoryScheme scheme = new InventoryScheme();
        masks.forEach(scheme::mask);

        final CompletableFuture<Void> itemsFuture = guiHelper.handleItems(section, scheme, fastInv, player).whenComplete((unused, throwable) -> plugin.debug("HANDLE ITEMS -> DONE"));
        final CompletableFuture<Void> serviceFuture = itemsFuture.thenCompose(unused -> guiHelper.handleServices(section, fastInv, player)).whenComplete((unused, throwable) -> plugin.debug("HANDLE SERVICES -> DONE"));

        return BukkitFuture.from(serviceFuture.thenApply(unused -> fastInv), plugin).getFuture();
    }

    public void openMenu(Player player) {
        getGuiOf(player).whenComplete((fastInv, throwable) -> {

            plugin.debug("FAST INV RECEIVED, OPENING IT...");

            if(fastInv == null || throwable != null) {
                player.sendMessage(ChatColor.RED + "An error occurred while opening the inventory.");
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> fastInv.open(player));
        });
    }
}
