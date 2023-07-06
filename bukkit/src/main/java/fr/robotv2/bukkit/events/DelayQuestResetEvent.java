package fr.robotv2.bukkit.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DelayQuestResetEvent extends Event {

    private final static HandlerList HANDLER_LIST = new HandlerList();
    private final String resetId;

    public DelayQuestResetEvent(String resetId) {
        this.resetId = resetId;
    }

    public String getResetId() {
        return this.resetId;
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
