package fr.robotv2.common.data.impl;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.robotv2.common.data.DatabaseCredentials;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class MySqlCredentials implements DatabaseCredentials {

    // https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing
    private static final int MAXIMUM_POOL_SIZE = (Runtime.getRuntime().availableProcessors() * 2) + 1;
    private static final int MINIMUM_IDLE = Math.min(MAXIMUM_POOL_SIZE, 10);

    private static final long MAX_LIFETIME = TimeUnit.MINUTES.toMillis(30);
    private static final long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
    private static final long LEAK_DETECTION_THRESHOLD = TimeUnit.SECONDS.toMillis(10);

    private final String host;
    private final String port;
    private final String database;
    private final String username;
    private final String password;
    private final boolean ssl;

    private HikariDataSource hikariDataSource;

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
        final HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + ssl);
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        hikariConfig.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
        hikariConfig.setMinimumIdle(MINIMUM_IDLE);

        hikariConfig.setMaxLifetime(MAX_LIFETIME);
        hikariConfig.setConnectionTimeout(CONNECTION_TIMEOUT);
        hikariConfig.setLeakDetectionThreshold(LEAK_DETECTION_THRESHOLD);

        // Ensure we use utf8 encoding
        hikariConfig.addDataSourceProperty("useUnicode", "true");
        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");

        // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
        hikariConfig.addDataSourceProperty("alwaysSendSetIsolation", "false");
        hikariConfig.addDataSourceProperty("cacheCallableStmts", "true");

        this.hikariDataSource = new HikariDataSource(hikariConfig);
        return new JdbcPooledConnectionSource(hikariDataSource.getJdbcUrl(), hikariDataSource.getUsername(), hikariDataSource.getPassword());
    }

    @Override
    public void close() {
        if(this.hikariDataSource != null && !this.hikariDataSource.isClosed()) {
            this.hikariDataSource.close();
        }
    }
}
