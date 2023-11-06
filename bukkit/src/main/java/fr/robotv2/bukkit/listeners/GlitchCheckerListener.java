package fr.robotv2.bukkit.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.util.Options;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class GlitchCheckerListener extends GlitchChecker implements Listener {

    public GlitchCheckerListener(RTQBukkitPlugin plugin) {
        super(plugin);
        CustomBlockData.registerListener(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {

        if(event.getPlayer().getGameMode() == GameMode.CREATIVE && Options.COUNT_BLOCK_FROM_CREATIVE) {
            return; // Do not mark if the player is in creative.
        }

        this.mark(event.getBlock(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        this.mark(event.getItemDrop(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBreak(BlockDropItemEvent event) {
        if(this.isMarked(event.getBlockState().getBlock())) { // block is placed by a player
            if(Options.COUNT_ITEMS_FROM_PLACED_BLOCK) {
                event.getItems().forEach(item -> {
                    this.mark(item, event.getPlayer());
                });
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
