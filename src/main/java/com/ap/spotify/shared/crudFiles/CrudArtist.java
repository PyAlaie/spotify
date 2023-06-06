package com.ap.spotify.shared.crudFiles;

import com.ap.spotify.server.Database;
import com.ap.spotify.shared.BCrypt;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Artist;
import com.ap.spotify.shared.models.Music;
import com.ap.spotify.shared.models.User;
import com.google.gson.Gson;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CrudArtist {
    Database database;

    public CrudArtist(Database database) {
        this.database = database;
    }

    public int newArtist(Artist artist) throws SQLException {
        String query = "INSERT INTO artists (username, biography, genre, password, profile_pic_path)" +
                "VALUES (?,?,?,?,?)";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setString(1, artist.getUsername());
        statement.setString(2, artist.getBiography());
        if(artist.getGenre() == 0)
            statement.setNull(3, Types.INTEGER);
        else
            statement.setNull(3, artist.getGenre());
        statement.setString(4, artist.hashPassword());
        statement.setString(5, artist.getProfilePicPath());

        return statement.executeUpdate();
    }


    public Response login(String username, String password) throws SQLException {
        Response response = new Response();

        String query = "SELECT * FROM artists WHERE username=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setString(1, username);

        ResultSet res = statement.executeQuery();

        if(res.next()){
            Artist artist = new Artist();
            artist.setUsername(res.getString("username"));
            artist.setPassword(res.getString("password"));
            artist.setId(res.getInt("id"));
            artist.setBiography(res.getString("biography"));
            artist.setProfilePicPath(res.getString("profile_pic_path"));
            artist.setGenre(res.getInt("genre"));

            if(BCrypt.checkpw(password, artist.getPassword())){
                response.setMessage("Logged in!");
                response.setStatusCode(200);
                Gson gson = new Gson();
                response.setJson(gson.toJson(artist));
            }
            else {
                response.setMessage("Wrong password!");
                response.setStatusCode(400);
            }
            return response;

        }
        else {
            response.setMessage("Username not found!");
            response.setStatusCode(404);
        }
        return response;
    }

    public Artist selectUserByUsername(String username) throws SQLException {
        String query = "SELECT * FROM artists WHERE username=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setString(1, username);

        ResultSet res = statement.executeQuery();

        if(res.next()){
            Artist artist = new Artist();
            artist.setUsername(res.getString("username"));
            artist.setPassword(res.getString("password"));
            artist.setId(res.getInt("id"));
            artist.setBiography(res.getString("biography"));
            artist.setProfilePicPath(res.getString("profile_pic_path"));
            artist.setGenre(res.getInt("genre"));
            return artist;
        }
        return null;
    }

    public boolean doesArtistExist(String username) throws SQLException {
        String query = "SELECT * FROM artists WHERE username=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setString(1, username);

        ResultSet resultSet = statement.executeQuery();

        return resultSet.next();
    }

    public Artist getArtistById(int id) throws SQLException {
        String query = "SELECT * FROM artists WHERE id=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setInt(1, id);

        ResultSet res = statement.executeQuery();

        if(res.next()){
            Artist artist = new Artist();
            artist.setUsername(res.getString("username"));
            artist.setPassword(res.getString("password"));
            artist.setId(res.getInt("id"));
            artist.setBiography(res.getString("biography"));
            artist.setProfilePicPath(res.getString("profile_pic_path"));
            artist.setGenre(res.getInt("genre"));
            return artist;
        }
        return null;
    }

    public List<Music> getMusicsOfArtist(int artistId) throws SQLException {
        String query = "SELECT * FROM musics WHERE artist=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setInt(1, artistId);

        ResultSet res = statement.executeQuery();
        List<Music> musics = new ArrayList<>();

        while (res.next()){
            Music music = new Music();
            music.setId(res.getInt("id"));
            music.setTitle(res.getString("title"));
            music.setDuration(res.getInt("duration"));
            music.setArtist(res.getInt("artist"));
            music.setCoverPicPath(res.getString("cover_pic_path"));
            music.setLyricsFilePath(res.getString("lyrics_file_path"));
            music.setPopularity(res.getInt("popularity"));
            music.setGenre(res.getInt("genre"));
            music.setReleaseDate(res.getDate("release_date"));
            musics.add(music);
        }

        return musics;
    }


    public void updateArtist(Artist artist) throws SQLException {
        String query = "UPDATE users SET username=?, password=?, biography=?, profile_pic_path=?, genre=? WHERE id=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setString(1, artist.getUsername());
        statement.setString(2, artist.hashPassword());
        statement.setString(3, artist.getBiography());
        statement.setString(4, artist.getProfilePicPath());
        statement.setInt(5, artist.getGenre());
        statement.setInt(5, artist.getId());

        statement.executeQuery();
    }

    public List<Artist> search(String expression) throws SQLException {
        String query = "SELECT * FROM artists WHERE username LIKE '%?%'";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setString(1, expression);

        ResultSet res = statement.executeQuery();
        List<Artist> artists = new ArrayList<>();

        if(res.next()){
            Artist artist = new Artist();
            artist.setUsername(res.getString("username"));
            artist.setPassword(res.getString("password"));
            artist.setId(res.getInt("id"));
            artist.setBiography(res.getString("biography"));
            artist.setProfilePicPath(res.getString("profile_pic_path"));
            artist.setGenre(res.getInt("genre"));

            artists.add(artist);
        }

        return artists;
    }


    public int getFollowersCount(int artistId) throws SQLException {
        String query = "SELECT COUNT(*) FROM follow_link WHERE artist_id=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setInt(1, artistId);

        ResultSet res = statement.executeQuery();
        if(res.next()){
            return res.getInt("count");
        }
        return 0;
    }
}
