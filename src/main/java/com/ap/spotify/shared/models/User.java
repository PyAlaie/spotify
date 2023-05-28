package com.ap.spotify.shared.models;


import com.ap.spotify.shared.BCrypt;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String profilePicPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public String hashPassword(){
        return BCrypt.hashpw(this.password, BCrypt.gensalt());
    }
}
