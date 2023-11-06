package fr.robotv2.common.data.impl;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.StatementBuilder;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.support.DatabaseResults;
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

    public static boolean columnExists(ConnectionSource source, String tableName, String columnName) throws SQLException {
        final String statement = "PRAGMA table_info(" + tableName + ")";
        final CompiledStatement compiledStatement = source.getReadOnlyConnection(tableName).compileStatement(
                statement,
                StatementBuilder.StatementType.SELECT,
                null,
                DatabaseConnection.DEFAULT_RESULT_FLAGS,
                true
        );

        final DatabaseResults results = compiledStatement.runQuery(null);

        boolean columnExists = false;
        while (results.next()) {
            if (results.getString(1).equalsIgnoreCase(columnName)) {
                columnExists = true;
                break;
            }
        }

        compiledStatement.closeQuietly();
        return columnExists;
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

    @Override
    public void close() {
        // ignored
    }
}
