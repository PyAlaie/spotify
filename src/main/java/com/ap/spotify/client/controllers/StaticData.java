package com.ap.spotify.client.controllers;

import com.ap.spotify.shared.models.Account;
import com.ap.spotify.shared.models.Artist;
import com.ap.spotify.shared.models.Music;
import com.ap.spotify.shared.models.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class StaticData {
    public static Socket socket;
    public static ObjectOutputStream objOut;
    public static ObjectInputStream objIn;
    public static Account loggedInAccount;
    public static User loggedInUser;
    public static boolean isLogggedIn = false;
    public static int musicToOpenId;
    public static int albumToOpenId;
    public static int playlistToOpen;
    public static Music musicToEdit;
    public static String musicToPlay;
    public static Artist artistToView;
}
