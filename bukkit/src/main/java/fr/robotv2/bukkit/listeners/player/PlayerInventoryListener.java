package fr.robotv2.bukkit.listeners.player;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.events.quest.QuestInventoryClickEvent;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.common.data.impl.ActiveQuest;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerInventoryListener extends QuestProgressionEnhancer<Material> {

    public PlayerInventoryListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onQuestInventoryClick(QuestInventoryClickEvent event) {

        final Player player = event.getPlayerWhoClicked();
        final ActiveQuest activeQuest = event.getActiveQuest();
        final Quest quest = event.getQuest();

        if(quest == null || activeQuest.isDone() || quest.getType() != QuestType.GATHER_ITEM) {
            return;
        }

        final ItemStack cursor = player.getItemOnCursor();

        if(cursor.getType() == Material.AIR) {
            return;
        }

        final int requiredAmount = quest.getRequiredAmount() - activeQuest.getProgress();
        final int itemToTake = Math.min(cursor.getAmount(), requiredAmount);

        final boolean result = this.incrementProgressionFor(player, activeQuest, cursor.getType(), event, itemToTake);

        if(result) {

            final Inventory inventory = event.getInventoryClickEvent().getClickedInventory();

            if(inventory != null) {
                inventory.setItem(
                        event.getInventoryClickEvent().getSlot(),
                        quest.getGuiItem(activeQuest, player)
                );
            }

            if(requiredAmount >= cursor.getAmount()) {
                player.setItemOnCursor(new ItemStack(Material.AIR));
            } else {
                final ItemStack replacement = cursor.clone();
                replacement.setAmount(cursor.getAmount() - requiredAmount);
                player.setItemOnCursor(replacement);
            }
        }
    }
}
