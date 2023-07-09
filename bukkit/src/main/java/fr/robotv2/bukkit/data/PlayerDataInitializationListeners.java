package fr.robotv2.bukkit.data;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.common.data.OrmData;
import fr.robotv2.common.data.impl.ActiveQuest;
import fr.robotv2.common.data.impl.QuestPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerDataInitializationListeners implements Listener {

    private final RTQBukkitPlugin plugin;

    public PlayerDataInitializationListeners(RTQBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {

        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();
        final QuestPlayer questPlayer = new QuestPlayer(playerUUID);

        QuestPlayer.registerQuestPlayer(questPlayer);

        final long now = System.currentTimeMillis();
        final OrmData<ActiveQuest, Integer> ormData = plugin.getDatabaseManager().getActiveQuestOrmData();

        ormData.removeWhere(where -> { // Remove expired quest.
            try {
                return where
                        .eq("owner", playerUUID)
                        .and()
                        .le("next_reset", now);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).thenCompose(v -> ormData.getWhere(where -> { // retrieve active quests for the given player asynchronously.
            try {
                return where.eq("owner", playerUUID);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        })).thenAccept(activeQuests -> { // apply the quest to the current QuestPlayer object synchronously.

            questPlayer.addActiveQuests(activeQuests);
            int total = plugin.getQuestManager().fillPlayer(questPlayer);

            plugin.getLogger().info(String.format("The " + total + " quest(s) has been loaded successfully for the player %s.", player.getName()));
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {

        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();
        final QuestPlayer questPlayer = QuestPlayer.getQuestPlayer(playerUUID);

        if(questPlayer == null) {
            throw new NullPointerException("questPlayer");
        }

        final OrmData<ActiveQuest, Integer> ormData = plugin.getDatabaseManager().getActiveQuestOrmData();
        final List<CompletableFuture<Void>> completableFutures = new ArrayList<>();

        for(ActiveQuest activeQuest : questPlayer.getActiveQuests()) {
            completableFutures.add(ormData.saveAsync(activeQuest));
        }

        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).thenAccept(ignored -> {
            QuestPlayer.unregisterQuestPlayer(questPlayer);
            plugin.getLogger().info(String.format("The quest(s) of player %s has been successfully saved to the database.", player.getName()));
        });


    }

}
