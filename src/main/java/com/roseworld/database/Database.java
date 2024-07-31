package com.roseworld.database;

import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static FileConfiguration config;

    public static boolean USE_FULLURL;
    public static String HOST;
    public static String USERNAME;
    public static String PASSWORD;
    public static String DATABASE;
    public static String JDBC;

    public Database(FileConfiguration config) {
        Database.config = config;
        readValues();
    }

    private void readValues(){
        USE_FULLURL = config.getBoolean("use_fullURL");
        HOST = config.getString("host");
        USERNAME = config.getString("username");
        PASSWORD = config.getString("password");
        DATABASE = config.getString("database");
        JDBC = config.getString("jdbc");
    }

    public static Connection getConnection() throws SQLException {
        Connection connection;
        if (USE_FULLURL) {
            connection = DriverManager.getConnection(JDBC);
        } else {
            connection = DriverManager.getConnection("jdbc:mysql://" + USERNAME + ":" + PASSWORD + "@" + HOST + "/" + DATABASE);
        }
        return connection;
    }
}
