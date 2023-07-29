package fr.robotv2.bukkit.quest.conditions.impl.entity;

import com.google.common.base.Enums;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.quest.conditions.Conditions;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

public class VillagerCondition implements Condition {

    private final EnumSet<Villager.Profession> professions = EnumSet.noneOf(Villager.Profession.class);
    private final EnumSet<Villager.Type> types = EnumSet.noneOf(Villager.Type.class);

    private final int requiredLevel;

    public VillagerCondition(ConfigurationSection parent, String key) {
        final ConfigurationSection child = Objects.requireNonNull(parent.getConfigurationSection(key));

        for(String professionString : child.getStringList("required_professions")) {
            final Villager.Profession profession = Enums.getIfPresent(Villager.Profession.class, professionString).orNull();
            Validate.notNull(profession, professionString + " is not a valid villager profession.");
            professions.add(profession);
        }

        for(String typeString : child.getStringList("required_types")) {
            final Villager.Type type = Enums.getIfPresent(Villager.Type.class, typeString).orNull();
            Validate.notNull(type, typeString + " is not a valid villager type.");
            types.add(type);
        }

        this.requiredLevel = child.getInt("required_level", Integer.MIN_VALUE);
    }

    @Override
    public boolean matchCondition(Player player, QuestType type, Event event) {

        final Optional<Entity> optional = Conditions.getEntityFor(type, event);

        if(!optional.isPresent()) {
            return false;
        }

        final Entity entity = optional.get();

        if(!(entity instanceof Villager)) {
            return true;
        }

        final Villager villager = (Villager) entity;

        if(!professions.isEmpty()
                && !professions.contains(villager.getProfession())) {
            return false;
        }

        if(!types.isEmpty()
                && !types.contains(villager.getVillagerType())) {
            return false;
        }

        if(requiredLevel != Integer.MIN_VALUE
                && villager.getVillagerLevel() < requiredLevel) {
            return false;
        }

        return true;
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return Conditions.ENTITY_RELATED_TYPES;
    }
}
