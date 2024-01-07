package com.dnamaster10.tcgui.util;

import com.dnamaster10.tcgui.TraincartsGui;

import javax.sql.DataSource;
import java.sql.*;

import java.util.HashMap;
import java.util.function.Supplier;

public class Database {
    static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    static final String DB_URL = "jdbc:mariadb://localhost";
    static final String DB_NAME = "tcdb"
    static final String USERNAME = "admin";
    static final String PASSWORD = "password";
    public static boolean checkConnection() {
        //Returns a bool indicating whether DB connection was successful
        try {
            Connection connection = DriverManager.getConnection(
                    DB_URL + DB_NAME,
                    USERNAME, PASSWORD
            );
        }
        catch (SQLException e) {
            TraincartsGui.plugin.getLogger().severe("Failed to connect to database: " + e);
            return false;
        }
        return true;
    }
    public static Connection getConnection() {
        //Returns a new connection to the database
        try {
            return DriverManager.getConnection(
                    DB_URL + DB_NAME,
                    USERNAME, PASSWORD
            );
        } catch (SQLException e) {
            TraincartsGui.plugin.getLogger().severe("Failed to connect to database: " + e);
            TraincartsGui.plugin.disable();
            return null;
        }
    }
    public static boolean createTables() {
        //Creates tables in database if they do not exist
        Connection connection = getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS players (
                        id int UNIQUE AUTO_INCREMENT,
                        username varchar(17),
                        uuid varchar(50) UNIQUE
                    );
                    """);
            statement.executeUpdate();
            statement.close();
            PreparedStatement statement =
        } catch (SQLException e) {
            TraincartsGui.plugin.getLogger().severe("Failed to create table in database: " + e);
            return false;
        }
    }

    public static boolean connect() {
        //Creates the connection to the database
        //Returns false if connection failed.
        return true;
    }
    public static boolean executeStatement(String statement) {
        //Executes a statement without returning
        //a value. Returns false if statement failed.
        return true;
    }
    public static HashMap<String, Object> getRow(String statement) {
        //Returns a hashmap linking column names and values
        //Meant to be used for single rows only.
        return null;
    }
    public static String getUsernameFromUuid(String uuid) {
        String statement = "SELECT username FROM players WHERE uuid=?";
        return null;
    }
    public static

}
