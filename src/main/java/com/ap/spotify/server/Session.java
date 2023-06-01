package com.ap.spotify.server;

import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.crudFiles.*;
import com.ap.spotify.shared.models.*;
import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class Session implements Runnable{
    private Socket socket;
    private Database database;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;

    // the boolean below is a simple way to check if the user is logged in or not
    // in the future it will be replaced with token auth...
    boolean isLoggedIn = false;
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

        isLoggedIn = true;
        role = "user";
        if(!isLoggedIn){
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
                    isLoggedIn = true;
                    loggedInAccount = gson.fromJson(response.getJson(), Artist.class);
                    role = "artist";
                }
            }
            else {
                response = crudUser.login(account.getUsername(), account.getPassword());
                if(response.getStatusCode() == 200){
                    isLoggedIn = true;
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
    public Response createNewGenre(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        Genre genre = gson.fromJson(json, Genre.class);
        CrudGenre crudGenre = new CrudGenre(database);

        try {
            if(!crudGenre.doesGenreExist(genre.getTitle())){
                crudGenre.newGenre(genre);
                response.setMessage("Genre created!");
                response.setStatusCode(200);
            }
            else {
                response.setMessage("Genre already exists!");
                response.setStatusCode(400);
            }
        } catch (SQLException e) {
            response.setMessage("Error in creating the genre!");
            response.setStatusCode(400);
        }
        return response;
    }
    public Response createNewComment(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        Comment comment = gson.fromJson(json, Comment.class);
        CrudComment crudComment = new CrudComment(database);

        try {
            crudComment.newComment(comment);
            response.setMessage("Comment added!");
            response.setStatusCode(201);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatusCode(400);
            response.setMessage("Error in adding the comment!");
        }

        return response;
    }
    public Response search(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        String searchedExpression = gson.fromJson(json, String.class);

        CrudMusic crudMusic = new CrudMusic(database);
        CrudArtist crudArtist = new CrudArtist(database);
        CrudPlaylist crudPlaylist = new CrudPlaylist(database);

        try {
            List<Music> musics = crudMusic.search(searchedExpression);
            List<Artist> artists = crudArtist.search(searchedExpression);
            // TODO: search also in playlists...

            HashMap<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("musics", musics);
            jsonMap.put("artists", artists);

            response.setJson(gson.toJson(jsonMap));
            response.setMessage("Search result");
            response.setStatusCode(200);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in search!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response getNewMusics(){
        CrudMusic crudMusic = new CrudMusic(database);
        Response response = new Response();
        Gson gson = new Gson();

        try {
            List<Music> musicList = crudMusic.getNewMusics();
            response.setJson(gson.toJson(musicList));
            response.setStatusCode(200);
            response.setMessage("New musics!");
        }
        catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in getting the musics!");
            response.setStatusCode(400);
        }
        return response;
    }
    public Response getNewAlbums(){
        CrudAlbum crudAlbum = new CrudAlbum(database);
        Response response = new Response();
        Gson gson = new Gson();

        try {
            List<Album> albums = crudAlbum.getNewAlbums();
            response.setJson(gson.toJson(albums));
            response.setStatusCode(200);
            response.setMessage("New albums!");
        }
        catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in getting the albums!");
            response.setStatusCode(400);
        }
        return response;
    }
    public Response viewArtist(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        int artistId = gson.fromJson(json, Integer.class);
        CrudArtist crudArtist = new CrudArtist(database);

        try{
            Artist artist = crudArtist.getArtistById(artistId);
            if(artist == null){
                response.setMessage("Artist not found!");
                response.setStatusCode(404);
            }
            else {
                List<Music> musics = crudArtist.getMusicsOfArtist(artistId);

                HashMap<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("artist", artist);
                jsonMap.put("musics", musics);
                response.setJson(gson.toJson(jsonMap));
                response.setStatusCode(200);
                response.setMessage("Artist returned!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in getting the musics!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response viewAlbum(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        int albumId = gson.fromJson(json, Integer.class);
        CrudAlbum crudAlbum = new CrudAlbum(database);

        try{
            Album album = crudAlbum.getAlbumById(albumId);
            if(album == null){
                response.setMessage("Album not found!");
                response.setStatusCode(404);
            }
            else {
                List<Music> musics = crudAlbum.getAlbumMusics(albumId);

                HashMap<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("album", album);
                jsonMap.put("musics", musics);
                response.setJson(gson.toJson(jsonMap));
                response.setStatusCode(200);
                response.setMessage("Album returned!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in getting the Album!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response updateUserProfile(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        User user = gson.fromJson(json, User.class);
        CrudUser crudUser = new CrudUser(database);

        try{
            crudUser.updateUser(user);
            response.setStatusCode(201);
            response.setMessage("User updated!");
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in updating the user profile!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response createNewMusic(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        Music music = gson.fromJson(json, Music.class);
        CrudMusic crudMusic = new CrudMusic(database);

        try {
            crudMusic.newMusic(music);
            response.setStatusCode(201);
            response.setMessage("Music added!");
        } catch (SQLException e) {
            response.setMessage("Error in creating the music!");
            response.setStatusCode(400);
        }
        return response;
    }
    public Response updateMusic(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        Music music = gson.fromJson(json, Music.class);
        CrudMusic crudMusic = new CrudMusic(database);

        try{
            crudMusic.updateMusic(music);
            response.setMessage("Music updated!");
            response.setStatusCode(201);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in updating the music");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response createNewAlbum(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        Album album = gson.fromJson(json, Album.class);
        CrudAlbum crudAlbum = new CrudAlbum(database);

        try {
            crudAlbum.newAlbum(album);
            response.setMessage("Album added!");
            response.setStatusCode(201);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatusCode(400);
            response.setMessage("Error in adding the album!");
        }

        return response;
    }
    public Response updateAlbum(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        Album album = gson.fromJson(json, Album.class);
        CrudAlbum crudAlbum = new CrudAlbum(database);

        try{
            crudAlbum.updateAlbum(album);
            response.setMessage("Album updated!");
            response.setStatusCode(201);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in updating the album");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response updateArtist(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        Artist artist = gson.fromJson(json, Artist.class);
        CrudArtist crudArtist = new CrudArtist(database);

        try{
            crudArtist.updateArtist(artist);
            response.setMessage("Artist updated!");
            response.setStatusCode(201);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in updating the Artist");
            response.setStatusCode(400);
        }

        return response;
    }

    public Response handleUserRequest(Request request){
        String command = request.getCommand();
        Response response = new Response();

        if (command.equals("getNewMusics")) {
            response = getNewMusics();
        }
        else if(command.equals("newComment")){
            response = createNewComment(request);
        }
        else if (command.equals("downloadMusic")) {
            // TODO: good luck with this section :|
        }
        else if (command.equals("newPlaylist")) {
            // TODO: create table
        }
        else if (command.equals("viewArtist")) {
            response = viewArtist(request);
        }
        else if (command.equals("showTimeline")) {
            // TODO: create table
        }
        else if (command.equals("search")) {
            response = search(request);
        }
        else if (command.equals("updateUserProfile")) {
            response = updateUserProfile(request);
        }
        else if (command.equals("viewAlbum")) {
            response = viewAlbum(request);
        }
        else if (command.equals("getNewAlbums")) {
            response = getNewAlbums();
        }

        return response;
    }
    public Response handleArtistRequest(Request request){
        String command = request.getCommand();
        Response response = new Response();

        if(command.equals("newGenre")){
            response = createNewGenre(request);
        }
        else if (command.equals("newMusic")) {
            response = createNewMusic(request);
        }
        else if(command.equals("editMusic")){
            response = updateMusic(request);
        }
        else if(command.equals("newAlbum")){
            response = createNewAlbum(request);
        }
        else if(command.equals("editAlbum")){
            response = updateAlbum(request);
        }
        else if (command.equals("updateArtistProfile")) {
            response = updateArtist(request);
        }

        return response;
    }
}
