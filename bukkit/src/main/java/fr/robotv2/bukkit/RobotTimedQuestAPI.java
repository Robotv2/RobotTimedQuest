package fr.robotv2.bukkit;

import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.common.reset.ResetService;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotTimedQuestAPI {

    private final static RTQBukkitPlugin INSTANCE = JavaPlugin.getPlugin(RTQBukkitPlugin.class);

    @Nullable
    public static Quest getQuest(@NotNull String identifier) {
        return INSTANCE.getQuestManager().fromId(identifier);
    }

    @Nullable
    public static ResetService getResetService(@NotNull String identifier) {
        return INSTANCE.getBukkitResetServiceRepo().getService(identifier);
    }
}
