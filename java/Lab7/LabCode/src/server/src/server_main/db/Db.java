package server_main.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
    private final String url;
    private final String user;
    private final String pass;

    public Db(String url, String user, String pass) {
        this.url = url; this.user = user; this.pass = pass;
    }

    public Connection get() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    public static Db fromEnv() {
        String url = System.getProperty("DB_URL",
                "jdbc:postgresql://localhost:5532/studs");
        String user = "postgres";//System.getProperty("DB_USER", System.getenv("DB_USER"));
        String pass = "12345678";//System.getProperty("DB_PASS", System.getenv("DB_PASS"));
        if (user == null) user = System.getenv("USER");
        if (pass == null) pass = "";
        return new Db(url, user, pass);
    }
}

