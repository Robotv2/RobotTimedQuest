package fr.robotv2.bukkit.listeners.entity;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class EntityMilkListener extends QuestProgressionEnhancer<EntityType> {

    public EntityMilkListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerInteractEntityEvent event) {

        final ItemStack itemInHand = event.getPlayer().getInventory().getItem(EquipmentSlot.HAND);

        if(itemInHand.getType() != Material.MILK_BUCKET) {
            return;
        }

        incrementProgression(event.getPlayer(), QuestType.MILKING, event.getRightClicked().getType(), event, 1);
    }
}
