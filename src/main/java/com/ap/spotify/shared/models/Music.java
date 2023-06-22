package com.ap.spotify.shared.models;

import java.sql.Date;

public class Music {
    private int id;
    private String title;
    private int duration;
    private Date releaseDate;
    private int popularity;
    private String coverPicPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public String getCoverPicPath() {
        return coverPicPath;
    }

    public void setCoverPicPath(String coverPicPath) {
        this.coverPicPath = coverPicPath;
    }

    public String getLyricsFilePath() {
        return lyricsFilePath;
    }

    public void setLyricsFilePath(String lyricsFilePath) {
        this.lyricsFilePath = lyricsFilePath;
    }

    public int getArtist() {
        return artist;
    }

    public void setArtist(int artist) {
        this.artist = artist;
    }

    public int getAlbum() {
        return album;
    }

    public void setAlbum(int album) {
        this.album = album;
    }

    public int getGenre() {
        return genre;
    }

    public void setGenre(int genre) {
        this.genre = genre;
    }

    private String lyricsFilePath;
    private int artist;
    private int album;
    private int genre;
    private String musicFilePath;
    private Artist artistObj;

    public Artist getArtistObj() {
        return artistObj;
    }

    public void setArtistObj(Artist artistObj) {
        this.artistObj = artistObj;
    }

    public String getMusicFilePath() {
        return musicFilePath;
    }

    public void setMusicFilePath(String musicFilePath) {
        this.musicFilePath = musicFilePath;
    }
}
