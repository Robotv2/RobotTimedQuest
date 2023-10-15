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
import org.bukkit.inventory.ItemStack;

public class PlayerCraftListener extends QuestProgressionEnhancer<Material> implements InventoryUtil {

    private final String[] FORBIDDEN_KEYS = {"REPAIR_ITEM", "ARMOR_DYE", "SHULKER_BOX_COLORING", "SHIELD_DECORATION", "BANNER_DUPLICATE", "MAP_CLONING"};

    public PlayerCraftListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {

        final Player player = (Player) event.getWhoClicked();

        if(event.getRecipe() instanceof ComplexRecipe) {
            final String key = ((ComplexRecipe) event.getRecipe()).getKey().getKey();
            if(this.isForbiddenKey(key)) {
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack result = event.getRecipe().getResult().clone();

        if(result.getType() == Material.AIR) {
            return;
        }

        final InventoryAction action = event.getAction();
        int amount = 0;

        if(action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {

            final int producedItemAmount = result.getAmount();

            getPlugin().debug("CRAFT -> produced item amount %d", producedItemAmount);

            int smallestNumber = Integer.MAX_VALUE;

            for (ItemStack itemStack : matrix) {
                if (itemStack != null) {
                    int itemAmount = itemStack.getAmount();
                    smallestNumber = Math.min(itemAmount, smallestNumber);
                }
            }

            if(smallestNumber == Integer.MAX_VALUE) {
                return;
            }

            amount = this.availableToTake(player.getInventory(), result, (smallestNumber * producedItemAmount));
        } else {
            amount = this.getAmountFromInventoryAction(player, result, action, event.getSlotType());
        }

        getPlugin().debug("CRAFT -> %d", amount);
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
