package com.ap.spotify.client.controllers;

import com.ap.spotify.Test;
import com.ap.spotify.client.HelloApplication;
import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Comment;
import com.ap.spotify.shared.models.Music;
import com.ap.spotify.shared.models.Playlist;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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

public class MusicView implements Initializable {
    @FXML
    Label musicName, artist, genre, date, popularity;
    @FXML
    ListView<String> commentsListView;
    @FXML
    TextField commentTxt;
    @FXML
    ImageView coverPic;
    @FXML
    Button likeBtn;
    @FXML
    VBox addToPlaylistVbox;

    private int openedMusic;
    Music music;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Requesting the server to get the music data
        Request request = new Request("viewMusic");
        request.setJson(new Gson().toJson(StaticData.musicToOpenId));

        Request request1 = new Request("myPlaylists");
        request1.setJson(new Gson().toJson(StaticData.loggedInAccount.getId()));

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();
            System.out.println(response.getMessage());
            if(response.getStatusCode()==200){
                Type type = new com.google.gson.reflect.TypeToken<HashMap<String, Object>>(){}.getType();
                HashMap<String, Object> map = new Gson().fromJson(response.getJson(), type);

                music = new Gson().fromJson(new Gson().toJson(map.get("music")), Music.class);
                openedMusic = music.getId();
                putMusic(music);
                putComments((List<Comment>) map.get("comments"));
                putLikes((Double) map.get("likesCount"));
            }

            StaticData.objOut.writeObject(request1);
            StaticData.objOut.flush();

            response = (Response) StaticData.objIn.readObject();
            Type type = new com.google.gson.reflect.TypeToken<List<Playlist>>(){}.getType();
            List<Playlist> playlists = new Gson().fromJson(response.getJson(), type);

            addToPlaylistVbox.getChildren().clear();
            for(int i = 0; i < playlists.size(); i++){
                Playlist playlist = new Gson().fromJson(new Gson().toJson(playlists.get(i)), Playlist.class);

                Button button = new Button(playlist.getTitle());
                button.setPrefWidth(Double.MAX_VALUE);
                button.setPrefHeight(20);
                button.setFont(new Font(20));
                button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        try {
                            Request request2 = new Request("addMusicToPlaylist");
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("musicId", music.getId());
                            map.put("playlistId", playlist.getId());
                            request2.setJson(new Gson().toJson(map));

                            StaticData.objOut.writeObject(request2);
                            StaticData.objOut.flush();

                            Response response1 = (Response) StaticData.objIn.readObject();
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Adding to playlist!");
                            alert.setHeaderText(response1.getMessage());
                            alert.show();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                addToPlaylistVbox.getChildren().add(button);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void putMusic(Music music){
        musicName.setText(music.getTitle());
        genre.setText(genre.getText()+ music.getGenre());
        artist.setText(artist.getText()+ music.getArtistObj().getUsername());
        popularity.setText(popularity.getText()+ music.getPopularity());
        date.setText("Release date: " + music.getReleaseDate());

        artist.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                StaticData.artistToView = music.getArtistObj();
                try {
                    openStage("artistProfileView.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Image image = new Image(Test.class.getResource("cloud/image.jpg").toExternalForm());
        if(music.getCoverPicPath() != null){
            try {
                image = new Image(Test.class.getResource("cloud/" + music.getCoverPicPath()).toExternalForm());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        coverPic.setImage(image);

        Request request = new Request("isLiked");
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", StaticData.loggedInAccount.getId());
        map.put("musicId", music.getId());
        request.setJson(new Gson().toJson(map));

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();
            Boolean isLiked = new Gson().fromJson(response.getJson(), Boolean.class);
            if(isLiked){
                likeBtn.setText("Liked");
            }
        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void putComments(List<Comment> comments){
        commentsListView.getItems().clear();
        for(int i = 0; i < comments.size(); i++){
            Comment comment = new Gson().fromJson(new Gson().toJson(comments.get(i)), Comment.class);
            commentsListView.getItems().add(comment.getText());
        }
    }

    public void putLikes(Double likes){
        // TODO: write this function
    }

    public void newComment(ActionEvent e){
        if(!commentTxt.getText().equals((""))){
            Comment comment = new Comment();
            comment.setText(commentTxt.getText());
            comment.setMusic(openedMusic);
            comment.setUser(StaticData.loggedInAccount.getId());

            Request request = new Request("newComment");
            request.setJson(new Gson().toJson(comment));

            try {
                StaticData.objOut.writeObject(request);
                StaticData.objOut.flush();
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }

            try {
                Response response = (Response) StaticData.objIn.readObject();
                System.out.println(response.getMessage());
            } catch (IOException | ClassNotFoundException exception) {
                throw new RuntimeException(exception);
            }

            commentTxt.setText("");
            commentsListView.getItems().add(comment.getText());
        }
    }

    public void likeAndDislike(ActionEvent event){
        Request request = null;
        if(likeBtn.getText().equals("Like")){
            request = new Request("likeMusic");
            HashMap<String, Object> map = new HashMap<>();
            map.put("userId", StaticData.loggedInAccount.getId());
            map.put("musicId", music.getId());
            request.setJson(new Gson().toJson(map));
        }
        else {
            request = new Request("removeLikeMusic");
            HashMap<String, Object> map = new HashMap<>();
            map.put("userId", StaticData.loggedInAccount.getId());
            map.put("musicId", music.getId());
            request.setJson(new Gson().toJson(map));
        }

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();
            if(response.getStatusCode() == 201){
                if(likeBtn.getText().equals("Like")){
                    likeBtn.setText("Liked");
                }
                else {
                    likeBtn.setText("Like");
                }
            }
            System.out.println(response.getMessage());
        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void download(){
        String directoryPath = "src/main/resources/com/ap/spotify/downloads";
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isFile() && (music.getTitle()+".mp3").equals(file.getName())) {
                System.out.println("Music is already downloaded!");
                return;
            }
        }

        Request request = new Request("downloadMusic");
        request.setJson(new Gson().toJson(music.getMusicFilePath()));

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();
            System.out.println(response.getMessage());

            byte[] bytes = new Gson().fromJson(response.getJson(), byte[].class);

            File file = new File( "src/main/resources/com/ap/spotify/downloads/" + music.getTitle() + ".mp3");
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bytes);
                fos.close();
                System.out.println("downloaded music successfully");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void play(){
        download();
        StaticData.musicToPlay = music.getTitle() + ".mp3";
        ArrayList<String> list = new ArrayList<String>();
        list.add(music.getTitle() + ".mp3");
        StaticData.musicsList = list;
        try {
            openStage("musicPlayer.fxml");
        } catch (IOException e) {
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
