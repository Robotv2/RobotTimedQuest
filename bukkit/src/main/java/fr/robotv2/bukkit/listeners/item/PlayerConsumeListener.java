package fr.robotv2.bukkit.listeners.item;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestActionData;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerConsumeListener extends QuestProgressionEnhancer<Material> {

    public PlayerConsumeListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {

        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItem();
        final QuestActionData data = QuestActionData.of(event.getPlayer(), itemStack);

        this.incrementProgression(player, QuestType.CONSUME, event.getItem().getType(), data,1);
    }
}
