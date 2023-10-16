package fr.robotv2.bukkit.quest.conditions;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.hook.elitemob.conditions.EntityFromEliteMob;
import fr.robotv2.bukkit.hook.elitemob.conditions.EntityFromEliteMobNpc;
import fr.robotv2.bukkit.hook.itemadder.conditions.BlockIsFromItemAdder;
import fr.robotv2.bukkit.hook.itemadder.conditions.ItemIsFromItemAdder;
import fr.robotv2.bukkit.hook.mythicmob.conditions.EntityFromMythicMob;
import fr.robotv2.bukkit.hook.oraxen.conditions.BlockIsFromOraxen;
import fr.robotv2.bukkit.hook.oraxen.conditions.ItemIsFromOraxen;
import fr.robotv2.bukkit.quest.conditions.impl.entity.IsCustomEntity;
import fr.robotv2.bukkit.quest.conditions.impl.entity.SheepColorCondition;
import fr.robotv2.bukkit.quest.conditions.impl.entity.VillagerCondition;
import fr.robotv2.bukkit.quest.conditions.impl.itemstack.EnchantCondition;
import fr.robotv2.bukkit.quest.conditions.impl.itemstack.IsCustomItem;
import fr.robotv2.bukkit.quest.conditions.impl.itemstack.PotionCondition;
import fr.robotv2.bukkit.quest.conditions.impl.player.HasInHandCondition;
import fr.robotv2.bukkit.quest.conditions.impl.player.IsInWorldCondition;
import fr.robotv2.bukkit.quest.conditions.impl.player.PlaceholdersCondition;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConditionManager {

    private final RTQBukkitPlugin plugin;
    private final Map<String, Class<? extends Condition>> conditions = new HashMap<>();
    private boolean canRegister = true;

    public ConditionManager(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
        registerDefaultConditions();
    }

    public void closeRegistration() {
        this.canRegister = false;
    }

    public void registerCondition(String key, Class<? extends Condition> conditionClazz) {

        if(!canRegister) {
            throw new IllegalStateException("Please register your condition in the JavaPlugin#onLoad method.");
        }

        if(this.checkConditionClass(conditionClazz)) {
            this.conditions.put(key, conditionClazz);
        } else {
            plugin.getLogger().warning("Couldn't register condition: " + key);
            plugin.getLogger().warning("Please, be sure that you're class have the required constructor.");
        }
    }

    private void registerDefaultConditions() {

        // PLAYER
        registerCondition("placeholders", PlaceholdersCondition.class);
        registerCondition("is_in_world", IsInWorldCondition.class);
        registerCondition("is_holding", HasInHandCondition.class);

        //ITEM
        registerCondition("is_custom_item", IsCustomItem.class);
        registerCondition("required_enchants", EnchantCondition.class);
        registerCondition("required_potions", PotionCondition.class);

        //ENTITY
        registerCondition("is_custom_entity", IsCustomEntity.class);
        registerCondition("sheep_color", SheepColorCondition.class);
        registerCondition("required_villager", VillagerCondition.class);

        //EXTERNAL
        registerCondition("is_block_from_itemadder", BlockIsFromItemAdder.class);
        registerCondition("is_block_from_oraxen", BlockIsFromOraxen.class);

        registerCondition("is_item_from_itemadder", ItemIsFromItemAdder.class);
        registerCondition("is_item_from_oraxen", ItemIsFromOraxen.class);

        registerCondition("is_entity_from_mythicmob", EntityFromMythicMob.class);
        registerCondition("is_entity_from_elitemob", EntityFromEliteMob.class);
        registerCondition("is_npc_from_elitemob", EntityFromEliteMobNpc.class);
    }

    private boolean checkConditionClass(Class<? extends Condition> conditionClazz) {
        try {
            conditionClazz.getConstructor(ConfigurationSection.class, String.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public Optional<Condition> toInstance(String key, ConfigurationSection parent) {
        final Class<? extends Condition> conditionClazz = conditions.get(key);

        if(conditionClazz == null) {
            throw new NullPointerException("key is not a valid condition. Maybe it isn't registered ?");
        }

        try {
            final Constructor<? extends Condition> constructor = conditionClazz.getConstructor(ConfigurationSection.class, String.class);
            return Optional.of(constructor.newInstance(parent, key));
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException exception) {
            exception.printStackTrace();
        }

        return Optional.empty();
    }
}
