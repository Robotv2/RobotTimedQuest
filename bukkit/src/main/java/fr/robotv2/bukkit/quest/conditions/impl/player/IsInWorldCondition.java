package fr.robotv2.bukkit.quest.conditions.impl.player;

import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.quest.conditions.Condition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.EnumSet;
import java.util.List;

public class IsInWorldCondition implements Condition {

    private final List<String> worlds;

    public IsInWorldCondition(ConfigurationSection parent, String key) {
        this.worlds = parent.getStringList(key);
    }

    @Override
    public boolean matchCondition(Player player, QuestType type, Event event) {
        return worlds.contains(player.getWorld().getName());
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return EnumSet.allOf(QuestType.class);
    }
}
