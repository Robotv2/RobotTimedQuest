package fr.robotv2.bukkit.data;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.common.channel.ChannelConstant;
import fr.robotv2.common.data.OrmData;
import fr.robotv2.common.data.impl.ActiveQuest;
import fr.robotv2.common.data.impl.QuestPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataInitListeners implements Listener {

    private final RTQBukkitPlugin plugin;
    private final Set<UUID> blockedPlayers = ConcurrentHashMap.newKeySet();

    public PlayerDataInitListeners(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    public Set<UUID> getBlockedPlayers() {
        return this.blockedPlayers;
    }

    public void setNeedSaving(UUID playerUniqueId, boolean needSaving) {
        if(needSaving) {
            blockedPlayers.add(playerUniqueId);
        } else {
            blockedPlayers.remove(playerUniqueId);
            loadPlayer(playerUniqueId);
        }
    }

    private void loadPlayer(UUID playerUniqueId) {

        final Player player = Bukkit.getPlayer(playerUniqueId);
        final QuestPlayer questPlayer = QuestPlayer.getQuestPlayer(playerUniqueId);

        if(player == null || questPlayer == null || !player.isOnline()) {
            return;
        }

        final long now = System.currentTimeMillis();
        final OrmData<ActiveQuest, Integer> ormData = plugin.getDatabaseManager().getActiveQuestOrmData();

        if(this.plugin.isBungeecordMode() && plugin.getRedisConnector() != null) {
            plugin.getRedisConnector()
                    .publish(ChannelConstant.WAIT_SAVING_CHANNEL, playerUniqueId.toString());
        }

        ormData.removeWhere(where -> { // Remove expired quest.
            try {
                return where
                        .eq("owner", playerUniqueId)
                        .and()
                        .le("next_reset", now);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).thenComposeAsync(v -> ormData.getWhere(where -> { // retrieve active quests for the given player asynchronously.
            try {
                return where.eq("owner", playerUniqueId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        })).thenAccept(activeQuests -> { // apply the quest to the current QuestPlayer object synchronously.

            questPlayer.addActiveQuests(activeQuests);
            final int newQuest = plugin.getQuestManager().fillPlayer(questPlayer);

            plugin.getLogger().info(String.format(questPlayer.getActiveQuests().size() + " quest(s) has been loaded successfully for the player %s (" + newQuest + " new).", player.getName()));
            player.sendMessage(ChatColor.GREEN + "Your quest(s) have been loaded successfully.");
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {

        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();

        final QuestPlayer questPlayer = new QuestPlayer(playerUUID);
        QuestPlayer.registerQuestPlayer(questPlayer);

        if(plugin.isBungeecordMode() && getBlockedPlayers().contains(playerUUID)) {
            player.sendMessage(" ");
            player.sendMessage(ChatColor.RED + "Please wait until your quest(s) are loaded...");
            player.sendMessage(ChatColor.RED + "If, in a few seconds, your quest(s) are not loaded");
            player.sendMessage(ChatColor.RED + "Please disconnect and reconnect, or contact an admin.");
            player.sendMessage(" ");
            blockedPlayers.remove(playerUUID);
        } else {
            this.loadPlayer(playerUUID);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {

        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();
        final QuestPlayer questPlayer = QuestPlayer.getQuestPlayer(playerUUID);

        if(questPlayer == null) {
            throw new NullPointerException("questPlayer");
        }

        this.plugin.getDatabaseManager()
                        .savePlayer(questPlayer, true)
                                .thenAccept(ignored -> {
                                    QuestPlayer.unregisterQuestPlayer(questPlayer);
                                    plugin.getLogger().info(String.format("The quest(s) of player %s has been successfully saved to the database.", player.getName()));

                                    if(this.plugin.isBungeecordMode()) {
                                        Objects.requireNonNull(plugin.getRedisConnector()).publish(ChannelConstant.IS_SAVED_CHANNEL, playerUUID.toString());
                                    }
                                }).exceptionally(exception -> {
                                    exception.printStackTrace();
                                    if(this.plugin.isBungeecordMode()) {
                                        Objects.requireNonNull(plugin.getRedisConnector()).publish(ChannelConstant.IS_SAVED_CHANNEL, playerUUID.toString());
                                        // Mark the player as being saved or else he will get blocked while switching server
                                    }
                                    return null;
                });
    }

}
