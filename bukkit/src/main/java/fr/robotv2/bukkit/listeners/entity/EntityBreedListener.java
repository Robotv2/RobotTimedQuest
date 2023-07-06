package fr.robotv2.bukkit.listeners.entity;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestActionData;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityBreedEvent;

public class EntityBreedListener extends QuestProgressionEnhancer<EntityType> {

    public EntityBreedListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityBread(EntityBreedEvent event) {
        if (event.getBreeder() instanceof Player) {

            final Player player = (Player) event.getBreeder();
            final QuestActionData data = QuestActionData.of(player, event.getEntity());

            this.incrementProgression(player, QuestType.BREED, event.getEntityType(), data, 1);
        }
    }
}
