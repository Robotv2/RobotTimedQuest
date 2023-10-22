package fr.robotv2.common.config;

import java.util.Collection;
import java.util.List;

public interface RConfiguration {

    String getString(String path);

    int getInt(String path);

    double getDouble(String path);

    long getLong(String path);

    boolean getBoolean(String path);

    List<String> getStringList(String path);

    String getString(String path, String def);

    int getInt(String path, int def);

    double getDouble(String path, double def);

    long getLong(String path, long def);

    boolean getBoolean(String path, boolean def);

    List<String> getStringList(String path, List<String> def);

    Collection<String> getKeys();

    Collection<String> getKeys(String path);
}
