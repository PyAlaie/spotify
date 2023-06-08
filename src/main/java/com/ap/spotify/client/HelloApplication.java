package com.ap.spotify.client;

import com.ap.spotify.client.controllers.StaticData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Socket socket = null;
        ObjectOutputStream objOut = null;
        ObjectInputStream objIn = null;
        try {
            socket = new Socket("localhost", 8000);
            objOut = new ObjectOutputStream(socket.getOutputStream());
            objIn = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected to server!");
        } catch (IOException e) {
            System.out.println("Unable to connect to server!");
            e.printStackTrace();
            return;
        }

        StaticData.socket = socket;
        StaticData.objIn = objIn;
        StaticData.objOut = objOut;

        launch();
    }
}