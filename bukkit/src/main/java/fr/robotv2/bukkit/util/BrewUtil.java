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
}
