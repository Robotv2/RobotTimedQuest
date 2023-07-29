package fr.robotv2.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VillagerTradeEvent extends Event {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;

    private final MerchantInventory merchantInventory;
    private final MerchantRecipe recipe;
    private final ItemStack result;

    public VillagerTradeEvent(@NotNull Player player, @NotNull MerchantInventory merchantInventory, @NotNull MerchantRecipe recipe, @NotNull ItemStack result) {
        this.player = player;
        this.merchantInventory = merchantInventory;
        this.recipe = recipe;
        this.result = result;
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @NotNull
    public MerchantInventory getMerchantInventory() {
        return this.merchantInventory;
    }

    @NotNull
    public MerchantRecipe getMerchantRecipe() {
        return this.recipe;
    }

    @NotNull
    public ItemStack getResult() {
        return this.result;
    }

    @Nullable
    public Villager getVillager() {
        final InventoryHolder holder = getMerchantInventory().getHolder();
        return holder instanceof Villager ? (Villager) holder : null;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
