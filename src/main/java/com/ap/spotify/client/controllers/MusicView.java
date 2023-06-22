package com.ap.spotify.client.controllers;

import com.ap.spotify.Test;
import com.ap.spotify.client.HelloApplication;
import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Comment;
import com.ap.spotify.shared.models.Music;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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

    private int openedMusic;
    Music music;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Requesting the server to get the music data
        Request request = new Request("viewMusic");
        request.setJson(new Gson().toJson(StaticData.musicToOpenId));

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

                music = new Gson().fromJson(new Gson().toJson(map.get("music")), Music.class);
                openedMusic = music.getId();
                putMusic(music);
                putComments((List<Comment>) map.get("comments"));
                putLikes((Double) map.get("likesCount"));
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

    public void download(ActionEvent event){
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException(exception);
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
