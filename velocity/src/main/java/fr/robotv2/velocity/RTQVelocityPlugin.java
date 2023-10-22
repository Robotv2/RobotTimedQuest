package fr.robotv2.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.robotv2.common.channel.ChannelConstant;
import fr.robotv2.common.config.RConfiguration;
import fr.robotv2.common.data.DatabaseCredentials;
import fr.robotv2.common.data.DatabaseManager;
import fr.robotv2.common.data.RedisConnector;
import fr.robotv2.common.data.impl.MySqlCredentials;
import fr.robotv2.common.proxy.ProxyRedisMessenger;
import fr.robotv2.common.proxy.ProxyResetServiceRepo;
import fr.robotv2.common.reset.AbstractResetServiceRepo;
import fr.robotv2.velocity.command.VelocityMainCommand;
import fr.robotv2.velocity.config.VelocityConfigFile;
import fr.robotv2.velocity.reset.VelocityResetPublisher;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.sql.SQLException;

@Plugin(
        id = RTQVelocityPlugin.PLUGIN_ID,
        name = "RobotTimedQuest-Velocity",
        version = "1.0-SNAPSHOT"
)
public class RTQVelocityPlugin {

    public static final String PLUGIN_ID = "robottimedquest-velocity";

    @Inject
    private Logger logger;

    @Inject
    private ProxyServer server;

    private VelocityConfigFile configFile;
    private VelocityConfigFile resetServiceFile;

    private AbstractResetServiceRepo resetServiceRepo;
    private VelocityResetPublisher resetPublisher;

    private DatabaseManager databaseManager;
    private RedisConnector redisConnector;

    private final Path dataDirectory;

    @Inject
    public RTQVelocityPlugin(@DataDirectory Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        if(!dataDirectory.toFile().exists()) {
            dataDirectory.toFile().mkdir();
        }

        this.configFile = new VelocityConfigFile(this, "velocity-config.yml", true);
        this.resetServiceFile = new VelocityConfigFile(this,"velocity-reset-service.yml", true);

        this.setupRedis();
        this.setupDatabase();

        this.resetPublisher = new VelocityResetPublisher(getDatabaseManager(), getRedisConnector());
        this.resetServiceRepo = new ProxyResetServiceRepo(getResetServiceFile(), getResetPublisher());

        getResetServiceRepo().registerServices();
        server.getCommandManager().register("rtq-velocity", new VelocityMainCommand(this));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if(this.databaseManager != null && this.databaseManager.isConnected()) {
            this.databaseManager.closeConnection();
        }

        if(this.redisConnector != null) {
            this.redisConnector.getJedis();
            this.redisConnector.close();
        }
    }

    public void onReload() {
        getConfigFile().reload();
        getResetServiceFile().reload();
        getResetServiceRepo().registerServices();
    }

    public VelocityConfigFile getConfigFile() {
        return configFile;
    }

    public VelocityConfigFile getResetServiceFile() {
        return resetServiceFile;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public RedisConnector getRedisConnector() {
        return redisConnector;
    }

    public VelocityResetPublisher getResetPublisher() {
        return resetPublisher;
    }

    public AbstractResetServiceRepo getResetServiceRepo() {
        return resetServiceRepo;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    private void setupDatabase() {

        final DatabaseCredentials credentials = new MySqlCredentials(getConfigFile());

        try {
            this.databaseManager = new DatabaseManager(credentials);
            logger.info("Successfully connected to the database.");
        } catch (SQLException exception) {
            logger.error(" ");
            logger.error("An error occurred while trying to connect to the database.", exception);
            logger.error(" ");
            logger.error("Please check again your credentials.");
            logger.error("The plugin will not work.");
            logger.error(" ");
        }
    }

    private void setupRedis() {

        try {
            this.redisConnector = new RedisConnector(configFile);
            this.redisConnector.setMessenger(new ProxyRedisMessenger(resetPublisher));
            this.redisConnector.subscribe(ChannelConstant.BUNGEECORD_CHANNEL);
        } catch (Exception exception) {

            logger.error(" ");
            logger.error("An error occurred while trying to connect to redis.", exception);
            logger.error(" ");
            logger.error("Please check again your credentials.");
            logger.error("The plugin will not work.");
            logger.error(" ");
        }
    }
}
