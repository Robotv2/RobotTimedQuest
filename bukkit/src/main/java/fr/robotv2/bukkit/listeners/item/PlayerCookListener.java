package fr.robotv2.bukkit.listeners.item;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.FurnaceExtractEvent;

public class PlayerCookListener extends QuestProgressionEnhancer<Material> {

    public PlayerCookListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceExtract(FurnaceExtractEvent event) {
        this.incrementProgression(event.getPlayer(), QuestType.COOK, event.getItemType(), event, event.getItemAmount());
    }
}
