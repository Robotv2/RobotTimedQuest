package fr.robotv2.bukkit.enums;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum QuestType {

    BREAK(true, QuestRequirementConstant.MATERIAL_REQUIREMENT), // DONE
    PLACE(true, QuestRequirementConstant.MATERIAL_REQUIREMENT), // DONE
    FARMING(true, QuestRequirementConstant.MATERIAL_REQUIREMENT), // DONE

    FISH(true, QuestRequirementConstant.ENTITY_REQUIREMENT), // DONE
    FISH_ITEM(true, QuestRequirementConstant.MATERIAL_REQUIREMENT), // DONE

    BREED(true, QuestRequirementConstant.ENTITY_REQUIREMENT), // DONE
    KILL(true, QuestRequirementConstant.ENTITY_REQUIREMENT), // DONE
    SHEAR(true, QuestRequirementConstant.ENTITY_REQUIREMENT), // DONE
    TAME(true, QuestRequirementConstant.ENTITY_REQUIREMENT), // DONE

    CONSUME(true, QuestRequirementConstant.MATERIAL_REQUIREMENT), // DONE
    COOK(true, QuestRequirementConstant.MATERIAL_REQUIREMENT), // DONE
    CRAFT(true, QuestRequirementConstant.MATERIAL_REQUIREMENT), // DONE
    ENCHANT(true, QuestRequirementConstant.MATERIAL_REQUIREMENT), // DONE
    PICKUP(true, QuestRequirementConstant.MATERIAL_REQUIREMENT), // DONE

    LAUNCH(true, QuestRequirementConstant.MATERIAL_REQUIREMENT), // DONE
    EXP_POINTS(true, null), // NOT DONE

    LOCATION(false, QuestRequirementConstant.LOCATION_REQUIREMENT), // DONE
    ;

    public static final QuestType[] VALUES = QuestType.values();
    private static final Map<String, QuestType> BY_NAME = new HashMap<>();

    private final boolean isNumerical;
    private final QuestRequirementConstant questRequirementConstant;

    static {
        for(QuestType questType : VALUES) {
            BY_NAME.put(questType.name(), questType);
        }
    }

    QuestType(boolean isNumerical, QuestRequirementConstant constant) {
        this.isNumerical = isNumerical;
        this.questRequirementConstant = constant;
    }

    public boolean isNumerical() {
        return this.isNumerical;
    }

    public QuestRequirementConstant getQuestRequirementConstant() {
        return this.questRequirementConstant;
    }

    @Nullable
    public static QuestType getByName(@Nullable String questType) {
        if(questType == null) return null;
        return BY_NAME.get(questType.toUpperCase());
    }
}
