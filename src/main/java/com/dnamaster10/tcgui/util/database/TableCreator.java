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
            PreparedStatement statement;
            statement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS players (
                        id int AUTO_INCREMENT PRIMARY KEY,
                        username varchar(17),
                        uuid varchar(50) UNIQUE NOT NULL
                    ) ENGINE=INNODB;
                    """);
            statement.execute();

            statement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS guis (
                        id int AUTO_INCREMENT PRIMARY KEY,
                        owner_id int,
                        name varchar(100) UNIQUE,
                        display_name varchar(100),
                        raw_display_name varchar(100),
                        FOREIGN KEY (owner_id) REFERENCES players(id)
                            ON DELETE SET NULL
                    ) ENGINE=INNODB;
                    """);
            statement.execute();

            statement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS tickets (
                        id int AUTO_INCREMENT PRIMARY KEY,
                        gui_id int,
                        page int,
                        slot int,
                        tc_name varchar(100),
                        display_name varchar(100),
                        raw_display_name varchar(100),
                        price int,
                        UNIQUE KEY gui_page_slot_unique (gui_id, page, slot),
                        FOREIGN KEY (gui_id) REFERENCES guis(id)
                            ON DELETE CASCADE
                    ) ENGINE=INNODB;
                    """);
            statement.execute();

            statement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS linkers (
                        id int AUTO_INCREMENT PRIMARY KEY,
                        gui_id int,
                        page int,
                        slot int,
                        linked_gui_id int,
                        linked_gui_page int,
                        display_name varchar(100),
                        raw_display_name varchar(100),
                        UNIQUE KEY gui_page_slot (gui_id, page, slot),
                        FOREIGN KEY (gui_id) REFERENCES guis(id)
                            ON DELETE CASCADE
                    ) ENGINE=INNODB;
                    """);
            statement.execute();

            statement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS guieditors (
                        id int AUTO_INCREMENT PRIMARY KEY,
                        gui_id int NOT NULL,
                        player_id int NOT NULL,
                        FOREIGN KEY (player_id) REFERENCES players(id)
                            ON DELETE CASCADE,
                        FOREIGN KEY (gui_id) REFERENCES guis(id)
                            ON DELETE CASCADE
                    ) ENGINE=INNODB;
                    """);
            statement.execute();

            statement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS companies (
                        id int AUTO_INCREMENT PRIMARY KEY,
                        owner_id int,
                        company_name varchar(100) UNIQUE,
                        FOREIGN KEY (owner_id) REFERENCES players(id)
                            ON DELETE SET NULL
                    ) ENGINE=INNODB;
                    """);
            statement.execute();

            statement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS companymembers (
                        id int AUTO_INCREMENT PRIMARY KEY,
                        company_id int NOT NULL,
                        member_id int NOT NULL,
                        FOREIGN KEY (company_id) REFERENCES companies(id)
                            ON DELETE CASCADE,
                        FOREIGN KEY (member_id) REFERENCES players(id)
                            ON DELETE CASCADE
                    ) ENGINE=INNODB;
                    """);
            statement.execute();
        }
    }
}
