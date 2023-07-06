package fr.robotv2.bukkit.quest.conditions.impl.player;

import fr.robotv2.bukkit.quest.conditions.interfaces.PlayerCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class IsInWorldCondition implements PlayerCondition {

    private final List<String> worlds;

    public IsInWorldCondition(ConfigurationSection parent, String key) {
        this.worlds = parent.getStringList(key);
    }

    @Override
    public boolean matchCondition(Player value) {
        return worlds.contains(value.getWorld().getName());
    }
}
