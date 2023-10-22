package fr.robotv2.bukkit.hook.elitemob;

import com.magmaguy.elitemobs.entitytracker.EntityTracker;
import com.magmaguy.elitemobs.mobconstructor.EliteEntity;
import com.magmaguy.elitemobs.mobconstructor.custombosses.CustomBossEntity;
import com.magmaguy.elitemobs.npcs.NPCEntity;
import fr.robotv2.bukkit.hook.Hook;
import fr.robotv2.bukkit.hook.elitemob.conditions.EntityFromEliteMob;
import fr.robotv2.bukkit.hook.elitemob.conditions.EntityFromEliteMobNpc;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public class EliteMobHook implements Hook {

    public static boolean isEliteMobEntity(Entity entity) {
        return EntityTracker.isEliteMob(entity);
    }

    public static boolean isEliteMobNpc(Entity entity) {
        return EntityTracker.isNPCEntity(entity);
    }

    public static boolean isEliteMobEntity(Entity entity, String type) {
        final String customType = getEliteMobType(entity);
        return customType != null && customType.equals(type);
    }

    public static boolean isEliteMobNpc(Entity entity, String type) {
        final String customType = getEliteMobNpc(entity);
        return customType != null && customType.equals(type);
    }

    public static String getEliteMobType(Entity entity) {
        final EliteEntity eliteEntity = EntityTracker.getEliteMobEntity(entity);
        if(eliteEntity instanceof CustomBossEntity) {
            return ((CustomBossEntity) eliteEntity).getCustomBossesConfigFields().getFilename();
        } else {
            return null;
        }
    }

    public static String getEliteMobNpc(Entity entity) {
        final NPCEntity npcEntity = EntityTracker.getNPCEntity(entity);
        return npcEntity.npCsConfigFields.getFilename();
    }

    @Override
    public boolean initialize(JavaPlugin plugin) {
        try {
            Class.forName("com.magmaguy.elitemobs.entitytracker.EntityTracker");
            return true;
        } catch (ClassNotFoundException e) {
            return  false;
        }
    }

    @Override
    public void loadConditions() {
        registerCondition("is_entity_from_elitemob", EntityFromEliteMob.class);
        registerCondition("is_npc_from_elitemob", EntityFromEliteMobNpc.class);
    }
}
