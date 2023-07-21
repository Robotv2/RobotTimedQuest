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
        for(String enchantString : parent.getStringList(key + ".required_types")) {
            final NamespacedKey namespacedKey = NamespacedKey.minecraft(enchantString);
            final Enchantment enchantment = Enchantment.getByKey(namespacedKey);
            if(enchantment == null) continue;
            enchants.add(enchantment);
        }
    }

    @Override
    public boolean matchCondition(Player player, QuestType type, Event event) {

        if(type != QuestType.ENCHANT) {
            return true;
        }

        final EnchantItemEvent enchantItemEvent = (EnchantItemEvent) event;
        final Set<Map.Entry<Enchantment, Integer>> entries = enchantItemEvent.getEnchantsToAdd().entrySet();

        for(Map.Entry<Enchantment, Integer> entry : entries) {

            if(this.isEnchantPresent(entry.getKey())
                    && this.isLevelSufficient(entry.getValue())) {
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
