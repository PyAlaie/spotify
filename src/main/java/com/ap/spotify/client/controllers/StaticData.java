package com.ap.spotify.client.controllers;

import com.ap.spotify.shared.models.Account;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class StaticData {
    public static Socket socket;
    public static ObjectOutputStream objOut;
    public static ObjectInputStream objIn;
    public static Account loggedInAccount;
    public static boolean isLogggedIn = false;
}
