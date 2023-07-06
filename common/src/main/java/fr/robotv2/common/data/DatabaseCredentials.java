package fr.robotv2.common.data;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public interface DatabaseCredentials {
    ConnectionSource createConnectionSource() throws SQLException;
}
