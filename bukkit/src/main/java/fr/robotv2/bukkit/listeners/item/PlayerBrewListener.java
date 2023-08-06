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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
            case MOVE_TO_OTHER_INVENTORY:
            case PICKUP_ALL:
            case DROP_ALL_SLOT:
            case HOTBAR_SWAP:
            case COLLECT_TO_CURSOR:
                this.getGlitchChecker().unMark(stand);
                this.getPlugin().debug("BREW - UNMARK BREWER");
                break;
            case PLACE_ALL:
            case PLACE_SOME:
            case PLACE_ONE:
            case SWAP_WITH_CURSOR:
                this.getGlitchChecker().unMark(stand);
                this.getGlitchChecker().mark(stand, player);
                this.getPlugin().debug("BREW - UNMARK & MARK BREWER");
                break;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick1(InventoryClickEvent event) {

        if(!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getWhoClicked();
        final Inventory topInventory = player.getOpenInventory().getTopInventory();

        if(topInventory instanceof BrewerInventory
                && event.getClickedInventory() instanceof PlayerInventory) {

            final ItemStack current = event.getCurrentItem();
            final ItemStack currentIngredient = topInventory.getItem(3);

            final BrewingStand stand = ((BrewerInventory) topInventory).getHolder();

            if(current == null || stand == null) {
                return;
            }

            if(event.isShiftClick() && BrewUtil.isPossibleIngredient(current.getType())) {
                if(currentIngredient == null || currentIngredient.getType() == Material.AIR) {
                    this.getGlitchChecker().mark(stand, player);
                    this.getPlugin().debug("BREW - MARK BREWER");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBrew(BrewEvent event) {

        final BrewingStand stand = event.getContents().getHolder();

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {

            Player player = null;

            if(stand != null) {
                final UUID uuid = getGlitchChecker().getActivator(stand);
                if(uuid != null) {
                    player = Bukkit.getPlayer(uuid);
                }
            }

            if(player == null || !player.isOnline()) {
                return;
            }

            final ItemStack stack = BrewUtil.getFirstNonNull(stand.getInventory());

            if(stack == null) {
                return;
            }

            this.incrementProgression(
                    player,
                    QuestType.BREW,
                    stack.getType(),
                    event,
                    BrewUtil.numberOfNonNullSlot(stand.getInventory())
            );
        }, 2L);
    }
}
