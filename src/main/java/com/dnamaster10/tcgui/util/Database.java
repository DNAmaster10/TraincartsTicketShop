package com.dnamaster10.tcgui.util;

import org.mariadb.jdbc.Connection;

import java.util.HashMap;

public class Database {
    public static Connection connection;

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

}
