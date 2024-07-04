package com.dnamaster10.traincartsticketshop.util.database.sqlite;

import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class SQLiteDatabaseAccessor {
    private static final String url;

    static {
        String name = getPlugin().getConfig().getString("database.database");
        String path = getPlugin().getDataFolder() + "/" + name + ".db";
        url = "jdbc:sqlite:" + path;
    }

    public Connection getConnection() throws QueryException {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }
}
