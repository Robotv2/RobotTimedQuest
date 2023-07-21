package fr.robotv2.bukkit.hook;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomEntity;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ItemAdderHook {

    private ItemAdderHook() { }

    public static boolean initialize(Plugin plugin) {
        return Hooks.isItemAdderEnabled();
    }

    public static boolean isValidItemRegistry(String namespaceID) {
        return CustomStack.isInRegistry(namespaceID);
    }

    public static boolean isValidBlockRegistry(String namespaceID) {
        return CustomBlock.isInRegistry(namespaceID);
    }

    public static boolean isValidEntityRegistry(String namespaceID) {
        return CustomEntity.isInRegistry(namespaceID);
    }

    public static boolean isCustomItem(ItemStack stack) {
        return CustomStack.byItemStack(stack) != null;
    }

    public static boolean isCustomBlock(Block block) {
        return CustomBlock.byAlreadyPlaced(block) != null;
    }

    public static boolean isCustomEntity(Entity entity) {
        return CustomEntity.isCustomEntity(entity);
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

    public static boolean isCustomBlock(Block block, String namespaceID) {

        if(!CustomBlock.isInRegistry(namespaceID)) {
            return false;
        }

        final CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);

        if(customBlock == null) {
            return false;
        }

        return customBlock.getNamespacedID().equals(namespaceID);
    }

    public static boolean isCustomEntity(Entity entity, String namespaceID) {

        if(!CustomEntity.isCustomEntity(entity)) {
            return false;
        }

        final CustomEntity customEntity = CustomEntity.byAlreadySpawned(entity);

        if(customEntity == null) {
            return false;
        }

        return customEntity.getNamespacedID().equals(namespaceID);
    }

    public static ItemStack getCustomStack(String namespaceID) {

        if(!ItemAdderHook.isValidItemRegistry(namespaceID)) {
            return null;
        }

        return CustomStack.getInstance(namespaceID).getItemStack();
    }
}
