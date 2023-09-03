package fr.robotv2.bukkit.config;

import org.bukkit.configuration.file.YamlConfiguration;

public interface FileUpdator {
    boolean reason(YamlConfiguration configuration);
    void update(YamlConfiguration configuration);
}
