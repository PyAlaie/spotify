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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MyLibrary implements Initializable {
    @FXML
    VBox downloadsVbox, playlistsVbox, likedMusicsVbox, followingsVbox;
    @FXML
    TextField usernameTxt, emailTxt;
    @FXML
    ImageView coverPicImg;
    @FXML
    Label pathLbl;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDownloads();
        loadPlaylists();
        loadLikedMusics();
        loadProfile();
        loadFollowings();
    }

    private void loadFollowings() {
        Request request = new Request("getFollowingsOfUser");
        request.setJson(new Gson().toJson(StaticData.loggedInAccount.getId()));

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();
            Type type = new com.google.gson.reflect.TypeToken<List<Artist>>(){}.getType();
            List<Artist> artists = new Gson().fromJson(response.getJson(), type);

            followingsVbox.getChildren().clear();
            for(int i = 0; i < artists.size(); i++){
                Artist artist = new Gson().fromJson(new Gson().toJson(artists.get(i)), Artist.class);

                Button button = new Button(artist.getUsername());
                button.setPrefWidth(Double.MAX_VALUE);
                button.setPrefHeight(20);
                button.setFont(new Font(20));
                button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        StaticData.artistToView = artist;
                        System.out.println(artist.getId());
                        try {
                            openStage("artistProfileView.fxml");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                followingsVbox.getChildren().add(button);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadDownloads(){
        ArrayList<String> musics = getDownloadedMusics();
        for (String music : musics){
            Button button = new Button(music);
            button.setPrefWidth(Double.MAX_VALUE);
            button.setPrefHeight(20);
            button.setFont(new Font(20));
            button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    StaticData.musicToPlay = button.getText();
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(button.getText());
                    StaticData.musicsList = list;
                    try {
                        openStage("musicPlayer.fxml");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            downloadsVbox.getChildren().add(button);
        }
    }
    public void loadLikedMusics(){
        Request request = new Request("getLikedMusics");
        request.setJson(new Gson().toJson(StaticData.loggedInAccount.getId()));

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();
            Type type = new com.google.gson.reflect.TypeToken<List<Music>>(){}.getType();
            List<Music> musics = new Gson().fromJson(response.getJson(), type);

            likedMusicsVbox.getChildren().clear();
            for(int i = 0; i < musics.size(); i++){
                Music music = new Gson().fromJson(new Gson().toJson(musics.get(i)), Music.class);

                Button button = new Button(music.getTitle());
                button.setPrefWidth(Double.MAX_VALUE);
                button.setPrefHeight(20);
                button.setFont(new Font(20));
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

                likedMusicsVbox.getChildren().add(button);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void loadPlaylists(){
        Request request = new Request("myPlaylists");
        request.setJson(new Gson().toJson(StaticData.loggedInAccount.getId()));

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();
            Type type = new com.google.gson.reflect.TypeToken<List<Playlist>>(){}.getType();
            List<Playlist> playlists = new Gson().fromJson(response.getJson(), type);

            playlistsVbox.getChildren().clear();
            for(int i = 0; i < playlists.size(); i++){
                Playlist playlist = new Gson().fromJson(new Gson().toJson(playlists.get(i)), Playlist.class);

                Button button = new Button(playlist.getTitle());
                button.setPrefWidth(Double.MAX_VALUE);
                button.setPrefHeight(20);
                button.setFont(new Font(20));
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

                playlistsVbox.getChildren().add(button);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<String> getDownloadedMusics(){
        String directoryPath = "src/main/resources/com/ap/spotify/downloads";
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        ArrayList<String> res = new ArrayList<>();
        assert files != null;
        for (File file : files) {
            if (file.isFile()) {
                res.add(file.getName());
            }
        }

        return res;
    }

    public void openStage(String stageName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(stageName));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    public void addNewPlaylist(){
        try {
            openStage("playlistAdd.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadProfile(){
        User user = StaticData.loggedInUser;

        usernameTxt.setText(user.getUsername());
        emailTxt.setText(user.getEmail());

        Image image = new Image(Test.class.getResource("cloud/profile.png").toExternalForm());
        if(user.getProfilePicPath() != null){
            try {
                image = new Image(Test.class.getResource("cloud/" + user.getProfilePicPath()).toExternalForm());
                pathLbl.setText(user.getProfilePicPath());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        coverPicImg.setImage(image);
    }

    public void saveProfile(){
        User user = new User();
        user.setId(StaticData.loggedInAccount.getId());
        user.setUsername(usernameTxt.getText());
        user.setEmail(emailTxt.getText());

        if(!pathLbl.getText().equals("path")){
            user.setProfilePicPath(pathLbl.getText());
        }

        Request request = new Request("updateUserProfile");
        request.setJson(new Gson().toJson(user));

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("User profile");
            alert.setHeaderText(response.getMessage());
            alert.show();
            if(response.getStatusCode() == 201){
                StaticData.loggedInUser = user;
                StaticData.loggedInAccount = user;
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
}
