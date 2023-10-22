package fr.robotv2.bukkit.config;

import fr.robotv2.common.config.ConfigFile;
import fr.robotv2.common.config.RConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class BukkitConfigFile implements ConfigFile<YamlConfiguration>, RConfiguration {

    private final Plugin plugin;
    private final String fileName;

    private File file;
    private YamlConfiguration configuration;

    public BukkitConfigFile(Plugin plugin, String fileName, boolean setup) {
        this.plugin = plugin;
        this.fileName = fileName;
        if(setup) {
            this.setup();
        }
    }

    @Override
    public void setup() {

        if(file == null) {
            this.file = new File(plugin.getDataFolder(), fileName);
        }

        if(!file.exists()) {
            this.plugin.saveResource(fileName, false);
        }
    }

    @Override
    public void save() throws IOException {

        if(file == null || configuration == null) {
            return;
        }

        this.configuration.save(this.file);
    }

    @Override
    public YamlConfiguration getConfiguration() {

        if(this.configuration == null) {
            reload();
        }

        return this.configuration;
    }

    @Override
    public void reload() {

        if(this.file == null) {
            file = new File(plugin.getDataFolder(), fileName);
        }

        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        final InputStream defaultStream = plugin.getResource(fileName);

        if(defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.configuration.setDefaults(defaultConfig);
        }
    }

    public void updator(FileUpdator updator) {

        final YamlConfiguration configuration = this.getConfiguration();

        if(updator.reason(configuration)) {

            updator.update(configuration);

            try {
                save();
                plugin.getLogger().info("File " + fileName + " has been added new keys.");
            } catch (IOException exception) {
                plugin.getLogger().log(Level.SEVERE, "Could not save updated configuration", exception);
            }
        }
    }

    public void updateConfig() {

        if(!this.file.exists()) {
            setup();
            return;
        }

        final YamlConfiguration configuration = this.getConfiguration();
        InputStream defaultFileStream = plugin.getResource(this.fileName);

        if(defaultFileStream == null) {
            return;
        }

        final YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultFileStream));
        final boolean modified = this.mergeConfigs(defaultConfig, configuration);

        if(modified) {
            try {
                save();
                plugin.getLogger().info("File " + fileName + " has been updated to newest version.");
            } catch (IOException exception) {
                plugin.getLogger().log(Level.SEVERE, "Could not save updated configuration", exception);
            }
        }
    }

    private boolean mergeConfigs(FileConfiguration source, FileConfiguration target) {
        boolean modified = false;

        for (String key : source.getKeys(true)) {

            if (!target.isSet(key)) {
                target.set(key, source.get(key));
                modified = true;
            }
        }

        return modified;
    }

    @Override
    public String getString(String path) {
        return getConfiguration().getString(path);
    }

    @Override
    public int getInt(String path) {
        return getConfiguration().getInt(path);
    }

    @Override
    public double getDouble(String path) {
        return getConfiguration().getDouble(path);
    }

    @Override
    public long getLong(String path) {
        return getConfiguration().getLong(path);
    }

    @Override
    public boolean getBoolean(String path) {
        return getConfiguration().getBoolean(path);
    }

    @Override
    public List<String> getStringList(String path) {
        return getConfiguration().getStringList(path);
    }

    @Override
    public String getString(String path, String def) {
        return getConfiguration().getString(path, def);
    }

    @Override
    public int getInt(String path, int def) {
        return getConfiguration().getInt(path, def);
    }

    @Override
    public double getDouble(String path, double def) {
        return getConfiguration().getDouble(path, def);
    }

    @Override
    public long getLong(String path, long def) {
        return getConfiguration().getLong(path, def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return getConfiguration().getBoolean(path, def);
    }

    @Override
    public List<String> getStringList(String path, List<String> def) {
        return getConfiguration().isSet(path) ? getConfiguration().getStringList(path) : def;
    }

    @Override
    public Collection<String> getKeys() {
        return getConfiguration().getKeys(false);
    }

    @Override
    public Collection<String> getKeys(String path) {
        final ConfigurationSection section = getConfiguration().getConfigurationSection(path);
        return section != null ? section.getKeys(false) : Collections.emptyList();
    }
}
