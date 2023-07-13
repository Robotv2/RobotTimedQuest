package fr.robotv2.bukkit.util;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemUtil {

    // UTILITY CLASS

    private ItemUtil() { }

    public static Optional<ItemMeta> getMetaSafe(ItemStack stack) {
        return stack.hasItemMeta() && stack.getType() != Material.AIR
                ? Optional.ofNullable(stack.getItemMeta())
                : Optional.empty();
    }

    public static boolean checkName(String name, ItemStack comparator) {
        final Optional<ItemMeta> optional = getMetaSafe(comparator);

        if(optional.isPresent() && optional.get().hasDisplayName()) {
            return optional.get().getDisplayName().equals(name);
        }

        return false;
    }

    public static boolean checkModelData(int modelData, ItemStack comparator) {
        final Optional<ItemMeta> optional = getMetaSafe(comparator);

        if(optional.isPresent() && optional.get().hasCustomModelData()) {
            return optional.get().getCustomModelData() == modelData;
        }

        return false;
    }

    public static boolean isItem(ConfigurationSection parent, ItemStack comparator) {

        if(parent == null) {
            return true; // there is nothing to check.
        }

        final String name = parent.getString("name");
        final int customModelData = parent.getInt("custom-model-data", Integer.MIN_VALUE);
        final Set<Material> materials = parent.getStringList("materials")
                .stream()
                .map(Material::matchMaterial)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet())
                ;

        if(name != null) {
            return checkName(name, comparator);
        }

        if(customModelData != Integer.MIN_VALUE) {
            return checkModelData(customModelData, comparator);
        }

        if(!materials.isEmpty()) {
            return materials.contains(comparator.getType());
        }

        return true;
    }

}
