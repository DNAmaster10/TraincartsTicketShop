package com.dnamaster10.traincartsticketshop.util.database;

import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseAccessor {
    private static final HikariDataSource dataSource;
    static {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setJdbcUrl(DatabaseConfig.getDbUrl());
        config.setUsername(DatabaseConfig.getDbUsername());
        config.setPassword(DatabaseConfig.getDbPassword());

        dataSource = new HikariDataSource(config);
    }
    public Connection getConnection() throws DQLException {
        //Returns a new connection to the database
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new DQLException(e);
        }
    }
}