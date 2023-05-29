package com.ap.spotify.shared.crudFiles;

import com.ap.spotify.server.Database;
import com.ap.spotify.shared.BCrypt;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Artist;
import com.ap.spotify.shared.models.User;
import com.google.gson.Gson;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

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

    public User selectUserByUsername(String username) throws SQLException {
        // TODO: do da function
        return null;
    }

    public boolean doesArtistExist(String username) throws SQLException {
        String query = "SELECT * FROM artists WHERE username=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setString(1, username);

        ResultSet resultSet = statement.executeQuery();

        return resultSet.next();
    }
}
