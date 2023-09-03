package fr.robotv2.bukkit.quest.conditions.impl.entity;

import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.quest.conditions.Conditions;
import fr.robotv2.bukkit.util.comparator.EntitySectionComparator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

public class IsCustomEntity implements Condition {

    private final EntitySectionComparator entitySectionComparator;

    public IsCustomEntity(ConfigurationSection parent, String key) {
        final ConfigurationSection child = Objects.requireNonNull(parent.getConfigurationSection(key));
        this.entitySectionComparator = new EntitySectionComparator(child);
    }

    @Override
    public boolean matchCondition(Player player, QuestType type, Event event) {
        final Optional<Entity> optional = Conditions.getEntityFor(type, event);
        return !optional.isPresent() || entitySectionComparator.isSame(optional.get());
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return Conditions.ENTITY_RELATED_TYPES;
    }
}
