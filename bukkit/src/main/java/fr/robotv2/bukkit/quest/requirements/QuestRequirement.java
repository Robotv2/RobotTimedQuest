package fr.robotv2.bukkit.quest.requirements;

import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.quest.Quest;
import org.jetbrains.annotations.NotNull;

public abstract class QuestRequirement<T> {

    private final Quest quest;
    private final QuestType type;
    private final int amount;

    public QuestRequirement(Quest quest) {
        this.quest = quest;
        this.type = quest.getType();
        this.amount = quest.isNumerical() ? quest.getSection().getInt("required_amount") : 1;
    }

    public Quest getQuest() {
        return quest;
    }

    public QuestType getType() {
        return this.type;
    }

    public int getRequiredAmount() {
        return this.amount;
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
