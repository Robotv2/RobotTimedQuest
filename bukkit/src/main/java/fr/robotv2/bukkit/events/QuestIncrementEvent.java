package fr.robotv2.bukkit.events;

import fr.robotv2.common.data.impl.ActiveQuest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class QuestIncrementEvent extends ActiveQuestEvent {

    private final static HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final int amountIncremented;

    public QuestIncrementEvent(ActiveQuest activeQuest, int amountIncremented) {
        super(activeQuest);
        this.player = Objects.requireNonNull(Bukkit.getPlayer(getActiveQuest().getOwner()));
        this.amountIncremented = amountIncremented;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    public int getAmountIncremented() {
        return this.amountIncremented;
    }
}
