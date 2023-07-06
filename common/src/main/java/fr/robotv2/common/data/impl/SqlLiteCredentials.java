package fr.robotv2.common.data.impl;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import fr.robotv2.common.data.DatabaseCredentials;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class SqlLiteCredentials implements DatabaseCredentials {

    private final File database;

    public SqlLiteCredentials(File database) {
        this.database = database;
        checkExist();
    }

    private void checkExist() {
        try {
            if(!this.database.exists()) {
                this.database.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ConnectionSource createConnectionSource() throws SQLException {
        return new JdbcConnectionSource("jdbc:sqlite:".concat(this.database.getPath()));
    }
}
