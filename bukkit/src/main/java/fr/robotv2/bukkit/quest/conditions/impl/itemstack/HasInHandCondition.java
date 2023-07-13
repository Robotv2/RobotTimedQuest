package fr.robotv2.bukkit.quest.conditions.impl.itemstack;

import com.google.common.base.Enums;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.quest.conditions.Condition;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.EnumSet;
import java.util.Objects;

public class HasInHandCondition implements Condition {

    private final String name;
    private final int customModelData;
    private final EnumSet<Material> materials = EnumSet.noneOf(Material.class);
    private final PlayerHandValue handValue;

    private enum PlayerHandValue {
        MAIN_HAND,
        OFF_HAND,
        BOTH,
        ;
    }

    public HasInHandCondition(ConfigurationSection parent, String key) {
        this.name = parent.getString(key + ".name");
        this.customModelData = parent.getInt(key + ".custom-model-data", Integer.MIN_VALUE);

        for(String materialString : parent.getStringList(key + ".materials")) {
            final Material material = Material.matchMaterial(materialString);
            if(material == null) continue;
            materials.add(material);
        }

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
                return isItem(mainHand);
            case OFF_HAND:
                return isItem(offHand);
            case BOTH:
                return isItem(mainHand) || isItem(offHand);
            default:
                throw new IllegalArgumentException(handValue + " is not a valid value");
        }
    }

    private boolean isItem(ItemStack stack) {
        if(stack.getType() != Material.AIR && stack.hasItemMeta()) {

            final ItemMeta meta = Objects.requireNonNull(stack.getItemMeta());

            if(name != null) {
                if(!meta.hasDisplayName() || !meta.getDisplayName().equals(name)) {
                    return false;
                }
            }

            if(customModelData != Integer.MIN_VALUE) {
                if(!meta.hasCustomModelData() || meta.getCustomModelData() != customModelData) {
                    return false;
                }
            }

            if(!materials.isEmpty()) {
                return materials.contains(stack.getType());
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return EnumSet.allOf(QuestType.class);
    }
}
