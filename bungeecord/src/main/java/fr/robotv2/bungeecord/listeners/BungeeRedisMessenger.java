package fr.robotv2.bungeecord.listeners;

import fr.robotv2.bungeecord.RTQBungeePlugin;
import fr.robotv2.common.data.RedisConnector;

public class BungeeRedisMessenger implements RedisConnector.AbstractMessenger {

    private final RTQBungeePlugin plugin;

    public BungeeRedisMessenger(RTQBungeePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerIncomingMessage(String channel, byte[] bytes) {
    }
}
