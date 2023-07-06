package fr.robotv2.bukkit.listeners;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.events.QuestDoneEvent;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.quest.conditions.interfaces.BlockCondition;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.quest.conditions.interfaces.EntityCondition;
import fr.robotv2.bukkit.quest.conditions.interfaces.ItemStackCondition;
import fr.robotv2.bukkit.quest.conditions.interfaces.PlayerCondition;
import fr.robotv2.common.data.impl.ActiveQuest;
import fr.robotv2.common.data.impl.QuestPlayer;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public abstract class QuestProgressionEnhancer<T> implements Listener {

    private final RTQBukkitPlugin plugin;

    protected QuestProgressionEnhancer(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    public GlitchChecker getGlitchChecker() {
        return this.plugin.getGlitchChecker();
    }

    public boolean allConditionsMatch(List<Condition<?>> conditions, QuestActionData data) {

        final Player player = data.getPlayer(); // can't be null
        final Block block = data.getBlock(); // can be null
        final Entity entity = data.getEntity(); // can be null
        final ItemStack itemStack = data.getItemStack(); // can be null

        for(Condition<?> condition : conditions) {
            if(condition instanceof PlayerCondition) {
                final PlayerCondition playerCondition = (PlayerCondition) condition;
                if(!playerCondition.matchCondition(player)) {
                    return false;
                }
            } else if(condition instanceof BlockCondition && block != null) {
                final BlockCondition blockCondition = (BlockCondition) condition;
                if(!blockCondition.matchCondition(block)) {
                    return false;
                }
            } else if(condition instanceof EntityCondition && entity != null) {
                final EntityCondition entityCondition = (EntityCondition) condition;
                if(!entityCondition.matchCondition(entity)) {
                    return false;
                }
            } else if(condition instanceof ItemStackCondition && itemStack != null) {
                final ItemStackCondition itemStackCondition = (ItemStackCondition) condition;
                if(!itemStackCondition.matchCondition(itemStack)) {
                    return false;
                }
            }
        }

        return true;
    }

    public void incrementProgression(Player player, QuestType type, T target, QuestActionData data, int amount) {
        incrementProgression(Objects.requireNonNull(QuestPlayer.getQuestPlayer(player.getUniqueId())), type, target, data, amount);
    }

    public void incrementProgression(QuestPlayer questPlayer, QuestType type, T target, QuestActionData data, int amount) {

        if(amount <= 0) {
            return;
        }

        for (ActiveQuest activeQuest : questPlayer.getActiveQuests()) {

            if(activeQuest.isDone() || activeQuest.hasEnded()) {
                continue;
            }

            final Quest quest = plugin.getQuestManager().fromId(activeQuest.getQuestId());

            if (quest == null // Quest doesn't exist ?? (shouldn't happen)
                    || quest.getType() != type // Quest type doesn't match. (another action)
                    || !quest.isTarget(target)) // The target isn't targeted by this quest.
            {
                continue;
            }

            if(!quest.getConditions().isEmpty() // Check if conditions are empty.
                    && !this.allConditionsMatch(quest.getConditions(), data)) // Does all the conditions are met ?
            {
                continue;
            }

            activeQuest.incrementProgress(amount);

            if(activeQuest.getProgress() >= quest.getRequiredAmount()) {
                activeQuest.setDone(true);
                Bukkit.getPluginManager().callEvent(new QuestDoneEvent(activeQuest));
            }
        }
    }
}
