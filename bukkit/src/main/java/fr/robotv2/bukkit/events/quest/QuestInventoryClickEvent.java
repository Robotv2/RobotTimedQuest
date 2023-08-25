package fr.robotv2.bukkit.events.quest;

import fr.robotv2.bukkit.RobotTimedQuestAPI;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.common.data.impl.ActiveQuest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

// Triggered when the player has clicked on a quest in the gui.

public class QuestInventoryClickEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ActiveQuest activeQuest;
    private final InventoryClickEvent inventoryClickEvent;

    public QuestInventoryClickEvent(InventoryClickEvent inventoryClickEvent, ActiveQuest activeQuest) {
        this.inventoryClickEvent = inventoryClickEvent;
        this.activeQuest = activeQuest;
    }

    @NotNull
    public Player getPlayerWhoClicked() {
        return Objects.requireNonNull(Bukkit.getPlayer(activeQuest.getOwner()));
    }

    @NotNull
    public ActiveQuest getActiveQuest() {
        return this.activeQuest;
    }

    @Nullable
    public Quest getQuest() {
        return RobotTimedQuestAPI.getQuest(getActiveQuest().getQuestId());
    }

    public InventoryClickEvent getInventoryClickEvent() {
        return this.inventoryClickEvent;
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
