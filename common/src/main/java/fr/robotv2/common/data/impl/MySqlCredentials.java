package fr.robotv2.common.data.impl;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import fr.robotv2.common.data.DatabaseCredentials;

import java.sql.SQLException;

public class MySqlCredentials implements DatabaseCredentials {

    private final String host;
    private final String port;
    private final String database;
    private final String username;
    private final String password;
    private final boolean ssl;

    public MySqlCredentials(String host, String port, String database, String username, String password, boolean ssl) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.ssl = ssl;
    }

    @Override
    public ConnectionSource createConnectionSource() throws SQLException {
        return new JdbcConnectionSource("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + ssl, username, password);
    }
}
