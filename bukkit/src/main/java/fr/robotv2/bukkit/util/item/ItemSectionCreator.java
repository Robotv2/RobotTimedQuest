package fr.robotv2.bukkit.util.item;

import fr.robotv2.bukkit.util.ItemUtil;
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
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemSectionCreator {

    private final ConfigurationSection section;

    private final Material material;
    private final String name;
    private final List<String> lore;
    private final int customModelData;

    boolean isCustomHead;
    boolean isCustomItem;

    public ItemSectionCreator(ConfigurationSection section) {
        this.section = section;

        final String materialString = section.getString("material");
        this.material = materialString != null ? Objects.requireNonNull(Material.matchMaterial(materialString), materialString + " not a valid material name.") : Material.STONE;

        this.name = section.getString("name");
        this.lore = section.getStringList("lore");
        this.customModelData = section.getInt("custom_model_data", Integer.MIN_VALUE);

        this.isCustomHead = (section.isSet("head_texture") || section.isSet("head_owner")) && material == Material.PLAYER_HEAD;
        this.isCustomItem = section.isSet("item_adder") || section.isSet("oraxen");
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public boolean hasCustomName() {
        return name != null;
    }

    public boolean hasCustomModelData() {
        return customModelData != Integer.MIN_VALUE;
    }

    public boolean isCustomHead() {
        return this.isCustomHead;
    }

    public boolean isCustomItem() {
        return this.isCustomItem;
    }

    public CompletableFuture<ItemStack> getFutureItem(@Nullable Player player) {

        CompletableFuture<ItemStack> future = null;

        if(isCustomHead()) {
            future = ItemUtil.getCustomHead(section);
        }

        if(isCustomItem()) {
            final ItemStack customItem = ItemUtil.getCustomItem(section);
            future = CompletableFuture.completedFuture(customItem);
        }

        if(future == null) {
            future = CompletableFuture.completedFuture(new ItemStack(this.material));
        }

        return future.thenApply(consumerItemstack(player));
    }

    private void applyCustomName(ItemMeta meta) {
        if(hasCustomName()) {
            meta.setDisplayName(ColorUtil.color(getName()));
        }
    }

    private void applyCustomModelData(ItemMeta meta) {
        if(hasCustomModelData()) {
            meta.setCustomModelData(getCustomModelData());
        }
    }

    private Function<ItemStack, ItemStack> consumerItemstack(@Nullable Player player) {
        return itemStack -> {
            final ItemMeta meta = Objects.requireNonNull(itemStack.getItemMeta());

            applyCustomName(meta);
            applyCustomModelData(meta);

            Stream<String> stream = lore.stream();

            if(player != null) {
                stream = stream.map(line -> PlaceholderUtil.parsePlaceholders(player, line));
            }

            meta.setLore(stream.map(ColorUtil::color).collect(Collectors.toList()));
            itemStack.setItemMeta(meta);

            return itemStack;
        };
    }
}
