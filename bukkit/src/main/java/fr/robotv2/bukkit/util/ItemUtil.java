package fr.robotv2.bukkit.util;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.hook.Hooks;
import fr.robotv2.bukkit.hook.ItemAdderHook;
import fr.robotv2.bukkit.hook.OraxenHook;
import fr.robotv2.bukkit.util.text.ColorUtil;
import fr.robotv2.bukkit.util.text.PlaceholderUtil;
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

        final Material material = Material.matchMaterial(parent.getString("material", "BOOK"));

        final ItemStack customItem = getCustomItem(parent);
        final ItemStack stack = customItem == null ? new ItemStack(Objects.requireNonNull(material)) : customItem;

        final ItemMeta meta = Objects.requireNonNull(stack.getItemMeta());

        final String name = parent.getString("name");
        final List<String> lore = parent.getStringList("lore");

        final int customModelData = parent.getInt("custom_model_data", Integer.MIN_VALUE);

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

    @Nullable
    private static ItemStack getCustomItem(ConfigurationSection parent) {

        final String itemAdder = parent.getString("item_adder");
        final String oraxen = parent.getString("oraxen");

        if(itemAdder != null && Hooks.isItemAdderEnabled()) {

            if(!ItemAdderHook.isValidItemRegistry(itemAdder)) {
                RTQBukkitPlugin.getPluginLogger().warning( itemAdder + " is not a valid item adder id.");
                return null;
            }

            return ItemAdderHook.getCustomStack(itemAdder);
        } else if(oraxen != null && Hooks.isOraxenEnabled()) {

            if(!OraxenHook.isValidItemRegistry(oraxen)) {
                RTQBukkitPlugin.getPluginLogger().warning( oraxen + " is not a valid oraxen item id.");
                return null;
            }

            return OraxenHook.getCustomStack(oraxen);
        }

        return null;
    }
}
