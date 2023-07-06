package fr.robotv2.bukkit.listeners.item;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestActionData;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class PlayerCraftListener extends QuestProgressionEnhancer<Material> {

    public PlayerCraftListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCraft(InventoryClickEvent event) {

        if(!(event.getInventory() instanceof CraftingInventory)) {
            return;
        }

        if(event.getSlot() != 0) {
            return;
        }

        final Player player = (Player) event.getWhoClicked();
        final ItemStack item = event.getCurrentItem();

        if(item == null) {
            return;
        }

        final QuestActionData data = QuestActionData.of(player, item);
        this.incrementProgression(player, QuestType.CRAFT, item.getType(), data, item.getAmount());
    }
}
