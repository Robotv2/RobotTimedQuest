package fr.robotv2.bukkit.util;

import org.bukkit.Material;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BrewUtil {

    // UTILITY CLASS

    private BrewUtil() { }

    @Nullable
    public static ItemStack getFirstNonNull(BrewerInventory inventory) {

        for(int i = 0; i <= 2; i++) {
            final ItemStack stack = inventory.getItem(i);

            if(stack == null || stack.getType() == Material.AIR) {
                continue;
            }

            return stack;
        }

        return null;
    }

    public static int numberOfNonNullSlot(BrewerInventory inventory) {

        int numberOfPotions = 0;

        for(int i = 0; i <= 2; i++) {
            final ItemStack stack = inventory.getItem(i);
            if(stack != null && stack.getType() != Material.AIR) {
                numberOfPotions++;
            }
        }

        return numberOfPotions;
    }

    public static boolean isPossibleIngredient(Material material) {
        switch (material) {
            case SUGAR:
            case RABBIT_FOOT:
            case GLISTERING_MELON_SLICE:
            case PUFFERFISH:
            case MAGMA_CREAM:
            case PHANTOM_MEMBRANE:
            case GOLDEN_CARROT:
            case BLAZE_POWDER:
            case GHAST_TEAR:
            case TURTLE_HELMET:
            case FERMENTED_SPIDER_EYE:
            case SPIDER_EYE:
            case NETHER_WART:
                return true;
            default:
                return false;
        }
    }
}
