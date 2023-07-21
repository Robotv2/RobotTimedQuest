package fr.robotv2.bukkit.listeners.item;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;

public class PlayerBrewListener extends QuestProgressionEnhancer<Material> {

    private final EnumSet<InventoryAction> actions = EnumSet.of(
            InventoryAction.PICKUP_SOME,
            InventoryAction.PICKUP_HALF,
            InventoryAction.PICKUP_ONE,
            InventoryAction.PICKUP_ALL
    );

    public PlayerBrewListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if(!(event.getInventory() instanceof BrewerInventory)) {
            return;
        }

        if(!this.actions.contains(event.getAction())) {
            return;
        }

        final ItemStack stack = event.getCurrentItem();

        if(stack == null || !stack.getType().name().contains("POTION")) {
            return;
        }

        this.incrementProgression(
                (Player) event.getWhoClicked(),
                QuestType.BREW,
                stack.getType(),
                event,
                stack.getAmount()
        );
    }
}
