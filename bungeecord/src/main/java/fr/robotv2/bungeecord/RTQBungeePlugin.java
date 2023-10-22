package fr.robotv2.bungeecord;

import fr.robotv2.bungeecord.command.BungeeMainCommand;
import fr.robotv2.bungeecord.config.BungeeConfigFile;
import fr.robotv2.bungeecord.reset.BungeeResetPublisher;
import fr.robotv2.common.channel.ChannelConstant;
import fr.robotv2.common.data.DatabaseCredentials;
import fr.robotv2.common.data.DatabaseManager;
import fr.robotv2.common.data.RedisConnector;
import fr.robotv2.common.data.impl.MySqlCredentials;
import fr.robotv2.common.proxy.ProxyRedisMessenger;
import fr.robotv2.common.proxy.ProxyResetServiceRepo;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.SQLException;
import java.util.logging.Level;

public class RTQBungeePlugin extends Plugin {

    private DatabaseManager databaseManager;

    private ProxyResetServiceRepo bungeeResetServiceRepo;
    private BungeeResetPublisher bungeeResetPublisher;

    private BungeeConfigFile configFile;
    private BungeeConfigFile resetServiceFile;

    private RedisConnector redisConnector;

    @Override
    public void onEnable() {

        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        this.configFile = new BungeeConfigFile(this, "bungee-config.yml", true);
        this.resetServiceFile = new BungeeConfigFile(this, "bungee-reset-service.yml", true);

        this.setupRedis();
        this.setupDatabase();

        this.bungeeResetPublisher = new BungeeResetPublisher(this, databaseManager, redisConnector);
        this.bungeeResetServiceRepo = new ProxyResetServiceRepo(resetServiceFile, bungeeResetPublisher);

        getBungeeResetServiceRepo().registerServices();
        this.setupCommandHandlers();
    }

    @Override
    public void onDisable() {
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
        this.getBungeeResetServiceRepo().registerServices();
    }

    public ProxyResetServiceRepo getBungeeResetServiceRepo() {
        return bungeeResetServiceRepo;
    }

    public BungeeResetPublisher getBungeeResetPublisher() {
        return this.bungeeResetPublisher;
    }

    public DatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    public RedisConnector getRedisConnector() {
        return redisConnector;
    }

    public BungeeConfigFile getResetServiceFile() {
        return this.resetServiceFile;
    }

    public BungeeConfigFile getConfigFile() {
        return configFile;
    }

    private void setupDatabase() {

        final DatabaseCredentials credentials = new MySqlCredentials(getConfigFile());

        try {
            this.databaseManager = new DatabaseManager(credentials);
            getLogger().info("Successfully connected to the database.");
        } catch (SQLException exception) {
            getLogger().warning(" ");
            getLogger().log(Level.WARNING, "An error occurred while trying to connect to the database.", exception);
            getLogger().warning(" ");
            getLogger().warning("Please check again your credentials.");
            getLogger().warning("The plugin will not work.");
            getLogger().warning(" ");
            disablePlugin();
        }
    }

    private void setupRedis() {

        try {
            this.redisConnector = new RedisConnector(configFile);
            this.redisConnector.setMessenger(new ProxyRedisMessenger(bungeeResetPublisher));
            this.redisConnector.subscribe(ChannelConstant.BUNGEECORD_CHANNEL);
        } catch (Exception exception) {

            getLogger().warning(" ");
            getLogger().log(Level.WARNING, "An error occurred while trying to connect to redis.", exception);
            getLogger().warning(" ");
            getLogger().warning("Please check again your credentials.");
            getLogger().warning("The plugin will not work.");
            getLogger().warning(" ");
            disablePlugin();
        }
    }

    private void disablePlugin() {
        getProxy().getPluginManager().unregisterListeners(this);
        getProxy().getPluginManager().unregisterCommands(this);
        this.onDisable();
    }

    private void setupCommandHandlers() {
        this.getProxy().getPluginManager().registerCommand(this, new BungeeMainCommand(this));
    }
}
