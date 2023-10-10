package fr.robotv2.bukkit.listeners.item;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.events.ActualFurnaceExtractEvent;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import fr.robotv2.bukkit.util.item.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerCookListener extends QuestProgressionEnhancer<Material> implements InventoryUtil {

    public PlayerCookListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    // @EventHandler(ignoreCancelled = true) not working, may need it later lol
    public void onFurnaceClick(InventoryClickEvent event) {

        final Player player = (Player) event.getWhoClicked();
        final Inventory inventory = event.getClickedInventory();
        final ItemStack itemStack = event.getCurrentItem();

        if(inventory == null || itemStack == null) {
            return;
        }

        final InventoryType type = inventory.getType();

        if(type != InventoryType.FURNACE && type != InventoryType.BLAST_FURNACE && type != InventoryType.SMOKER) {
            return;
        }

        if(event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }

        final BlockInventoryHolder holder = (BlockInventoryHolder) inventory.getHolder();
        int amount = this.getAmountFromInventoryAction(player, itemStack, event.getAction(), event.getSlotType());

        if (amount == 0) return;

        Bukkit.getPluginManager().callEvent(
                new ActualFurnaceExtractEvent(holder != null ? holder.getBlock() : null, player, itemStack, amount)
        );
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceExtract(FurnaceExtractEvent event) {
        this.getPlugin().debug("FurnaceExtractEvent - %d", event.getItemAmount());
        this.incrementProgression(event.getPlayer(), QuestType.COOK, event.getItemType(), event, event.getItemAmount());
    }
}
