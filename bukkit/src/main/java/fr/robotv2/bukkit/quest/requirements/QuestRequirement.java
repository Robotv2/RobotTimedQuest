package fr.robotv2.bukkit.quest.requirements;

import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.quest.Quest;
import org.jetbrains.annotations.NotNull;

public abstract class QuestRequirement<T> {

    private final Quest quest;
    private final QuestType type;

    public QuestRequirement(Quest quest) {
        this.quest = quest;
        this.type = quest.getType();
    }

    public Quest getQuest() {
        return quest;
    }

    public QuestType getType() {
        return this.type;
    }

    // internal use only.
    public boolean isTarget0(Object object) {

        if(!classGeneric().isAssignableFrom(object.getClass())) {
            return false;
        }

        if(type == null) return false;
        if(type.getRequiredClass() == null) return true;

        return this.isTarget((T) object);
    }

    public abstract Class<? extends T> classGeneric();
    public abstract boolean isTarget(@NotNull T t);
}
