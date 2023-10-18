package fr.robotv2.bukkit.events;

import fr.robotv2.bukkit.hook.pyrofishpro.PyroFishProHook;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PyroFishCaughtEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Item item;
    private final PyroFishProHook.PyroFishWrapper fishWrapper;
    private final FishHook hook;

    public PyroFishCaughtEvent(@NotNull Player who, Item item, PyroFishProHook.PyroFishWrapper fishWrapper, FishHook hook) {
        super(who);
        this.item = item;
        this.fishWrapper = fishWrapper;
        this.hook = hook;
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

    public Item getEntity() {
        return item;
    }

    public PyroFishProHook.PyroFishWrapper getFishWrapper() {
        return fishWrapper;
    }

    public FishHook getHook() {
        return hook;
    }
}
