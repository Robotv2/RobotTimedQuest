package fr.robotv2.common.proxy;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fr.robotv2.common.channel.ChannelConstant;
import fr.robotv2.common.data.RedisConnector;

import java.util.Locale;
import java.util.UUID;

public class ProxyRedisMessenger implements RedisConnector.AbstractMessenger {

    private final ProxyResetPublisher publisher;

    public ProxyRedisMessenger(ProxyResetPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void registerIncomingMessage(String channel, byte[] bytes) {

        if(!channel.equals(ChannelConstant.BUNGEECORD_CHANNEL)) {
            return;
        }

        final ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
        final String sub = input.readUTF();

        switch (sub.toUpperCase(Locale.ROOT)) {

            case "BUNGEECORD_PLAYER_RESET_ALL": {
                final UUID uuid = UUID.fromString(input.readUTF());
                publisher.reset(uuid, null);
                break;
            }

            case "BUNGEECORD_PLAYER_RESET_ID": {
                final UUID uuid = UUID.fromString(input.readUTF());
                final String resetId = input.readUTF();
                publisher.reset(uuid, resetId);
                break;
            }
        }
    }
}
