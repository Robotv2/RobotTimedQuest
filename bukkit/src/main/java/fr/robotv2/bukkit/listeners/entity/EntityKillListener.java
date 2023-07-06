package fr.robotv2.bukkit.listeners.entity;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestActionData;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityKillListener extends QuestProgressionEnhancer<EntityType> {

    public EntityKillListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityKill(EntityDeathEvent event) {

        final LivingEntity entity = event.getEntity();
        final Player player = entity.getKiller();

        if(this.getGlitchChecker().isMarked(entity)) {
            return; // entity is from a spawner.
        }

        if(player == null) {
            return;
        }

        final QuestActionData data = QuestActionData.of(player, entity);
        this.incrementProgression(player, QuestType.KILL, event.getEntityType(), data,1);
    }
}
