package com.ap.spotify.client.controllers;

import com.ap.spotify.client.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class UserHome implements Initializable {
    @FXML
    AnchorPane container;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadScene("home.fxml");
    }

    public void home(ActionEvent e){
        loadScene("home.fxml");
    }

    public void loadScene(String sceneName){
        try {
            container.getChildren().clear();
            Parent parent = FXMLLoader.load(Objects.requireNonNull(HelloApplication.class.getResource(sceneName)));

            AnchorPane.setTopAnchor(parent, 0.0);
            AnchorPane.setBottomAnchor(parent, 0.0);
            AnchorPane.setLeftAnchor(parent, 0.0);
            AnchorPane.setRightAnchor(parent, 0.0);

            container.getChildren().add(parent);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    public void myLibrary(ActionEvent e){
        StaticData.playlistToOpen = 1;
        loadScene("playlistView.fxml");
    }

    public void search(ActionEvent event){
        loadScene("search.fxml");
    }
}
