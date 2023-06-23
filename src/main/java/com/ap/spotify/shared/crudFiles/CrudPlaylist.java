package com.ap.spotify.shared.crudFiles;

import com.ap.spotify.server.Database;
import com.ap.spotify.shared.models.Music;
import com.ap.spotify.shared.models.Playlist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrudPlaylist {
    private Database database;

    public CrudPlaylist(Database database) {
        this.database = database;
    }

    public void newPlaylist(Playlist playlist) throws SQLException {
        String query = "INSERT INTO playlists (title, \"user\", description, is_public)" +
                "VALUES (?,?,?,?)";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setString(1, playlist.getTitle());
        statement.setInt(2, playlist.getUser());
        statement.setString(3, playlist.getDescription());
        statement.setBoolean(4, playlist.isPublic());

        statement.executeUpdate();
    }

    public List<Playlist> getPlaylistsOfUser(int userId) throws SQLException {
        String query = "SELECT * FROM playlists WHERE \"user\"=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setInt(1, userId);

        ResultSet res = statement.executeQuery();
        List<Playlist> playlists = new ArrayList<>();

        while (res.next()){
            Playlist playlist = new Playlist();
            playlist.setId(res.getInt("id"));
            playlist.setTitle(res.getString("title"));
            playlist.setUser(res.getInt("user"));
            playlist.setDescription(res.getString("description"));
            playlist.setPublic(res.getBoolean("is_public"));
            playlists.add(playlist);
        }

        return playlists;
    }

    public List<Music> getPlaylistMusics(int playlistId) throws SQLException {
        String query = "SELECT musics.*" +
                " FROM musics" +
                " JOIN playlist_music_link" +
                " ON musics.id = playlist_music_link.music" +
                " WHERE playlist_music_link.playlist = ?;";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setInt(1, playlistId);

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

    public Playlist getPlaylistById(int playlistId) throws SQLException {
        System.out.println(playlistId);
        String query = "SELECT * FROM playlists WHERE id=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setInt(1, playlistId);

        ResultSet res = statement.executeQuery();

        if (res.next()){
            Playlist playlist = new Playlist();
            playlist.setId(res.getInt("id"));
            playlist.setTitle(res.getString("title"));
            playlist.setUser(res.getInt("user"));
            playlist.setDescription(res.getString("description"));
            return playlist;
        }
        return null;
    }

    public void addMusicToPlaylist(int musicId, int playlistId) throws SQLException {
        boolean shouldAdd = !doesMusicExistInPlaylist(playlistId, musicId);
        if(shouldAdd){
            String query = "INSERT INTO playlist_music_link (music, playlist) VALUES (?,?)";

            PreparedStatement statement = database.getConnection().prepareStatement(query);
            statement.setInt(1, musicId);
            statement.setInt(2, playlistId);

            statement.executeUpdate();
        }
    }

    public void removeMusicFromPlaylist(int musicId, int playlistId) throws SQLException {
        String query = "DELETE FROM playlist_music_link WHERE music=? AND playlist=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setInt(1, musicId);
        statement.setInt(2, playlistId);

        statement.executeUpdate();
    }

    public List<Playlist> search(String expression) throws SQLException {
        String query = "SELECT * FROM playlists WHERE title LIKE ?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setString(1, "%" + expression + "%");


        ResultSet res = statement.executeQuery();
        List<Playlist> playlists = new ArrayList<>();

        if(res.next()){
            Playlist playlist = new Playlist();
            playlist.setId(res.getInt("id"));
            playlist.setTitle(res.getString("title"));
            playlist.setUser(res.getInt("user"));
            playlist.setDescription(res.getString("description"));

            playlists.add(playlist);
        }

        return playlists;
    }

    public List<Playlist> getPublicPlaylists() throws SQLException {
        String query = "SELECT * FROM playlists WHERE is_public=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setBoolean(1, true);

        ResultSet res = statement.executeQuery();
        List<Playlist> playlists = new ArrayList<>();

        while (res.next()){
            Playlist playlist = new Playlist();
            playlist.setId(res.getInt("id"));
            playlist.setTitle(res.getString("title"));
            playlist.setUser(res.getInt("user"));
            playlist.setDescription(res.getString("description"));
            playlist.setPublic(res.getBoolean("is_public"));
            playlists.add(playlist);
        }

        return playlists;
    }

    public boolean doesMusicExistInPlaylist(int playlistId, int musicId) throws SQLException {
        System.out.println(playlistId);
        String query = "SELECT * FROM playlist_music_link WHERE music=? AND playlist=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setInt(1, musicId);
        statement.setInt(2, playlistId);

        ResultSet res = statement.executeQuery();

        if (res.next()){
            return true;
        }
        return false;
    }
}
