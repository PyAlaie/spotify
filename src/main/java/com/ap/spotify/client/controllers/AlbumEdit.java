package com.ap.spotify.client.controllers;

import com.ap.spotify.Test;
import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Album;
import com.ap.spotify.shared.models.Genre;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class AlbumEdit implements Initializable {
    @FXML
    TextField titleTxt, artistTxt, dateTxt, popularityTxt, pathTxt;
    @FXML
    ComboBox<Genre> genreCombo;
    @FXML
    ImageView coverImg;

    Album album;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Request request = new Request("viewAlbum");
        int albumId = StaticData.albumToOpenId;

        request.setJson(new Gson().toJson(albumId));

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();
            Type type = new com.google.gson.reflect.TypeToken<HashMap<String,Object>>(){}.getType();
            HashMap<String, Object> map = new Gson().fromJson(response.getJson(), type);
            album =  new Gson().fromJson(new Gson().toJson(map.get("album")), Album.class);
            System.out.println(album);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        titleTxt.setText(album.getTitle());
        artistTxt.setText(String.valueOf(album.getArtist()));
        popularityTxt.setText(String.valueOf(album.getPopularity()));
        dateTxt.setText(String.valueOf(album.getReleaseDate()));
        pathTxt.setText(album.getCoverPicPath());
        Image image = new Image(Test.class.getResource("cloud/image.jpg").toExternalForm());
        try {
            coverImg = new ImageView(Test.class.getResource("cloud/").toExternalForm() + album.getCoverPicPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        coverImg.setImage(image);

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
                Genre albumGenre = new Genre();
                for (Genre genre : genres){
                    genreCombo.getItems().add(genre);
                    if(genre.getId() == albumId){
                        albumGenre = genre;
                    }
                }

                genreCombo.setValue(albumGenre);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(){
        album.setTitle(titleTxt.getText());
        album.setGenre(genreCombo.getValue().getId());
        if(pathTxt.getText() != null){
            album.setCoverPicPath(pathTxt.getText());
        }

        Request request = new Request("editAlbum");
        request.setJson(new Gson().toJson(album));

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
}
