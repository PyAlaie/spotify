package com.ap.spotify.client.controllers;

import com.ap.spotify.Test;
import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Album;
import com.ap.spotify.shared.models.Genre;
import com.ap.spotify.shared.models.Music;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.sql.Date;
import java.util.List;
import java.util.ResourceBundle;

public class MusicAdd implements Initializable {
    @FXML
    ComboBox<Album> albumCombo;
    @FXML
    ComboBox<Genre> genreCombo;
    @FXML
    TextField titleTxt, durationTxt;
    @FXML
    ImageView coverPicView;
    @FXML
    Label pathLbl, musicPath;
    @FXML
    TextArea lyricsTxt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
                    albumCombo.getItems().add(album);
                }

            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        request = new Request("getGenres");
        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();
            Response response = (Response) StaticData.objIn.readObject();
            System.out.println(response.getMessage());
            if(response.getStatusCode() == 200){
                Gson gson = new Gson();

                Type type = new com.google.gson.reflect.TypeToken<List<Genre>>(){}.getType();
                List<Genre> genres = gson.fromJson(response.getJson(), type);
                for (Genre genre : genres){
                    genreCombo.getItems().add(genre);
                }
            }
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
                pathLbl.setText(new Gson().fromJson(response.getJson(), String.class));
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void uploadMusic(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");

        // Set initial directory
        File initialDirectory = new File(System.getProperty("user.home"));
        fileChooser.setInitialDirectory(initialDirectory);

        // Add file filters
        FileChooser.ExtensionFilter musicFilter = new FileChooser.ExtensionFilter("Music Files mp3", "*.mp3");
        fileChooser.getExtensionFilters().addAll(musicFilter);

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

            Request request = new Request("uploadMusic");
            request.setJson(new Gson().toJson(bytes));
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();
            System.out.println("Request Sent!");

            Response response = (Response) StaticData.objIn.readObject();
            System.out.println(response.getMessage());
            if(response.getStatusCode() == 201){
                musicPath.setText(new Gson().fromJson(response.getJson(), String.class));
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void save(ActionEvent event){
        Music music = new Music();
        music.setTitle(titleTxt.getText());
        music.setArtist(StaticData.loggedInAccount.getId());
        music.setGenre(genreCombo.getValue().getId());
        music.setAlbum(albumCombo.getValue().getId());
        music.setLyricsFilePath(lyricsTxt.getText());
        music.setDuration(Integer.parseInt(durationTxt.getText()));
        music.setReleaseDate(new java.sql.Date(new java.util.Date().getTime()));

        if(!musicPath.getText().equals("path")){
            music.setMusicFilePath(musicPath.getText());
        }
        if(!pathLbl.getText().equals("path")){
            music.setCoverPicPath(pathLbl.getText());
        }

        Request request = new Request("newMusic");
        request.setJson(new Gson().toJson(music));

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();
            System.out.println(response.getMessage());

            if(response.getStatusCode() == 201){
                closeWindow(event);
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Adding music failed!");
                alert.setContentText(response.getMessage());

                alert.show();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeWindow(ActionEvent actionEvent){
        Node source = (Node)  actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
