package fr.robotv2.bukkit.quest.conditions.impl.itemstack;

import com.google.common.base.Enums;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.util.comparator.ItemStackSectionComparator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Objects;

public class HasInHandCondition implements Condition {

    private final ItemStackSectionComparator itemStackSectionComparator;
    private final PlayerHandValue handValue;

    private enum PlayerHandValue {
        MAIN_HAND,
        OFF_HAND,
        BOTH,
        ;
    }

    public HasInHandCondition(ConfigurationSection parent, String key) {

        final ConfigurationSection child = Objects.requireNonNull(parent.getConfigurationSection(key));
        this.itemStackSectionComparator = new ItemStackSectionComparator(child);

        final String handValueString = parent.getString(key + ".hand");
        this.handValue = handValueString != null
                ? Enums.getIfPresent(PlayerHandValue.class, handValueString).or(PlayerHandValue.BOTH)
                : PlayerHandValue.BOTH;
    }

    @Override
    public boolean matchCondition(Player player, Event event) {

        final ItemStack mainHand = player.getInventory().getItemInMainHand();
        final ItemStack offHand = player.getInventory().getItemInOffHand();

        switch (handValue) {
            case MAIN_HAND:
                return itemStackSectionComparator.isSame(mainHand);
            case OFF_HAND:
                return itemStackSectionComparator.isSame(offHand);
            case BOTH:
                return itemStackSectionComparator.isSame(mainHand) || itemStackSectionComparator.isSame(offHand);
            default:
                throw new IllegalArgumentException(handValue + " is not a valid value.");
        }
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return EnumSet.allOf(QuestType.class);
    }
}
