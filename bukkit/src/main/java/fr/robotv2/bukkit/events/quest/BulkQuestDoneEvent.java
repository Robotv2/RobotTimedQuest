package fr.robotv2.bukkit.events.quest;

import fr.robotv2.common.data.impl.ActiveQuest;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BulkQuestDoneEvent extends ActiveQuestEvent {

    private final Player player;
    private final String resetId;

    private final static HandlerList HANDLER_LIST = new HandlerList();

    public BulkQuestDoneEvent(ActiveQuest activeQuest, Player player, String resetId) {
        super(activeQuest);
        this.resetId = resetId;
        this.player = player;
    }

    public @NotNull ActiveQuest getLastDoneQuest() {
        return super.getActiveQuest();
    }

    public @NotNull String getResetId() {
        return this.resetId;
    }

    public @NotNull Player getPlayer() {
        return player;
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
