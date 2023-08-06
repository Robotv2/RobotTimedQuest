package fr.robotv2.bukkit;

import co.aikar.commands.BukkitCommandIssuer;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.util.text.ProgressUtil;
import fr.robotv2.common.data.impl.ActiveQuest;
import fr.robotv2.common.data.impl.QuestPlayer;
import fr.robotv2.placeholderannotation.BasePlaceholderExpansion;
import fr.robotv2.placeholderannotation.PlaceholderAnnotationProcessor;
import fr.robotv2.placeholderannotation.RequestIssuer;
import fr.robotv2.placeholderannotation.annotations.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

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

    private QuestPlayer fromIssuer(RequestIssuer issuer) {

        if(!issuer.isOnlinePlayer()) {
            return null;
        }

        final Player player = Objects.requireNonNull(issuer.getOnlinePlayer());
        return QuestPlayer.getQuestPlayer(player.getUniqueId());
    }

    @Placeholder(identifier = "done")
    public String onDone(RequestIssuer issuer) {

        final QuestPlayer questPlayer = this.fromIssuer(issuer);

        if(questPlayer == null) {
            return null;
        }

        return String.valueOf(
                questPlayer.getActiveQuests().stream()
                        .filter(ActiveQuest::isDone)
                        .count()
        );
    }

    @Placeholder(identifier = "quest")
    public String onQuest(RequestIssuer issuer, String serviceId, Integer index, String param) {

        final QuestPlayer questPlayer = this.fromIssuer(issuer);
        if(questPlayer == null) return null;

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
}
