package fr.robotv2.bukkit.util.comparator;

import org.bukkit.configuration.ConfigurationSection;

public abstract class SectionComparator<T> {

    private final ConfigurationSection parent;

    protected SectionComparator(ConfigurationSection parent) {
        this.parent = parent;
    }

    abstract public boolean isSame(T value);

    public ConfigurationSection getParent() {
        return this.parent;
    }
}
