package fr.robotv2.bukkit.quest.custom;

import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.quest.requirements.QuestRequirement;
import org.jetbrains.annotations.Nullable;

public interface CustomType {

    String getCustomTypeName();

    boolean isNumerical();

    @Nullable
    QuestRequirement<?> toQuestRequirement(Quest quest);
}
