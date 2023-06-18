package com.ap.spotify.shared.crudFiles;

import com.ap.spotify.server.Database;
import com.ap.spotify.shared.models.Genre;
import com.ap.spotify.shared.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CrudGenre {
    private Database database;

    public CrudGenre(Database database) {
        this.database = database;
    }

    public void newGenre(Genre genre) throws SQLException {
        String query = "INSERT INTO genres (title, description)" +
                "VALUES (?,?)";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setString(1, genre.getTitle());
        statement.setString(2, genre.getDescription());

        statement.executeUpdate();
    }
    public boolean doesGenreExist(String title) throws SQLException {
        String query = "SELECT * FROM genres WHERE title=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setString(1, title);

        ResultSet res = statement.executeQuery();

        return res.next();
    }

    public Genre getGenreById(int id) throws SQLException {
        String query = "SELECT * FROM genres WHERE id=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();
        Genre genre = null;

        if(resultSet.next()){
            genre = new Genre();
            genre.setTitle(resultSet.getString("title"));
            genre.setDescription(resultSet.getString("description"));
            genre.setId(resultSet.getInt("id"));
        }

        return genre;
    }

    public List<Genre> getGenres() throws SQLException {
        String query = "SELECT * FROM genres";

        PreparedStatement statement = database.getConnection().prepareStatement(query);

        ResultSet resultSet = statement.executeQuery();
        List<Genre> genres = new ArrayList<>();

        while (resultSet.next()){
            Genre genre = new Genre();
            genre.setTitle(resultSet.getString("title"));
            genre.setDescription(resultSet.getString("description"));
            genre.setId(resultSet.getInt("id"));
            genres.add(genre);
        }

        return genres;
    }
}
