package fr.robotv2.bukkit.listeners.item;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.listeners.QuestActionData;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerEnchantItemListener extends QuestProgressionEnhancer<Material> {

    public PlayerEnchantItemListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {

        final Player player = event.getEnchanter();
        final ItemStack stack = event.getItem();
        final Block enchantBlock = event.getEnchantBlock();

        final QuestActionData data = QuestActionData.of(player, enchantBlock, null, stack);
        this.incrementProgression(event.getEnchanter(), QuestType.ENCHANT, event.getItem().getType(), data,1);
    }
}
