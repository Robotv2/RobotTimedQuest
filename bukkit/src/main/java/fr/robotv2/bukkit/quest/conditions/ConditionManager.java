package fr.robotv2.bukkit.quest.conditions;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.quest.conditions.impl.entity.SheepColorCondition;
import fr.robotv2.bukkit.quest.conditions.impl.itemstack.HasInHandCondition;
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
        registerCondition("is-in-world", IsInWorldCondition.class);
        registerCondition("is-holding", HasInHandCondition.class);

        //ENTITY
        registerCondition("sheep-color", SheepColorCondition.class);
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
