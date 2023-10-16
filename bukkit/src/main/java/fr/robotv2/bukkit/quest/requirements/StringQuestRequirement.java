package fr.robotv2.bukkit.quest.requirements;

// Lambda quest requirement which may be useful for some custom types
// used by: PyroFishType

import fr.robotv2.bukkit.quest.Quest;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class StringQuestRequirement extends QuestRequirement<String> {

    private final List<String> targetList;
    private final boolean isAllTargets;
    private final boolean isCaseSensitive;

    public StringQuestRequirement(Quest quest, boolean isCaseSensitive) {
        super(quest);

        this.targetList = quest.getSection()
                .getStringList("required_targets")
                .stream()
                .map(target -> isCaseSensitive ? target : target.toUpperCase())
                .collect(Collectors.toList());

        this.isAllTargets = targetList.contains("*");
        this.isCaseSensitive = isCaseSensitive;
    }

    @Override
    public Class<? extends String> classGeneric() {
        return String.class;
    }

    @Override
    public boolean isTarget(@NotNull String target) {
        return isAllTargets ||
                targetList.contains(isCaseSensitive ? target : target.toUpperCase());
    }
}
