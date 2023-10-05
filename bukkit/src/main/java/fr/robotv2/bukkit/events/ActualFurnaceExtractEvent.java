package fr.robotv2.bukkit.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ActualFurnaceExtractEvent extends BlockEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player player;
    private final ItemStack stack;
    private final int amount;

    public ActualFurnaceExtractEvent(Block block, Player player, ItemStack stack, int amount) {
        super(block);
        this.player = player;
        this.stack = stack;
        this.amount = amount;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getAmount() {
        return amount;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
