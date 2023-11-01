package fr.robotv2.velocity.config;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import fr.robotv2.common.config.ConfigFile;
import fr.robotv2.velocity.RTQVelocityPlugin;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class VelocityConfigFile implements ConfigFile<YamlDocument> {

    @Inject
    private ProxyServer server;

    @Inject
    private Logger logger;

    private final RTQVelocityPlugin plugin;
    private final String fileName;

    private File file;
    private YamlDocument document;

    public VelocityConfigFile(RTQVelocityPlugin plugin, String fileName, boolean setup) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.file = new File(plugin.getDataDirectory().toFile(), fileName);
        if(setup) {
            setup();
        }
    }

    @Override
    public void setup() {
        if(file == null) {
            this.file = new File(plugin.getDataDirectory().toFile(), fileName);
        }

        try {

            if(!file.exists()) {
                try(InputStream inputStream = plugin.getClass().getResourceAsStream("/" + fileName)) {
                    if(inputStream != null) {
                        Files.copy(inputStream, file.toPath());
                    }
                }
            }

            try (InputStream inputStream = plugin.getClass().getResourceAsStream("/" + fileName);
                 InputStream fileStream = Files.newInputStream(file.toPath())){

                this.document = YamlDocument.create(
                        fileStream,
                        inputStream,
                        GeneralSettings.DEFAULT,
                        LoaderSettings.builder().setAutoUpdate(true).build(),
                        DumperSettings.DEFAULT,
                        UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version"))
                                .setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS).build()
                );
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save() throws IOException {
        document.save(file);
    }

    @Override
    public void reload() {
        try {
            document.reload();
        } catch (IOException exception) {
            logger.error("An error occurred while reloading file '" + fileName + "'", exception);
        }
    }

    @Override
    public YamlDocument getConfiguration() {
        return document;
    }

    @Override
    public String getString(String path) {
        return document.getString(path);
    }

    @Override
    public int getInt(String path) {
        return document.getInt(path);
    }

    @Override
    public double getDouble(String path) {
        return document.getDouble(path);
    }

    @Override
    public long getLong(String path) {
        return document.getLong(path);
    }

    @Override
    public boolean getBoolean(String path) {
        return document.getBoolean(path);
    }

    @Override
    public List<String> getStringList(String path) {
        return document.getStringList(path);
    }

    @Override
    public String getString(String path, String def) {
        return document.getString(path, def);
    }

    @Override
    public int getInt(String path, int def) {
        return document.getInt(path, def);
    }

    @Override
    public double getDouble(String path, double def) {
        return document.getDouble(path, def);
    }

    @Override
    public long getLong(String path, long def) {
        return document.getLong(path, def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return document.getBoolean(path, def);
    }

    @Override
    public List<String> getStringList(String path, List<String> def) {
        return document.getStringList(path, def);
    }

    @Override
    public Collection<String> getKeys() {
        return document.getRoutesAsStrings(false);

    }

    @Override
    public Collection<String> getKeys(String path) {
        final Section section = document.getSection(path);
        return section != null ? section.getRoutesAsStrings(false) : Collections.emptyList();
    }
}
