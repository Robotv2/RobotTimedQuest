package fr.robotv2.bukkit.listeners.block;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.events.MultipleCropsBreakEvent;
import fr.robotv2.bukkit.listeners.QuestActionData;
import fr.robotv2.bukkit.listeners.QuestProgressionEnhancer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HarvestBlockListener extends QuestProgressionEnhancer<Material> {

    public HarvestBlockListener(RTQBukkitPlugin plugin) {
        super(plugin);
    }

    @FunctionalInterface
    private interface CropFilter {
        Material filter(Material material);
    }

    private void checkAboveBlock(Player player, Block initial, @Nullable CropFilter filter) {

        if(!MultipleCropsBreakEvent.VERTICAL_CROPS.contains(initial.getType())) {
            return;
        }

        final List<Block> blocks = new ArrayList<>();
        blocks.add(initial);

        Block above = initial.getRelative(BlockFace.UP);

        while(MultipleCropsBreakEvent.VERTICAL_CROPS.contains(above.getType())) {
            blocks.add(above);
            above = above.getRelative(BlockFace.UP);
        }

        final Material material = filter != null ? filter.filter(initial.getType()) : initial.getType();
        final MultipleCropsBreakEvent multipleCropsBreakEvent = new MultipleCropsBreakEvent(player, material, blocks);

        Bukkit.getPluginManager().callEvent(multipleCropsBreakEvent);
    }

    private void handleCrops(Player player, BlockData data, Collection<ItemStack> stacks, @Nullable CropFilter filter) {

        if(!(data instanceof Ageable)) {
            return;
        }

        final Ageable ageable = (Ageable) data;

        if(ageable.getAge() != ageable.getMaximumAge()) {
            return;
        }

        final Material material = filter != null ? filter.filter(data.getMaterial()) : data.getMaterial();

        final int amount = stacks.stream()
                .filter(stack -> stack.getType() == material)
                .mapToInt(ItemStack::getAmount)
                .sum();

        final QuestActionData questActionData = QuestActionData.of(player);
        this.incrementProgression(player, QuestType.FARMING, material, questActionData, amount);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMultipleCropsBreak(MultipleCropsBreakEvent event) {

        final int amount = event.getBlocks().stream()
                .filter(block -> !this.getGlitchChecker().isMarked(block)) // Check if the block isn't placed by the player.
                .flatMap(block -> block.getDrops().stream())
                .filter(stack -> stack.getType() == event.getMaterial())
                .mapToInt(ItemStack::getAmount)
                .sum();

        final QuestActionData questActionData = QuestActionData.of(event.getPlayer());
        this.incrementProgression(event.getPlayer(), QuestType.FARMING, event.getMaterial(), questActionData, amount);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerHarvestBlock(PlayerHarvestBlockEvent event) {

        final CropFilter filter = material -> {
            switch (material) {
                case SWEET_BERRY_BUSH:
                    return Material.SWEET_BERRIES;
                default:
                    return material;
            }
        };

        this.handleCrops(
                event.getPlayer(),
                event.getHarvestedBlock().getBlockData(),
                event.getItemsHarvested(),
                filter
        );
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        final CropFilter filter = material -> {
            switch (material) {
                case POTATOES:
                    return Material.POTATO;
                case CARROTS:
                    return Material.CARROT;
                case BEETROOTS:
                    return Material.BEETROOT;
                case COCOA:
                    return Material.COCOA_BEANS;
                case SWEET_BERRY_BUSH:
                    return Material.SWEET_BERRIES;
                case KELP_PLANT:
                    return Material.KELP;
                default:
                    return material;
            }
        };

        this.checkAboveBlock(event.getPlayer(), event.getBlock(), filter);

        this.handleCrops(
                event.getPlayer(),
                event.getBlock().getBlockData(),
                event.getBlock().getDrops(),
                filter
        );
    }
}
