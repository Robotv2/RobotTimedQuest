package fr.robotv2.bukkit.listeners.entity;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;

public class EntityFishListener extends QuestProgressionEnhancer<EntityType> {

    public EntityFishListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {

        if(event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        final Player player = event.getPlayer();
        final Entity entity = event.getCaught();

        if(entity == null) {
            return;
        }

        this.incrementProgression(player, QuestType.FISH, entity.getType(), event, 1);
    }
}
