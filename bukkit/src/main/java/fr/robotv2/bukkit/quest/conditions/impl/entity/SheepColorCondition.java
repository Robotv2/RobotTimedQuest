package fr.robotv2.bukkit.quest.conditions.impl.entity;

import com.google.common.base.Enums;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.quest.conditions.Conditions;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.Event;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

public class SheepColorCondition implements Condition {

    private final DyeColor color;

    public SheepColorCondition(ConfigurationSection parent, String key) {
        final String dyeColorString = Objects.requireNonNull(parent.getString(key));
        this.color = Enums.getIfPresent(DyeColor.class, dyeColorString).orNull();
    }

    @Override
    public boolean matchCondition(Player player, QuestType type, Event event) {

        final Optional<Entity> optional = Conditions.getEntityFor(type, event);

        if(optional.isPresent() && this.isSheep(optional.get())) {
            return this.hasColor((Sheep) optional.get());
        }

        return true;
    }

    private boolean isSheep(Entity entity) {
        return entity instanceof Sheep;
    }

    private boolean hasColor(Sheep sheep) {
        return Objects.equals(sheep.getColor(), color);
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return Conditions.ENTITY_RELATED_TYPES;
    }
}
