package fr.robotv2.bukkit.quest.requirements;

import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.quest.Quest;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public abstract class QuestRequirement<T> {

    private static final Map<Class<?>, Function<Quest, ? extends QuestRequirement<?>>> classToFunctionMap = createClassToFunctionMap();

    private static Map<Class<?>, Function<Quest, ? extends QuestRequirement<?>>> createClassToFunctionMap() {
        Map<Class<?>, Function<Quest, ? extends QuestRequirement<?>>> map = new HashMap<>();
        map.put(MaterialQuestRequirement.class, MaterialQuestRequirement::new);
        map.put(EntityQuestRequirement.class, EntityQuestRequirement::new);
        map.put(LocationQuestRequirement.class, LocationQuestRequirement::new);
        return map;
    }

    public static Optional<QuestRequirement<?>> toClassInstance(Class<? extends QuestRequirement<?>> questRequirementClazz, Quest quest) {
        final Function<Quest, ? extends QuestRequirement<?>> questFunction = classToFunctionMap.get(questRequirementClazz);

        if(questFunction == null) {
            return Optional.empty();
        }

        return Optional.of(questFunction.apply(quest));
    }

    private final Quest quest;
    private final QuestType type;
    private final int amount;

    public QuestRequirement(Quest quest) {
        this.quest = quest;
        this.type = quest.getType();
        this.amount = this.type.isNumerical() ? 1 : quest.getSection().getInt("required_amount");
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
        if(type.getQuestRequirementConstant() == null) return true;

        return this.isTarget((T) object);
    }

    public abstract Class<? extends T> classGeneric();
    public abstract boolean isTarget(@NotNull T t);
}
