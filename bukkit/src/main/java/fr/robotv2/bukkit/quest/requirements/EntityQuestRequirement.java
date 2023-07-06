package fr.robotv2.bukkit.quest.requirements;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.quest.Quest;
import org.bukkit.ChatColor;
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
                RTQBukkitPlugin.getInstance().getLogger().warning(ChatColor.RED + entityString + " isn't a valid entity type name.");
                continue;
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
