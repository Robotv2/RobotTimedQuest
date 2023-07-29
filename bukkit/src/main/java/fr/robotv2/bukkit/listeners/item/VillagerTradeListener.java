package fr.robotv2.bukkit.listeners.item;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.events.VillagerTradeEvent;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

public class VillagerTradeListener extends QuestProgressionEnhancer<Material> {

    public VillagerTradeListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (!(event.getClickedInventory() instanceof MerchantInventory)) {
            return;
        }

        final Player player = (Player) event.getWhoClicked();
        final MerchantInventory merchantInventory = (MerchantInventory) event.getClickedInventory();
        final ItemStack stack = event.getCurrentItem();

        if(stack == null || stack.getType() == Material.AIR) {
            return;
        }

        if(event.getSlotType() == InventoryType.SlotType.RESULT && merchantInventory.getSelectedRecipe() != null) {
            Bukkit.getPluginManager().callEvent(new VillagerTradeEvent(player, merchantInventory, merchantInventory.getSelectedRecipe(), stack));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onTrade(VillagerTradeEvent event) {
        final ItemStack result = event.getResult();
        this.incrementProgression(event.getPlayer(), QuestType.VILLAGER_TRADE, result.getType(), event, result.getAmount());
    }
}
