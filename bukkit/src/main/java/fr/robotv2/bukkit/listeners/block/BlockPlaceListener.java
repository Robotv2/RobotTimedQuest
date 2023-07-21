package fr.robotv2.bukkit.listeners.block;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
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
        this.incrementProgression(event.getPlayer(), QuestType.PLACE, event.getBlock().getType(), event, 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if(this.getGlitchChecker().isMarked(block)) {
            final UUID uuid = getGlitchChecker().getActivator(block);
            if(uuid != null && uuid.equals(player.getUniqueId())) {
                this.incrementProgression(player, QuestType.PLACE, block.getType(), event, -1);
            }
        }
    }
}
