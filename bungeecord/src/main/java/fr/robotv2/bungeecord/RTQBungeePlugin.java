package fr.robotv2.bungeecord;

import fr.robotv2.bungeecord.command.BungeeMainCommand;
import fr.robotv2.bungeecord.config.BungeeConfigFile;
import fr.robotv2.bungeecord.reset.BungeeResetPublisher;
import fr.robotv2.bungeecord.reset.BungeeResetServiceRepo;
import fr.robotv2.common.data.DatabaseCredentials;
import fr.robotv2.common.data.DatabaseManager;
import fr.robotv2.common.data.impl.MySqlCredentials;
import fr.robotv2.common.reset.ResetService;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bungee.BungeeCommandHandler;

import java.sql.SQLException;
import java.util.stream.Collectors;

public class RTQBungeePlugin extends Plugin {

    private DatabaseManager databaseManager;
    private BungeeCommandHandler commandHandler;

    private BungeeResetServiceRepo bungeeResetServiceRepo;
    private BungeeResetPublisher bungeeResetPublisher;

    private BungeeConfigFile configFile;
    private BungeeConfigFile resetServiceFile;

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

        this.commandHandler = BungeeCommandHandler.create(this);
        this.commandHandler.registerContextResolver(ResetService.class, (context)
                -> this.getBungeeResetServiceRepo().getService(context.input().get(0)));
        this.registerPluginSuggestion();
        this.commandHandler.register(new BungeeMainCommand(this));
    }

    @Override
    public void onDisable() {
        if(this.databaseManager != null && this.databaseManager.isConnected()) {
            this.databaseManager.closeConnection();
        }
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
            getLogger().info("Successfully connected to the database.");
        } catch (SQLException exception) {
            getLogger().warning(" ");
            getLogger().warning("An error occurred trying to connect to the database.");
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

    private void registerPluginSuggestion() {
        final SuggestionProvider provider = (args, sender, command) -> this.bungeeResetServiceRepo.getServices().stream().map(ResetService::getId).collect(Collectors.toList());
        this.commandHandler.getAutoCompleter()
                .registerSuggestion("services", provider);
    }
}
