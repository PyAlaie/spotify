package com.ap.spotify.server;

import java.sql.*;

public class Database {
    // Set the parameters accordingly
    private String url = "jdbc:postgresql://localhost:5432/postgres";
    private String user = "postgres";
    private String pass = "1382";
    private Connection connection;
    private Statement statement;

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(url, user, pass);
        System.out.println(connection);
        statement = connection.createStatement();
    }

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }
}
