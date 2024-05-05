package com.dnamaster10.traincartsticketshop.util.newdatabase.sqlite;

import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class SQLiteDatabaseAccessor {
    private static final String url;

    static {
        String name = getPlugin().getConfig().getString("database.name");
        url = "jdbc:sqlite:" + getPlugin().getDataFolder() + name + ".db";
    }

    public Connection getConnection() throws QueryException {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }
}
