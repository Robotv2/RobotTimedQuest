package fr.robotv2.bukkit.hook.pyrofishpro.conditions;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.hook.pyrofishpro.PyroFishProHook;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.quest.conditions.Conditions;
import fr.robotv2.bukkit.util.item.ItemUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class IsPyroTier implements Condition {

    private final List<String> tierList;

    public IsPyroTier(ConfigurationSection parent, String key) {
        this.tierList = parent.getStringList(key);
    }

    @Override
    public boolean matchCondition(Player player, QuestType type, Event event) {

        final Optional<ItemStack> optional = Conditions.getItemStackFor(type, event);

        if(!optional.isPresent()) { // no item
            return false;
        }

        final PyroFishProHook.PyroFishWrapper wrapper = PyroFishProHook.toWrapper(optional.get());

        if(wrapper == null) { // not a pyro fish
            return false;
        }

        RTQBukkitPlugin.getInstance().debug("PYRO TIER -> %s", wrapper.tier);

        return tierList.contains(wrapper.tier);
    }

    @Override
    public boolean matchCondition(Player player, QuestType type, Event event, @NotNull String customType) {

        if(type == QuestType.CUSTOM && Objects.equals(customType, "PYRO_FISH") && event instanceof EntityPickupItemEvent) {
            final EntityPickupItemEvent entityPickupItemEvent = (EntityPickupItemEvent) event;
            final PyroFishProHook.PyroFishWrapper wrapper = PyroFishProHook.toWrapper(entityPickupItemEvent.getItem().getItemStack());

            if(wrapper == null) {
                return false;
            }

            return tierList.contains(wrapper.tier);
        }

        return matchCondition(player, type, event);
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return EnumSet.of(QuestType.CUSTOM);
    }
}
