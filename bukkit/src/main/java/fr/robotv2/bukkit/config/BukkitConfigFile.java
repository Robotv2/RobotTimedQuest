package fr.robotv2.bukkit.config;

import fr.robotv2.common.config.ConfigFile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
}
