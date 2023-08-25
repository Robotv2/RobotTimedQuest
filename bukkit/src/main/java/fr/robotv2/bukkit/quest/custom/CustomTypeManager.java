package fr.robotv2.bukkit.quest.custom;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CustomTypeManager {

    private final Map<String, CustomType> customTypes = new HashMap<>();

    public void registerCustomType(String name, CustomType customType) {
        this.customTypes.put(name.toUpperCase(Locale.ROOT), customType);
    }

    public boolean isCustomType(String name) {
        return customTypes.containsKey(name.toUpperCase(Locale.ROOT));
    }

    public CustomType getCustomType(String name) {
        if(name == null) return null;
        return customTypes.get(name.toUpperCase(Locale.ROOT));
    }
}
