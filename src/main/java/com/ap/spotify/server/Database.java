package com.ap.spotify.server;

import java.sql.*;

public class Database {
    // Set the parameters accordingly
    private String url = "jdbc:postgresql://localhost:5432/spotify";
    private String user = "postgres";
    private String pass = "11235813";
    private Connection connection;
    private Statement statement;

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(url, user, pass);
        statement = connection.createStatement();
    }

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }
}
