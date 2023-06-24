package com.ap.spotify.shared.models;

public class Artist extends Account{
    private String biography;
    private Genre genreObj;

    public Genre getGenreObj() {
        return genreObj;
    }

    public void setGenreObj(Genre genreObj) {
        this.genreObj = genreObj;
    }

    private int genre;
    public String getProfilePicPath() {
        return profilePicPath;
    }

    public void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }

    private String profilePicPath;

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public int getGenre() {
        return genre;
    }

    public void setGenre(int genre) {
        this.genre = genre;
    }
}
