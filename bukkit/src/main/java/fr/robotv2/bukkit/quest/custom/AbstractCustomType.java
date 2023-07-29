package fr.robotv2.bukkit.quest.custom;

import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.quest.requirements.QuestRequirement;

public abstract class AbstractCustomType {
    public abstract boolean isNumerical();
    public abstract QuestRequirement<?> toQuestRequirement(Quest quest);
}
