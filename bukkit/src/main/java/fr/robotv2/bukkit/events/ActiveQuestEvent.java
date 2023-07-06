package fr.robotv2.bukkit.events;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.common.data.impl.ActiveQuest;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ActiveQuestEvent extends Event {

    private final ActiveQuest activeQuest;

    public ActiveQuestEvent(ActiveQuest activeQuest) {
        this.activeQuest = activeQuest;
    }

    @NotNull
    public ActiveQuest getActiveQuest() {
        return activeQuest;
    }

    @Nullable
    public Quest getQuest() {
        return RTQBukkitPlugin.getInstance().getQuestManager().fromId(activeQuest.getQuestId());
    }
}
