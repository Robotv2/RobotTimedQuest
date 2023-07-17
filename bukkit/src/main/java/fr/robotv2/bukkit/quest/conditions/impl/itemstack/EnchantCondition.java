package fr.robotv2.bukkit.quest.conditions.impl.itemstack;

import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.quest.conditions.Condition;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.enchantment.EnchantItemEvent;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EnchantCondition implements Condition {

    private final int requiredLevel;
    private final Set<Enchantment> enchants = new HashSet<>();

    public EnchantCondition(ConfigurationSection parent, String key) {
        this.requiredLevel = parent.getInt(key + ".required_level", Integer.MIN_VALUE);
        for(String enchantString : parent.getStringList(key + ".required_enchants")) {
            final NamespacedKey namespacedKey = NamespacedKey.minecraft(enchantString);
            final Enchantment enchantment = Enchantment.getByKey(namespacedKey);
            if(enchantment == null) continue;
            enchants.add(enchantment);
        }
    }

    @Override
    public boolean matchCondition(Player player, Event event) {

        if(event instanceof EnchantItemEvent) {
            final EnchantItemEvent enchantItemEvent = (EnchantItemEvent) event;

            for(Map.Entry<Enchantment, Integer> entry : enchantItemEvent.getEnchantsToAdd().entrySet()) {

                if(!enchants.isEmpty() // an enchant is required
                        && !enchants.contains(entry.getKey())) { // if it does not match this required enchant
                    return false;
                }

                if(requiredLevel != Integer.MIN_VALUE // a level is required
                        && entry.getValue() < requiredLevel) { // if it LESS than this required level
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return EnumSet.of(QuestType.ENCHANT);
    }
}
