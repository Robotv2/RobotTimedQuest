package fr.robotv2.common.config;

import java.io.IOException;

public interface ConfigFile<T> extends RConfiguration {
    void setup();
    void save() throws IOException;
    void reload();
    T getConfiguration();
}
