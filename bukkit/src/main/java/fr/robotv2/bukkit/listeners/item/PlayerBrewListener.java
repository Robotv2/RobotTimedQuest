package fr.robotv2.bukkit.listeners.item;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import fr.robotv2.bukkit.util.BrewUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerBrewListener extends QuestProgressionEnhancer<Material> {


    public PlayerBrewListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {

        if(!(event.getClickedInventory() instanceof BrewerInventory)) {
            return;
        }

        final BrewerInventory brewerInventory = (BrewerInventory) event.getClickedInventory();
        final Player player = (Player) event.getWhoClicked();

        final BrewingStand stand = brewerInventory.getHolder();

        if(stand == null || event.getSlot() != 3) {
            return;
        }

        switch (event.getAction()) {
            case PICKUP_ALL:
            case PICKUP_HALF:
            case PICKUP_ONE:
            case PICKUP_SOME:
            case DROP_ALL_SLOT:
            case DROP_ONE_SLOT:
            case HOTBAR_SWAP:
            case COLLECT_TO_CURSOR:
                this.getGlitchChecker().unMark(stand);
                break;
            case PLACE_ALL:
            case PLACE_SOME:
            case PLACE_ONE:
            case SWAP_WITH_CURSOR:
                this.getGlitchChecker().unMark(stand);
                this.getGlitchChecker().mark(stand, player);
                break;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBrew(BrewEvent event) {

        final BrewerInventory brewerInventory = event.getContents();
        final BrewingStand stand = brewerInventory.getHolder();

        Player player = null;

        if(stand == null && !brewerInventory.getViewers().isEmpty()) {
            player = (Player) brewerInventory.getViewers().get(0);
        }

        if(player == null && stand != null) {
            final UUID uuid = getGlitchChecker().getActivator(stand);
            if(uuid != null) {
                player = Bukkit.getPlayer(uuid);
            }
        }

        if(player == null || !player.isOnline()) {
            return;
        }

        final ItemStack stack = BrewUtil.getFirstNonNull(event.getContents());

        if(stack == null) {
            return;
        }

        this.incrementProgression(
                player,
                QuestType.BREW,
                stack.getType(),
                event,
                stack.getAmount()
        );
    }
}
