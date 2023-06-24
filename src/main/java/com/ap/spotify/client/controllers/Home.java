package com.ap.spotify.client.controllers;

import com.ap.spotify.Test;
import com.ap.spotify.client.HelloApplication;
import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Album;
import com.ap.spotify.shared.models.Music;
import com.google.gson.Gson;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Home implements Initializable {
    @FXML
    HBox newMusics, newAlbums, recom;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadNewMusics();
        loadNewAlbums();
        loadRecommendedMusics();
    }

    private void loadRecommendedMusics() {
        List<Music> musics = getRecommendedMusics(StaticData.loggedInUser.getId());

        for (Music music : musics) {
            Image image = new Image(Test.class.getResource("cloud/image.jpg").toExternalForm());
            ImageView musicCover = new ImageView();
            musicCover.setImage(image);
            if (music.getCoverPicPath() != null) {
                try {
                    musicCover = new ImageView(Test.class.getResource("cloud/").toExternalForm() + music.getCoverPicPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            musicCover.setFitHeight(110);
            musicCover.setPreserveRatio(false);
            musicCover.setFitWidth(130);
            musicCover.setFitHeight(130);

            Label title = new Label(music.getTitle());
            title.setPrefWidth(Double.MAX_VALUE);
            title.setFont(new Font(18));
            title.setBackground(Background.fill(Color.BLACK));
            title.setTextFill(Paint.valueOf(String.valueOf(Color.WHITE)));
            AnchorPane.setBottomAnchor(title, 0.0);
            AnchorPane.setRightAnchor(title, 0.0);
            AnchorPane.setLeftAnchor(title, 0.0);
            title.setAlignment(Pos.CENTER);

            VBox musicPane = new VBox();
            musicPane.getChildren().add(musicCover);
            musicPane.getChildren().add(title);

            musicPane.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                    StaticData.musicToOpenId = music.getId();

                    try {
                        openStage("musicView.fxml");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            recom.getChildren().add(musicPane);
        }
    }

    private List<Music> getRecommendedMusics(int userId) {
        Request request = new Request("getRecommendedMusics");
        request.setJson(new Gson().toJson(userId));

        List<Music> musics = new ArrayList<>();
        try{
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();
            System.out.println(response.getMessage());

            if(response.getStatusCode() == 200){
                Type type = new com.google.gson.reflect.TypeToken<List<Music>>(){}.getType();
                musics = new Gson().fromJson(response.getJson(), type);
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return musics;
    }

    private void loadNewAlbums() {
        try {
            ArrayList<Album> albums = getNewAlbums();
            if(albums != null){
                for (Album album : albums){
                    Image image = new Image(Test.class.getResource("cloud/image.jpg").toExternalForm());
                    ImageView albumCover = new ImageView();
                    albumCover.setImage(image);
                    if (album.getCoverPicPath() != null) {
                        try {
                            albumCover = new ImageView(Test.class.getResource("cloud/").toExternalForm() + album.getCoverPicPath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    albumCover.setFitHeight(110);
                    albumCover.setPreserveRatio(false);
                    albumCover.setFitWidth(130);
                    albumCover.setFitHeight(130);

                    Label title = new Label(album.getTitle());
                    title.setPrefWidth(Double.MAX_VALUE);
                    title.setFont(new Font(18));
                    title.setBackground(Background.fill(Color.BLACK));
                    title.setTextFill(Paint.valueOf(String.valueOf(Color.WHITE)));
                    AnchorPane.setBottomAnchor(title, 0.0);
                    AnchorPane.setRightAnchor(title, 0.0);
                    AnchorPane.setLeftAnchor(title, 0.0);
                    title.setAlignment(Pos.CENTER);

                    VBox musicPane = new VBox();
                    musicPane.getChildren().add(albumCover);
                    musicPane.getChildren().add(title);

                    musicPane.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            StaticData.albumToView = album;

                            try {
                                openStage("albumView.fxml");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                    newAlbums.getChildren().add(musicPane);
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadNewMusics() {
        try {
            ArrayList<Music> musics = getNewMusics();

            for (Music music : musics) {
                Image image = new Image(Test.class.getResource("cloud/image.jpg").toExternalForm());
                ImageView musicCover = new ImageView();
                musicCover.setImage(image);
                if (music.getCoverPicPath() != null) {
                    try {
                        musicCover = new ImageView(Test.class.getResource("cloud/").toExternalForm() + music.getCoverPicPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                musicCover.setFitHeight(110);
                musicCover.setPreserveRatio(false);
                musicCover.setFitWidth(130);
                musicCover.setFitHeight(130);

                Label title = new Label(music.getTitle());
                title.setPrefWidth(Double.MAX_VALUE);
                title.setFont(new Font(18));
                title.setBackground(Background.fill(Color.BLACK));
                title.setTextFill(Paint.valueOf(String.valueOf(Color.WHITE)));
                AnchorPane.setBottomAnchor(title, 0.0);
                AnchorPane.setRightAnchor(title, 0.0);
                AnchorPane.setLeftAnchor(title, 0.0);
                title.setAlignment(Pos.CENTER);

                VBox musicPane = new VBox();
                musicPane.getChildren().add(musicCover);
                musicPane.getChildren().add(title);

                musicPane.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent event) {
                        StaticData.musicToOpenId = music.getId();

                        try {
                            openStage("musicView.fxml");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                newMusics.getChildren().add(musicPane);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Music> getNewMusics() throws IOException, ClassNotFoundException {
        Request request = new Request("getNewMusics");
        StaticData.objOut.writeObject(request);
        StaticData.objOut.flush();

        Response response = (Response) StaticData.objIn.readObject();
        if(response.getStatusCode() == 200){
            Gson gson = new Gson();

            Type type = new com.google.gson.reflect.TypeToken<ArrayList<Music>>(){}.getType();
            ArrayList<Music> musics = gson.fromJson(response.getJson(), type);

            return musics;
        }

        return null;
    }

    public ArrayList<Album> getNewAlbums() throws IOException, ClassNotFoundException {
        Request request = new Request("getNewAlbums");
        StaticData.objOut.writeObject(request);
        StaticData.objOut.flush();

        ArrayList<Album> albums = new ArrayList<>();
        Response response = (Response) StaticData.objIn.readObject();
        System.out.println(response.getMessage());
        if(response.getStatusCode() == 200){
            Gson gson = new Gson();

            Type type = new com.google.gson.reflect.TypeToken<ArrayList<Album>>(){}.getType();
            albums = gson.fromJson(response.getJson(), type);

        }

        return albums;
    }

    public void openStage(String stageName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(stageName));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
