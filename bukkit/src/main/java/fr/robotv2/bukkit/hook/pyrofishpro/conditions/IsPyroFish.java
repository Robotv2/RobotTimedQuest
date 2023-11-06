package fr.robotv2.bukkit.hook.pyrofishpro.conditions;

import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.hook.pyrofishpro.PyroFishProHook;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.quest.conditions.Conditions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class IsPyroFish implements Condition {

    private final boolean all;
    private final List<String> fishList;

    public IsPyroFish(ConfigurationSection parent, String key) {
        if(parent.isBoolean(key)) {
            this.all = parent.getBoolean(key);
            this.fishList = Collections.emptyList();
        } else if(parent.isList(key)) {
            this.fishList = parent.getStringList(key);
            this.all = fishList.isEmpty();
        } else {
            this.all = false;
            this.fishList = Collections.emptyList();
        }
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

        final String fishId = (wrapper.tier + ":" + wrapper.fishnumber);
        return all || fishList.contains(fishId);
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return EnumSet.of(QuestType.FISH_ITEM);
    }
}
