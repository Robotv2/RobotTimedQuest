package fr.robotv2.bukkit.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import fr.robotv2.bukkit.RTQBukkitPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class GlitchChecker {

    private static final NamespacedKey METADATA_KEY;
    private static final String DUMMY_VALUE = "DUMMY";

    static {
        METADATA_KEY = new NamespacedKey(RTQBukkitPlugin.getInstance(), "robot_timed_quest:marked");
    }

    private final RTQBukkitPlugin plugin;

    public GlitchChecker(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }


    // DUMMY
    public void mark(PersistentDataContainer container) {
        container.set(METADATA_KEY, PersistentDataType.STRING, DUMMY_VALUE);
    }

    public void mark(PersistentDataHolder holder) {
        mark(holder.getPersistentDataContainer());
    }

    public void mark(Block block) {
        mark(new CustomBlockData(block, plugin));
    }


    //PLAYER
    public void mark(PersistentDataContainer container, Player activator) {
        container.set(METADATA_KEY, PersistentDataType.STRING, activator.getUniqueId().toString());
    }

    public void mark(PersistentDataHolder holder, Player activator) {
        mark(holder.getPersistentDataContainer(), activator);
    }

    public void mark(Block block, Player activator) {
        mark(new CustomBlockData(block, plugin), activator);
    }


    // UNMARK
    public void unMark(PersistentDataContainer container) {
        container.remove(METADATA_KEY);
    }

    public void unMark(PersistentDataHolder holder) {
        unMark(holder.getPersistentDataContainer());
    }

    public void unMark(Block block) {
        unMark(new CustomBlockData(block, plugin));
    }


    // IS MARKED
    public boolean isMarked(PersistentDataContainer container) {
        return container.has(METADATA_KEY, PersistentDataType.STRING);
    }

    public boolean isMarked(PersistentDataHolder holder) {
        return isMarked(holder.getPersistentDataContainer());
    }

    public boolean isMarked(Block block) {
        return isMarked(new CustomBlockData(block, plugin));
    }


    // GET ACTIVATOR
    @Nullable
    public UUID getActivator(PersistentDataContainer container) {

        if(!this.isMarked(container)) {
            return null;
        }

        final String value = container.get(METADATA_KEY, PersistentDataType.STRING);

        if(value == null) {
            return null;
        }

        try {
            return UUID.fromString(value);
        } catch (Exception exception) {
            return null;
        }
    }

    @Nullable
    public UUID getActivator(PersistentDataHolder holder) {
        return getActivator(holder.getPersistentDataContainer());
    }

    @Nullable
    public UUID getActivator(Block block) {
        return getActivator(new CustomBlockData(block, plugin));
    }
}
