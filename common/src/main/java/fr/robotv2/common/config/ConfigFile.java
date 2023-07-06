package fr.robotv2.common.config;

import java.io.IOException;

public interface ConfigFile<T> {
    void setup();
    void save() throws IOException;
    void reload();
    T getConfiguration();
}
