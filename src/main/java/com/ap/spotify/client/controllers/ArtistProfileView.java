package com.ap.spotify.client.controllers;

import com.ap.spotify.Test;
import com.ap.spotify.client.HelloApplication;
import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Artist;
import com.ap.spotify.shared.models.Music;
import com.google.gson.Gson;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class ArtistProfileView implements Initializable {
    @FXML
    Label followersLbl, genreLbl, artistLbl;
    @FXML
    TextArea biographyTxt;
    @FXML
    VBox musicsVbox;
    @FXML
    ImageView coverImg;
    @FXML
    Button followBtn;

    Artist artist;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        artist = StaticData.artistToView;

        biographyTxt.setText(artist.getBiography());
        artistLbl.setText(artist.getUsername());
        genreLbl.setText("Genre: " + artist.getGenre());

        Image image = new Image(Test.class.getResource("cloud/profile.png").toExternalForm());
        if(artist.getProfilePicPath() != null){
            try {
                image = new Image(Test.class.getResource("cloud/" + artist.getProfilePicPath()).toExternalForm());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        coverImg.setImage(image);


        Request request1 = new Request("getArtistFollowerCount");
        request1.setJson(new Gson().toJson(artist.getId()));

        Request request2 = new Request("isFollowing");
        HashMap<String,Object> map = new HashMap<>();
        map.put("userId", StaticData.loggedInAccount.getId());
        map.put("artistId", artist.getId());
        request2.setJson(new Gson().toJson(map));

        Request request3 = new Request("getArtistMusics");
        request3.setJson(new Gson().toJson(artist.getId()));

        try {
            StaticData.objOut.writeObject(request1);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();
            int followers = new Gson().fromJson(response.getJson(), Integer.class);
            followersLbl.setText("Followers: " + followers);

            StaticData.objOut.writeObject(request2);
            StaticData.objOut.flush();

            response = (Response) StaticData.objIn.readObject();
            Boolean isFollowing = new Gson().fromJson(response.getJson(), Boolean.class);
            if(isFollowing){
                followBtn.setText("UnFollow");
            }

            StaticData.objOut.writeObject(request3);
            StaticData.objOut.flush();

            response = (Response) StaticData.objIn.readObject();
            Type type = new com.google.gson.reflect.TypeToken<List<Music>>(){}.getType();
            List<Music> musics = new Gson().fromJson(response.getJson(), type);

            musicsVbox.getChildren().clear();
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

                musicsVbox.getChildren().add(button);
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void followAndUnfollow(){
        if(followBtn.getText().equals("UnFollow")){
            Request request = new Request("unfollowArtist");
            HashMap<String,Object> map = new HashMap<>();
            map.put("userId", StaticData.loggedInAccount.getId());
            map.put("artistId", artist.getId());
            request.setJson(new Gson().toJson(map));

            try{
                StaticData.objOut.writeObject(request);
                StaticData.objOut.flush();

                Response response = (Response) StaticData.objIn.readObject();
                System.out.println(response.getMessage());
                if(response.getStatusCode() == 201){
                    followBtn.setText("Follow");
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            Request request = new Request("followArtist");
            HashMap<String,Object> map = new HashMap<>();
            map.put("userId", StaticData.loggedInAccount.getId());
            map.put("artistId", artist.getId());
            request.setJson(new Gson().toJson(map));

            try{
                StaticData.objOut.writeObject(request);
                StaticData.objOut.flush();

                Response response = (Response) StaticData.objIn.readObject();
                System.out.println(response.getMessage());
                if(response.getStatusCode() == 201){
                    followBtn.setText("UnFollow");
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
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
