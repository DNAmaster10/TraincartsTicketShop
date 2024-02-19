package com.dnamaster10.traincartsticketshop.util.database;

public class DatabaseConfig {
    private static String dbUrl;
    private static String dbUsername;
    private static String dbPassword;
    public static void setUrl(String url, String port, String dbName) {
        dbUrl = "jdbc:mariadb://" + url + ":" + port + "/" + dbName;
    }
    public static String getDbUrl() {
        return dbUrl;
    }
    public static void setUsername(String username) {
        dbUsername = username;
    }
    public static String getDbUsername() {
        return dbUsername;
    }
    public static void setPassword(String password) {
        dbPassword = password;
    }
    public static String getDbPassword() {
        return dbPassword;
    }
}
