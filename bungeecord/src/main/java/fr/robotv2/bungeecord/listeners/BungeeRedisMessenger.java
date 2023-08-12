package fr.robotv2.bungeecord.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fr.robotv2.bungeecord.RTQBungeePlugin;
import fr.robotv2.common.channel.ChannelConstant;
import fr.robotv2.common.data.RedisConnector;

import java.util.Locale;
import java.util.UUID;

public class BungeeRedisMessenger implements RedisConnector.AbstractMessenger {

    private final RTQBungeePlugin plugin;

    public BungeeRedisMessenger(RTQBungeePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerIncomingMessage(String channel, byte[] bytes) {

        if(!channel.equals(ChannelConstant.BUNGEECORD_CHANNEL)) {
            return;
        }

        final ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
        final String sub = input.readUTF();

        switch (sub.toLowerCase(Locale.ROOT)) {

            case "BUNGEECORD_PLAYER_RESET_ALL": {
                final UUID uuid = UUID.fromString(input.readUTF());
                plugin.getBungeeResetPublisher().reset(uuid, null);
                break;
            }

            case "BUNGEECORD_PLAYER_RESET_ID": {
                final UUID uuid = UUID.fromString(input.readUTF());
                final String resetId = input.readUTF();
                plugin.getBungeeResetPublisher().reset(uuid, resetId);
                break;
            }
        }
    }
}
