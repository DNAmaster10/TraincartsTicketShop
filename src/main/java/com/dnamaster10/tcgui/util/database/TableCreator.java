package com.dnamaster10.tcgui.util.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
                        display_name varchar(100),
                        raw_display_name varchar(100),
                        owner_uuid varchar(50)
                    );
                    """);
            statement2.execute();

            PreparedStatement statement3 = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS tickets (
                        id int UNIQUE AUTO_INCREMENT,
                        guiid int,
                        page int,
                        slot int,
                        tc_name varchar(100),
                        display_name varchar(100),
                        raw_display_name varchar(100),
                        price int
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

            PreparedStatement statement5 = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS linkers (
                        id int UNIQUE AUTO_INCREMENT,
                        guiid int,
                        page int,
                        slot int,
                        linked_guiid int,
                        display_name varchar(100),
                        raw_display_name varchar(100)
                    );
                    """);
            statement5.execute();
        }
    }
}
