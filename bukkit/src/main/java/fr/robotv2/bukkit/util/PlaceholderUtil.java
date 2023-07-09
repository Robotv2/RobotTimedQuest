package fr.robotv2.bukkit.util;

import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.common.data.impl.ActiveQuest;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlaceholderUtil {

    // UTILITY CLASS

    private PlaceholderUtil() { }

    public static String parsePlaceholders(OfflinePlayer offlinePlayer, String input) {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") ? PlaceholderAPI.setPlaceholders(offlinePlayer, input) : input;
    }

    public static InternalPlaceholder<Quest> QUEST_PLACEHOLDER = ((value, input) -> input
            .replace("%quest_name%", value.getName())
            .replace("%quest_id%", value.getId())
            .replace("%quest_service%", value.getResetId())
            .replace("%quest_type%", value.getType().name())
    );

    public InternalPlaceholder<ActiveQuest> ACTIVE_QUEST_PLACEHOLDER = ((value, input) -> input
            .replace("%quest_done%", String.valueOf(value.isDone()))
    );

    @FunctionalInterface
    public interface InternalPlaceholder<T> {
        String parse(T value, String input);
    }

    @FunctionalInterface
    public interface RelationalInternalPlaceholder<A, B> {
        String parse(A value1, B value2, String input);
    }
}
