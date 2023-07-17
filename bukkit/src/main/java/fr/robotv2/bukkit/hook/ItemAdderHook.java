package fr.robotv2.bukkit.hook;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ItemAdderHook {

    private ItemAdderHook() { }

    public static boolean initialize(Plugin plugin) {
        return Hooks.isItemAdderEnabled();
    }

    public static boolean isCustomItem(ItemStack stack) {
        return CustomStack.byItemStack(stack) != null;
    }

    public static boolean isCustomItem(ItemStack stack, String namespaceID) {

        if(!CustomStack.isInRegistry(namespaceID)) {
            return false;
        }

        final CustomStack customStack = CustomStack.byItemStack(stack);

        if(customStack == null) {
            return false;
        }

        return customStack.getNamespacedID().equals(namespaceID);
    }

    public static boolean isValidItemRegistry(String namespaceID) {
        return CustomStack.isInRegistry(namespaceID);
    }

    public static ItemStack getCustomStack(String namespaceID) {

        if(!ItemAdderHook.isValidItemRegistry(namespaceID)) {
            return null;
        }

        return CustomStack.getInstance(namespaceID).getItemStack();
    }

}
