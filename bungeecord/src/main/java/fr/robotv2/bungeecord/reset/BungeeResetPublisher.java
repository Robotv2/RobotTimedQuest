package fr.robotv2.bungeecord.reset;

import fr.robotv2.bungeecord.RTQBungeePlugin;
import fr.robotv2.common.data.DatabaseManager;
import fr.robotv2.common.data.RedisConnector;
import fr.robotv2.common.proxy.ProxyResetPublisher;

import java.util.UUID;

public class BungeeResetPublisher extends ProxyResetPublisher {

    private final RTQBungeePlugin plugin;

    public BungeeResetPublisher(RTQBungeePlugin plugin, DatabaseManager databaseManager, RedisConnector redisConnector) {
        super(databaseManager, redisConnector);
        this.plugin = plugin;
    }

    @Override
    public boolean isOnline(UUID ownerUniqueId) {
        return plugin.getProxy().getPlayer(ownerUniqueId) != null;
    }
}
