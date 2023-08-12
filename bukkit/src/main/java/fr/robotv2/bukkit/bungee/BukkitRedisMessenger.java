package fr.robotv2.bukkit.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.data.PlayerDataInitListeners;
import fr.robotv2.common.channel.ChannelConstant;
import fr.robotv2.common.data.RedisConnector;
import fr.robotv2.common.data.impl.QuestPlayer;

import java.util.Locale;
import java.util.UUID;

public class BukkitRedisMessenger implements RedisConnector.AbstractMessenger {

    private final RTQBukkitPlugin plugin;
    private final PlayerDataInitListeners playerDataInitListeners;

    public BukkitRedisMessenger(RTQBukkitPlugin plugin, PlayerDataInitListeners playerDataInitListeners) {
        this.plugin = plugin;
        this.playerDataInitListeners = playerDataInitListeners;
    }

    @Override
    public void registerIncomingMessage(String channel, byte[] bytes) {

        final ByteArrayDataInput input = ByteStreams.newDataInput(bytes);

        if(!channel.equals(ChannelConstant.BUKKIT_CHANNEL)) {
            return;
        }

        final String sub = input.readUTF();

        switch (sub.toUpperCase(Locale.ROOT)) {

            case "WAIT_SAVING": {
                final UUID uuid = UUID.fromString(input.readUTF());
                playerDataInitListeners.setNeedSaving(uuid, true);
                break;
            }

            case "IS_SAVED": {
                final UUID uuid = UUID.fromString(input.readUTF());
                playerDataInitListeners.setNeedSaving(uuid, false);
                break;
            }

            case "AUTOMATIC_RESET": {
                final String resetId = input.readUTF();
                plugin.getResetPublisher().publishReset(resetId);
                break;
            }

            case "PLAYER_RESET_ALL": {
                final UUID uuid = UUID.fromString(input.readUTF());
                final QuestPlayer questPlayer = QuestPlayer.getQuestPlayer(uuid);
                if(questPlayer != null) {
                    questPlayer.clearActiveQuests();
                    plugin.getQuestManager().fillPlayer(questPlayer);
                }
            }

            case "PLAYER_RESET_ID": {

                final UUID uuid = UUID.fromString(input.readUTF());
                final String resetId = input.readUTF();
                final QuestPlayer questPlayer = QuestPlayer.getQuestPlayer(uuid);

                if(questPlayer != null) {
                    questPlayer.removeActiveQuest(resetId);
                    plugin.getQuestManager().fillPlayer(questPlayer);
                }
                break;
            }
        }
    }
}
