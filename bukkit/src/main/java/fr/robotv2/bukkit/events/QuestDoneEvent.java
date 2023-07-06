package fr.robotv2.bukkit.events;

import fr.robotv2.common.data.impl.ActiveQuest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class QuestDoneEvent extends ActiveQuestEvent {

    private final static HandlerList HANDLER_LIST = new HandlerList();

    public QuestDoneEvent(ActiveQuest activeQuest) {
        super(activeQuest);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull // can't be null if the player just did his quest.
    public Player getPlayer() {
        return Objects.requireNonNull(Bukkit.getPlayer(getActiveQuest().getOwner()));
    }
}
