package fr.robotv2.bukkit.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Objects;

public class Options {

    public static boolean DEBUG;

    public static boolean DISABLE_SPAWNERS_PROGRESSION;
    public static boolean DISABLE_BLOCK_MARKING_FROM_CREATIVE;
    public static boolean DISABLE_ITEMS_FROM_PLACED_BLOCK;
    public static boolean DISABLE_BLOCK_BREAK_DECREASE;

    public static boolean BUNGEECORD_MODE;

    public static void load(YamlConfiguration configuration) {
        final ConfigurationSection section = configuration.getConfigurationSection("options");
        Objects.requireNonNull(section, "options section");

        DEBUG = section.getBoolean("debug", false);

        DISABLE_SPAWNERS_PROGRESSION = section.getBoolean("anti-dupe.disable_spawners_progression", false);
        DISABLE_BLOCK_MARKING_FROM_CREATIVE = section.getBoolean("anti-dupe.disable_block_marking_from_creative", false);
        DISABLE_ITEMS_FROM_PLACED_BLOCK = section.getBoolean("anti-dupe.disable_items_from_placed_block", false);
        DISABLE_BLOCK_BREAK_DECREASE = section.getBoolean("anti-dupe.disable_block_break_decrease");

        BUNGEECORD_MODE = section.getBoolean("bungeecord.enabled", false);
    }
}
