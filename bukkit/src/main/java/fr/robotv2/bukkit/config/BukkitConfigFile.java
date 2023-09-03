package fr.robotv2.bukkit.config;

import fr.robotv2.common.config.ConfigFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class BukkitConfigFile implements ConfigFile<YamlConfiguration> {

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

        try {
            save();
            plugin.getLogger().info("File " + fileName + " has been updated to newest version.");
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, "Could not save updated configuration", exception);
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
}
