package com.ap.spotify.shared.models;

import com.ap.spotify.shared.BCrypt;

public class Artist extends Account{
    private int id;
    private String biography;
    private int genre;
    public String getProfilePicPath() {
        return profilePicPath;
    }

    public void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }

    private String profilePicPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
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
