package fr.robotv2.bukkit.hook.pyrofishpro.listeners;

import fr.robotv2.bukkit.events.PyroFishCaughtEvent;
import fr.robotv2.bukkit.hook.pyrofishpro.PyroFishProHook;
import fr.robotv2.bukkit.quest.custom.CustomQuestProgressionEnhancer;
import org.bukkit.Bukkit;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class PyroFishProEventCaller extends CustomQuestProgressionEnhancer<String> {

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
        final FishHook hook = PyroFishProHook.getRecentHook(player.getUniqueId());

        if(pyroFish == null || hook == null) {
            return;
        }

        Bukkit.getServer().getPluginManager().callEvent(new PyroFishCaughtEvent(player, item, pyroFish, hook));
    }
}
