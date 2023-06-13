package com.ap.spotify.shared.models;


import com.ap.spotify.shared.BCrypt;

public class User extends Account{
    private String email;
    private String profilePicPath;
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicPath() {
        return profilePicPath;
    }

    public void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }
}
