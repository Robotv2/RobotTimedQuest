package fr.robotv2.bukkit.quest.conditions.impl.itemstack;

import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.quest.conditions.Conditions;
import fr.robotv2.bukkit.util.comparator.ItemStackSectionComparator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

public class IsCustomItem implements Condition {

    private final ItemStackSectionComparator itemStackComparator;

    public IsCustomItem(ConfigurationSection parent, String key) {
        final ConfigurationSection child = Objects.requireNonNull(parent.getConfigurationSection(key));
        this.itemStackComparator = new ItemStackSectionComparator(child);
    }

    @Override
    public boolean matchCondition(Player player, QuestType type, Event event) {
        final Optional<ItemStack> optional = Conditions.getItemStackFor(type, event);
        return !optional.isPresent() || itemStackComparator.isSame(optional.get());
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return Conditions.ITEMSTACK_RELATED_TYPES;
    }
}
