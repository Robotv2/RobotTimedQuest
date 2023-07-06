package fr.robotv2.bukkit.quest.conditions;

public interface Condition<T> {
    boolean matchCondition(T value);
}
