package fr.robotv2.bukkit.hook.pyrofishpro.listeners;

import fr.robotv2.bukkit.hook.pyrofishpro.PyroFishProHook;
import fr.robotv2.bukkit.quest.custom.CustomQuestProgressionEnhancer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class PyroFishProListener extends CustomQuestProgressionEnhancer<String> {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPyroFishPickup(EntityPickupItemEvent event) {

        if(!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();
        final Item item = event.getItem();

        if(this.getGlitchChecker().isMarked(item)) {
            return;
        }

        final ItemStack fish = item.getItemStack();

        if(!PyroFishProHook.isPyroFish(fish)) {
            return;
        }

        final PyroFishProHook.PyroFishWrapper pyroFish = PyroFishProHook.toWrapper(fish);
        final boolean hasFishedRecently = PyroFishProHook.hasFishedRecently(player.getUniqueId());

        if(pyroFish == null || !hasFishedRecently) {
            return;
        }

        this.trigger(player, "PYRO_FISH", (pyroFish.tier + ":" + pyroFish.fishnumber), event, 1);
    }
}
