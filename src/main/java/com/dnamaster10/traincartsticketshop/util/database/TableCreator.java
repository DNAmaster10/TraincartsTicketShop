package com.dnamaster10.traincartsticketshop.util.database;

import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TableCreator extends DatabaseAccessor {


    public TableCreator() throws DQLException {
        super();
    }

    public void createTables() throws DMLException {
        //TODO Need to force collation to be case insensitive
        //TODO Need to define an index for display names
        try (Connection connection = getConnection()) {
            PreparedStatement statement;
            statement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS players (
                        uuid varchar(50) PRIMARY KEY NOT NULL,
                        username varchar(20),
                        last_join bigint
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
                        linked_gui_page int DEFAULT 0,
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

            //Post plugin release updates
            statement = connection.prepareStatement("""
                    ALTER TABLE tickets ADD COLUMN IF NOT EXISTS purchase_message varchar(1000);
                    """);

        } catch (SQLException e) {
            throw new DMLException(e);
        }
    }
}
