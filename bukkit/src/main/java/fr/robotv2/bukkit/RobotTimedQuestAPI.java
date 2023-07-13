package fr.robotv2.bukkit;

import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.common.data.impl.ActiveQuest;
import fr.robotv2.common.data.impl.QuestPlayer;
import fr.robotv2.common.reset.ResetService;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

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

    @Nullable
    public static QuestPlayer getQuestPlayer(UUID uuid) {
        return QuestPlayer.getQuestPlayer(uuid);
    }

    @UnmodifiableView
    public static Collection<ActiveQuest> getPlayerQuest(UUID uuid) {
        QuestPlayer questPlayer;
        return (questPlayer = getQuestPlayer(uuid)) != null ? questPlayer.getActiveQuests() : Collections.emptyList();
    }

    public static void registerCondition(String key, Class<? extends Condition> condition) {
        INSTANCE.getConditionManager().registerCondition(key, condition);
    }
}
