package fr.robotv2.bukkit;

import fr.robotv2.bukkit.bungee.BukkitMessageListener;
import fr.robotv2.bukkit.bungee.BukkitRedisMessenger;
import fr.robotv2.bukkit.command.BukkitMainCommand;
import fr.robotv2.bukkit.config.BukkitConfigFile;
import fr.robotv2.bukkit.data.BukkitDatabaseManager;
import fr.robotv2.bukkit.data.PlayerDataInitListeners;
import fr.robotv2.bukkit.hook.Hooks;
import fr.robotv2.bukkit.listeners.GlitchChecker;
import fr.robotv2.bukkit.listeners.block.BlockBreakListener;
import fr.robotv2.bukkit.listeners.block.BlockPlaceListener;
import fr.robotv2.bukkit.listeners.block.HarvestBlockListener;
import fr.robotv2.bukkit.listeners.entity.*;
import fr.robotv2.bukkit.listeners.item.*;
import fr.robotv2.bukkit.listeners.player.PlayerMoveListener;
import fr.robotv2.bukkit.listeners.quest.QuestDoneListener;
import fr.robotv2.bukkit.listeners.quest.QuestIncrementListener;
import fr.robotv2.bukkit.quest.QuestManager;
import fr.robotv2.bukkit.quest.conditions.ConditionManager;
import fr.robotv2.bukkit.reset.BukkitResetPublisher;
import fr.robotv2.bukkit.reset.BukkitResetServiceRepo;
import fr.robotv2.bukkit.ui.GuiHandler;
import fr.robotv2.common.channel.ChannelConstant;
import fr.robotv2.common.data.DatabaseCredentials;
import fr.robotv2.common.data.DatabaseManager;
import fr.robotv2.common.data.RedisConnector;
import fr.robotv2.common.data.impl.MySqlCredentials;
import fr.robotv2.common.data.impl.SqlLiteCredentials;
import fr.robotv2.common.reset.ResetService;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Logger;

public class RTQBukkitPlugin extends JavaPlugin {

    private boolean bungeecordMode;

    private QuestManager questManager;
    private ConditionManager conditionManager;

    private GuiHandler guiHandler;
    private BukkitCommandHandler commandHandler;

    private GlitchChecker glitchChecker;

    private final BukkitResetServiceRepo resetServiceRepo = new BukkitResetServiceRepo(this);
    private final BukkitResetPublisher resetPublisher = new BukkitResetPublisher(this);

    private BukkitConfigFile configurationFile;
    private BukkitConfigFile resetServiceFile;
    private BukkitConfigFile guiFile;

    private BukkitDatabaseManager databaseManager;
    private DatabaseManager.DatabaseType type;

    private RedisConnector redisConnector;
    private PlayerDataInitListeners playerDataInitListeners;

    public static RTQBukkitPlugin getInstance() {
        return JavaPlugin.getPlugin(RTQBukkitPlugin.class);
    }

    public static Logger getPluginLogger() {
        return getInstance().getLogger();
    }

    @Override
    public void onLoad() {
        this.conditionManager = new ConditionManager(this);
    }

    @Override
    public void onEnable() {

        if(!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
            this.setupDefaultFilesQuest();
        }

        this.conditionManager.closeRegistration();

        this.setupFiles();
        this.setupListeners();

        this.bungeecordMode = getConfig().getBoolean("options.bungeecord.enabled", false);

        if(this.isBungeecordMode()) {
            this.setupBungeeMode();
        }

        this.resetServiceRepo.registerServices();

        this.questManager = new QuestManager(this);
        this.glitchChecker = new GlitchChecker(this);
        this.guiHandler = new GuiHandler(this);

        this.setupDatabase();
        this.setupQuests();
        this.setupCommandHandlers();

        Hooks.loadHooks(this);

        Bukkit.getScheduler().runTaskTimer(this,
                () -> this.databaseManager.savePlayers(true),
                20 * 60 * 2, // Every 2 minutes
                20 * 60 * 2
        );

        //setup metrics
        final int serviceId = 19047;
        new Metrics(this, serviceId);

        printBeautifulMessage();
    }

