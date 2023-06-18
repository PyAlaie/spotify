package com.ap.spotify.client.controllers;

import com.ap.spotify.client.HelloApplication;
import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Album;
import com.ap.spotify.shared.models.Genre;
import com.ap.spotify.shared.models.Music;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ArtistPanel implements Initializable {
    @FXML
    VBox musicsBox, albumsBox, genresBox;
    @FXML
    TextField genreName;
    @FXML
    TextArea genreDescription;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadMusics();
        loadAlbums();
        loadGenres();
    }

    public void loadMusics(){
        musicsBox.getChildren().clear();

        Request request = new Request("getArtistMusics");
        request.setJson(new Gson().toJson(StaticData.loggedInAccount.getId()));

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();

            if(response.getStatusCode() == 200){
                Gson gson = new Gson();

                Type type = new com.google.gson.reflect.TypeToken<List<Music>>(){}.getType();
                List<Music> musics = gson.fromJson(response.getJson(), type);

                for(Music music : musics){
                    Button button = new Button(music.getTitle());
                    button.setPrefWidth(Double.MAX_VALUE);
                    button.setPrefHeight(20);
                    button.setFont(new Font(20));
                    button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            StaticData.musicToEdit = music;
                            try {
                                openStage("musicEdit.fxml");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                    musicsBox.getChildren().add(button);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadAlbums(){
        albumsBox.getChildren().clear();
        Request request = new Request("getArtistAlbums");
        request.setJson(new Gson().toJson(StaticData.loggedInAccount.getId()));

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();

            if(response.getStatusCode() == 200){
                Gson gson = new Gson();

                Type type = new com.google.gson.reflect.TypeToken<List<Album>>(){}.getType();
                List<Album> albums = gson.fromJson(response.getJson(), type);

                for(Album album : albums){
                    Button button = new Button(album.getTitle());
                    button.setPrefWidth(Double.MAX_VALUE);
                    button.setPrefHeight(20);
                    button.setFont(new Font(20));

                    button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            StaticData.albumToOpenId = album.getId();
                            try {
                                StaticData.albumToOpenId = album.getId();
                                openStage("albumEdit.fxml");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                    albumsBox.getChildren().add(button);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadGenres(){
        Request request = new Request("getGenres");

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();

            System.out.println(response.getMessage());

            if(response.getStatusCode() == 200){
                Gson gson = new Gson();

                Type type = new com.google.gson.reflect.TypeToken<List<Genre>>(){}.getType();
                List<Genre> genres = gson.fromJson(response.getJson(), type);
                genresBox.getChildren().clear();

                for(Genre genre : genres){
                    Button button = new Button(genre.getTitle());
                    button.setPrefWidth(Double.MAX_VALUE);
                    button.setPrefHeight(20);
                    button.setFont(new Font(20));
                    genresBox.getChildren().add(button);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void newGenre(ActionEvent event){
        if(!genreName.getText().equals("")){
            Request request = new Request("newGenre");
            Genre genre = new Genre();
            genre.setTitle(genreName.getText());
            genre.setDescription(genreDescription.getText());
            request.setJson(new Gson().toJson(genre));

            try {
                StaticData.objOut.writeObject(request);
                StaticData.objOut.flush();

                Response response = (Response) StaticData.objIn.readObject();
                System.out.println(response.getMessage());
                if(response.getStatusCode() == 200){
                    loadGenres();
                    genreName.setText("");
                    genreDescription.setText("");
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addMusic(ActionEvent event) throws IOException {
        openStage("musicAdd.fxml");
    }

    public void openStage(String stageName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(stageName));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
