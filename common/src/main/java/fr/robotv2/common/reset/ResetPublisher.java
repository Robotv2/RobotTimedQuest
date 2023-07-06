package fr.robotv2.common.reset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ResetPublisher {
    void publishReset(@NotNull String resetId);
    void reset(@NotNull UUID ownerUniqueId, @Nullable String resetId);
}
