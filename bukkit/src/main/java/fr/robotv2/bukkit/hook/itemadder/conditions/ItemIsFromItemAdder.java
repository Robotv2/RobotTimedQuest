package fr.robotv2.bukkit.hook.itemadder.conditions;

import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.hook.Hooks;
import fr.robotv2.bukkit.hook.itemadder.ItemAdderHook;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.quest.conditions.Conditions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Optional;

public class ItemIsFromItemAdder implements Condition {

    private final boolean fromItemAdder;

    public ItemIsFromItemAdder(ConfigurationSection parent, String key) {
        this.fromItemAdder = parent.getBoolean(key);
    }

    @Override
    public boolean matchCondition(Player player, QuestType type, Event event) {
        final Optional<ItemStack> optional = Conditions.getItemStackFor(type, event);
        return Conditions.simpleConditionChecker(
                optional,
                itemStack -> Hooks.ITEM_ADDER.isInitialized() && ItemAdderHook.isCustomItem(itemStack)
        ) == fromItemAdder;
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return Conditions.ITEMSTACK_RELATED_TYPES;
    }
}
