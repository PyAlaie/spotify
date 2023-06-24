package com.ap.spotify.client.controllers;

import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Album;
import com.ap.spotify.shared.models.Genre;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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

public class AlbumAdd implements Initializable {
    @FXML
    ComboBox<Genre> genreCombo;
    @FXML
    Label pathLbl;
    @FXML
    TextField titleTxt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
                for (Genre genre : genres){
                    genreCombo.getItems().add(genre);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveAlbum(){
        Album album = new Album();
        album.setTitle(titleTxt.getText());
        album.setArtist(StaticData.loggedInArtist.getId());
        album.setGenre(genreCombo.getValue().getId());
        album.setReleaseDate(new java.sql.Date(new java.util.Date().getTime()));

        if(!pathLbl.getText().equals("path")){
            album.setCoverPicPath(pathLbl.getText());
        }

        Request request = new Request("newAlbum");
        request.setJson(new Gson().toJson(album));

        try{
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Message!");
            alert.setHeaderText(response.getMessage());
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
                pathLbl.setText(new Gson().fromJson(response.getJson(), String.class));
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
