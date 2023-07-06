package fr.robotv2.bukkit.listeners.entity;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestActionData;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class EntityShearListener extends QuestProgressionEnhancer<EntityType> {

    public EntityShearListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event) {

        final Player player = event.getPlayer();
        final Entity entity = event.getEntity();

        final QuestActionData data = QuestActionData.of(player, entity);
        this.incrementProgression(player, QuestType.SHEAR, event.getEntity().getType(), data, 1);
    }
}
