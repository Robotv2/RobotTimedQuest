package fr.robotv2.bukkit.util.item;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.hook.Hooks;
import fr.robotv2.bukkit.hook.itemadder.ItemAdderHook;
import fr.robotv2.bukkit.hook.oraxen.OraxenHook;
import fr.robotv2.bukkit.util.text.PlaceholderUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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

    public static void printDataContainer(ItemStack stack) {

        getMetaSafe(stack).ifPresentOrElse(meta -> {
            final PersistentDataContainer container = meta.getPersistentDataContainer();
            for(NamespacedKey key : container.getKeys()) {

                String value;

                try {
                    value = container.get(key, PersistentDataType.STRING);
                } catch (IllegalArgumentException ignored) {
                    try {
                        Integer intValue = container.get(key, PersistentDataType.INTEGER);
                        value = intValue != null ? intValue.toString() : "NULL INT";
                    } catch (IllegalArgumentException ignored2) {
                        Double doubleValue = container.get(key, PersistentDataType.DOUBLE);
                        value = doubleValue != null ? doubleValue.toString() : "NULL DOUBLE";
                    }
                }


                RTQBukkitPlugin.getInstance().debug("DATA CONTAINER -> key: %s -> value: %s", key, value);
            }
        }, () -> RTQBukkitPlugin.getInstance().debug("DATA CONTIANER -> NO META FOUND FOR %s", stack.getType()));
    }

    public static CompletableFuture<ItemStack> toItemStack(ConfigurationSection parent) {
        return toItemStack(parent, null);
    }

    public static CompletableFuture<ItemStack> toItemStack(ConfigurationSection parent, @Nullable Player player) {
        return new ItemStackSectionCreator(parent).getFutureItem(player);
    }

    @Nullable
    public static ItemStack getCustomItem(ConfigurationSection parent) {

        final String itemAdder = parent.getString("item_adder");
        final String oraxen = parent.getString("oraxen");

        if(itemAdder != null && Hooks.ITEM_ADDER.isInitialized()) {

            if(!ItemAdderHook.isValidItemRegistry(itemAdder)) {
                RTQBukkitPlugin.getPluginLogger().warning( itemAdder + " is not a valid item adder id.");
                return null;
            }

            return ItemAdderHook.getCustomStack(itemAdder);
        } else if(oraxen != null && Hooks.ORAXEN.isInitialized()) {

            if(!OraxenHook.isValidItemRegistry(oraxen)) {
                RTQBukkitPlugin.getPluginLogger().warning( oraxen + " is not a valid oraxen item id.");
                return null;
            }

            return OraxenHook.getCustomStack(oraxen);
        }

        return null;
    }

    @Nullable
    public static CompletableFuture<ItemStack> getCustomHead(Player player, ConfigurationSection parent) {
        if(parent.isSet("head_texture")) {
            return HeadUtil.createSkull(parent.getString("head_texture"));
        } else if(parent.isSet("head_owner")) {
            final String headOwner = PlaceholderUtil.PLAYER_PLACEHOLDER.parse(player, parent.getString("head_owner"));
            return HeadUtil.getPlayerHead(headOwner);
        } else {
            return null;
        }
    }
}