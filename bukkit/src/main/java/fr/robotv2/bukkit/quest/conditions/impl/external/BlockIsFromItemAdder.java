package fr.robotv2.bukkit.quest.conditions.impl.external;

import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.hook.Hooks;
import fr.robotv2.bukkit.hook.ItemAdderHook;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.quest.conditions.Conditions;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.EnumSet;
import java.util.Optional;

public class BlockIsFromItemAdder implements Condition {

    private final boolean fromItemAdder;

    public BlockIsFromItemAdder(ConfigurationSection parent, String key) {
        this.fromItemAdder = parent.getBoolean(key);
    }

    @Override
    public boolean matchCondition(Player player, QuestType type, Event event) {
        final Optional<Block> optional = Conditions.getBlockFor(type, event);
        return Conditions.simpleConditionChecker(
              optional,
                block -> Hooks.ITEM_ADDER.isInitialized() && ItemAdderHook.isCustomBlock(block)
        ) == fromItemAdder;
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return Conditions.BLOCK_RELATED_TYPES;
    }
}
