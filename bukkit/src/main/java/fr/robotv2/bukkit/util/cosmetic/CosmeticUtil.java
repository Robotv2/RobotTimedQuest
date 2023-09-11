package fr.robotv2.bukkit.util.cosmetic;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.UUID;

/**
 * Utility class for handling cosmetics.
 */
public final class CosmeticUtil {

    public enum CosmeticType {
        ACTIONBAR,
        TITLE,
        BOSS_BAR,
    }

    private final Multimap<CosmeticType, UUID> disabled = ArrayListMultimap.create();

    /**
     * Set the status of the cosmetic type for the specified UUID. If value is true, cosmetic type will be disabled.
     * Otherwise, it will be enabled.
     *
     * @param uuid - UUID for which the cosmetic type will be set
     * @param type - type of the cosmetic
     * @param value - true for disabling and false for enabling
     */
    public void setDisabled(UUID uuid, CosmeticType type, boolean value) {
        if(value) {
            disabled.put(type, uuid);
        } else {
            disabled.remove(type, uuid);
        }
    }

    /**
     * Toggles the status of the cosmetic type for the specified UUID.
     *
     * @param uuid - UUID for which the cosmetic type will be toggled
     * @param type - type of the cosmetic
     * @return - returns whether the type is disabled.
     */
    public boolean toggleDisabled(UUID uuid, CosmeticType type) {
        setDisabled(uuid, type, !isDisabled(uuid, type));
        return isDisabled(uuid, type);
    }

    /**
     * Determine if the cosmetic type for the specified UUID is disabled.
     *
     * @param uuid - UUID to check
     * @param type - type of the cosmetic
     * @return - returns true if the cosmetic type is disabled. Otherwise, returns false.
     */
    public boolean isDisabled(UUID uuid, CosmeticType type) {
        return disabled.get(type).contains(uuid);
    }
}
