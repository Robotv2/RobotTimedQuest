package fr.robotv2.bukkit.hook.pyrofishpro.listeners;

import fr.robotv2.bukkit.events.PyroFishCaughtEvent;
import fr.robotv2.bukkit.hook.pyrofishpro.PyroFishProHook;
import fr.robotv2.bukkit.quest.custom.CustomQuestProgressionEnhancer;
import me.arsmagica.API.PyroFishCatchEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Collection;
import java.util.List;

public class PyroFishProEventCaller extends CustomQuestProgressionEnhancer<String> {

    // @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPyroFishSpawnEvent(ItemSpawnEvent event) {

        final Item item = event.getEntity();
        final ItemStack stack = item.getItemStack();

        if(this.getGlitchChecker().isMarked(item)) {
            return;
        }

        if(!PyroFishProHook.isPyroFish(stack)) {
            return;
        }

        final PyroFishProHook.PyroFishWrapper pyroFish = PyroFishProHook.toWrapper(stack);
        final Location playerLocation = item.getLocation().add(0.0d, -1.0d, 0.0d); // due to the way PyroFishingPro spawns items

        if(playerLocation.getWorld() == null || pyroFish == null) {
            return;
        }

        final Collection<Entity> entities = playerLocation.getWorld().getNearbyEntities(playerLocation, 0.001d, 0.001d, 0.001d);

        for(Entity entity : entities) {
            if(entity instanceof Player) {

                final Player player = (Player) entity;
                final FishHook hook = PyroFishProHook.getRecentHook((player.getUniqueId()));

                if(hook != null) {
                    Bukkit.getServer().getPluginManager().callEvent(new PyroFishCaughtEvent(player, item, pyroFish, hook));
                }
            }
        }
    }
}
