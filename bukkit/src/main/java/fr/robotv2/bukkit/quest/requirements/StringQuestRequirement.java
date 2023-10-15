package fr.robotv2.bukkit.quest.requirements;

// Lambda quest requirement which may be useful for some custom types
// used by: PyroFishType

import fr.robotv2.bukkit.quest.Quest;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StringQuestRequirement extends QuestRequirement<String> {

    private final List<String> targets;
    private final boolean all;

    public StringQuestRequirement(Quest quest) {
        super(quest);
        this.targets = quest.getSection().getStringList("required_targets");
        this.all = targets.contains("*");
    }

    @Override
    public Class<? extends String> classGeneric() {
        return String.class;
    }

    @Override
    public boolean isTarget(@NotNull String target) {
        return all || targets.contains(target);
    }
}
