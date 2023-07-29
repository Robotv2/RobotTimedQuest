package fr.robotv2.bukkit.quest.conditions.impl.itemstack;

import com.google.common.base.Enums;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.quest.conditions.Conditions;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

public class PotionCondition implements Condition {

    private final boolean upgraded;
    private final boolean extended;

    private final EnumSet<PotionType> types = EnumSet.noneOf(PotionType.class);

    public PotionCondition(ConfigurationSection parent, String key) {
        this.upgraded = parent.getBoolean(key + ".required_upgraded", false);
        this.extended = parent.getBoolean(key + ".required_extended", false);

        for(String typeString : parent.getStringList(key + ".required_types")) {
            final PotionType type = Enums.getIfPresent(PotionType.class, typeString).orNull();
            Validate.notNull(type, typeString + " is not valid potion effect.");
            types.add(type);
        }
    }

    @Override
    public boolean matchCondition(Player player, QuestType type, Event event) {

        final Optional<ItemStack> optional = Conditions.getItemStackFor(type, event);

        if(!optional.isPresent()) {
            return false;
        }

        final ItemStack stack = optional.get();

        if(stack.getType() == Material.AIR) {
            return false;
        }

        if(!stack.getType().name().contains("POTION")) {
            return true;
        }

        final PotionMeta potionMeta = Objects.requireNonNull((PotionMeta) stack.getItemMeta());
        final PotionData potionData = potionMeta.getBasePotionData();

        if(!types.isEmpty() && !types.contains(potionData.getType())) {
            return false;
        }

        if(upgraded && !potionData.isUpgraded()) {
            return false;
        }

        if(extended && !potionData.isExtended()) {
            return false;
        }

        return true;
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return Conditions.ITEMSTACK_RELATED_TYPES;
    }
}
