package com.dnamaster10.tcgui.util;

import com.dnamaster10.tcgui.TraincartsGui;

import javax.sql.DataSource;
import java.sql.*;

import java.util.HashMap;
import java.util.function.Supplier;

public class Database {
    static final String driver = "org.mariadb.jdbc.Driver";
    static final String DB_URL = "jdbc:mariadb://localhost:3306/";
    static final String DB_NAME = "tcdb";
    static final String USERNAME = "admin";
    static final String PASSWORD = "password";
    public static boolean checkConnection() {
        //Returns a bool indicating whether DB connection was successful
        try {
            Class.forName(driver);
            Connection connection = DriverManager.getConnection(
                    DB_URL + DB_NAME,
                    USERNAME, PASSWORD
            );
        }
        catch (SQLException e) {
            TraincartsGui.plugin.getLogger().severe("Failed to connect to database: " + e);
            return false;
        } catch (ClassNotFoundException e) {
            TraincartsGui.plugin.getLogger().severe("No MariaDB driver found: " + e);
            return false;
        }
        return true;
    }
    public static Connection getConnection() {
        //Returns a new connection to the database
        try {
            Class.forName(driver);
            return DriverManager.getConnection(
                    DB_URL + DB_NAME,
                    USERNAME, PASSWORD
            );
        } catch (SQLException e) {
            TraincartsGui.plugin.getLogger().severe("Failed to connect to database: " + e);
            TraincartsGui.plugin.disable();
            return null;
        } catch (ClassNotFoundException e) {
            TraincartsGui.plugin.getLogger().severe("No MaraiDB driver found: " + e);
            TraincartsGui.plugin.disable();
            return null;
        }
    }
    public static boolean executeStatement(Connection connection, String statement) {
        //Used to execute statement which will not return a valuable result
        try {
            PreparedStatement stmt = connection.prepareStatement(statement);
            stmt.execute();
            stmt.close();
            return true;
        }
        catch (SQLException e) {
            TraincartsGui.plugin.getLogger().severe("SQL error: " + e);
        }
        return false;
    }
    public static boolean createTables() {
        //Creates tables in database if they do not exist
        Connection connection = getConnection();
        assert connection != null;
        if(!executeStatement(connection, """
                    CREATE TABLE IF NOT EXISTS players (
                        id int UNIQUE AUTO_INCREMENT,
                        username varchar(17),
                        uuid varchar(50) UNIQUE
                    );
                    """)) {
            TraincartsGui.plugin.getLogger().severe("Failed to create table in database.");
            TraincartsGui.plugin.disable();
            return false;
        }
        if (!executeStatement(connection, """
                CREATE TABLE IF NOT EXISTS guis (
                    id int UNIQUE AUTO_INCREMENT,
                    name varchar(100) UNIQUE,
                    owner_uuid varchar(50),
                    ticket_count int
                );
                """)) {
            TraincartsGui.plugin.getLogger().severe("Failed to create table in database.");
            TraincartsGui.plugin.disable();
            return false;
        }
        if (!executeStatement(connection, """
                CREATE TABLE IF NOT EXISTS tickets (
                    id int UNIQUE AUTO_INCREMENT,
                    guiid int,
                    page int,
                    type varchar(15),
                    name varchar(100)
                );
                """)) {
            TraincartsGui.plugin.getLogger().severe("Failed to create table in database.");
            TraincartsGui.plugin.disable();
            return false;
        }
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
}
