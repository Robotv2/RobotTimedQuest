package fr.robotv2.common.data;

import com.j256.ormlite.support.ConnectionSource;
import fr.robotv2.common.data.impl.ActiveQuest;

import java.sql.SQLException;

public class DatabaseManager {

    private final ConnectionSource source;
    private final OrmData<ActiveQuest, Integer> activeQuestOrmData = new OrmData<>();

    public enum DatabaseType {
        MYSQL,
        SQLITE,
        ;
    }

    public DatabaseManager(DatabaseCredentials credentials) throws SQLException {
        this(credentials.createConnectionSource());
    }

    public DatabaseManager(ConnectionSource source) throws SQLException {
        this.source = source;
        this.activeQuestOrmData.initialize(source, ActiveQuest.class);
    }

    public void closeConnection() {
        this.source.closeQuietly();
    }

    public boolean isConnected() {
        return this.source != null;
    }

    public OrmData<ActiveQuest, Integer> getActiveQuestOrmData() {
        return this.activeQuestOrmData;
    }
}
