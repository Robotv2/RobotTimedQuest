package fr.robotv2.bukkit.quest.conditions.impl.itemstack;

import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.quest.conditions.Conditions;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EnchantCondition implements Condition {

    private final int requiredLevel;
    private final Set<Enchantment> enchants = new HashSet<>();

    public EnchantCondition(ConfigurationSection parent, String key) {
        this.requiredLevel = parent.getInt(key + ".required_level", Integer.MIN_VALUE);
        for(String enchantString : parent.getStringList(key + ".required_types")) {
            final Enchantment enchantment = Enchantment.getByName(enchantString);
            Validate.notNull(enchantment, enchantString + " is not a valid enchantment key.");
            enchants.add(enchantment);
        }
    }

    @Override
    public boolean matchCondition(Player player, QuestType type, Event event) {
        if (type == QuestType.ENCHANT && event instanceof EnchantItemEvent) {
            final EnchantItemEvent enchantItemEvent = (EnchantItemEvent) event;
            return checkEnchantment(enchantItemEvent.getEnchantsToAdd().entrySet());
        } else {
            Optional<ItemStack> optional = Conditions.getItemStackFor(type, event);
            if (optional.isPresent()) {
                ItemStack stack = optional.get();
                return checkEnchantment(stack.getEnchantments().entrySet());
            } else {
                return true;
            }
        }
    }

    private boolean checkEnchantment(Set<Map.Entry<Enchantment, Integer>> entries) {
        for (Map.Entry<Enchantment, Integer> entry : entries) {
            if (isEnchantPresent(entry.getKey()) && isLevelSufficient(entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    private boolean isEnchantPresent(Enchantment enchantment) {
        return enchants.isEmpty() // if true, no enchant is required.
                || enchants.contains(enchantment);
    }

    private boolean isLevelSufficient(int level) {
        return requiredLevel == Integer.MIN_VALUE // if true, no level is required.
                || level >= requiredLevel;
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return EnumSet.of(QuestType.ENCHANT);
    }
}
