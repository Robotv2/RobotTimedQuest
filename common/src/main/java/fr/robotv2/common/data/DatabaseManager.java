package fr.robotv2.common.data;

import com.j256.ormlite.support.ConnectionSource;
import fr.robotv2.common.data.impl.ActiveQuest;

import java.sql.SQLException;

public class DatabaseManager {

    private final DatabaseCredentials databaseCredentials;

    private ConnectionSource source;
    private OrmData<ActiveQuest, Integer> activeQuestOrmData = new OrmData<>();

    public enum DatabaseType {
        MYSQL,
        SQLITE,
        ;
    }

    public DatabaseManager(DatabaseCredentials credentials) throws SQLException {
        this.databaseCredentials = credentials;
        this.source = credentials.createConnectionSource();
        this.activeQuestOrmData.initialize(source, ActiveQuest.class, false);
    }

    public void closeConnection() {
        this.databaseCredentials.close();
        this.source.closeQuietly();

        this.source = null;
        this.activeQuestOrmData = null;
    }

    public boolean isConnected() {
        return this.source != null;
    }

    public OrmData<ActiveQuest, Integer> getActiveQuestOrmData() {
        return this.activeQuestOrmData;
    }
}
