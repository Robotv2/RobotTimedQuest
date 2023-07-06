package fr.robotv2.common.data.impl;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class QuestPlayer {

    private static final Map<UUID, QuestPlayer> questPlayers = new HashMap<>();

    private final UUID uuid;
    private final List<ActiveQuest> activeQuests = new CopyOnWriteArrayList<>();

    public QuestPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    @Nullable
    public static QuestPlayer getQuestPlayer(UUID uuid) {
        return questPlayers.get(uuid);
    }

    @UnmodifiableView
    public static Collection<QuestPlayer> getRegistered() {
        return Collections.unmodifiableCollection(questPlayers.values());
    }

    public static void registerQuestPlayer(QuestPlayer questPlayer) {
        questPlayers.put(questPlayer.getUniqueId(), questPlayer);
    }

    public static void unregisterQuestPlayer(QuestPlayer questPlayer) {
        questPlayers.remove(questPlayer.getUniqueId());
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public void clearActiveQuests() {
        activeQuests.clear();
    }

    @UnmodifiableView
    public List<ActiveQuest> getActiveQuests() {
        return Collections.unmodifiableList(this.activeQuests);
    }

    @UnmodifiableView
    public List<ActiveQuest> getActiveQuests(String resetId) {
        return Collections.unmodifiableList(getActiveQuests().stream()
                .filter(quest -> quest.getResetId().equals(resetId))
                .collect(Collectors.toList()));
    }

    public void addActiveQuests(Collection<ActiveQuest> activeQuests) {
        this.activeQuests.addAll(activeQuests);
    }

    public void addActiveQuest(ActiveQuest activeQuest) {
        this.activeQuests.add(activeQuest);
    }

    public void removeActiveQuest(String resetId) {
        this.activeQuests.removeIf(quest -> quest.getResetId().equals(resetId));
    }

    public boolean hasQuest(String questId) {
        return getActiveQuests().stream()
                .anyMatch(quest -> quest.getQuestId().equals(questId));
    }
}
