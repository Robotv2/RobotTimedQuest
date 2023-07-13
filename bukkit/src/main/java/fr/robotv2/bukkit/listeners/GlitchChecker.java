package fr.robotv2.bukkit.listeners;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

public class GlitchChecker implements Listener {

    private static final String METADATA_KEY = "robot_timed_quest_marked";

    private final RTQBukkitPlugin plugin;
    private final MetadataValue value;

    public GlitchChecker(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
        this.value = new FixedMetadataValue(plugin, METADATA_KEY);
    }

    public void mark(Metadatable source) {
        source.setMetadata(METADATA_KEY, value);
    }

    public void unMark(Metadatable source) {
        source.removeMetadata(METADATA_KEY, plugin);
    }

    public boolean isMarked(Metadatable source) {
        return source.hasMetadata(METADATA_KEY);
    }

    /* { LISTENERS } */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {

        if(event.getPlayer().getGameMode() == GameMode.CREATIVE
                && this.plugin.getConfig().getBoolean("options.anti-dupe.disable_marking_creative_progression")) {
            return; // Do not mark if the player is in creative.
        }

        this.mark(event.getBlock());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        this.mark(event.getItemDrop());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntitySpawn(SpawnerSpawnEvent event) {
        if(this.plugin.getConfig().getBoolean("options.anti-dupe.disable_spawners_progression")) {
            this.mark(event.getEntity());
        }
    }
}
