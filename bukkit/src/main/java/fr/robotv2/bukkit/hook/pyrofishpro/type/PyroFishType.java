package fr.robotv2.bukkit.hook.pyrofishpro.type;

import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.quest.custom.CustomType;
import fr.robotv2.bukkit.quest.requirements.QuestRequirement;
import fr.robotv2.bukkit.quest.requirements.StringQuestRequirement;
import org.jetbrains.annotations.Nullable;

public class PyroFishType implements CustomType {

    @Override
    public String getCustomTypeName() {
        return "PYRO_FISH";
    }

    @Override
    public boolean isNumerical() {
        return true;
    }

    @Override
    public @Nullable QuestRequirement<?> toQuestRequirement(Quest quest) {
        return new StringQuestRequirement(quest);
    }
}
