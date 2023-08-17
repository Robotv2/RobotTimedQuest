package fr.robotv2.bukkit.quest.conditions.impl.external;

import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.hook.Hooks;
import fr.robotv2.bukkit.hook.OraxenHook;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.quest.conditions.Conditions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Optional;

public class ItemIsFromOraxen implements Condition {

    private final boolean fromOraxen;

    public ItemIsFromOraxen(ConfigurationSection parent, String key) {
        this.fromOraxen = parent.getBoolean(key);
    }

    @Override
    public boolean matchCondition(Player player, QuestType type, Event event) {
        final Optional<ItemStack> optional = Conditions.getItemStackFor(type, event);
        return Conditions.simpleConditionChecker(
                optional,
                itemstack -> Hooks.ORAXEN.isInitialized() && OraxenHook.isCustomItem(itemstack)
        ) == fromOraxen;
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return Conditions.ITEMSTACK_RELATED_TYPES;
    }
}
