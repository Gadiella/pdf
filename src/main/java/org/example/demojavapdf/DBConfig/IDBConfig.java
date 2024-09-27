package org.example.demojavapdf.DBConfig;

import java.sql.Connection;
import java.sql.DriverManager;

public interface IDBConfig {
    static String host = "localhost";
    static String port = "3306";
    static String username = "root";
    static String password = "";
    static String database = "archiva_nexus";

    static String URL = "jdbc:mysql://" + host + ":" + port + "/" + database;

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, username, password);
        } catch (Exception e) {
            return null;
        }
    }

}