    @Override
    public void onDisable() {
        this.databaseManager.savePlayers(false).join();
        this.databaseManager.closeConnection();

        if(this.redisConnector != null) {
            this.redisConnector.close();
        }
    }

    public void onReload() {

        getConfigurationFile().reload();
        getResetServiceFile().reload();
        getGuiFile().reload();

        this.setupQuests();

        this.getBukkitResetServiceRepo().registerServices();
    }

    public void debug(String message) {
        if(this.getConfig().getBoolean("options.debug")) {
            getLogger().info("[DEBUG] " + message);
        }
    }

    // BUNGEE

    public boolean isBungeecordMode() {
        return this.bungeecordMode;
    }

    private void setupBungeeMode() {

        this.redisConnector = new RedisConnector(
                getConfig().getString("options.bungeecord.redis_address", "127.0.0.1"),
                getConfig().getInt("options.bungeecord.redis_port", 6379),
                getConfig().getString("options.bungeecord.redis_password")
        );

        this.redisConnector.setMessenger(new BukkitRedisMessenger(this, this.playerDataInitListeners));
        this.redisConnector.subscribe(
                ChannelConstant.RESET_CHANNEL,
                ChannelConstant.IS_SAVED_CHANNEL,
                ChannelConstant.WAIT_SAVING_CHANNEL
        );
        getServer().getMessenger().registerIncomingPluginChannel(this, ChannelConstant.RESET_CHANNEL, new BukkitMessageListener(this));
    }

    // GETTERS

    public BukkitResetServiceRepo getBukkitResetServiceRepo() {
        return this.resetServiceRepo;
    }

    public BukkitResetPublisher getResetPublisher() {
        return this.resetPublisher;
    }

    public BukkitConfigFile getConfigurationFile() {
        return this.configurationFile;
    }

    @Override
    public @NotNull YamlConfiguration getConfig() {
        return this.configurationFile.getConfiguration();
    }

    public BukkitConfigFile getResetServiceFile() {
        return this.resetServiceFile;
    }

    public BukkitConfigFile getGuiFile() {
        return this.guiFile;
    }

    public BukkitDatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    public QuestManager getQuestManager() {
        return this.questManager;
    }

    public ConditionManager getConditionManager() {
        return this.conditionManager;
    }

    public GlitchChecker getGlitchChecker() {
        return this.glitchChecker;
    }

    public GuiHandler getGuiHandler() {
        return this.guiHandler;
    }

    @Nullable
    public RedisConnector getRedisConnector() {
        return this.redisConnector;
    }

    // LOADERS

    private void setupFiles() {
        this.configurationFile = new BukkitConfigFile(this, "bukkit-config.yml", true);
        this.resetServiceFile = new BukkitConfigFile(this, "reset-service.yml", true);
        this.guiFile = new BukkitConfigFile(this, "gui.yml", true);
    }

    private void setupDefaultFilesQuest() {
        new BukkitConfigFile(this, "Qdaily.yml", true);
        new BukkitConfigFile(this, "Qweekly.yml", true);
    }

