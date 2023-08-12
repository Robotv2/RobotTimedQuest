package fr.robotv2.bukkit.listeners.item;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ComplexRecipe;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerCraftListener extends QuestProgressionEnhancer<Material> {

    private final String[] FORBIDDEN_KEYS = {"REPAIR_ITEM", "ARMOR_DYE", "SHULKER_BOX_COLORING", "SHIELD_DECORATION", "BANNER_DUPLICATE", "MAP_CLONING"};

    public PlayerCraftListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {

        final Player player = (Player) event.getWhoClicked();
        final CraftingInventory craftingInventory = event.getInventory();

        if(event.getRecipe() instanceof ComplexRecipe) {
            final String key = ((ComplexRecipe) event.getRecipe()).getKey().getKey();

            this.getPlugin().debug("CRAFT -> KEY: " + key);

            if(this.isForbiddenKey(key)) {
                return;
            }
        }

        final ItemStack result = event.getRecipe().getResult().clone();

        if(result.getType() == Material.AIR) {
            return;
        }

        final InventoryAction action = event.getAction();
        int amount = 0;

        this.getPlugin().debug("CRAFT -> " + action);

        switch (action) {
            case PICKUP_ALL:
                amount = result.getAmount();
                break;
            case DROP_ONE_SLOT:
            case DROP_ALL_SLOT:
                if(player.getItemOnCursor().getType() != Material.AIR) {
                    return;
                } else {
                    amount = result.getAmount();
                    break;
                }
            case MOVE_TO_OTHER_INVENTORY: {

                int amountCraftable = Integer.MAX_VALUE;
                for (ItemStack itemStack : craftingInventory.getMatrix()) {
                    if (itemStack != null && itemStack.getType() != Material.AIR){
                        amountCraftable = Math.min(amountCraftable, itemStack.getAmount() / result.getAmount());
                    }
                }

                result.setAmount(amountCraftable);
                amount = this.canTakeItem(player.getInventory(), result, amountCraftable);

                this.getPlugin().debug("CRAFT -> CRAFTABLE ITEMS: " + amount);

                if(amount == -1) {
                    return;
                }
            }
                break;
            default:
                return;
        }

        this.incrementProgression(player, QuestType.CRAFT, result.getType(), event, amount);
    }

    /*
    Return the amount of item that can be placed in the inventory. return -1 if none.
     */
    private int canTakeItem(Inventory inventory, ItemStack stack, int amount) {

        int availableSlots = 0;

        for (ItemStack inventoryItem : inventory.getStorageContents()) {
            if (inventoryItem == null || inventoryItem.getType() == Material.AIR) {
                availableSlots += stack.getMaxStackSize();
            } else if (inventoryItem.getType() == stack.getType()) {
                availableSlots += stack.getMaxStackSize() - inventoryItem.getAmount();
            }
        }

        // Returns -1 if no space available
        if(availableSlots == 0) {
            return -1;
        }

        // Returns the amount that can be added to the inventory, not exceeding required amount
        // If the available slots are more than the required amount, it returns the required amount
        // If less, it returns the available slots
        return Math.min(availableSlots, amount);
    }

    private boolean isForbiddenKey(String key) {
        for(String forbiddenKey : FORBIDDEN_KEYS) {
            if(key.equalsIgnoreCase(forbiddenKey)) {
                return true;
            }
        }
        return false;
    }
}
