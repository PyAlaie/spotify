package com.ap.spotify.client.controllers;

import com.ap.spotify.shared.models.*;
import javafx.scene.media.MediaPlayer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class StaticData {
    public static Socket socket;
    public static ObjectOutputStream objOut;
    public static ObjectInputStream objIn;
    public static Account loggedInAccount;
    public static User loggedInUser;
    public static Artist loggedInArtist;
    public static boolean isLogggedIn = false;
    public static int musicToOpenId;
    public static int albumToOpenId;
    public static int playlistToOpen;
    public static Music musicToEdit;
    public static String musicToPlay;
    public static Artist artistToView;
    public static Album albumToView;
    public static MediaPlayer mediaPlayer;
    public static Boolean isMediaRunning = false;
    public static Genre genreToView;
    public static ArrayList<String> musicsList;
}
