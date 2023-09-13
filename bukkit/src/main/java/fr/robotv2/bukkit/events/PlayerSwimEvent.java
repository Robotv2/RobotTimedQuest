package fr.robotv2.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerSwimEvent extends PlayerEvent {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    private final int beforeValue;
    private final int newValue;

    public PlayerSwimEvent(@NotNull Player who, int beforeValue, int newValue) {
        super(who);
        this.beforeValue = beforeValue;
        this.newValue = newValue;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public int getBeforeValue() {
        return beforeValue;
    }

    public int getNewValue() {
        return newValue;
    }
}
