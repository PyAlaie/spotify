package com.ap.spotify.client.controllers;

import com.ap.spotify.client.HelloApplication;
import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Album;
import com.ap.spotify.shared.models.Artist;
import com.ap.spotify.shared.models.Music;
import com.ap.spotify.shared.models.Playlist;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class Search {
    @FXML
    TextField searchTxt;
    @FXML
    VBox searchRes;

    public void search(ActionEvent event){
        String searchedExpression = searchTxt.getText();

        Request request = new Request("search");
        request.setJson(new Gson().toJson(searchedExpression));

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Response response = (Response) StaticData.objIn.readObject();

            Type type = new com.google.gson.reflect.TypeToken<HashMap<String,Object>>(){}.getType();
            HashMap<String, Object> map = new Gson().fromJson(response.getJson(), type);
            System.out.println(map);

            searchRes.getChildren().clear();

            List<Music> musics = (List<Music>) map.get("musics");
            for(int i = 0; i < musics.size(); i++){
                Music music = new Gson().fromJson(new Gson().toJson(musics.get(i)), Music.class);

                Button button = new Button(music.getTitle());
                button.setPrefWidth(Double.MAX_VALUE);
                button.setPrefHeight(50);
                button.setFont(new Font(22));
                button.setBackground(Background.fill(Color.rgb(14, 41, 84)));
                button.setTextFill(Color.WHITE);
                searchRes.getChildren().add(button);

                button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        StaticData.musicToOpenId = music.getId();

                        try {
                            openStage("musicView.fxml");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }

            List<Playlist> playlists = (List<Playlist>) map.get("playlists");
            for(int i = 0; i < playlists.size(); i++){
                Playlist playlist = new Gson().fromJson(new Gson().toJson(playlists.get(i)), Playlist.class);

                Button button = new Button(playlist.getTitle());
                button.setPrefWidth(Double.MAX_VALUE);
                button.setPrefHeight(50);
                button.setFont(new Font(22));
                button.setBackground(Background.fill(Color.rgb(31, 110, 140)));
                button.setTextFill(Color.WHITE);
                searchRes.getChildren().add(button);

                button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        StaticData.playlistToOpen = playlist.getId();
                        try {
                            openStage("playlistView.fxml");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }

            List<Album> albums = (List<Album>) map.get("albums");
            for(int i = 0; i < albums.size(); i++){
                Album album = new Gson().fromJson(new Gson().toJson(albums.get(i)), Album.class);

                Button button = new Button(album.getTitle());
                button.setPrefWidth(Double.MAX_VALUE);
                button.setPrefHeight(50);
                button.setFont(new Font(22));
                button.setBackground(Background.fill(Color.rgb(31, 110, 140)));
                button.setTextFill(Color.WHITE);
                searchRes.getChildren().add(button);

                button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        StaticData.albumToView = album;

                        try {
                            openStage("albumView.fxml");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }

            List<Artist> artists = (List<Artist>) map.get("artists");
            for(int i = 0; i < artists.size(); i++){
                Artist artist = new Gson().fromJson(new Gson().toJson(artists.get(i)), Artist.class);

                Button button = new Button(artist.getUsername());
                button.setPrefWidth(Double.MAX_VALUE);
                button.setPrefHeight(50);
                button.setFont(new Font(22));
                button.setBackground(Background.fill(Color.rgb(46, 138, 153)));
                button.setTextFill(Color.WHITE);
//                searchRes.getChildren().add(button);

                button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        //TODO: fill this shit
                    }
                });
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
}
