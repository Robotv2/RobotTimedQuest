package fr.robotv2.bukkit.hook.itemadder;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomEntity;
import dev.lone.itemsadder.api.CustomStack;
import fr.robotv2.bukkit.hook.Hook;
import fr.robotv2.bukkit.hook.itemadder.conditions.BlockIsFromItemAdder;
import fr.robotv2.bukkit.hook.itemadder.conditions.ItemIsFromItemAdder;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemAdderHook implements Hook {

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

    @Override
    public boolean initialize(JavaPlugin plugin) {
        try {
            Class.forName("dev.lone.itemsadder.api.CustomBlock");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public void loadConditions() {
        registerCondition("is_item_from_itemadder", ItemIsFromItemAdder.class);
        registerCondition("is_block_from_itemadder", BlockIsFromItemAdder.class);
    }
}
