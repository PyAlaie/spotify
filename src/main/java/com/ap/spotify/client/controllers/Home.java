package com.ap.spotify.client.controllers;

import com.ap.spotify.Test;
import com.ap.spotify.client.HelloApplication;
import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Music;
import com.google.gson.Gson;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Home implements Initializable {
    @FXML
    HBox newMusics;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // setting the new musics in order :)
        try {
            ArrayList<Music> musics = getNewMusics();
            System.out.println(musics);
            for (Music music : musics){
                Image image = new Image(Test.class.getResource("cloud/image.jpg").toExternalForm());
                ImageView musicCover = new ImageView();
                musicCover.setImage(image);
                if(music.getCoverPicPath() != null){
                    musicCover = new ImageView(music.getCoverPicPath());
                }
                musicCover.setFitHeight(100);
                musicCover.setPreserveRatio(false);
                musicCover.setFitWidth(120);
                musicCover.setFitHeight(120);

                Label title = new Label(music.getTitle());
                title.setFont(new Font(18));
                title.setBackground(Background.fill(Color.BLACK));
                title.setTextFill(Paint.valueOf(String.valueOf(Color.WHITE)));
                AnchorPane.setBottomAnchor(title,0.0);
                AnchorPane.setRightAnchor(title,0.0);
                AnchorPane.setLeftAnchor(title,0.0);
                title.setAlignment(Pos.CENTER);

                AnchorPane musicPane = new AnchorPane();
                musicPane.getChildren().add(musicCover);
                musicPane.getChildren().add(title);

                musicPane.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        // TODO: open music page
                    }
                });

                newMusics.getChildren().add(musicPane);
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Music> getNewMusics() throws IOException, ClassNotFoundException {
        Request request = new Request("getNewMusics");
        StaticData.objOut.writeObject(request);
        StaticData.objOut.flush();

        Response response = (Response) StaticData.objIn.readObject();
        if(response.getStatusCode() == 200){
            Gson gson = new Gson();

            Type type = new com.google.gson.reflect.TypeToken<ArrayList<Music>>(){}.getType();
            ArrayList<Music> musics = gson.fromJson(response.getJson(), type);

            return musics;
        }

        return null;
    }
}
