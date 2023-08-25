package fr.robotv2.bukkit.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Objects;

public class Options {

    public static boolean DEBUG;

    public static int PLAYER_MOVE_EVENT_THRESHOLD;

    public static boolean DISABLE_SPAWNERS_PROGRESSION;
    public static boolean COUNT_BLOCK_FROM_CREATIVE;
    public static boolean COUNT_ITEMS_FROM_PLACED_BLOCK;
    public static boolean COUNT_BREAKING_PLACED_BLOCK;

    public static boolean BUNGEECORD_MODE;

    public static void load(YamlConfiguration configuration) {
        final ConfigurationSection section = configuration.getConfigurationSection("options");
        Objects.requireNonNull(section, "options' section");

        DEBUG = section.getBoolean("debug", false);

        PLAYER_MOVE_EVENT_THRESHOLD = section.getInt("player_move_event_threshold", 5);

        DISABLE_SPAWNERS_PROGRESSION = section.getBoolean("anti-dupe.disable_spawners_progression", false);
        COUNT_BLOCK_FROM_CREATIVE = section.getBoolean("anti-dupe.count_block_from_creative", false);
        COUNT_ITEMS_FROM_PLACED_BLOCK = section.getBoolean("anti-dupe.count_items_from_placed_block", false);
        COUNT_BREAKING_PLACED_BLOCK = section.getBoolean("anti-dupe.count_breaking_placed_block", false);

        BUNGEECORD_MODE = section.getBoolean("bungeecord.enabled", false);
    }
}
