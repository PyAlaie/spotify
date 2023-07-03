package com.ap.spotify.client.controllers;

import com.ap.spotify.client.HelloApplication;
import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Album;
import com.ap.spotify.shared.models.Artist;
import com.ap.spotify.shared.models.Music;
import com.google.gson.Gson;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class AlbumView implements Initializable {
    @FXML
    Label albumName, artistLbl;
    @FXML
    VBox albumMusics;

    Album album;
    ArrayList<String> musics = new ArrayList<>();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        album = StaticData.albumToView;

        loadAlbum();
    }

    private void loadAlbum() {
        albumName.setText(album.getTitle());

        Request request = new Request("viewAlbum");
        request.setJson(new Gson().toJson(album.getId()));

        try{
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();
            System.out.println(response.getMessage());

            if(response.getStatusCode() == 200){
                Type type = new com.google.gson.reflect.TypeToken<HashMap<String, Object>>(){}.getType();
                HashMap<String, Object> map = new Gson().fromJson(response.getJson(), type);

                Type typeArtist = new com.google.gson.reflect.TypeToken<Artist>(){}.getType();
                Artist artist = new Gson().fromJson(new Gson().toJson(map.get("artist")), typeArtist);
                artistLbl.setText(String.valueOf(artist.getUsername()));

                List<Music> musicList = (List<Music>) map.get("musics");
                for(int i = 0; i < musicList.size(); i++){
                    Music music = new Gson().fromJson(new Gson().toJson(musicList.get(i)), Music.class);
                    musics.add(music.getTitle() + ".mp3");

                    Button button = new Button(music.getTitle());

                    button.setMaxWidth(Double.MAX_VALUE);
                    button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {

                            StaticData.musicToPlay = music.getTitle() + ".mp3";
                            StaticData.musicsList = musics;

                            try {
                                openStage("musicPlayer.fxml");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                    albumMusics.getChildren().add(button);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void openStage(String stageName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(stageName));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    public void play() {
        StaticData.musicToPlay = musics.get(0);
        StaticData.musicsList = musics;

        try {
            openStage("musicPlayer.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
