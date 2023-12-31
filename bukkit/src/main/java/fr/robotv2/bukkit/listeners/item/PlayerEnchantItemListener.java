package fr.robotv2.bukkit.listeners.item;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class PlayerEnchantItemListener extends QuestProgressionEnhancer<Material> {

    public PlayerEnchantItemListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {
        this.incrementProgression(event.getEnchanter(), QuestType.ENCHANT, event.getItem().getType(), event,event.getItem().getAmount());
    }
}
