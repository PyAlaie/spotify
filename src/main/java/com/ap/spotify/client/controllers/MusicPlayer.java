package com.ap.spotify.client.controllers;

import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Music;
import com.google.gson.Gson;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayer implements Initializable {
    @FXML
    ProgressBar musicTimeline;
    @FXML
    Label musicTitleLbl;
    @FXML
    Button pausePlayBtn;
    @FXML
    Slider volumeSlider;
    @FXML
    ComboBox<String> speedBox;
    @FXML
    AnchorPane root;
    Stage stage;
    Music music;
    Timer timer;

    ArrayList<String> musics = new ArrayList<>();
    int songIndex;
    TimerTask task;
    private boolean running;
    private int[] speeds = {25, 50, 75, 100, 125, 150, 175, 200};


    MediaPlayer mediaPlayer;
    Media sound;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Request request = new Request("getMusicByName");
        request.setJson(new Gson().toJson(StaticData.musicToPlay.split("\\.")[0]));

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();
            System.out.println("test" + response.getMessage());
            music = new Gson().fromJson(response.getJson(), Music.class);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        musics = StaticData.musicsList;

        songIndex = musics.indexOf(StaticData.musicToPlay);

        try {
            String musicFile = "src/main/resources/com/ap/spotify/downloads/" + StaticData.musicToPlay;
            sound = new Media(new File(musicFile).toURI().toString());
        }
        catch (Exception e){
            download();
            String musicFile = "src/main/resources/com/ap/spotify/downloads/" + StaticData.musicToPlay;
            sound = new Media(new File(musicFile).toURI().toString());
        }
        mediaPlayer = new MediaPlayer(sound);

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });

        musicTitleLbl.setText(music.getTitle());
        for(int i = 0; i < speeds.length; i++) {
            speedBox.getItems().add(Integer.toString(speeds[i])+"%");
        }
        speedBox.setOnAction(this::changeSpeed);
        changeSpeed(null);

        beginTimer();
        mediaPlayer.play();
    }

    public void playMedia() {
        beginTimer();
        changeSpeed(null);
        mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
        mediaPlayer.play();
    }

    public void previousMedia() {
        if(songIndex > 0) {
            songIndex--;

            mediaPlayer.stop();

            if(running) {
                cancelTimer();
            }

            Request request = new Request("getMusicByName");
            request.setJson(new Gson().toJson(musics.get(songIndex).split("\\.")[0]));

            try {
                StaticData.objOut.writeObject(request);
                StaticData.objOut.flush();

                Response response = (Response) StaticData.objIn.readObject();
                System.out.println("test" + response.getMessage());
                music = new Gson().fromJson(response.getJson(), Music.class);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            try {
                String musicFile = "src/main/resources/com/ap/spotify/downloads/" + musics.get(songIndex);
                sound = new Media(new File(musicFile).toURI().toString());
            }
            catch (Exception e){
                download();
                String musicFile = "src/main/resources/com/ap/spotify/downloads/" + musics.get(songIndex);
                sound = new Media(new File(musicFile).toURI().toString());
            }
            mediaPlayer = new MediaPlayer(sound);

            musicTitleLbl.setText(musics.get(songIndex));

            playMedia();
        }
        else {
            songIndex = musics.size() - 1;
            mediaPlayer.stop();
            if(running) {
                cancelTimer();
            }

            String musicFile = "src/main/resources/com/ap/spotify/downloads/" + musics.get(songIndex);
            sound = new Media(new File(musicFile).toURI().toString());
            mediaPlayer = new MediaPlayer(sound);

            musicTitleLbl.setText(musics.get(songIndex));

            playMedia();
        }
    }

    public void nextMedia() {
        if(songIndex < musics.size() - 1) {
            songIndex++;
            mediaPlayer.stop();
            if(running) {
                cancelTimer();
            }

            Request request = new Request("getMusicByName");
            request.setJson(new Gson().toJson(musics.get(songIndex).split("\\.")[0]));

            try {
                StaticData.objOut.writeObject(request);
                StaticData.objOut.flush();

                Response response = (Response) StaticData.objIn.readObject();
                System.out.println("test" + response.getMessage());
                music = new Gson().fromJson(response.getJson(), Music.class);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            try {
                String musicFile = "src/main/resources/com/ap/spotify/downloads/" + musics.get(songIndex);
                sound = new Media(new File(musicFile).toURI().toString());
            }
            catch (Exception e){
                download();
                String musicFile = "src/main/resources/com/ap/spotify/downloads/" + musics.get(songIndex);
                sound = new Media(new File(musicFile).toURI().toString());
            }
            mediaPlayer = new MediaPlayer(sound);

            musicTitleLbl.setText(musics.get(songIndex));

            playMedia();
        }
        else {
            songIndex = 0;
            mediaPlayer.stop();
            if(running) {
                cancelTimer();
            }

            String musicFile = "src/main/resources/com/ap/spotify/downloads/" + musics.get(songIndex);
            sound = new Media(new File(musicFile).toURI().toString());
            mediaPlayer = new MediaPlayer(sound);

            musicTitleLbl.setText(musics.get(songIndex));

            playMedia();
        }
    }

    public void pauseAndPlay(ActionEvent event){
        if(mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)){
            mediaPlayer.pause();
        }
        else {
            mediaPlayer.play();
        }
    }

    public void beginTimer() {
        timer = new Timer();

        task = new TimerTask() {
            public void run() {
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = sound.getDuration().toSeconds();
                musicTimeline.setProgress(current/end);

                if(current/end == 1) {
                    cancelTimer();
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void cancelTimer() {
        running = false;
        timer.cancel();
    }

    public void changeSpeed(ActionEvent event) {
        if(speedBox.getValue() == null) {
            mediaPlayer.setRate(1);
        }
        else {
            mediaPlayer.setRate(Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01);
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
}
