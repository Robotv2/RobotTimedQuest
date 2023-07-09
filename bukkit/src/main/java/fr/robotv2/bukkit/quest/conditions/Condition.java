package fr.robotv2.bukkit.quest.conditions;

import fr.robotv2.bukkit.enums.QuestType;
import org.bukkit.event.Event;
import org.bukkit.entity.Player;

import java.util.EnumSet;

public interface Condition {
    boolean matchCondition(Player player, Event event);
    EnumSet<QuestType> referencedType();
}
