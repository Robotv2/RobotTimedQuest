package fr.robotv2.bukkit.bungee;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.data.PlayerDataInitListeners;
import fr.robotv2.common.channel.ChannelConstant;
import fr.robotv2.common.data.RedisConnector;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BukkitRedisMessenger implements RedisConnector.AbstractMessenger {

    private final RTQBukkitPlugin plugin;
    private final PlayerDataInitListeners playerDataInitListeners;

    public BukkitRedisMessenger(RTQBukkitPlugin plugin, PlayerDataInitListeners playerDataInitListeners) {
        this.plugin = plugin;
        this.playerDataInitListeners = playerDataInitListeners;
    }

    private String bytesToString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private UUID bytesToUUID(byte[] bytes) {
        return UUID.fromString(bytesToString(bytes));
    }

    @Override
    public void registerIncomingMessage(String channel, byte[] bytes) {

        switch (channel) {

            case ChannelConstant.WAIT_SAVING_CHANNEL: {
                final UUID uuid = bytesToUUID(bytes);
                playerDataInitListeners.setNeedSaving(uuid, true);
                break;
            }

            case ChannelConstant.IS_SAVED_CHANNEL: {
                final UUID uuid = bytesToUUID(bytes);
                playerDataInitListeners.setNeedSaving(uuid, false);
                break;
            }

            case ChannelConstant.RESET_CHANNEL: {
                plugin.getResetPublisher().publishReset(this.bytesToString(bytes));
                break;
            }
        }
    }
}
