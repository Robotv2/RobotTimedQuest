package fr.robotv2.bukkit.quest.requirements;

import fr.robotv2.bukkit.quest.Quest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class QuestRequirements {

    private static final Map<Class<?>, Function<Quest, QuestRequirement<?>>> functions = new HashMap<>();

    static {
        registerRequirement(Material.class, MaterialQuestRequirement::new);
        registerRequirement(EntityType.class, EntityQuestRequirement::new);
        registerRequirement(Location.class, LocationQuestRequirement::new);
    }

    private static void registerRequirement(Class<?> clazz, Function<Quest, QuestRequirement<?>> function) {
        functions.put(clazz, function);
    }

    @Nullable
    public static QuestRequirement<?> toQuestRequirement(@NotNull Class<?> clazz, @NotNull Quest quest) {
        if(!functions.containsKey(clazz)) {
            throw new IllegalArgumentException(clazz.getSimpleName() + " is not a valid quest requirement class. Please contact the developer.");
        }
        return functions.get(clazz).apply(quest);
    }
}
