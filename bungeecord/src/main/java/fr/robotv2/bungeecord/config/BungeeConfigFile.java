package fr.robotv2.bungeecord.config;

import fr.robotv2.common.config.ConfigFile;
import fr.robotv2.common.config.RConfiguration;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;

public class BungeeConfigFile implements ConfigFile<Configuration>, RConfiguration {

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
        return getConfiguration().get(path) != null ? getConfiguration().getStringList(path) : def;
    }

    @Override
    public Collection<String> getKeys() {
        return getConfiguration().getKeys();
    }

    @Override
    public Collection<String> getKeys(String path) {
        return getConfiguration().getSection(path).getKeys();
    }
}
