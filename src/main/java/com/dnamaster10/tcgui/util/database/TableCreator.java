package com.dnamaster10.tcgui.util.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class TableCreator extends DatabaseAccessor {


    public TableCreator() throws SQLException {
        super();
    }

    public void createTables() throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement1 = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS players (
                        id int UNIQUE AUTO_INCREMENT,
                        username varchar(17),
                        uuid varchar(50) UNIQUE
                    );
                    """);
            statement1.execute();

            PreparedStatement statement2 = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS guis (
                        id int UNIQUE AUTO_INCREMENT,
                        name varchar(100) UNIQUE,
                        owner_uuid varchar(50),
                        ticket_count int
                    );
                    """);
            statement2.execute();

            PreparedStatement statement3 = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS tickets (
                        id int UNIQUE AUTO_INCREMENT,
                        guiid int,
                        page int,
                        name varchar(100)
                    );
                    """);
            statement3.execute();

            PreparedStatement statement4 = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS guieditors (
                        id int UNIQUE AUTO_INCREMENT,
                        guiid int,
                        player_uuid varchar(50)
                    );
                    """);
            statement4.execute();
        }
    }
}
