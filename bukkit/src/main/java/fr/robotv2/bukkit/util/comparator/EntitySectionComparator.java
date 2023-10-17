package fr.robotv2.bukkit.util.comparator;

import com.google.common.base.Enums;
import fr.robotv2.bukkit.hook.Hooks;
import fr.robotv2.bukkit.hook.elitemob.EliteMobHook;
import fr.robotv2.bukkit.hook.mythicmob.MythicMobHook;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class EntitySectionComparator extends SectionComparator<Entity> {

    private final String mythicMob;

    private final String eliteMob;
    private final String eliteNpc;

    private final String name;
    private final Set<EntityType> types;

    public EntitySectionComparator(ConfigurationSection parent) {
        super(parent);
        this.mythicMob = parent.getString("mythic_mob");
        this.eliteMob = parent.getString("elite_mob");
        this.eliteNpc = parent.getString("elite_npc");
        this.name = parent.getString("name");
        this.types = parent.getStringList("types").stream()
                .map(type -> {
                    final Optional<EntityType> optional = Optional.ofNullable(Enums.getIfPresent(EntityType.class, type).orNull());
                    return optional.orElseGet(() -> EntityType.fromName(type.toUpperCase(Locale.ROOT)));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isSame(Entity value) {

        if(mythicMob != null && Hooks.MYTHIC_MOB.isInitialized()) {
            return MythicMobHook.isMythicMobEntity(value, mythicMob);
        }

        if(Hooks.ELITE_MOB.isInitialized()) {

            if(eliteMob != null) {
                return EliteMobHook.isEliteMobEntity(value, eliteMob);
            }

            if(eliteNpc != null) {
                return EliteMobHook.isEliteMobNpc(value, eliteNpc);
            }
        }

        if(name != null) {
            if(value.getCustomName() == null || !value.getCustomName().equals(name)) {
                return false;
            }
        }

        if(!types.isEmpty() && !types.contains(value.getType())) {
            return false;
        }

        return true;
    }
}
