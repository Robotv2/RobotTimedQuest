package fr.robotv2.bungeecord;

import co.aikar.commands.BungeeCommandManager;
import fr.robotv2.bungeecord.command.BungeeMainCommand;
import fr.robotv2.bungeecord.config.BungeeConfigFile;
import fr.robotv2.bungeecord.listeners.BungeeRedisMessenger;
import fr.robotv2.bungeecord.reset.BungeeResetPublisher;
import fr.robotv2.bungeecord.reset.BungeeResetServiceRepo;
import fr.robotv2.common.channel.ChannelConstant;
import fr.robotv2.common.data.DatabaseCredentials;
import fr.robotv2.common.data.DatabaseManager;
import fr.robotv2.common.data.RedisConnector;
import fr.robotv2.common.data.impl.MySqlCredentials;
import fr.robotv2.common.reset.ResetService;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.sql.SQLException;
import java.util.stream.Collectors;

public class RTQBungeePlugin extends Plugin {

    private DatabaseManager databaseManager;

    private BungeeResetServiceRepo bungeeResetServiceRepo;
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

        this.bungeeResetServiceRepo = new BungeeResetServiceRepo(this);
        this.bungeeResetPublisher = new BungeeResetPublisher(this);

        this.getBungeeResetServiceRepo().registerServices();

        this.setupDatabase();
        this.setupRedis();

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

    public BungeeResetServiceRepo getBungeeResetServiceRepo() {
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

    public boolean isOnlineMode() {
        return getProxy().getConfig().isOnlineMode();
    }

    private void setupRedis() {

        final Configuration configuration = this.getConfigFile().getConfiguration();
        final String address = configuration.getString("options.redis_address", "127.0.0.1");
        final int port = configuration.getInt("options.redis_port", 6379);
        final String password = configuration.getString("options.redis_password");

        try {
            this.redisConnector = new RedisConnector(
                    address,
                    port,
                    password
            );
            this.redisConnector.setMessenger(new BungeeRedisMessenger(this));
            this.redisConnector.subscribe(
                    ChannelConstant.RESET_CHANNEL,
                    ChannelConstant.IS_SAVED_CHANNEL,
                    ChannelConstant.WAIT_SAVING_CHANNEL
            );
        } catch (Exception exception) {

            exception.printStackTrace();

            getLogger().warning(" ");
            getLogger().warning("An error occurred while trying to connect to redis.");
            getLogger().warning("Please check again your credentials.");
            getLogger().warning("The plugin will not work.");
            getLogger().warning(" ");
            this.disablePlugin();
        }
    }

    private void setupDatabase() {

        final Configuration configuration = this.getConfigFile().getConfiguration();
        final String host = configuration.getString("storage.mysql-credentials.host");
        final String port = configuration.getString("storage.mysql-credentials.port");
        final String database = configuration.getString("storage.mysql-credentials.database");
        final String username = configuration.getString("storage.mysql-credentials.username");
        final String password = configuration.getString("storage.mysql-credentials.password");
        final boolean ssl = configuration.getBoolean("storage.mysql-credentials.useSSL", false);

        final DatabaseCredentials credentials = new MySqlCredentials(host, port, database, username, password, ssl);

        try {
            this.databaseManager = new DatabaseManager(credentials);
            getLogger().info("Successfully connected to the database.");
        } catch (SQLException exception) {
            getLogger().warning(" ");
            getLogger().warning("An error occurred while trying to connect to the database.");
            getLogger().warning("Please check again your credentials.");
            getLogger().warning("The plugin will not work.");
            getLogger().warning(" ");
            this.disablePlugin();
        }
    }

    private void disablePlugin() {
        getProxy().getPluginManager().unregisterListeners(this);
        getProxy().getPluginManager().unregisterCommands(this);
        this.onDisable();
    }

    private void setupCommandHandlers() {
        BungeeCommandManager bungeeCommandManager = new BungeeCommandManager(this);
        bungeeCommandManager.getCommandCompletions().registerCompletion("services", context -> getBungeeResetServiceRepo().getServicesNames());
        bungeeCommandManager.getCommandContexts().registerContext(ResetService.class, context -> getBungeeResetServiceRepo().getService(context.popFirstArg()));
        bungeeCommandManager.registerCommand(new BungeeMainCommand(this));
    }
}
