package fr.robotv2.bukkit.listeners.item;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;

public class PlayerFishItemListener extends QuestProgressionEnhancer<Material> {

    public PlayerFishItemListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFishItem(PlayerFishEvent event) {

        if(event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        final Player player = event.getPlayer();
        final Entity entity = event.getCaught();

        if(entity == null) {
            return;
        }

        if(entity instanceof Item) {
            this.incrementProgression(player, QuestType.FISH_ITEM, ((Item) entity).getItemStack().getType(), event, 1);
        }
    }
}
