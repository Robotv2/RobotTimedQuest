package fr.robotv2.bukkit.hook.mythicmob.conditions;

import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.hook.Hooks;
import fr.robotv2.bukkit.hook.mythicmob.MythicMobHook;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.quest.conditions.Conditions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.EnumSet;
import java.util.Optional;

public class EntityFromMythicMob implements Condition {

    private final boolean fromMythicMob;

    public EntityFromMythicMob(ConfigurationSection parent, String key) {
        this.fromMythicMob = parent.getBoolean(key);
    }

    @Override
    public boolean matchCondition(Player player, QuestType type, Event event) {
        final Optional<Entity> optional = Conditions.getEntityFor(type, event);
        return Conditions.simpleConditionChecker(
                optional,
                entity -> Hooks.MYTHIC_MOB.isInitialized() && MythicMobHook.isMythicMobEntity(entity)
        ) == fromMythicMob;
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return Conditions.ENTITY_RELATED_TYPES;
    }
}
