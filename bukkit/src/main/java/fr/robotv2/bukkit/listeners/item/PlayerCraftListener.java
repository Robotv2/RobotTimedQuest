package fr.robotv2.bukkit.listeners.item;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import fr.robotv2.bukkit.util.item.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ComplexRecipe;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class PlayerCraftListener extends QuestProgressionEnhancer<Material> implements InventoryUtil {

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
            if(this.isForbiddenKey(key)) {
                return;
            }
        }

        final ItemStack result = event.getRecipe().getResult().clone();

        if(result.getType() == Material.AIR) {
            return;
        }

        final InventoryAction action = event.getAction();
        int amount;

        if(action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            int amountCraftable = Integer.MAX_VALUE;

            for (ItemStack itemStack : craftingInventory.getMatrix()) {
                if (itemStack != null && itemStack.getType() != Material.AIR){
                    amountCraftable = Math.min(amountCraftable, itemStack.getAmount() / result.getAmount());
                }
            }

            result.setAmount(amountCraftable);
            amount = this.canTakeItem(player.getInventory(), result, amountCraftable);
        } else {
            amount = this.getAmountFromInventoryAction(player, result, action, event.getSlotType());
        }

        if(amount == 0) {
            return;
        }

        this.incrementProgression(player, QuestType.CRAFT, result.getType(), event, amount);
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
