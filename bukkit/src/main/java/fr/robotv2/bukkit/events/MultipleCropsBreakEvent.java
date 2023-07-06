package fr.robotv2.bukkit.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

public class MultipleCropsBreakEvent extends PlayerEvent {

    public static EnumSet<Material> VERTICAL_CROPS = EnumSet.of(
            Material.BAMBOO,
            Material.SUGAR_CANE,
            Material.KELP_PLANT,
            Material.CACTUS
    );

    private static final HandlerList handlers = new HandlerList();
    private final List<Block> blocks;
    private final Material material;

    public MultipleCropsBreakEvent(@NotNull Player player, Material material, List<Block> blocks) {
        super(player);
        this.blocks = blocks;
        this.material = material;
    }

    public Material getMaterial() {
        return this.material;
    }

    public List<Block> getBlocks() {
        return this.blocks;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
