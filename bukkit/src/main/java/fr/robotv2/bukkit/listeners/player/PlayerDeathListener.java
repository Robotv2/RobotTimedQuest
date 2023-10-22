package fr.robotv2.bukkit.listeners.player;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener extends QuestProgressionEnhancer<String> {

    public PlayerDeathListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {

        final EntityDamageEvent entityDamageEvent = event.getEntity().getLastDamageCause();
        final EntityDamageEvent.DamageCause cause = entityDamageEvent != null ? entityDamageEvent.getCause() : EntityDamageEvent.DamageCause.CUSTOM;

        this.incrementProgression(event.getEntity(), QuestType.PLAYER_DEATH, cause.name(), event, 1);
    }
}
