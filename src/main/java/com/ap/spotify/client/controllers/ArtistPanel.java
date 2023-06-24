package com.ap.spotify.client.controllers;

import com.ap.spotify.Test;
import com.ap.spotify.client.HelloApplication;
import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.*;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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

public class ArtistPanel implements Initializable {
    @FXML
    VBox musicsBox, albumsBox, genresBox;
    @FXML
    TextField genreName, usernameTxt;
    @FXML
    TextArea genreDescription, biographyTxt;
    @FXML
    ImageView coverPicImg;
    @FXML
    Label pathLbl;
    @FXML
    ComboBox<Genre> genresCombo;

    Artist artist;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadMusics();
        loadAlbums();
        loadGenres();
        loadProfile();
    }

    private void loadProfile() {
        artist = StaticData.loggedInArtist;

        usernameTxt.setText(artist.getUsername());
        biographyTxt.setText(artist.getBiography());
        Image image = new Image(Test.class.getResource("cloud/profile.png").toExternalForm());
        if(artist.getProfilePicPath() != null){
            try {
                image = new Image(Test.class.getResource("cloud/" + artist.getProfilePicPath()).toExternalForm());
                pathLbl.setText(artist.getProfilePicPath());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        coverPicImg.setImage(image);

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
                Genre artistGenre = new Genre();
                for(Genre genre : genres){
                    genresCombo.getItems().add(genre);
                    if(genre.getId() == artist.getGenre()){
                        artistGenre = genre;
                    }
                }
                genresCombo.setValue(artistGenre);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
                    button.setBackground(Background.fill(Color.rgb(22, 75, 96)));
                    button.setTextFill(Color.WHITE);
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

    public void logout(ActionEvent event){
        StaticData.loggedInAccount = null;
        StaticData.loggedInUser = null;
        StaticData.isLogggedIn = false;
        Request request = new Request("logout");

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();
            Response response = (Response) StaticData.objIn.readObject();
            System.out.println(response.getMessage());
            openStage("login.fxml");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        closeWindow(event);
    }

    public void closeWindow(ActionEvent actionEvent){
        Node source = (Node)  actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void saveProfile(){
        artist.setBiography(biographyTxt.getText());
        artist.setUsername(usernameTxt.getText());

        if(genresCombo.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Artist profile");
            alert.setHeaderText("Genre Should not be null!");
            alert.show();
            return;
        }
        artist.setGenre(genresCombo.getValue().getId());

        if(!pathLbl.getText().equals("path")){
            artist.setProfilePicPath(pathLbl.getText());
        }

        Request request = new Request("updateArtistProfile");
        request.setJson(new Gson().toJson(artist));

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Artist profile");
            alert.setHeaderText(response.getMessage());
            alert.show();
            if(response.getStatusCode() == 201){
                StaticData.loggedInArtist = artist;
                StaticData.loggedInAccount = artist;
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
            if(response.getStatusCode() == 201){
                System.out.println(new Gson().fromJson(response.getJson(), String.class));
                pathLbl.setText(new Gson().fromJson(response.getJson(), String.class));
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void addNewAlbum(){
        try {
            openStage("albumAdd.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
