package com.ap.spotify.client.controllers;

import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Comment;
import com.ap.spotify.shared.models.Music;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

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

    private int openedMusic;

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

                Music music = new Gson().fromJson(new Gson().toJson(map.get("music")), Music.class);
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
        artist.setText(artist.getText()+ music.getArtist());
        popularity.setText(popularity.getText()+ music.getPopularity());
        // TODO: write this function
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

    public void download(ActionEvent event){
        // TODO: download music
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
        }
    }
}
