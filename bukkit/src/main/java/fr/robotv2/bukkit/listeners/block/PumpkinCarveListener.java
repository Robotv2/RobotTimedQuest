package fr.robotv2.bukkit.listeners.block;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PumpkinCarveListener extends QuestProgressionEnhancer<Material> {

    public PumpkinCarveListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPumpkinCarve(PlayerInteractEvent event) {

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if(event.getItem() == null || event.getItem().getType() != Material.SHEARS) {
            return;
        }

        if(event.getClickedBlock() == null && event.getClickedBlock().getType() != Material.PUMPKIN) {
            return;
        }

        this.incrementProgression(event.getPlayer(), QuestType.CARVE, Material.PUMPKIN, event, 1);
    }
}
