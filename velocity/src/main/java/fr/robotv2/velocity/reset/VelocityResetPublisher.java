package fr.robotv2.velocity.reset;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.robotv2.common.data.DatabaseManager;
import fr.robotv2.common.data.RedisConnector;
import fr.robotv2.common.proxy.ProxyResetPublisher;

import java.util.UUID;

public class VelocityResetPublisher extends ProxyResetPublisher {

    @Inject
    private ProxyServer server;

    public VelocityResetPublisher(DatabaseManager databaseManager, RedisConnector redisConnector) {
        super(databaseManager, redisConnector);
    }

    @Override
    public boolean isOnline(UUID ownerUniqueId) {
        return server.getPlayer(ownerUniqueId).isPresent();
    }
}
