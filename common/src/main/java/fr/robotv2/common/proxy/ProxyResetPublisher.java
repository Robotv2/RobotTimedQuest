package fr.robotv2.common.proxy;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.robotv2.common.channel.ChannelConstant;
import fr.robotv2.common.data.DatabaseManager;
import fr.robotv2.common.data.RedisConnector;
import fr.robotv2.common.reset.ResetPublisher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.UUID;

public abstract class ProxyResetPublisher implements ResetPublisher {

    private final RedisConnector redisConnector;
    private final DatabaseManager databaseManager;

    public ProxyResetPublisher(DatabaseManager databaseManager, RedisConnector redisConnector) {
        this.redisConnector = redisConnector;
        this.databaseManager = databaseManager;
    }

    @Override
    public void publishReset(@NotNull String resetId) {
        redisConnector.publish(ChannelConstant.BUKKIT_CHANNEL, "AUTOMATIC_RESET", resetId);
        databaseManager.getActiveQuestOrmData().removeWhere(where -> {
            try {
                return where.eq("reset_id", resetId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }

    @Override
    public void reset(@NotNull UUID ownerUniqueId, @Nullable String resetId) {
        if(this.isOnline(ownerUniqueId)) {

            ByteArrayDataOutput out = ByteStreams.newDataOutput();

            if(resetId == null) {
                out.writeUTF("PLAYER_RESET_ALL");
                out.writeUTF(ownerUniqueId.toString());
            } else {
                out.writeUTF("PLAYER_RESET_ID");
                out.writeUTF(ownerUniqueId.toString());
                out.writeUTF(resetId);
            }

            redisConnector.publish(ChannelConstant.BUKKIT_CHANNEL, out.toByteArray());
        }

        databaseManager.getActiveQuestOrmData().removeWhere(where -> {
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

    public abstract boolean isOnline(UUID ownerUniqueId);
}
