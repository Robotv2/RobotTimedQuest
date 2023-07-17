package fr.robotv2.bukkit.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.common.channel.ChannelConstant;
import fr.robotv2.common.data.impl.QuestPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BukkitMessageListener implements PluginMessageListener {

    private final RTQBukkitPlugin plugin;

    public BukkitMessageListener(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {

        if(!channel.equals(ChannelConstant.RESET_CHANNEL)) {
            return;
        }

        final ByteArrayDataInput input = ByteStreams.newDataInput(message);
        final String sub = input.readUTF();

        switch (sub.toLowerCase()) {

            case "reset": {
                final String resetId = input.readUTF();
                plugin.getResetPublisher().publishReset(resetId);
                break;
            }


            case "clear-quests": {
                final UUID uuid = UUID.fromString(input.readUTF());
                final QuestPlayer questPlayer = QuestPlayer.getQuestPlayer(uuid);
                if(questPlayer != null) {
                    questPlayer.clearActiveQuests();
                    plugin.getQuestManager().fillPlayer(questPlayer);
                }
                break;
            }

            case "clear-quests-id": {
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
