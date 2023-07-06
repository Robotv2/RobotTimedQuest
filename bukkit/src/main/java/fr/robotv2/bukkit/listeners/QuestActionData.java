package fr.robotv2.bukkit.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class QuestActionData {

    // DATA CLASS

    private final UUID playerUUID;
    private final Block block;
    private final Entity entity;
    private final ItemStack itemStack;

    public static QuestActionData of(@NotNull Player player, @Nullable Block block, @Nullable Entity entity, @Nullable ItemStack stack) {
        return new QuestActionData(player.getUniqueId(), block, entity, stack);
    }

    public static QuestActionData of(@NotNull Player player, ItemStack itemStack) {
        return of(player, null, null, itemStack);
    }

    public static QuestActionData of(@NotNull Player player, Block block) {
        return of(player, block, null, null);
    }

    public static QuestActionData of(@NotNull Player player, Entity entity) {
        return of(player, null, entity, null);
    }

    public static QuestActionData of(@NotNull Player player) {
        return of(player, null, null, null);
    }

    public QuestActionData(UUID playerUUID, Block block, Entity entity, ItemStack itemStack) {
        this.playerUUID = playerUUID;
        this.block = block;
        this.entity = entity;
        this.itemStack = itemStack;
    }

    @NotNull
    public Player getPlayer() {
        return Objects.requireNonNull(Bukkit.getPlayer(playerUUID));
    }

    public Block getBlock() {
        return block;
    }

    public Entity getEntity() {
        return entity;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
