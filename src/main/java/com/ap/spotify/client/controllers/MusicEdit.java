package com.ap.spotify.client.controllers;

import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Album;
import com.ap.spotify.shared.models.Genre;
import com.ap.spotify.shared.models.Music;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MusicEdit implements Initializable {
    @FXML
    TextField titleTxt, pathTxt;
    @FXML
    ComboBox<Genre> genreMenu;
    @FXML
    ComboBox<Album> albumMenu;

    Music music = StaticData.musicToEdit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadMusic();
    }

    public void loadMusic(){
        titleTxt.setText(music.getTitle());

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
                Genre musicGenre = new Genre();
                for (Genre genre : genres){
                    genreMenu.getItems().add(genre);
                    if(genre.getId() == music.getGenre()){
                        musicGenre = genre;
                    }
                }

                genreMenu.setValue(musicGenre);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        request = new Request("getArtistAlbums");
        request.setJson(new Gson().toJson(StaticData.loggedInAccount.getId()));
        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();
            Response response = (Response) StaticData.objIn.readObject();
            if(response.getStatusCode() == 200){
                Gson gson = new Gson();

                Type type = new com.google.gson.reflect.TypeToken<List<Album>>(){}.getType();
                List<Album> albums = gson.fromJson(response.getJson(), type);
                Album musicAlbum = new Album();

                for(Album album : albums){
                    albumMenu.getItems().add(album);
                    if(album.getId() == music.getArtist()){
                        musicAlbum = album;
                    }
                }

                albumMenu.setValue(musicAlbum);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(ActionEvent event){
        music.setTitle(titleTxt.getText());
        music.setGenre(genreMenu.getValue().getId());
        music.setAlbum(albumMenu.getValue().getId());
        if(pathTxt.getText() != null){
            music.setCoverPicPath(pathTxt.getText());
        }

        Request request = new Request("editMusic");
        request.setJson(new Gson().toJson(music));

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();
            Response response = (Response) StaticData.objIn.readObject();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save");
            alert.setHeaderText(String.valueOf(response.getStatusCode()));
            alert.setContentText(response.getMessage());
            alert.show();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadCoverPic(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");

        // Set initial directory
        File initialDirectory = new File(System.getProperty("user.home"));
        fileChooser.setInitialDirectory(initialDirectory);

        // Add file filters
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files (*.png)", "*.png");
        fileChooser.getExtensionFilters().addAll(imageFilter);

        // Show open file dialog
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }

        FileInputStream fileInputStream = null;
        try {
            assert selectedFile != null;
            fileInputStream = new FileInputStream(selectedFile);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            byte[] bytes = new byte[(int) selectedFile.length()];
            bufferedInputStream.read(bytes, 0, bytes.length);

            Request request = new Request("uploadCoverPic");
            request.setJson(new Gson().toJson(bytes));
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();
            System.out.println("Request Sent!");

            Response response = (Response) StaticData.objIn.readObject();
            System.out.println(response.getMessage());
            if(response.getStatusCode() == 201){
                pathTxt.setText(new Gson().fromJson(response.getJson(), String.class));
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
