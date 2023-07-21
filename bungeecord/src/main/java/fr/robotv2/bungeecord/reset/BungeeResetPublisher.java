package fr.robotv2.bungeecord.reset;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.robotv2.bungeecord.RTQBungeePlugin;
import fr.robotv2.common.channel.ChannelConstant;
import fr.robotv2.common.reset.ResetPublisher;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.UUID;

public class BungeeResetPublisher implements ResetPublisher {

    private final RTQBungeePlugin plugin;

    public BungeeResetPublisher(RTQBungeePlugin plugin) {
        this.plugin = plugin;
    }

    private void sendServerMessage(ByteArrayDataOutput out, ServerInfo... servers) {
        for(ServerInfo serverInfo : servers) {
            if(!serverInfo.getPlayers().isEmpty()) { // server won't receive message if no player on it.
                serverInfo.sendData(ChannelConstant.RESET_CHANNEL, out.toByteArray());
            }
        }
    }

    @Override
    public void publishReset(@NotNull String resetId) {

        this.plugin.getRedisConnector().publish(ChannelConstant.RESET_CHANNEL, resetId);

        plugin.getDatabaseManager().getActiveQuestOrmData().removeWhere(where -> {
            try {
                return where.eq("reset_id", resetId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void reset(@NotNull UUID ownerUniqueId, @Nullable String resetId) {

        final ProxiedPlayer proxiedPlayer = plugin.getProxy().getPlayer(ownerUniqueId);

        if(proxiedPlayer != null && proxiedPlayer.isConnected()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();

            if(resetId == null) {
                out.writeUTF("clear-quests");
                out.writeUTF(proxiedPlayer.getUniqueId().toString());
            } else {
                out.writeUTF("clear-quests-id");
                out.writeUTF(proxiedPlayer.getUniqueId().toString());
                out.writeUTF(resetId);
            }

            this.sendServerMessage(out, proxiedPlayer.getServer().getInfo());
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