    private void setupDatabase() {

        DatabaseCredentials credentials = null;
        DatabaseManager.DatabaseType type;

        try {
            type = DatabaseManager.DatabaseType.valueOf(getConfig().getString("storage.mode", "SQLITE").toUpperCase());
        } catch (IllegalArgumentException exception) {
            type = DatabaseManager.DatabaseType.SQLITE;
        }

        if(type == DatabaseManager.DatabaseType.SQLITE && this.isBungeecordMode()) {
            getLogger().warning("If you're using bungeecord, you need to setup MySQL in order for it to work.");
            getLogger().warning("The plugin will automatically switch to MySQL.");
            type = DatabaseManager.DatabaseType.MYSQL;
        }

        switch (type) {
            case SQLITE:
                final File file = new File(getDataFolder(), "database.db");
                credentials = new SqlLiteCredentials(file);
                break;
            case MYSQL:
                final String host = getConfig().getString("storage.mysql-credentials.host");
                final String port = getConfig().getString("storage.mysql-credentials.port");
                final String database = getConfig().getString("storage.mysql-credentials.database");
                final String username = getConfig().getString("storage.mysql-credentials.username");
                final String password = getConfig().getString("storage.mysql-credentials.password");
                final boolean ssl = getConfig().getBoolean("storage.mysql-credentials.useSSL", false);
                credentials = new MySqlCredentials(host, port, database, username, password, ssl);
                break;
        }

        if(credentials == null) {
            throw new NullPointerException("credentials");
        }

        try {
            this.databaseManager = new BukkitDatabaseManager(credentials);
            this.type = type;
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void setupQuests() {
        getQuestManager().clearQuests();
        getConfig().getStringList("quest-files")
                .forEach(questPath -> getQuestManager().loadQuests(questPath));
        getLogger().info(getQuestManager().getQuests().size() + " quest(s) has been loaded.");
    }

    private void setupListeners() {
        final PluginManager pm = getServer().getPluginManager();

        // DATA
        pm.registerEvents((this.playerDataInitListeners = new PlayerDataInitListeners(this)), this);

        // BLOCK
        pm.registerEvents(new BlockBreakListener(this), this);
        pm.registerEvents(new BlockPlaceListener(this), this);
        pm.registerEvents(new HarvestBlockListener(this), this);

        // ENTITY
        pm.registerEvents(new EntityBreedListener(this), this);
        pm.registerEvents(new PlayerFishItemListener(this), this);
        pm.registerEvents(new EntityFishListener(this), this);
        pm.registerEvents(new EntityKillListener(this), this);
        pm.registerEvents(new EntityShearListener(this), this);
        pm.registerEvents(new EntityTameListener(this), this);

        // ITEM
        pm.registerEvents(new PlayerBrewListener(this), this);
        pm.registerEvents(new PlayerConsumeListener(this), this);
        pm.registerEvents(new PlayerCookListener(this), this);
        pm.registerEvents(new PlayerCraftListener(this), this);
        pm.registerEvents(new PlayerEnchantItemListener(this), this);
        pm.registerEvents(new PlayerPickupItemListener(this), this);
        pm.registerEvents(new PlayerProjectileListener(this), this);

        //player
        pm.registerEvents(new PlayerMoveListener(this), this);

        // QUEST
        // pm.registerEvents(new QuestResetListener(this), this);
        pm.registerEvents(new QuestIncrementListener(this), this);
        pm.registerEvents(new QuestDoneListener(this), this);
    }

    private void setupCommandHandlers() {
        this.commandHandler = BukkitCommandHandler.create(this);
        this.commandHandler.registerContextResolver(ResetService.class, (context)
                -> this.getBukkitResetServiceRepo().getService(context.input().get(0)));

        final SuggestionProvider provider = (args, sender, command) -> this.resetServiceRepo.getServicesNames();
        this.commandHandler.getAutoCompleter()
                .registerSuggestion("services", provider);

        this.commandHandler.register(new BukkitMainCommand(this));
    }

    private void printBeautifulMessage() {

        final Logger logger = getLogger();
        final PluginDescriptionFile description = getDescription();

        logger.info("-- RobotTimedQuest --");
        logger.info("");
        logger.info("Author(s): " + String.join(", ", description.getAuthors()));
        logger.info("Version: " + description.getVersion());
        logger.info("");
        logger.info("Database Type: " + type.name());
        logger.info("Bungeecord: " + isBungeecordMode());
        logger.info("");
        logger.info("Thanks you for using RTQ !");
    }
}
