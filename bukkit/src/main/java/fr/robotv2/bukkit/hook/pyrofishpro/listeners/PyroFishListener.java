package fr.robotv2.bukkit.hook.pyrofishpro.listeners;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.events.PyroFishCaughtEvent;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class PyroFishListener extends QuestProgressionEnhancer<Material> {

    public PyroFishListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPyroFishCaught(PyroFishCaughtEvent event) {

        final Player player = event.getPlayer();
        final ItemStack stack = event.getEntity().getItemStack();

        final PlayerFishEvent playerFishEvent = new PlayerFishEvent(event.getPlayer(), event.getEntity(), event.getHook(), PlayerFishEvent.State.CAUGHT_FISH);
        this.incrementProgression(
                player,
                QuestType.FISH_ITEM,
                stack.getType(),
                playerFishEvent,
                stack.getAmount()
        );
    }
}
