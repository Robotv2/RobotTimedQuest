package fr.robotv2.bukkit.hook.pyrofishpro;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fr.robotv2.bukkit.RobotTimedQuestAPI;
import fr.robotv2.bukkit.hook.pyrofishpro.type.PyroFishType;
import fr.robotv2.bukkit.util.item.ItemUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PyroFishProHook {

    private final static Cache<UUID, Boolean> FISH_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .build();

    public static final String PYRO_KEY_PREFIX = "pyrofishingpro";

    private PyroFishProHook() { }

    public static boolean initialize(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new PyroFishProListener(), plugin);
        RobotTimedQuestAPI.registerCustomType(new PyroFishType());
        return true;
    }

    public static class PyroFishWrapper {

        public final String tier;
        public final Integer fishnumber;
        public final Double price;

        public PyroFishWrapper(ItemStack fish) {
            final PersistentDataContainer container = Objects.requireNonNull(fish.getItemMeta()).getPersistentDataContainer();
            this.tier = container.get(new NamespacedKey(PYRO_KEY_PREFIX, "fishnumber"), PersistentDataType.STRING);
            this.fishnumber = container.get(new NamespacedKey(PYRO_KEY_PREFIX, "tier"), PersistentDataType.INTEGER);
            this.price = container.get(new NamespacedKey(PYRO_KEY_PREFIX, "price"), PersistentDataType.DOUBLE);
        }
    }

    public static boolean isPyroFish(ItemStack fish) {
        final Optional<ItemMeta> optional = ItemUtil.getMetaSafe(fish);

        if(!optional.isPresent()) {
            return false;
        }

        final PersistentDataContainer container = optional.get().getPersistentDataContainer();
        return container.has(new NamespacedKey(PYRO_KEY_PREFIX, "fishnumber"), PersistentDataType.STRING);
    }

    public static PyroFishWrapper toWrapper(ItemStack fish) {
        return isPyroFish(fish) ? new PyroFishWrapper(fish) : null;
    }

    public static boolean hasFishedRecently(UUID playerId) {
        return FISH_CACHE.getIfPresent(playerId) != null;
    }

    public static void setHasFishedRecently(UUID playerId) {
        FISH_CACHE.put(playerId, true);
    }
}
