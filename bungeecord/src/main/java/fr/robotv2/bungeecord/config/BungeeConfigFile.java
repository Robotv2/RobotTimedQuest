package fr.robotv2.bungeecord.config;

import fr.robotv2.common.config.ConfigFile;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class BungeeConfigFile implements ConfigFile<Configuration> {

    private final Plugin plugin;
    private final String fileName;

    private File file;
    private Configuration configuration;

    public BungeeConfigFile(Plugin plugin, String fileName, boolean setup) {
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
            try(InputStream inputStream = plugin.getResourceAsStream(fileName)) {
                Files.copy(inputStream, file.toPath());
                this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void save() throws IOException {

        if(file == null) {
            this.file = new File(plugin.getDataFolder(), fileName);
        }

        ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
    }

    @Override
    public void reload() {

        if(this.file == null) {
            file = new File(plugin.getDataFolder(), fileName);
        }

        try {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Configuration getConfiguration() {

        if(this.configuration == null) {
            reload();
        }

        return this.configuration;
    }
}
