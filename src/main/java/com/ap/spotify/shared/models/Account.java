package com.ap.spotify.shared.models;

import com.ap.spotify.shared.BCrypt;

public class Account {
    private String username;
    private String password;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    private String role;
    public String hashPassword(){
        return BCrypt.hashpw(this.password, BCrypt.gensalt());
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
}
