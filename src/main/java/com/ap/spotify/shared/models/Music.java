package com.ap.spotify.shared.models;

import java.sql.Date;

public class Music {
    private int id;
    private String title;
    private int duration;
    private Date releaseDate;
    private int popularity;
    private String coverPicPath;
    private String lyricsFilePath;
    private Artist artist;
    private Album album;
    private Genre genre;
}
