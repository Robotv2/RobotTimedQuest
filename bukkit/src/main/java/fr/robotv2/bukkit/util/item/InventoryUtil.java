package fr.robotv2.bukkit.util.item;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface InventoryUtil {

    /*
    Return the amount of item that can be placed in the inventory.
    */
    default int availableToTake(Inventory inventory, ItemStack stack, int amountToAdd) {
        int availableSlots = getAvailableSlots(inventory, stack);
        return Math.min(availableSlots, amountToAdd);
    }

    default int getAvailableSlots(Inventory inventory, ItemStack stack) {

        int availableSlots = 0;

        for(ItemStack inventoryItem : inventory.getStorageContents()) {
            if(inventoryItem == null || inventoryItem.getType() == Material.AIR) {
                availableSlots += stack.getMaxStackSize();
            } else if(inventoryItem.isSimilar(stack)) {
                availableSlots += stack.getMaxStackSize() - inventoryItem.getAmount();
            }
        }

        return availableSlots;
    }

    default int getAmountFromInventoryAction(Player initiator, ItemStack itemStack, InventoryAction action, InventoryType.SlotType slotType) {

        if(slotType != InventoryType.SlotType.RESULT) {
            return 0;
        }

        // Null checks to prevent NullPointerException
        if (initiator == null || itemStack == null || action == null) {
            return 0;
        }

        RTQBukkitPlugin.getInstance().debug("AMOUNT_FROM_ACTION - action = %s", action.name());

        int amount = 0;

        switch (action) {
            case PICKUP_ALL:
                amount = itemStack.getAmount();
                break;

            case PICKUP_HALF:
                amount = (int) Math.ceil(itemStack.getAmount() / 2.0);
                break;

            case PICKUP_ONE:
                amount = 1;
                break;

            case DROP_ALL_SLOT:
                if(initiator.getItemOnCursor().getType() == Material.AIR) {
                    amount = itemStack.getAmount();
                }
                break;

            case DROP_ONE_SLOT:
                if(initiator.getItemOnCursor().getType() == Material.AIR) {
                    amount = 1;
                }
                break;

            case MOVE_TO_OTHER_INVENTORY:
                amount = this.availableToTake(initiator.getInventory(), itemStack, itemStack.getAmount());
                break;

            case SWAP_WITH_CURSOR:
                amount = itemStack.getAmount();
                break;

            case NOTHING:
            case PLACE_ALL:
            case PLACE_ONE:
            case PLACE_SOME:
            case DROP_ALL_CURSOR:
            case DROP_ONE_CURSOR:
            case HOTBAR_MOVE_AND_READD:
            case HOTBAR_SWAP:
            case CLONE_STACK:
            case COLLECT_TO_CURSOR:
            case UNKNOWN:
            case PICKUP_SOME:
            default:
                break;
        }

        RTQBukkitPlugin.getInstance().debug("AMOUNT_FROM_ACTION - amount = %d", amount);

        // Ensure the return value is never below 0.
        return Math.max(amount, 0);
    }
}
