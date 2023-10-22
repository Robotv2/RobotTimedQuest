package fr.robotv2.bukkit.hook.mythicmob;

import fr.robotv2.bukkit.hook.Hook;
import fr.robotv2.bukkit.hook.mythicmob.conditions.EntityFromMythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class MythicMobHook implements Hook {

    @Override
    public boolean initialize(JavaPlugin plugin) {
        return MythicBukkit.inst() != null;
    }

    @Override
    public void loadConditions() {
        registerCondition("is_entity_from_mythicmob", EntityFromMythicMob.class);
    }

    public static boolean isMythicMobEntity(Entity entity) {
        return MythicBukkit.inst().getMobManager().isActiveMob(entity.getUniqueId());
    }

    public static boolean isMythicMobEntity(Entity entity, String type) {
        final Optional<ActiveMob> activeMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId());
        return activeMob.isPresent() && activeMob.get().getMobType().equals(type);
    }

    public static String getMythicMobType(Entity entity) {
        final Optional<ActiveMob> activeMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId());
        return activeMob.map(ActiveMob::getMobType).orElse(null);
    }
}
