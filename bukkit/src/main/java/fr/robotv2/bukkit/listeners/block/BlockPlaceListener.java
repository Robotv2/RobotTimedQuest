package fr.robotv2.bukkit.listeners.block;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import fr.robotv2.bukkit.util.Options;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.UUID;

public class BlockPlaceListener extends QuestProgressionEnhancer<Material> {

    public BlockPlaceListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        final boolean result = this.incrementProgression(event.getPlayer(), QuestType.PLACE, event.getBlockPlaced().getType(), event);
        if(result) {
            this.getGlitchChecker().mark(event.getBlockPlaced(), event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {

        if(!Options.COUNT_BREAKING_PLACED_BLOCK) {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if(this.getGlitchChecker().isMarked(block)) {
            final UUID uuid = getGlitchChecker().getActivator(block);
            if(uuid != null && uuid.equals(player.getUniqueId())) {
                this.incrementProgression(player, QuestType.PLACE, block.getType(), event, -1);
                this.getPlugin().debug("PLACE -> REMOVAL");
            }
        }
    }
}
