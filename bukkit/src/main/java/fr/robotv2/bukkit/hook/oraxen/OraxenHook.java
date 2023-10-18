package fr.robotv2.bukkit.hook.oraxen;

import fr.robotv2.bukkit.hook.Hook;
import fr.robotv2.bukkit.hook.oraxen.conditions.BlockIsFromOraxen;
import fr.robotv2.bukkit.hook.oraxen.conditions.ItemIsFromOraxen;
import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class OraxenHook implements Hook {

    public static boolean isValidItemRegistry(String namespaceID) {
        return OraxenItems.exists(namespaceID);
    }

    public static boolean isValidBlockRegistry(String namespaceID) {
        return OraxenBlocks.isOraxenBlock(namespaceID);
    }

    public static boolean isCustomItem(ItemStack stack) {
        return OraxenItems.exists(stack);
    }

    public static boolean isCustomBlock(Block block) {
        return OraxenBlocks.isOraxenNoteBlock(block);
    }

    public static boolean isCustomItem(ItemStack stack, String namespaceID) {

        if(!OraxenHook.isValidItemRegistry(namespaceID)) {
            return false;
        }

        final String identifier = OraxenItems.getIdByItem(stack);

        if(identifier == null) {
            return false;
        }

        return identifier.equals(namespaceID);
    }

    public static boolean isCustomBlock(Block block, String namespaceID) {

        if(!OraxenHook.isValidBlockRegistry(namespaceID)) {
            return false;
        }

        final String identifier = OraxenBlocks.getOraxenBlock(block.getBlockData()).getItemID();

        if(identifier == null) {
            return false;
        }

        return identifier.equals(namespaceID);
    }

    public static ItemStack getCustomStack(String namespaceID) {

        if(!OraxenHook.isValidBlockRegistry(namespaceID)) {
            return null;
        }

        return OraxenItems.getItemById(namespaceID).build();
    }

    @Override
    public boolean initialize(JavaPlugin plugin) {
        try {
            Class.forName("io.th0rgal.oraxen.api.OraxenBlocks");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public void loadConditions() {
        registerCondition("is_block_from_oraxen", BlockIsFromOraxen.class);
        registerCondition("is_item_from_oraxen", ItemIsFromOraxen.class);
    }
}
