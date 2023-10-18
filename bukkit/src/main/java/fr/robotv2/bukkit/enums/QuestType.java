package fr.robotv2.bukkit.enums;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum QuestType {

    BREAK(true, Material.class),
    PLACE(true, Material.class),
    FARMING(true, Material.class),
    FISH(true, EntityType.class),
    FISH_ITEM(true, Material.class),
    BREED(true, EntityType.class),
    KILL(true, EntityType.class),
    SHEAR(true, EntityType.class),
    TAME(true, EntityType.class),
    CONSUME(true, Material.class),
    COOK(true, Material.class),
    CRAFT(true, Material.class),
    ENCHANT(true, Material.class),
    PICKUP(true, Material.class),
    BREW(true, Material.class),
    LAUNCH(true, EntityType.class),
    LOCATION(false, Location.class),
    VILLAGER_TRADE(true, Material.class),
    MILKING(true, EntityType.class),

    GATHER_ITEM(true, Material.class),
    CARVE(true, Material.class),
    PLAYER_DEATH(true, String.class),
    WALK(true, null),
    SWIM(true, null),
    STAY_ONLINE(true, null),

    $EXPLORE(true, Biome.class),
    CUSTOM(false, null),
    ;

    public static final QuestType[] VALUES = QuestType.values();
    private static final Map<String, QuestType> BY_NAME = new HashMap<>();

    private final boolean isNumerical;
    private final Class<?> requiredClazz;

    static {
        for(QuestType questType : VALUES) {
            BY_NAME.put(questType.name(), questType);
        }
    }

    QuestType(boolean isNumerical, Class<?> requiredClazz) {
        this.isNumerical = isNumerical;
        this.requiredClazz = requiredClazz;
    }

    public boolean isNumerical() {
        return this.isNumerical;
    }

    @Nullable
    public Class<?> getRequiredClass() {
        return this.requiredClazz;
    }

    @Nullable
    public static QuestType getByName(@Nullable String questType) {
        if(questType == null) return null;
        return BY_NAME.get(questType.toUpperCase(Locale.ROOT));
    }
}
