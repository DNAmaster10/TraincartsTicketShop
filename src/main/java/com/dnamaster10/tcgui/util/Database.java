package com.dnamaster10.tcgui.util;

import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.scheduler.BukkitRunnable;

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
    public static Connection getConnection() throws SQLException {
        //Returns a new connection to the database
        try {
            Class.forName(driver);
            return DriverManager.getConnection(
                    DB_URL + DB_NAME,
                    USERNAME, PASSWORD
            );
        } catch (ClassNotFoundException e) {
            throw new SQLException("Failed to load mariadb driver: " + e);
        }
    }
    public static void executeStatement(Connection connection, String statement) throws SQLException {
        //Used to execute statement which will not return a valuable result
        PreparedStatement stmt = connection.prepareStatement(statement);
        stmt.execute();
        stmt.close();
    }
    public static void createTables() throws SQLException {
        //Creates tables in database if they do not exist
        Connection connection = getConnection();
        executeStatement(connection, """
                        CREATE TABLE IF NOT EXISTS players (
                        id int UNIQUE AUTO_INCREMENT,
                        username varchar(17),
                        uuid varchar(50) UNIQUE
                    );
                    """);
        executeStatement(connection, """
                CREATE TABLE IF NOT EXISTS guis (
                    id int UNIQUE AUTO_INCREMENT,
                    name varchar(100) UNIQUE,
                    owner_uuid varchar(50),
                    ticket_count int
                );
                """);
        executeStatement(connection, """
                CREATE TABLE IF NOT EXISTS tickets (
                    id int UNIQUE AUTO_INCREMENT,
                    guiid int,
                    page int,
                    type varchar(15),
                    name varchar(100)
                );
                """);
        connection.close();
    }
    public static boolean checkGuiByName(String name) throws SQLException {
        //returns true if the gui with the given name exists in database
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM guis WHERE name=?;");
        statement.setString(1, name);
        ResultSet result = statement.executeQuery();
        int total = 0;
        while (result.next()) {
            total = result.getInt(1);
        }
        result.close();
        statement.close();
        connection.close();
        return (total > 0);
    }
    public static void addGui(String name, String ownerUuid) throws SQLException {
        //Registers a new GUI in the database
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO guis (name, owner_uuid) VALUES (?,?)");
        statement.setString(1, name);
        statement.setString(2, ownerUuid);
        statement.executeUpdate();
        statement.close();
        connection.close();
    }
    public static boolean checkPlayerByUuid(String uuid) throws SQLException {
        //Returns true if player exists in database
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM players WHERE uuid=?");
        statement.setString(1, uuid);
        ResultSet result = statement.executeQuery();
        int total = 0;
        while (result.next()) {
            total = result.getInt(1);
        }
        result.close();
        statement.close();
        connection.close();
        return (total > 0);
    }
    public static String getUsernameFromUuid(String uuid) throws SQLException {
        //Returns player name from UUID as string
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT username FROM players WHERE uuid=?");
        statement.setString(1, uuid);
        ResultSet result = statement.executeQuery();
        String name = null;
        while (result.next()) {
            name = result.getString("username");
        }
        result.close();
        statement.close();
        connection.close();
        return name;
    }
    public static void updatePlayer(String name, String uuid) throws SQLException {
        //Updates player information in database
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement("UPDATE players SET username=? WHERE uuid=?");
        statement.setString(1, name);
        statement.setString(2, uuid);
        statement.executeUpdate();
        statement.close();
        connection.close();
    }
    public static void addPlayer(String name, String uuid) throws SQLException {
        //Updates a players username from their UUID
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO players (username, uuid) VALUES (?, ?)");
        statement.setString(1, name);
        statement.setString(2, uuid);
        statement.executeUpdate();
        statement.close();
        connection.close();
    }
}