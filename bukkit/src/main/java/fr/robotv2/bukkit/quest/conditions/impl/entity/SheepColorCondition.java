package fr.robotv2.bukkit.quest.conditions.impl.entity;

import com.google.common.base.Enums;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.quest.conditions.Condition;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

import java.util.EnumSet;
import java.util.Objects;

public class SheepColorCondition implements Condition {

    private DyeColor color;

    public SheepColorCondition(ConfigurationSection parent, String key) {
        final String dyeColorString = parent.getString(key);
        if(dyeColorString == null) return;
        this.color = Enums.getIfPresent(DyeColor.class, dyeColorString).orNull();
    }

    @Override
    public boolean matchCondition(Player player, Event event) {

        Entity entity = null;

        if(event instanceof EntityEvent) {
            final EntityEvent entityEvent = (EntityEvent) event;
            entity = entityEvent.getEntity();
        } else if(event instanceof PlayerShearEntityEvent) {
            final PlayerShearEntityEvent entityEvent = (PlayerShearEntityEvent) event;
            entity = entityEvent.getEntity();
        }

        if(this.isSheep(entity)) {
            return this.hasColor((Sheep) entity);
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
        return EnumSet.of(QuestType.KILL, QuestType.SHEAR);
    }
}
