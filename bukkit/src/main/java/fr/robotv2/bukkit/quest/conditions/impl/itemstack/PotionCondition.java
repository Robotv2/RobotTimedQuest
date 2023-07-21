package fr.robotv2.bukkit.quest.conditions.impl.itemstack;

import com.google.common.base.Enums;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.quest.conditions.Condition;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.EnumSet;
import java.util.Objects;

public class PotionCondition implements Condition {

    private final boolean upgraded;
    private final boolean extended;

    private final EnumSet<PotionType> types = EnumSet.noneOf(PotionType.class);

    public PotionCondition(ConfigurationSection parent, String key) {
        this.upgraded = parent.getBoolean(key + ".required_upgraded", false);
        this.extended = parent.getBoolean(key + ".required_extended", false);

        for(String typeString : parent.getStringList(key + ".required_types")) {
            final PotionType type = Enums.getIfPresent(PotionType.class, typeString).orNull();
            if(type == null) {
                throw new IllegalArgumentException(typeString + " is not valid potion effect.");
            }
            types.add(type);
        }
    }

    @Override
    public boolean matchCondition(Player player, QuestType type, Event event) {

        ItemStack stack = null;

        switch (type) {

            case FISH_ITEM: {
                final PlayerFishEvent playerFishEvent = (PlayerFishEvent) event;
                final Item item = (Item) playerFishEvent.getCaught();
                if(item != null) {
                    stack = item.getItemStack();
                }
                break;
            }

            case CONSUME: {
                final PlayerItemConsumeEvent playerItemConsumeEvent = (PlayerItemConsumeEvent) event;
                stack = playerItemConsumeEvent.getItem();
                break;
            }

            case PICKUP: {
                final EntityPickupItemEvent entityPickupItemEvent = (EntityPickupItemEvent) event;
                stack = entityPickupItemEvent.getItem().getItemStack();
                break;
            }

            case BREW: {
                final InventoryClickEvent inventoryClickEvent = (InventoryClickEvent) event;
                stack = inventoryClickEvent.getCurrentItem();
                break;
            }

            default: {
                throw new IllegalArgumentException(type.name() + " is not a valid type for this condition. Please contact the developer.");
            }
        }

        if(stack == null || stack.getType() == Material.AIR) {
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
        return EnumSet.of(
                QuestType.FISH_ITEM,
                QuestType.CONSUME,
                QuestType.PICKUP,
                QuestType.BREW
        );
    }
}
