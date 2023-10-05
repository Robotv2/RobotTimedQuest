package fr.robotv2.bukkit.quest.custom.example;

import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.quest.custom.CustomType;
import fr.robotv2.bukkit.quest.requirements.QuestRequirement;

public class JumpCustomType implements CustomType {

    @Override
    public String getCustomTypeName() {
        return "JUMP";
    }

    @Override
    public boolean isNumerical() {
        return true;
    }

    @Override
    public QuestRequirement<?> toQuestRequirement(Quest quest) {
        return new BiomeRequirement(quest);
    }
}
