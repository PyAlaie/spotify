package com.ap.spotify;

import com.ap.spotify.shared.*;
import com.ap.spotify.shared.models.Artist;
import com.ap.spotify.shared.models.Genre;
import com.ap.spotify.shared.models.Music;
import com.ap.spotify.shared.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Test {
    static Scanner scanner = new Scanner(System.in);
    static boolean loggedIn = false;
    static User loggedInUser = null;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = null;
        try {
            socket = new Socket("localhost", 8000);
            System.out.println("Connected to server!");
        } catch (IOException e) {
            System.out.println("Unable to connect to server!");
            e.printStackTrace();
        }

        assert socket != null;
        ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());




        Gson gson = new Gson();
        Request request = new Request("getNewMusics");


        objOut.writeObject(request);
        objOut.flush();

        Response response = (Response) objIn.readObject();

        Type listType = new TypeToken<List<Music>>(){}.getType();

        List<Music> musics = gson.fromJson(response.getJson(), listType);

        System.out.println(musics.get(0).getTitle());


    }
}
