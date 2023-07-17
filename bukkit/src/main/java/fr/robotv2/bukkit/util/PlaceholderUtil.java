package fr.robotv2.bukkit.util;

import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.common.data.impl.ActiveQuest;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceholderUtil {

    // UTILITY CLASS

    private PlaceholderUtil() { }

    public static String parsePlaceholders(OfflinePlayer offlinePlayer, String input) {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") ? PlaceholderAPI.setPlaceholders(offlinePlayer, input) : input;
    }

    public static InternalPlaceholder<Player> PLAYER_PLACEHOLDER = ((value, input) -> input
            .replace("%player%", value.getName())
    );

    public static InternalPlaceholder<Quest> QUEST_PLACEHOLDER = ((value, input) -> input
            .replace("%quest_display%", value.getDisplay())
            .replace("%quest_id%", value.getId())
            .replace("%quest_service%", value.getResetId())
            .replace("%quest_type%", value.getType().name())
            .replace("%quest_required%", String.valueOf(value.getRequiredAmount()))
    );

    public static InternalPlaceholder<ActiveQuest> ACTIVE_QUEST_PLACEHOLDER = ((value, input) -> input
            .replace("%quest_done%", String.valueOf(value.isDone()))
            .replace("%quest_progression%", String.valueOf(value.getProgress()))
    );

    public static RelationalInternalPlaceholder<Quest, ActiveQuest> ACTIVE_QUEST_RELATIONAL_PLACEHOLDER = ((value1, value2, input) -> input
            .replace("%quest_progressbar%", ProgressUtil.getProcessBar(value2.getProgress(), value1.getRequiredAmount()))
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
