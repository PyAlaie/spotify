package com.ap.spotify.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

public class Server {
    private int portNumber;
    private ServerSocket serverSocket;
    private Database database;
    private ArrayList<Session> sessions;

    public Server(int portNumber) {
        this.portNumber = portNumber;
        database = new Database();
    }

    public void runServer() throws IOException, SQLException {
        try {
            // Connecting to database
            database.connect();
            System.out.println("Connected to database!");

            // Creating server socket
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Server started on port " + portNumber);

            System.out.println("Listening for Connections ...");
            
            while(true){
                // Listening for connections
                Socket socket = serverSocket.accept();
                Session session = new Session(socket, database);
                System.out.println("New connection: " + socket.getRemoteSocketAddress());

                Thread thread = new Thread(session);
                thread.start();
            }

        } catch (SQLException e) {
            System.err.println("Unable to connect to database!");
            throw new RuntimeException(e);
        }
        finally {
            database.getConnection().close();
        }


    }

    public static void main(String[] args) throws IOException, SQLException {
        Server server = new Server(8000);
        server.runServer();
    }
}
