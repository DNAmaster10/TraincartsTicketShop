package com.dnamaster10.traincartsticketshop.util.database;

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
                        uuid varchar(50) PRIMARY KEY NOT NULL,
                        username varchar(17),
                        last_join int
                    ) ENGINE=INNODB;
                    """);
            statement.execute();

            statement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS guis (
                        id int AUTO_INCREMENT PRIMARY KEY,
                        owner_uuid varchar(50),
                        name varchar(100) UNIQUE,
                        display_name varchar(100),
                        raw_display_name varchar(100),
                        FOREIGN KEY (owner_uuid) REFERENCES players(uuid)
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
                        editor_uuid varchar(50) NOT NULL,
                        FOREIGN KEY (editor_uuid) REFERENCES players(uuid)
                            ON DELETE CASCADE,
                        FOREIGN KEY (gui_id) REFERENCES guis(id)
                            ON DELETE CASCADE
                    ) ENGINE=INNODB;
                    """);
            statement.execute();

            statement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS companies (
                        id int AUTO_INCREMENT PRIMARY KEY,
                        owner_uuid varchar(50),
                        company_name varchar(100) UNIQUE,
                        FOREIGN KEY (owner_uuid) REFERENCES players(uuid)
                            ON DELETE SET NULL
                    ) ENGINE=INNODB;
                    """);
            statement.execute();

            statement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS companymembers (
                        id int AUTO_INCREMENT PRIMARY KEY,
                        company_id int NOT NULL,
                        member_uuid varchar(100) NOT NULL,
                        FOREIGN KEY (company_id) REFERENCES companies(id)
                            ON DELETE CASCADE,
                        FOREIGN KEY (member_uuid) REFERENCES players(uuid)
                            ON DELETE CASCADE
                    ) ENGINE=INNODB;
                    """);
            statement.execute();
        }
    }
}
