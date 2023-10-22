package fr.robotv2.bukkit.hook;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.quest.conditions.Condition;
import org.bukkit.plugin.java.JavaPlugin;

public interface Hook {

    boolean initialize(JavaPlugin plugin);
    void loadConditions();

    default void registerCondition(String key, Class<? extends Condition> clazz) {
        RTQBukkitPlugin.getInstance().getConditionManager().registerCondition(key, clazz);
    }
}
