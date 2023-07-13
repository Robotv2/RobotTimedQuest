package fr.robotv2.bukkit.quest.requirements;

import fr.robotv2.bukkit.quest.Quest;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

public class EntityQuestRequirement extends QuestRequirement<EntityType> {

    private final EnumSet<EntityType> entities = EnumSet.noneOf(EntityType.class);
    private boolean all = false;

    public EntityQuestRequirement(Quest quest) {
        super(quest);

        final List<String> entityStrings = quest.getSection().getStringList("required_targets");

        if(entityStrings.contains("*")) {
            all = true;
            return;
        }

        for(String entityString : entityStrings) {

            final EntityType entityType = EntityType.fromName(entityString);

            if(entityType == null) {
                throw new IllegalArgumentException(String.format("%s is not a valid entity type.", entityString));
            }

            entities.add(entityType);
        }
    }

    @Override
    public Class<? extends EntityType> classGeneric() {
        return EntityType.class;
    }

    @Override
    public boolean isTarget(@NotNull EntityType type) {
        return all || entities.contains(type);
    }
}
