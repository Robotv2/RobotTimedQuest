package fr.robotv2.bukkit.quest.conditions.impl.entity;

import com.google.common.base.Enums;
import fr.robotv2.bukkit.quest.conditions.interfaces.EntityCondition;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;

public class SheepColorCondition implements EntityCondition {

    private DyeColor color = null;

    public SheepColorCondition(ConfigurationSection parent, String key) {
        final String dyeColorString = parent.getString(key);
        if(dyeColorString == null) return;
        this.color = Enums.getIfPresent(DyeColor.class, dyeColorString).orNull();
    }

    @Override
    public boolean matchCondition(Entity value) {
        if(color != null && value.getType() == EntityType.SHEEP) {
            final Sheep sheep = (Sheep) value;
            return sheep.getColor() == color;
        }
        return true;
    }
}
