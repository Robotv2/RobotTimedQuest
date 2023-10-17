package fr.robotv2.bukkit.reset;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.events.DelayQuestResetEvent;
import fr.robotv2.common.channel.ChannelConstant;
import fr.robotv2.common.data.impl.QuestPlayer;
import fr.robotv2.common.reset.ResetPublisher;
import fr.robotv2.common.reset.ResetService;
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
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(new DelayQuestResetEvent(resetId)));

        if(!plugin.isBungeecordMode()) {
            plugin.getDatabaseManager().getActiveQuestOrmData().removeWhere(where -> {
                try {
                    return where.eq("reset_id", resetId);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        final ResetService service = this.plugin.getBukkitResetServiceRepo().getService(resetId);

        if(service != null) {
            service.calculateNextExecution();
        }

        QuestPlayer.getRegistered()
                        .forEach(questPlayer -> plugin.getQuestManager().fillPlayer(questPlayer, resetId));
    }

    @Override
    public void reset(@NotNull UUID ownerUniqueId, @Nullable String resetId) {

        if(this.plugin.isBungeecordMode()) {
            this.sendRequestToBungeecord(ownerUniqueId, resetId);
            return;
        }

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

    private void sendRequestToBungeecord(@NotNull UUID ownerUniqueId, @Nullable String resetId) {
        final ByteArrayDataOutput output = ByteStreams.newDataOutput();

        if(resetId == null) {
            output.writeUTF("BUNGEECORD_PLAYER_RESET_ALL");
            output.writeUTF(ownerUniqueId.toString());
        } else {
            output.writeUTF("BUNGEECORD_PLAYER_RESET_ID");
            output.writeUTF(ownerUniqueId.toString());
            output.writeUTF(resetId);
        }

        plugin.getRedisConnector().publish(ChannelConstant.BUNGEECORD_CHANNEL, output.toByteArray());
    }
}
