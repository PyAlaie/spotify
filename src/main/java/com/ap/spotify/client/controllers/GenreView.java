package com.ap.spotify.client.controllers;

import com.ap.spotify.client.HelloApplication;
import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Genre;
import com.ap.spotify.shared.models.Music;
import com.google.gson.Gson;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GenreView implements Initializable {
    @FXML
    Label titleLbl;
    @FXML
    TextArea descriptionTxt;
    @FXML
    VBox musicsVbox;

    Genre genre;
    ArrayList<String> musicsList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        genre = StaticData.genreToView;

        loadGenre();
        loadMusics();
    }

    private void loadMusics() {
        Request request = new Request("getMusicsOfGenre");
        request.setJson(new Gson().toJson(genre.getId()));

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();
            Type type = new com.google.gson.reflect.TypeToken<List<Music>>(){}.getType();
            List<Music> musics = new Gson().fromJson(response.getJson(), type);

            for(int i = 0; i < musics.size(); i++){
                Music music = new Gson().fromJson(new Gson().toJson(musics.get(i)), Music.class);

                musicsList.add(music.getTitle() + ".mp3");
                String title = Integer.toString(i)+". ";
                title += music.getTitle() + ", " + music.getPopularity();

                Button button = new Button(title);
                button.setMaxWidth(Double.MAX_VALUE);
                button.setPrefHeight(20);
                button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {

                        StaticData.musicToPlay = music.getTitle() + ".mp3";
                        StaticData.musicsList = musicsList;

                        try {
                            openStage("musicPlayer.fxml");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                musicsVbox.getChildren().add(button);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadGenre() {
        titleLbl.setText(genre.getTitle());
        descriptionTxt.setText(genre.getDescription());
    }

    public void openStage(String stageName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(stageName));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
