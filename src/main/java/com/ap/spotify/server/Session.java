package com.ap.spotify.server;

import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.crudFiles.CrudArtist;
import com.ap.spotify.shared.crudFiles.CrudUser;
import com.ap.spotify.shared.models.Account;
import com.ap.spotify.shared.models.Artist;
import com.ap.spotify.shared.models.User;
import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

public class Session implements Runnable{
    private Socket socket;
    private Database database;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;

    // the boolean below is a simple way to check if the user is logged in or not
    // in the future it will be replaced with token auth...
    boolean isLoggedin = false;
    private Account loggedInAccount;
    private String role;

    public Session(Socket socket, Database database) {
        this.socket = socket;
        this.database = database;
    }

    @Override
    public void run() {
        // Initializing streams
        try {
            objIn = new ObjectInputStream(socket.getInputStream());
            objOut = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error in starting the streams!");
            throw new RuntimeException(e);
        }

        // Listening for upcoming requests
        Request r;
        try {
            while ((r = (Request) objIn.readObject()) != null){
                handleRequest(r);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Connection lost with socket " + socket.getRemoteSocketAddress());
        }
        finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Could not close socket!");
                e.printStackTrace();
            }
        }

    }

    public void handleRequest(Request request) throws IOException {
        String command = request.getCommand();

        if(!isLoggedin){
            if(command.equals("login")){
                Response response = login(request);
                objOut.writeObject(response);
                objOut.flush();
            }
            else if(command.equals("newUser")){
                Response response = createNewUser(request);
                objOut.writeObject(response);
                objOut.flush();
            }
            else if(command.equals("newArtist")){
                Response response = createNewArtist(request);
                objOut.writeObject(response);
                objOut.flush();
            }
        }
        else {
            if(role.equals("user")){
                Response response = handleUserRequest(request);
                objOut.writeObject(response);
                objOut.flush();
            }
            else{
                Response response = handleArtistRequest(request);
                objOut.writeObject(response);
                objOut.flush();
            }
        }
    }

    public Response createNewUser(Request request){
        String json = request.getJson();
        Gson gson = new Gson();
        User user = gson.fromJson(json, User.class);
        CrudUser crudUser = new CrudUser(database);
        CrudArtist crudArtist = new CrudArtist(database);
        Response response = new Response();

        try {
            if(crudArtist.doesArtistExist(user.getUsername()) || crudUser.doesUserExist(user.getUsername())){
                response.setMessage("Username already exists!");
                response.setStatusCode(400);
            }
            else {
                crudUser.newUser(user);
                response.setMessage("User created!");
                response.setStatusCode(201);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error while creating the user!");
            System.out.println(response.getMessage());
            response.setStatusCode(400);
        }

        return response;
    }

    public Response createNewArtist(Request request){
        String json = request.getJson();
        Gson gson = new Gson();
        Artist artist = gson.fromJson(json, Artist.class);
        CrudArtist crudArtist = new CrudArtist(database);
        CrudUser crudUser = new CrudUser(database);
        Response response = new Response();

        try {
            if(crudArtist.doesArtistExist(artist.getUsername()) || crudUser.doesUserExist(artist.getUsername())){
                response.setMessage("Username already exists!");
                response.setStatusCode(400);
            }
            else {
                crudArtist.newArtist(artist);
                response.setMessage("Artist created!");
                response.setStatusCode(201);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error while creating the artist!");
            System.out.println(response.getMessage());
            response.setStatusCode(400);
        }

        return response;
    }

    public Response login(Request request){
        String json = request.getJson();
        Gson gson = new Gson();
        Account account = gson.fromJson(json, Account.class);
        CrudUser crudUser = new CrudUser(database);
        CrudArtist crudArtist = new CrudArtist(database);
        Response response = new Response();

        try {
            if(crudArtist.doesArtistExist(account.getUsername())){
                response = crudArtist.login(account.getUsername(), account.getPassword());
                if(response.getStatusCode() == 200){
                    isLoggedin = true;
                    loggedInAccount = gson.fromJson(response.getJson(), Artist.class);
                    role = "artist";
                }
            }
            else {
                response = crudUser.login(account.getUsername(), account.getPassword());
                if(response.getStatusCode() == 200){
                    isLoggedin = true;
                    loggedInAccount = gson.fromJson(response.getJson(), User.class);
                    role = "user";
                }
            }
        }
        catch (SQLException e){
            response.setMessage("Error while logging in!");
            response.setStatusCode(400);
        }

        return response;
    }

    public Response handleUserRequest(Request request){
        // TODO: write this function
        return null;
    }
    public Response handleArtistRequest(Request request){
        // TODO: write this function
        return null;
    }
}
