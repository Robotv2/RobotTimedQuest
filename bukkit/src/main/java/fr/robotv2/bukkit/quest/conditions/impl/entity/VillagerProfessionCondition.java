package fr.robotv2.bukkit.quest.conditions.impl.entity;

import com.google.common.base.Enums;
import fr.robotv2.bukkit.quest.conditions.interfaces.EntityCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;

import java.util.Objects;

public class VillagerProfessionCondition implements EntityCondition {

    private Villager.Profession profession = null;

    public VillagerProfessionCondition(ConfigurationSection parent, String key) {
        final String professionString = parent.getString(key);
        if(professionString == null) return;
        this.profession = Enums.getIfPresent(Villager.Profession.class, professionString).orNull();
    }

    @Override
    public boolean matchCondition(Entity value) {

        if(value instanceof Villager) {
            final Villager villager = (Villager) value;
            return Objects.equals(profession, villager.getProfession());
        }

        return true;
    }
}
