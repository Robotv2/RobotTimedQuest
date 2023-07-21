package fr.robotv2.bukkit.util;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.hook.Hooks;
import fr.robotv2.bukkit.hook.ItemAdderHook;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Nullable
    public static ItemStack toItemStack(ConfigurationSection parent) {
        return toItemStack(parent, null);
    }

    @Nullable
    public static ItemStack toItemStack(ConfigurationSection parent, @Nullable Player player) {
        final String itemAdder = parent.getString("item_adder");
        if(itemAdder != null && Hooks.isItemAdderEnabled()) {

            if(!ItemAdderHook.isValidItemRegistry(itemAdder)) {
                RTQBukkitPlugin.getPluginLogger().warning( itemAdder + " is not a valid item adder id.");
                return null;
            }

            return ItemAdderHook.getCustomStack(itemAdder);
        }

        final Material material = Material.matchMaterial(parent.getString("material", "BOOK"));
        final String name = parent.getString("name");
        final List<String> lore = parent.getStringList("lore");
        final int customModelData = parent.getInt("custom_model_data", Integer.MIN_VALUE);

        final ItemStack stack = new ItemStack(Objects.requireNonNull(material));
        final ItemMeta meta = Objects.requireNonNull(stack.getItemMeta());

        if(name != null) {
            meta.setDisplayName(ColorUtil.color(name));
        }

        if(customModelData != Integer.MIN_VALUE) {
            meta.setCustomModelData(customModelData);
        }

        Stream<String> stream = lore.stream().map(ColorUtil::color);

        if(player != null) {
            stream = stream.map(line -> PlaceholderUtil.parsePlaceholders(player, line));
        }

        meta.setLore(stream.collect(Collectors.toList()));
        stack.setItemMeta(meta);

        return stack;
    }
}
