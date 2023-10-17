package fr.robotv2.bukkit.hook.elitemob.conditions;

import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.hook.Hooks;
import fr.robotv2.bukkit.hook.elitemob.EliteMobHook;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.quest.conditions.Conditions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.EnumSet;
import java.util.Optional;

public class EntityFromEliteMobNpc implements Condition {

    private final boolean fromEliteMobNpc;

    public EntityFromEliteMobNpc(ConfigurationSection parent, String key) {
        this.fromEliteMobNpc = parent.getBoolean(key);
    }

    @Override
    public boolean matchCondition(Player player, QuestType type, Event event) {
        final Optional<Entity> optional = Conditions.getEntityFor(type, event);
        return Conditions.simpleConditionChecker(
                optional,
                entity -> Hooks.ELITE_MOB.isInitialized() && EliteMobHook.isEliteMobNpc(entity)
        ) == fromEliteMobNpc;
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return Conditions.ENTITY_RELATED_TYPES;
    }
}
