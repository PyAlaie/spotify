package com.ap.spotify.client.controllers;

import com.ap.spotify.client.HelloApplication;
import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Music;
import com.ap.spotify.shared.models.Playlist;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class PlaylistView implements Initializable {
    @FXML
    Label playlistName;
    @FXML
    VBox playlistMusics;
    Playlist playlist;
    ArrayList<String> musicsList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Request request = new Request("viewPlaylist");
        request.setJson(new Gson().toJson(StaticData.playlistToOpen));

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Response response = (Response) StaticData.objIn.readObject();
            System.out.println(response.getMessage());
            if(response.getStatusCode()==200){
                Type type = new com.google.gson.reflect.TypeToken<HashMap<String, Object>>(){}.getType();
                HashMap<String, Object> map = new Gson().fromJson(response.getJson(), type);

                playlist = new Gson().fromJson(new Gson().toJson(map.get("playlist")), Playlist.class);
                List<Music> musics = (List<Music>) map.get("musics");

                playlistName.setText(playlist.getTitle());

                for(int i = 0; i < musics.size(); i++){
                    Music music = new Gson().fromJson(new Gson().toJson(musics.get(i)), Music.class);

                    musicsList.add(music.getTitle() + ".mp3");
                    String title = Integer.toString(i)+". ";
                    title += music.getTitle() + ", " + music.getPopularity();

                    Button button = new Button(title);
                    button.setMaxWidth(Double.MAX_VALUE);
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
                    playlistMusics.getChildren().add(button);

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
    public void closeWindow(ActionEvent actionEvent){
        Node source = (Node)  actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
