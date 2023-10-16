package fr.robotv2.bukkit.hook.oraxen.conditions;

import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.hook.Hooks;
import fr.robotv2.bukkit.hook.oraxen.OraxenHook;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.quest.conditions.Conditions;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.EnumSet;
import java.util.Optional;

public class BlockIsFromOraxen implements Condition {

    private final boolean fromOraxen;

    public BlockIsFromOraxen(ConfigurationSection parent, String key) {
        this.fromOraxen = parent.getBoolean(key);
    }

    @Override
    public boolean matchCondition(Player player, QuestType type, Event event) {
        final Optional<Block> optional = Conditions.getBlockFor(type, event);
        return Conditions.simpleConditionChecker(
                optional,
                block -> Hooks.ORAXEN.isInitialized() && OraxenHook.isCustomBlock(block)
        ) == fromOraxen;
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return Conditions.BLOCK_RELATED_TYPES;
    }
}
