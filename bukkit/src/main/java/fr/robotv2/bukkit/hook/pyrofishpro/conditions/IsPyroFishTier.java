package fr.robotv2.bukkit.hook.pyrofishpro.conditions;

import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.hook.pyrofishpro.PyroFishProHook;
import fr.robotv2.bukkit.quest.conditions.Condition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class IsPyroFishTier implements Condition {

    private final List<String> tierList;

    public IsPyroFishTier(ConfigurationSection parent, String key) {
        this.tierList = parent.getStringList(key);
    }

    @Override
    public boolean matchCondition(Player player, QuestType type, Event event) {
        throw new IllegalStateException("The condition 'is_pyro_tier' MUST be used with custom type 'PYRO_FISH' exclusively.");
    }

    @Override
    public boolean matchCondition(Player player, QuestType type, Event event, @Nullable String customType) {

        if(Objects.equals(customType, "PYRO_FISH") && event instanceof EntityPickupItemEvent) {
            final EntityPickupItemEvent entityPickupItemEvent = (EntityPickupItemEvent) event;
            final PyroFishProHook.PyroFishWrapper wrapper = PyroFishProHook.toWrapper(entityPickupItemEvent.getItem().getItemStack());

            if(wrapper == null) {
                return false;
            }

            return tierList.contains(customType);
        }

        return matchCondition(player, type, event);
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return EnumSet.of(QuestType.CUSTOM);
    }
}
