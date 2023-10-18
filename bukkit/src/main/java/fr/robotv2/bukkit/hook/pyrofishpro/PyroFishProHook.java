package fr.robotv2.bukkit.hook.pyrofishpro;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fr.robotv2.bukkit.RobotTimedQuestAPI;
import fr.robotv2.bukkit.hook.Hook;
import fr.robotv2.bukkit.hook.pyrofishpro.conditions.IsPyroFish;
import fr.robotv2.bukkit.hook.pyrofishpro.conditions.IsPyroTier;
import fr.robotv2.bukkit.hook.pyrofishpro.listeners.PyroFishProEventCaller;
import fr.robotv2.bukkit.hook.pyrofishpro.type.PyroFishType;
import fr.robotv2.bukkit.util.item.ItemUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.FishHook;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PyroFishProHook implements Hook {

    private final static Cache<UUID, FishHook> FISH_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(3500, TimeUnit.MILLISECONDS)
            .expireAfterAccess(1000, TimeUnit.MILLISECONDS)
            .build();

    public static final String PYRO_KEY_PREFIX = "pyrofishingpro";

    public static class PyroFishWrapper {

        public final String tier;
        public final Integer fishnumber;
        public final Double price;

        public PyroFishWrapper(ItemStack fish) {
            final PersistentDataContainer container = Objects.requireNonNull(fish.getItemMeta()).getPersistentDataContainer();
            this.tier = container.get(new NamespacedKey(PYRO_KEY_PREFIX, "tier"), PersistentDataType.STRING);
            this.fishnumber = container.get(new NamespacedKey(PYRO_KEY_PREFIX, "fishnumber"), PersistentDataType.INTEGER);
            this.price = container.get(new NamespacedKey(PYRO_KEY_PREFIX, "price"), PersistentDataType.DOUBLE);
        }
    }

    public boolean initialize(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new PyroFishProEventCaller(), plugin);
        RobotTimedQuestAPI.registerCustomType(new PyroFishType());
        return true;
    }

    @Override
    public void loadConditions() {
        registerCondition("is_pyro_tier", IsPyroTier.class);
        registerCondition("is_pyro_fish", IsPyroFish.class);
    }

    public static boolean isPyroFish(ItemStack fish) {
        final Optional<ItemMeta> optional = ItemUtil.getMetaSafe(fish);

        if(!optional.isPresent()) {
            return false;
        }

        final NamespacedKey key = new NamespacedKey(PYRO_KEY_PREFIX, "tier");

        final PersistentDataContainer container = optional.get().getPersistentDataContainer();
        return container.has(key, PersistentDataType.STRING);
    }

    public static PyroFishWrapper toWrapper(ItemStack fish) {
        return isPyroFish(fish) ? new PyroFishWrapper(fish) : null;
    }

    public static FishHook getRecentHook(UUID playerId) {
        return FISH_CACHE.getIfPresent(playerId);
    }

    public static void setRecentHook(UUID playerId, FishHook hook) {
        FISH_CACHE.put(playerId, hook);
    }
}
