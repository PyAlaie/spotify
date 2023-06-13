package com.ap.spotify.shared.crudFiles;

import com.ap.spotify.server.Database;
import com.ap.spotify.shared.models.Music;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrudMusic {
    Database database;

    public CrudMusic(Database database) {
        this.database = database;
    }
    public void newMusic(Music music) throws SQLException {
        String query = "INSERT INTO users (title, duration, release_date, popularity, cover_pic_path, lyrics_file_path, artist, album, genre)" +
                "VALUES (?,?,?,?,?,?,?,?,?)";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setString(1, music.getTitle());
        statement.setInt(2, music.getDuration());
        statement.setDate(3, music.getReleaseDate());
        statement.setInt(4, music.getPopularity());
        statement.setString(5, music.getCoverPicPath());
        statement.setString(6, music.getLyricsFilePath());
        statement.setInt(7, music.getArtist());
        statement.setInt(8, music.getAlbum());
        statement.setInt(9, music.getGenre());

        statement.executeUpdate();
    }

    public Music getMusicById(int id) throws SQLException {
        String query = "SELECT * FROM musics WHERE id=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setInt(1,id);
        ResultSet res = statement.executeQuery();

        if(res.next()){
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
            return music;
        }

        return null;
    }

    public List<Music> getNewMusics() throws SQLException {
        String query = "SELECT * FROM musics LIMIT 10";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        ResultSet res = statement.executeQuery();

        List<Music> musics = new ArrayList<>();
        while(res.next()){
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
    public void increasePopularityOfMusic(int id) throws SQLException {
        String query = "SELECT popularity FROM musics WHERE id=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setInt(1,id);
        ResultSet res = statement.executeQuery();

        if(!res.next()){
            return;
        }

        int popularity = res.getInt("popularity");

        query = "UPDATE musics SET popularity=? WHERE id=?";
        statement = database.getConnection().prepareStatement(query);

        statement.setInt(1, popularity+1);
        statement.setInt(2, id);

        statement.executeUpdate();
    }

    public List<Music> viewMusicsByGenre(int genreId) throws SQLException {
        String query = "SELECT * FROM musics WHERE genre=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setInt(1, genreId);

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

    public List<Music> search(String expression) throws SQLException {
        String query = "SELECT * FROM musics WHERE title LIKE '%?%'";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setString(1, expression);

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

    public void updateMusic(Music music) throws SQLException {
        String query = "UPDATE musics SET " +
                "title=?, cover_pic_path=?, genre=?, album=?, duration=?, lyrics_file_path=?, release_date=?" +
                " WHERE id=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setString(1, music.getTitle());
        statement.setString(2, music.getCoverPicPath());
        statement.setInt(3, music.getGenre());
        statement.setInt(4, music.getAlbum());
        statement.setInt(5, music.getDuration());
        statement.setString(6, music.getLyricsFilePath());
        statement.setDate(7, music.getReleaseDate());
        statement.setInt(8, music.getId());

        statement.executeQuery();
    }

    public int getLikesCount(int musicId) throws SQLException {
        String query = "SELECT COUNT(*) FROM like_link WHERE music_id=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setInt(1,musicId);

        ResultSet res = statement.executeQuery();

        if(res.next()){
            return res.getInt("count");
        }

        return 0;
    }
}
