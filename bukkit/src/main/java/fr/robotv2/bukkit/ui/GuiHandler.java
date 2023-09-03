package fr.robotv2.bukkit.ui;

import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.FastInvManager;
import fr.mrmicky.fastinv.InventoryScheme;
import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.events.quest.QuestInventoryClickEvent;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.util.ItemUtil;
import fr.robotv2.bukkit.util.StringListProcessor;
import fr.robotv2.bukkit.util.text.ColorUtil;
import fr.robotv2.common.data.impl.ActiveQuest;
import fr.robotv2.common.data.impl.QuestPlayer;
import fr.robotv2.common.reset.ResetService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

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

        final List<CompletableFuture<Void>> futures = new ArrayList<>();
        futures.add(guiHelper.handleItems(section, scheme, fastInv, player));
        futures.add(guiHelper.handleServices(section, fastInv, player));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenApply(ignored -> fastInv);
    }

    public void openMenu(Player player) {
        getGuiOf(player).thenAccept(fastInv -> {

            if(fastInv == null) {
                player.sendMessage(ChatColor.RED + "An error occurred while opening the inventory.");
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> fastInv.open(player));
        });
    }
}
