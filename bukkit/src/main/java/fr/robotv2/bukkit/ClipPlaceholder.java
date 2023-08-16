package fr.robotv2.bukkit;

import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.util.DateUtil;
import fr.robotv2.bukkit.util.text.ProgressUtil;
import fr.robotv2.common.data.impl.ActiveQuest;
import fr.robotv2.common.data.impl.QuestPlayer;
import fr.robotv2.common.reset.ResetService;
import fr.robotv2.placeholderannotation.BasePlaceholderExpansion;
import fr.robotv2.placeholderannotation.PlaceholderAnnotationProcessor;
import fr.robotv2.placeholderannotation.RequestIssuer;
import fr.robotv2.placeholderannotation.annotations.DefaultPlaceholder;
import fr.robotv2.placeholderannotation.annotations.Optional;
import fr.robotv2.placeholderannotation.annotations.Placeholder;
import fr.robotv2.placeholderannotation.annotations.RequireOnlinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class ClipPlaceholder extends BasePlaceholderExpansion {

    private final RTQBukkitPlugin plugin;

    public ClipPlaceholder(RTQBukkitPlugin plugin, PlaceholderAnnotationProcessor processor) {
        super(processor);
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "robottimedquest";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @RequireOnlinePlayer
    @Placeholder(identifier = "done")
    public String onDone(RequestIssuer issuer) {
        final QuestPlayer questPlayer = QuestPlayer.getQuestPlayer(issuer.getPlayer().getUniqueId());
        return String.valueOf(
                questPlayer.getActiveQuests()
                        .stream()
                        .filter(ActiveQuest::isDone)
                        .count()
        );
    }

    @DefaultPlaceholder
    @Placeholder(identifier = "quest")
    @RequireOnlinePlayer
    public String onQuest(RequestIssuer issuer, String serviceId, Integer index, String param) {

        index = index - 1;

        final QuestPlayer questPlayer = QuestPlayer.getQuestPlayer(issuer.getPlayer().getUniqueId());
        final List<ActiveQuest> activeQuests = questPlayer.getActiveQuests(serviceId);

        if(activeQuests.size() <= index) {
            return null;
        }

        final ActiveQuest activeQuest = activeQuests.get(index);
        final Quest quest = plugin.getQuestManager().fromId(activeQuest.getQuestId());

        switch (param.toLowerCase(Locale.ROOT)) {
            case "display":
                return quest.getDisplay();
            case "type":
                return quest.getType().name().toLowerCase(Locale.ROOT);
            case "progress":
                return String.valueOf(activeQuest.getProgress());
            case "required":
                return String.valueOf(quest.getRequiredAmount());
            case "progressbar":
                return ProgressUtil.getProcessBar(activeQuest.getProgress(), quest.getRequiredAmount());
            default:
                return null;
        }
    }

    // %robottimedquest_time_DAILY_until%
    @Placeholder(identifier = "time")
    public String onTime(String resetId, String param) {
        final ResetService service = plugin.getBukkitResetServiceRepo().getService(resetId.toUpperCase());

        if(service == null) {
            return null;
        }

        switch (param.toLowerCase(Locale.ROOT)) {
            case "reset":
                return DateUtil.getDateFormatted(service);
            case "until":
                return DateUtil.getTimeUntilFormatted(service);
            default:
                return null;
        }
    }
}
