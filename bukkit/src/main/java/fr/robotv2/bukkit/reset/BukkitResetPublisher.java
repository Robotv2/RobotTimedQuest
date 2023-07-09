package fr.robotv2.bukkit.reset;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.events.DelayQuestResetEvent;
import fr.robotv2.common.data.impl.QuestPlayer;
import fr.robotv2.common.reset.ResetPublisher;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.UUID;

public class BukkitResetPublisher implements ResetPublisher {

    private final RTQBukkitPlugin plugin;

    public BukkitResetPublisher(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void publishReset(@NotNull String resetId) {

        QuestPlayer.getRegistered().forEach(questPlayer -> questPlayer.removeActiveQuest(resetId));

        if(!plugin.isBungeecordMode()) {
            plugin.getDatabaseManager().getActiveQuestOrmData().removeWhere(where -> {
                try {
                    return where.eq("reset_id", resetId);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(new DelayQuestResetEvent(resetId)));
    }

    @Override
    public void reset(@NotNull UUID ownerUniqueId, @Nullable String resetId) {
        final QuestPlayer questPlayer = QuestPlayer.getQuestPlayer(ownerUniqueId);

        if(questPlayer != null) {
            if(resetId != null) {
                questPlayer.removeActiveQuest(resetId);
            } else {
                questPlayer.clearActiveQuests();
            }
            plugin.getQuestManager().fillPlayer(questPlayer);
        }

        plugin.getDatabaseManager().getActiveQuestOrmData().removeWhere(where -> {
            try {
                if(resetId != null) {
                    return where
                            .eq("owner", ownerUniqueId)
                            .and()
                            .eq("reset_id", resetId);
                } else {
                    return where.eq("owner", ownerUniqueId);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
