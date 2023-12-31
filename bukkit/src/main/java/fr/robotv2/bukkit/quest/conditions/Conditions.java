package fr.robotv2.bukkit.quest.conditions;

import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.events.MultipleCropsBreakEvent;
import fr.robotv2.bukkit.events.VillagerTradeEvent;
import fr.robotv2.bukkit.events.quest.QuestInventoryClickEvent;
import fr.robotv2.bukkit.util.item.BrewUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class Conditions {

    public static final EnumSet<QuestType> BLOCK_RELATED_TYPES = EnumSet.of(
            QuestType.BREAK, // BlockBreakEvent
            QuestType.PLACE, // BlockPlaceEvent, BlockBreakEvent
            QuestType.FARMING, // MultipleCropsBreakEvent, PlayerHarvestBlockEvent, BlockBreakEvent
            QuestType.ENCHANT, // EnchantItemEvent
            QuestType.BREW, // BrewEvent
            QuestType.COOK, // FurnaceExtractEvent
            QuestType.CARVE, // PlayerInteractEvent
            QuestType.CUSTOM
    );

    public static final EnumSet<QuestType> ENTITY_RELATED_TYPES = EnumSet.of(
            QuestType.FISH, // PlayerFishEvent
            QuestType.BREED, // EntityBreedEvent
            QuestType.KILL, // EntityDeathEvent
            QuestType.SHEAR, // PlayerShearEntityEvent
            QuestType.TAME, // EntityTameEvent
            QuestType.VILLAGER_TRADE, // VillagerTradeEvent
            QuestType.MILKING
    );

    public static final EnumSet<QuestType> ITEMSTACK_RELATED_TYPES = EnumSet.of(
            QuestType.FISH_ITEM, // PlayerFishEvent
            QuestType.CONSUME, // PlayerItemConsumeEvent
            QuestType.CRAFT, // CraftItemEvent
            QuestType.ENCHANT, // EnchantItemEvent
            QuestType.PICKUP, // EntityPickupItemEvent
            QuestType.BREW, // BrewEvent
            QuestType.VILLAGER_TRADE, // VillagerTradeEvent
            QuestType.GATHER_ITEM, // QuestInventoryClickEvent
            QuestType.CUSTOM
    );

    private static <T, U> U processEventFunction(Class<? extends T> eventClass, Event event, Function<T, U> function) {
        return function.apply((T) event);
    }

    public static Optional<Entity> getEntityFor(QuestType type, Event event) {

        if(event == null) {
            return Optional.empty();
        }

        Entity entity = null;

        switch (type) {
            case FISH:
                entity = Conditions.processEventFunction(PlayerFishEvent.class, event, (PlayerFishEvent::getCaught));
                break;
            case BREED:
                entity = Conditions.processEventFunction(EntityBreedEvent.class, event, (EntityBreedEvent::getEntity));
                break;
            case KILL:
                entity = Conditions.processEventFunction(EntityDeathEvent.class, event, (EntityDeathEvent::getEntity));
                break;
            case SHEAR:
                entity = Conditions.processEventFunction(PlayerShearEntityEvent.class, event, (PlayerShearEntityEvent::getEntity));
                break;
            case VILLAGER_TRADE:
                entity = Conditions.processEventFunction(VillagerTradeEvent.class, event, (VillagerTradeEvent::getVillager));
                break;
            case MILKING:
                entity = Conditions.processEventFunction(PlayerInteractEntityEvent.class, event, (PlayerInteractEntityEvent::getRightClicked));
                break;
            default:
                break;
        }

        if(entity == null && event instanceof EntityEvent) {
            entity = ((EntityEvent) event).getEntity();
        }

        return Optional.ofNullable(entity);
    }

    public static Optional<ItemStack> getItemStackFor(QuestType type, Event event) {

        if(event == null) {
            return Optional.empty();
        }

        ItemStack itemStack = null;

        switch (type) {
            case FISH_ITEM:
                itemStack = Conditions.processEventFunction(PlayerFishEvent.class, event, playerFishEvent -> {
                    final Item item = (Item) playerFishEvent.getCaught();
                    return item != null ? item.getItemStack() : null;
                });
                break;
            case CONSUME:
                itemStack = Conditions.processEventFunction(PlayerItemConsumeEvent.class, event, (PlayerItemConsumeEvent::getItem));
                break;
            case CRAFT:
                itemStack = Conditions.processEventFunction(CraftItemEvent.class, event, craftItemEvent -> craftItemEvent.getRecipe().getResult());
                break;
            case ENCHANT:
                itemStack = Conditions.processEventFunction(EnchantItemEvent.class, event, (EnchantItemEvent::getItem));
                break;
            case PICKUP:
                itemStack = Conditions.processEventFunction(EntityPickupItemEvent.class, event, (entityPickupItemEvent -> entityPickupItemEvent.getItem().getItemStack()));
                break;
            case BREW:
                itemStack = Conditions.processEventFunction(BrewEvent.class, event, brewEvent -> BrewUtil.getFirstNonNull(brewEvent.getContents()));
                break;
            case VILLAGER_TRADE:
                itemStack = Conditions.processEventFunction(VillagerTradeEvent.class, event, (VillagerTradeEvent::getResult));
                break;
            case GATHER_ITEM:
                itemStack = Conditions.processEventFunction(QuestInventoryClickEvent.class, event, questInventoryClickEvent -> questInventoryClickEvent.getInventoryClickEvent().getCursor());
                break;
            default:
                break;
        }

        return Optional.ofNullable(itemStack);
    }

    public static Optional<Block> getBlockFor(QuestType type, Event event) {

        if(event == null) {
            return Optional.empty();
        }

        Block block = null;

        switch (type) {
            case COOK:
            case BREAK:
            case PLACE:
            case BREW:
                // BlockEvent
                break;
            case FARMING: {
                if(event instanceof BlockEvent) break;
                else if(event instanceof PlayerHarvestBlockEvent) block = Conditions.processEventFunction(PlayerHarvestBlockEvent.class, event, PlayerHarvestBlockEvent::getHarvestedBlock);
                else if(event instanceof MultipleCropsBreakEvent) block = Conditions.processEventFunction(MultipleCropsBreakEvent.class, event, multipleCropsBreakEvent -> multipleCropsBreakEvent.getBlocks().get(0));
                break;
            }
            case ENCHANT:
                block = Conditions.processEventFunction(EnchantItemEvent.class, event, EnchantItemEvent::getEnchantBlock);
                break;
            case CARVE:
                block = Conditions.processEventFunction(PlayerInteractEvent.class, event, (PlayerInteractEvent::getClickedBlock));
                break;
            default:
                break;
        }

        if(block == null && event instanceof BlockEvent) {
            block = ((BlockEvent) event).getBlock();
        }

        return Optional.ofNullable(block);
    }

    public static <T> boolean simpleConditionChecker(Optional<T> optional, Predicate<T> predicate) {
        return !optional.isPresent() || predicate.test(optional.get());
    }
}
