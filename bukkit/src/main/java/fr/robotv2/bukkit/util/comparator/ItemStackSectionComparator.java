package fr.robotv2.bukkit.util.comparator;

import fr.robotv2.bukkit.hook.Hooks;
import fr.robotv2.bukkit.hook.ItemAdderHook;
import fr.robotv2.bukkit.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemStackSectionComparator extends SectionComparator<ItemStack> {

    private final String itemAdder;
    private final String name;
    private final int customModelData;
    private final Set<Material> materials;

    public ItemStackSectionComparator(ConfigurationSection parent) {
        super(parent);
        this.itemAdder = parent.getString("item_adder");
        this.name = parent.getString("name");
        this.customModelData = parent.getInt("custom_model_data");
        this.materials = parent.getStringList("materials")
                .stream()
                .map(materialString -> Material.matchMaterial(materialString.toUpperCase()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isSame(ItemStack value) {

        if(itemAdder != null && Hooks.isItemAdderEnabled()) {
            return ItemAdderHook.isCustomItem(value, itemAdder);
        }

        if(name != null) { // The target itemstack NEED a custom name
            if(!ItemUtil.checkName(name, value)) { // if it doesn't have this custom name then false.
                return false;
            }
        }

        if(customModelData != Integer.MIN_VALUE) {
            if(!ItemUtil.checkModelData(customModelData, value)) {
                return false;
            }
        }

        if(!materials.isEmpty()) {
            if(!materials.contains(value.getType())) {
                return false;
            }
        }

        return true;
    }
}
