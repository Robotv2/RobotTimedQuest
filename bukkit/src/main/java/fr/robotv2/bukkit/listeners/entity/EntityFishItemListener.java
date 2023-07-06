package fr.robotv2.bukkit.listeners.entity;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestActionData;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;

public class EntityFishItemListener extends QuestProgressionEnhancer<Material> {

    public EntityFishItemListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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
            final QuestActionData data = QuestActionData.of(player, entity);
            this.incrementProgression(player, QuestType.FISH, ((Item) entity).getItemStack().getType(), data, 1);
        }
    }
}
