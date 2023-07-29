package fr.robotv2.bukkit.data;

import fr.robotv2.common.data.DatabaseCredentials;
import fr.robotv2.common.data.DatabaseManager;
import fr.robotv2.common.data.OrmData;
import fr.robotv2.common.data.impl.ActiveQuest;
import fr.robotv2.common.data.impl.QuestPlayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BukkitDatabaseManager extends DatabaseManager {

    public BukkitDatabaseManager(DatabaseCredentials credentials) throws SQLException {
        super(credentials);
    }

    @Override
    public OrmData<ActiveQuest, Integer> getActiveQuestOrmData() {
        return super.getActiveQuestOrmData();
    }

    public CompletableFuture<Void> savePlayer(QuestPlayer questPlayer, boolean async) {

        final List<CompletableFuture<Void>> futures = new ArrayList<>();

        for(ActiveQuest activeQuest : questPlayer.getActiveQuests()) {
            if(activeQuest.isDirty()) {
                if(async) {
                    futures.add(
                            getActiveQuestOrmData()
                            .saveAsync(activeQuest)
                            .thenAccept(ignored -> activeQuest.markDirty(true))
                    );
                } else {
                    getActiveQuestOrmData().save(activeQuest);
                    activeQuest.markDirty(true);
                    futures.add(CompletableFuture.completedFuture(null));
                }
            }
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public CompletableFuture<Void> savePlayers(boolean async) {

        final List<CompletableFuture<Void>> futures = new ArrayList<>();

        for(QuestPlayer questPlayer : QuestPlayer.getRegistered()) {
            futures.add(this.savePlayer(questPlayer, async));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
}
