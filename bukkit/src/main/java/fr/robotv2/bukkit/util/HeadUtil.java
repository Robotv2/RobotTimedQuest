package fr.robotv2.bukkit.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class HeadUtil {

    private static final Map<String, ItemStack> heads = new ConcurrentHashMap<>();

    public static Map<String, ItemStack> getCachedHeads() {
        return HeadUtil.heads;
    }

    public static CompletableFuture<ItemStack> getPlayerHead(OfflinePlayer offlinePlayer) {

        final CompletableFuture<ItemStack> future = CompletableFuture.supplyAsync(() -> {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = Objects.requireNonNull((SkullMeta) head.getItemMeta());

            meta.setOwningPlayer(offlinePlayer);
            head.setItemMeta(meta);

            return head;
        });

        future.thenAccept(itemStack -> {
            if(!heads.containsKey(offlinePlayer.getUniqueId().toString())) {
                heads.put(offlinePlayer.getUniqueId().toString(), itemStack);
            }
        });

        return future;
    }

    public static CompletableFuture<ItemStack> getPlayerHead(UUID playerUUID) {

        if(HeadUtil.getCachedHeads().containsKey(playerUUID.toString())) {
            return CompletableFuture.completedFuture(HeadUtil.getCachedHeads().get(playerUUID.toString()));
        }

        return getPlayerHead(Bukkit.getOfflinePlayer(playerUUID));
    }

    public static CompletableFuture<ItemStack> getPlayerHead(String playerName) {

        if(HeadUtil.getCachedHeads().containsKey(playerName)) {
            return CompletableFuture.completedFuture(HeadUtil.getCachedHeads().get(playerName));
        }

        return getPlayerHead(Bukkit.getOfflinePlayer(playerName));
    }

    public static CompletableFuture<ItemStack> createSkull(String value) {

        if(HeadUtil.getCachedHeads().containsKey(value)) {
            return CompletableFuture.completedFuture(HeadUtil.getCachedHeads().get(value));
        }

        final CompletableFuture<ItemStack> future = CompletableFuture.supplyAsync(() -> {
            final ItemStack head = new ItemStack(Material.PLAYER_HEAD);

            if (value.isEmpty()) {
                return head;
            }

            SkullMeta headMeta = Objects.requireNonNull((SkullMeta) head.getItemMeta());
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", value));

            try {
                Field profileField = headMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(headMeta, profile);
            } catch (IllegalArgumentException|NoSuchFieldException|SecurityException | IllegalAccessException error) {
                error.printStackTrace();
            }

            head.setItemMeta(headMeta);
            return head;
        });

        future.thenAccept(itemStack -> {
            if(!heads.containsKey(value)) {
                heads.put(value, itemStack);
            }
        });

        return future;
    };

}
