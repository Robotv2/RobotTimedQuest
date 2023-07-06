package fr.robotv2.bukkit.data;

import fr.robotv2.common.data.DatabaseCredentials;
import fr.robotv2.common.data.DatabaseManager;
import fr.robotv2.common.data.OrmData;
import fr.robotv2.common.data.impl.ActiveQuest;
import fr.robotv2.common.data.impl.QuestPlayer;

import java.sql.SQLException;

public class BukkitDatabaseManager extends DatabaseManager {

    public BukkitDatabaseManager(DatabaseCredentials credentials) throws SQLException {
        super(credentials);
    }

    @Override
    public OrmData<ActiveQuest, Integer> getActiveQuestOrmData() {
        return super.getActiveQuestOrmData();
    }

    public void savePlayer(QuestPlayer questPlayer, boolean async) {
        for(ActiveQuest activeQuest : questPlayer.getActiveQuests()) {
            if(activeQuest.isDirty()) {
                if(async) {
                    getActiveQuestOrmData().saveAsync(activeQuest)
                            .thenAccept(ignored -> activeQuest.markDirty(false));
                } else {
                    getActiveQuestOrmData().save(activeQuest);
                    activeQuest.markDirty(false);
                }
            }
        }
    }

    public void savePlayers(boolean async) {
        QuestPlayer.getRegistered().forEach(questPlayer -> savePlayer(questPlayer, async));
    }
}
