package com.ap.spotify.shared.crudFiles;

import com.ap.spotify.server.Database;
import com.ap.spotify.shared.models.Album;
import com.ap.spotify.shared.models.Music;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrudAlbum {
    Database database;

    public CrudAlbum(Database database) {
        this.database = database;
    }

    public void newAlbum(Album album) throws SQLException {
        String query = "INSERT INTO albums (title, release_date, popularity, artist, genre) VALUES " +
                "(?,?,?,?,?)";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setString(1, album.getTitle());
        statement.setDate(2, album.getReleaseDate());
        statement.setInt(3, album.getPopularity());
        statement.setInt(4, album.getArtist());
        statement.setInt(5, album.getGenre());

        statement.executeUpdate();
    }

    public List<Music> getAlbumMusics(int albumId) throws SQLException {
        String query = "SELECT * FROM musics WHERE album=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setInt(1, albumId);

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

    public List<Album> getNewAlbums() throws SQLException {
        String query = "SELECT * FROM albums LIMIT 10";

        PreparedStatement statement = database.getConnection().prepareStatement(query);

        ResultSet res = statement.executeQuery();
        List<Album> albums = new ArrayList<>();

        while (res.next()){
            Album album = new Album();

            album.setTitle(res.getString("title"));
            album.setId(res.getInt("id"));
            album.setGenre(res.getInt("genre"));
            album.setPopularity(res.getInt("popularity"));
            album.setArtist(res.getInt("artist"));
            album.setReleaseDate(res.getDate("release_date"));

            albums.add(album);
        }

        return albums;
    }

    public Album getAlbumById(int albumId) throws SQLException {
        String query = "SELECT * FROM albums WHERE id=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setInt(1, albumId);

        ResultSet res = statement.executeQuery();

        if (res.next()){
            Album album = new Album();

            album.setTitle(res.getString("title"));
            album.setId(res.getInt("id"));
            album.setGenre(res.getInt("genre"));
            album.setPopularity(res.getInt("popularity"));
            album.setArtist(res.getInt("artist"));
            album.setReleaseDate(res.getDate("release_date"));

            return album;
        }

        return null;
    }

    public void updateAlbum(Album album) throws SQLException {
        String query = "UPDATE musics SET " +
                "title=?, genre=?, release_date=?" +
                " WHERE id=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setString(1, album.getTitle());
        statement.setInt(2, album.getGenre());
        statement.setDate(3, album.getReleaseDate());
        statement.setInt(4, album.getId());

        statement.executeQuery();
    }
}
