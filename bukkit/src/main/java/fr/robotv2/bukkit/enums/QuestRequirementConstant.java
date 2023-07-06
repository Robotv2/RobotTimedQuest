package fr.robotv2.bukkit.enums;

import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.quest.requirements.EntityQuestRequirement;
import fr.robotv2.bukkit.quest.requirements.LocationQuestRequirement;
import fr.robotv2.bukkit.quest.requirements.MaterialQuestRequirement;
import fr.robotv2.bukkit.quest.requirements.QuestRequirement;

import java.util.function.Function;

public enum QuestRequirementConstant {

    MATERIAL_REQUIREMENT(MaterialQuestRequirement::new),
    ENTITY_REQUIREMENT(EntityQuestRequirement::new),
    LOCATION_REQUIREMENT(LocationQuestRequirement::new),
    ;

    private final Function<Quest, QuestRequirement<?>> function;

    QuestRequirementConstant(Function<Quest, QuestRequirement<?>> function) {
        this.function = function;
    }

    public QuestRequirement<?> toInstance(Quest quest) {
        return function.apply(quest);
    }
}
