package fr.robotv2.bukkit.quest.conditions;

import fr.robotv2.bukkit.enums.QuestType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public interface Condition {

    boolean matchCondition(Player player, QuestType type, Event event);
    EnumSet<QuestType> referencedType();

    default boolean matchCondition(Player player, QuestType type, Event event, @Nullable String customType) {
        return matchCondition(player, type, event);
    }
}
