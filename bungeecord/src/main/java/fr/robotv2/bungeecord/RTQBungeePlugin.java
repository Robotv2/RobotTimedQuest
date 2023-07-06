package fr.robotv2.bungeecord;

import fr.robotv2.bungeecord.config.BungeeConfigFile;
import fr.robotv2.bungeecord.reset.BungeeResetPublisher;
import fr.robotv2.bungeecord.reset.BungeeResetServiceRepo;
import fr.robotv2.common.data.DatabaseCredentials;
import fr.robotv2.common.data.DatabaseManager;
import fr.robotv2.common.data.impl.MySqlCredentials;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import revxrsal.commands.bungee.BungeeCommandHandler;

import java.sql.SQLException;

public class RTQBungeePlugin extends Plugin {

    private DatabaseManager databaseManager;
    private BungeeCommandHandler commandHandler;

    private BungeeResetServiceRepo bungeeResetServiceRepo;
    private BungeeResetPublisher bungeeResetPublisher;

    private BungeeConfigFile configFile;
    private BungeeConfigFile resetServiceFile;

    @Override
    public void onEnable() {

        this.configFile = new BungeeConfigFile(this, "bungee-config.yml", true);
        this.resetServiceFile = new BungeeConfigFile(this, "bungee-reset-services.yml", true);

        this.bungeeResetServiceRepo = new BungeeResetServiceRepo(this);
        this.bungeeResetPublisher = new BungeeResetPublisher(this);

        this.getBungeeResetServiceRepo().registerServices();
        this.setupDatabase();

        this.commandHandler = BungeeCommandHandler.create(this);
    }

    @Override
    public void onDisable() {
        this.databaseManager.closeConnection();
    }

    public void onReload() {
        configFile.reload();
        resetServiceFile.reload();
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

    public BungeeConfigFile getResetServiceFile() {
        return this.resetServiceFile;
    }

    public BungeeConfigFile getConfigFile() {
        return configFile;
    }

    public boolean isOnlineMode() {
        return getProxy().getConfig().isOnlineMode();
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
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}
