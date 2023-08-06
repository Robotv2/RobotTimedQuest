package fr.robotv2.bukkit.listeners;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.util.Options;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class GlitchChecker implements Listener {

    private static final String METADATA_KEY = "robot_timed_quest_marked:";

    private final RTQBukkitPlugin plugin;

    public GlitchChecker(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    public void mark(Metadatable source, Player activator) {
        source.setMetadata(METADATA_KEY,
                new FixedMetadataValue(plugin, activator.getUniqueId().toString()));
    }

    public void mark(Metadatable source) {
        source.setMetadata(METADATA_KEY,
                new FixedMetadataValue(plugin, METADATA_KEY)
        );
    }

    public void unMark(Metadatable source) {
        source.removeMetadata(METADATA_KEY, plugin);
    }

    public boolean isMarked(Metadatable source) {
        return source.hasMetadata(METADATA_KEY);
    }

    @Nullable
    public UUID getActivator(Metadatable source) {

        if(!this.isMarked(source) || source.getMetadata(METADATA_KEY).isEmpty()) {
            return null;
        }

        final FixedMetadataValue value = (FixedMetadataValue) source.getMetadata(METADATA_KEY).get(0);

        try {
            return UUID.fromString((String) Objects.requireNonNull(value.value()));
        } catch (Exception exception) {
            return null;
        }
    }

    /* { LISTENERS } */

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {

        if(event.getPlayer().getGameMode() == GameMode.CREATIVE
                && Options.DISABLE_BLOCK_MARKING_FROM_CREATIVE) {
            return; // Do not mark if the player is in creative.
        }

        this.mark(event.getBlock(), event.getPlayer());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        final Block block = event.getBlock();

        if(this.isMarked(block)) {
            this.unMark(block);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        this.mark(event.getItemDrop(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBreak(BlockDropItemEvent event) {
        if(this.isMarked(event.getBlockState())) { // block is placed by a player
            if(Options.DISABLE_ITEMS_FROM_PLACED_BLOCK) {
                event.getItems().forEach(item -> this.mark(item, event.getPlayer())); // not consider
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntitySpawn(SpawnerSpawnEvent event) {
        if(Options.DISABLE_SPAWNERS_PROGRESSION) {
            this.mark(event.getEntity());
        }
    }
}
