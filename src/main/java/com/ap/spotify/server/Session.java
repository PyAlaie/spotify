package com.ap.spotify.server;

import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.crudFiles.*;
import com.ap.spotify.shared.models.*;
import com.google.gson.Gson;
import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.google.gson.reflect.TypeToken;

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
        CrudAlbum crudAlbum = new CrudAlbum(database);

        try {
            List<Music> musics = crudMusic.search(searchedExpression);
            List<Artist> artists = crudArtist.search(searchedExpression);
            List<Playlist> playlists = crudPlaylist.search(searchedExpression);
            List<Album> albums = crudAlbum.search(searchedExpression);

            HashMap<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("musics", musics);
            jsonMap.put("artists", artists);
            jsonMap.put("playlists", playlists);
            jsonMap.put("albums", albums);

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
                int followers = crudArtist.getFollowersCount(artistId);

                HashMap<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("artist", artist);
                jsonMap.put("musics", musics);
                jsonMap.put("followers", followers);
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
        CrudArtist crudArtist = new CrudArtist(database);

        try{
            Album album = crudAlbum.getAlbumById(albumId);
            if(album == null){
                response.setMessage("Album not found!");
                response.setStatusCode(404);
            }
            else {
                List<Music> musics = crudAlbum.getAlbumMusics(albumId);
                Artist artist = crudArtist.getArtistById(album.getArtist());

                HashMap<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("album", album);
                jsonMap.put("musics", musics);
                jsonMap.put("artist", artist);
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
            response.setMessage(e.getMessage());
            response.setStatusCode(400);
            e.printStackTrace();
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
    public Response createNewPlaylist(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        Playlist playlist = gson.fromJson(json, Playlist.class);
        CrudPlaylist crudPlaylist = new CrudPlaylist(database);

        try {
            crudPlaylist.newPlaylist(playlist);
            response.setMessage("Playlist added!");
            response.setStatusCode(201);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatusCode(400);
            response.setMessage("Error in adding the playlist!");
        }

        return response;
    }
    public Response viewMyPlaylists(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        int userId = gson.fromJson(json, Integer.class);
        CrudPlaylist crudPlaylist = new CrudPlaylist(database);

        try{
            List<Playlist> playlists = crudPlaylist.getPlaylistsOfUser(userId);
            response.setJson(gson.toJson(playlists));
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in getting the playlists!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response viewPlaylist(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        int playlistId = gson.fromJson(json, Integer.class);
        CrudPlaylist crudPlaylist = new CrudPlaylist(database);

        try{
            List<Music> musics = crudPlaylist.getPlaylistMusics(playlistId);
            Playlist playlist = crudPlaylist.getPlaylistById(playlistId);
            HashMap<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("playlist", playlist);
            jsonMap.put("musics", musics);
            response.setJson(gson.toJson(jsonMap));
            response.setStatusCode(200);
            response.setMessage("Playlist returned!");
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in getting the playlist!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response addMusicToPlaylist(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();

        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        HashMap<String, Object> map = gson.fromJson(json, type);
        int musicId = ((Double) map.get("musicId")).intValue();
        int playlistId = ((Double) map.get("playlistId")).intValue();
        CrudPlaylist crudPlaylist = new CrudPlaylist(database);

        try{
            crudPlaylist.addMusicToPlaylist(musicId, playlistId);
            response.setMessage("Music added!");
            response.setStatusCode(201);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in adding the song to playlist!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response removeMusicFromPlaylist(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();

        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        HashMap<String, Object> map = gson.fromJson(json, type);
        int musicId = (Integer) map.get("musicId");
        int playlistId = (Integer) map.get("playlistId");
        CrudPlaylist crudPlaylist = new CrudPlaylist(database);

        try{
            crudPlaylist.removeMusicFromPlaylist(musicId, playlistId);
            response.setMessage("Music added!");
            response.setStatusCode(201);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in adding the song to playlist!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response followArtist(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();

        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        HashMap<String, Object> map = gson.fromJson(json, type);
        int userId = ((Double) map.get("userId")).intValue();
        int artistId = ((Double) map.get("artistId")).intValue();
        CrudUser crudUser = new CrudUser(database);

        try{
            crudUser.followArtist(userId, artistId);
            response.setMessage("Artist is followed!");
            response.setStatusCode(201);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in following artist!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response unfollowArtist(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();

        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        HashMap<String, Object> map = gson.fromJson(json, type);
        int userId = ((Double) map.get("userId")).intValue();
        int artistId = ((Double) map.get("artistId")).intValue();
        CrudUser crudUser = new CrudUser(database);

        try{
            crudUser.unfollowArtist(userId, artistId);
            response.setMessage("Artist is unfollowed!");
            response.setStatusCode(201);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in unfollowing artist!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response getFollowings(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();

        int userId = gson.fromJson(json, Integer.class);
        CrudUser crudUser = new CrudUser(database);

        try{
            List<Artist> artists = crudUser.getFollowings(userId);
            response.setJson(new Gson().toJson(artists));
            response.setMessage("Followings!");
            response.setStatusCode(200);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in getting the followings!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response likeMusic(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();

        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        HashMap<String, Object> map = gson.fromJson(json, type);
        int userId = ((Double) map.get("userId")).intValue();
        int musicId = ((Double) map.get("musicId")).intValue();
        CrudUser crudUser = new CrudUser(database);

        try{
            crudUser.likeMusic(userId, musicId);
            response.setMessage("Music is liked!");
            response.setStatusCode(201);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in liking the music!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response unlikeMusic(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();

        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        HashMap<String, Object> map = gson.fromJson(json, type);
        int userId = ((Double) map.get("userId")).intValue();
        int musicId = ((Double) map.get("musicId")).intValue();
        CrudUser crudUser = new CrudUser(database);

        try{
            crudUser.unlikeMusic(userId, musicId);
            response.setMessage("Music is unliked!");
            response.setStatusCode(201);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in unliking the music!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response isLiked(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();

        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        HashMap<String, Object> map = gson.fromJson(json, type);
        int userId = ((Double) map.get("userId")).intValue();
        int musicId = ((Double) map.get("musicId")).intValue();
        CrudUser crudUser = new CrudUser(database);

        try{
            Boolean res = crudUser.isLiked(userId, musicId);
            response.setJson(new Gson().toJson(res));
            response.setMessage("Like status: " + res);
            response.setStatusCode(201);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in getting the like!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response addFriend(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();

        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        HashMap<String, Object> map = gson.fromJson(json, type);
        int userId = (Integer) map.get("userId");
        int friendId = (Integer) map.get("friendId");
        CrudUser crudUser = new CrudUser(database);

        try{
            crudUser.addFriend(userId, friendId);
            response.setMessage("Friend added");
            response.setStatusCode(201);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in adding the friend!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response removeFriend(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();

        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        HashMap<String, Object> map = gson.fromJson(json, type);
        int userId = (Integer) map.get("userId");
        int friendId = (Integer) map.get("friendId");
        CrudUser crudUser = new CrudUser(database);

        try{
            crudUser.removeFriend(userId, friendId);
            response.setMessage("Friend removed!");
            response.setStatusCode(201);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in removing the friend!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response viewMusic(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        int musicId = gson.fromJson(json, Integer.class);
        CrudMusic crudMusic = new CrudMusic(database);
        CrudComment crudComment = new CrudComment(database);

        try{
            Music music = crudMusic.getMusicById(musicId);

            if(music == null){
                response.setMessage("Music not found!");
                response.setStatusCode(404);
            }
            else {
                List<Comment> comments = crudComment.getCommentsOfMusic(musicId);
                int likesCount = crudMusic.getLikesCount(musicId);

                HashMap<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("music", music);
                jsonMap.put("comments", comments);
                jsonMap.put("likesCount", likesCount);

                response.setJson(gson.toJson(jsonMap));
                response.setStatusCode(200);
                response.setMessage("Music!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in getting the music!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response getArtistMusics(Request request){
        CrudArtist crudArtist = new CrudArtist(database);
        int artistId = new Gson().fromJson(request.getJson(), Integer.class);
        Response response = new Response();
        Gson gson = new Gson();

        try {
            List<Music> musicList = crudArtist.getMusicsOfArtist(artistId);
            response.setJson(gson.toJson(musicList));
            response.setStatusCode(200);
            response.setMessage("Artist Musics!");
        }
        catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in getting the musics!");
            response.setStatusCode(400);
        }
        return response;
    }
    public Response getArtistAlbums(Request request){
        CrudArtist crudArtist = new CrudArtist(database);
        int artistId = new Gson().fromJson(request.getJson(), Integer.class);
        Response response = new Response();
        Gson gson = new Gson();

        try {
            List<Album> musicList = crudArtist.getAlbumsOfArtist(artistId);
            response.setJson(gson.toJson(musicList));
            response.setStatusCode(200);
            response.setMessage("Artist albums!");
        }
        catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in getting the albums!");
            response.setStatusCode(400);
        }
        return response;
    }
    public Response getGenres(Request request){
        CrudGenre crudGenre = new CrudGenre(database);
        Response response = new Response();
        Gson gson = new Gson();

        try {
            List<Genre> genres = crudGenre.getGenres();
            response.setJson(gson.toJson(genres));
            response.setStatusCode(200);
            response.setMessage("genres!");
        }
        catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in getting the genres!");
            response.setStatusCode(400);
        }
        return response;
    }
    public Response uploadCoverPic(Request request){
        Response response = new Response();

        byte[] bytes = new Gson().fromJson(request.getJson(), byte[].class);

        String imgName = createRandomString() + ".png";
        File file = new File( "src/main/resources/com/ap/spotify/cloud/" + imgName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();

            response.setMessage("Image Uploaded!");
            response.setStatusCode(201);
            response.setJson(new Gson().toJson(imgName));
        } catch (IOException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
            throw new RuntimeException(e);
        }
        return response;
    }
    public Response uploadMusic(Request request){
        Response response = new Response();

        byte[] bytes = new Gson().fromJson(request.getJson(), byte[].class);

        String musicName = createRandomString() + ".mp3";
        File file = new File( "src/main/resources/com/ap/spotify/cloud/" + musicName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();

            response.setMessage("Music Uploaded!");
            response.setStatusCode(201);
            response.setJson(new Gson().toJson(musicName));
        } catch (IOException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
            throw new RuntimeException(e);
        }
        return response;
    }
    public Response downloadMusic(Request request){
        String json = request.getJson();
        Response response = new Response();
        String musicName = new Gson().fromJson(json, String.class);

        File selectedFile = new File("src/main/resources/com/ap/spotify/cloud/" + musicName);

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(selectedFile);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            byte[] bytes = new byte[(int) selectedFile.length()];
            bufferedInputStream.read(bytes, 0, bytes.length);

            response.setJson(new Gson().toJson(bytes));
            response.setMessage("Music is downloaded!");
            response.setStatusCode(200);
            CrudMusic crudMusic = new CrudMusic(database);
            crudMusic.increasePopularityOfMusic(crudMusic.getMusicByFileName(musicName).getId());
        } catch (IOException e) {
            response.setStatusCode(400);
            response.setMessage("Error while downloading the music!");
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return response;
    }
    public Response getLikedMusics(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        int userId = gson.fromJson(json, Integer.class);
        CrudMusic crudMusic = new CrudMusic(database);

        try{
            List<Music> musics = crudMusic.getLikedMusicsOfUser(userId);
            response.setJson(gson.toJson(musics));
            response.setMessage("Musics!");
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in getting the musics!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response getArtistFollowerCount(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();

        int artistId = gson.fromJson(json, Integer.class);
        CrudArtist crudArtist = new CrudArtist(database);

        try{
            int followers = crudArtist.getFollowersCount(artistId);
            response.setJson(new Gson().toJson(followers));
            response.setMessage("followers!");
            response.setStatusCode(200);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in getting the followers!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response isFollowing(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();

        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        HashMap<String, Object> map = gson.fromJson(json, type);
        int userId = ((Double) map.get("userId")).intValue();
        int artistId = ((Double) map.get("artistId")).intValue();
        CrudUser crudUser = new CrudUser(database);

        try{
            Boolean isFollowing = crudUser.isFollowing(userId, artistId);
            response.setJson(new Gson().toJson(isFollowing));
            response.setMessage("Friend removed!");
            response.setStatusCode(201);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in removing the friend!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response logout(){
        isLoggedIn = false;
        loggedInAccount = null;
        role = "";

        Response response = new Response();
        response.setMessage("Logout!");
        response.setStatusCode(200);

        return response;
    }
    public Response getMusicByName(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        String fileName = gson.fromJson(json, String.class);
        CrudMusic crudMusic = new CrudMusic(database);

        try{
            Music music = crudMusic.getMusicByName(fileName);
            response.setJson(new Gson().toJson(music));
            response.setMessage("Music!");
            response.setStatusCode(200);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in getting the music!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response getMusicsOfGenre(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        int genreId = gson.fromJson(json, Integer.class);
        CrudMusic crudMusic = new CrudMusic(database);

        try{
            List<Music> musics = crudMusic.getMusicByGenre(genreId);
            response.setJson(new Gson().toJson(musics));
            response.setMessage("Music!");
            response.setStatusCode(200);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in getting the music!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response getRecommendedMusics(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        int userId = gson.fromJson(json, Integer.class);
        CrudMusic crudMusic = new CrudMusic(database);

        try{
            List<Music> musics = crudMusic.getMusicsOfFollowingArtists(userId);
            response.setJson(new Gson().toJson(musics));
            response.setMessage("Musics!");
            response.setStatusCode(200);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in getting the music!");
            response.setStatusCode(400);
        }

        return response;
    }
    public Response getGenreById(Request request){
        String json = request.getJson();
        Response response = new Response();
        Gson gson = new Gson();
        int genreId = gson.fromJson(json, Integer.class);
        CrudGenre crudGenre = new CrudGenre(database);

        try{
            Genre genre = crudGenre.getGenreById(genreId);
            response.setJson(new Gson().toJson(genre));
            response.setMessage("Genre!");
            response.setStatusCode(200);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setMessage("Error in getting the genre!");
            response.setStatusCode(400);
        }

        return response;
    }
    public String createRandomString(){
        int length = 10;

        // Define the character set
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // Create a StringBuilder object to build the string
        StringBuilder sb = new StringBuilder(length);

        // Create a new Random object
        Random random = new Random();

        // Generate random characters and add them to the StringBuilder
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        // Convert the StringBuilder to a String and print it
        return sb.toString();
    }

    public Response handleUserRequest(Request request){
        String command = request.getCommand();
        Response response = new Response();

        if (command.equals("getNewMusics")) {
            response = getNewMusics();
        }
        else if (command.equals("getRecommendedMusics")) {
            response = getRecommendedMusics(request);
        }
        else if(command.equals("newComment")){
            response = createNewComment(request);
        }
        else if (command.equals("downloadMusic")) {
            response = downloadMusic(request);
        }
        else if (command.equals("viewMusic")) {
            response = viewMusic(request);
        }
        else if (command.equals("newPlaylist")) {
            response = createNewPlaylist(request);
        }
        else if (command.equals("myPlaylists")) {
            response = viewMyPlaylists(request);
        }
        else if (command.equals("viewPlaylist")) {
            response = viewPlaylist(request);
        }
        else if(command.equals("addMusicToPlaylist")){
            response = addMusicToPlaylist(request);
        }
        else if(command.equals("removeMusicFromPlaylist")){
            response = removeMusicFromPlaylist(request);
        }
        else if(command.equals("likeMusic")){
            response = likeMusic(request);
        }
        else if(command.equals("removeLikeMusic")){
            response = unlikeMusic(request);
        }
        else if(command.equals("isLiked")){
            response = isLiked(request);
        }
        else if(command.equals("getLikedMusics")){
            response = getLikedMusics(request);
        }
        else if (command.equals("viewArtist")) {
            response = viewArtist(request);
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
        else if (command.equals("followArtist")) {
            response = followArtist(request);
        }
        else if (command.equals("unfollowArtist")) {
            response = unfollowArtist(request);
        }
        else if (command.equals("isFollowing")) {
            response = isFollowing(request);
        }
        else if (command.equals("getFollowingsOfUser")) {
            response = getFollowings(request);
        }
        else if (command.equals("getArtistFollowerCount")) {
            response = getArtistFollowerCount(request);
        }
        else if (command.equals("addFriend")) {
            response = addFriend(request);
        }
        else if (command.equals("removeFriend")) {
            response = removeFriend(request);
        }
        else if (command.equals("getArtistMusics")) {
            response = getArtistMusics(request);
        }
        else if (command.equals("getMusicByName")) {
            response = getMusicByName(request);
        }
        else if(command.equals("uploadCoverPic")){
            response = uploadCoverPic(request);
        }
        else if(command.equals("logout")){
            response = logout();
        }
        else if (command.equals("getMusicsOfGenre")) {
            response = getMusicsOfGenre(request);
        }
        else if (command.equals("getGenreById")) {
            response = getGenreById(request);
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
        else if (command.equals("getArtistMusics")) {
            response = getArtistMusics(request);
        }
        else if (command.equals("getArtistAlbums")) {
            response = getArtistAlbums(request);
        }
        else if (command.equals("getGenres")) {
            response = getGenres(request);
        }
        else if(command.equals("uploadCoverPic")){
            response = uploadCoverPic(request);
        }
        else if(command.equals("uploadMusic")){
            response = uploadMusic(request);
        }
        else if (command.equals("viewAlbum")) {
            response = viewAlbum(request);
        }
        else if(command.equals("logout")){
            response = logout();
        }

        return response;
    }
}
