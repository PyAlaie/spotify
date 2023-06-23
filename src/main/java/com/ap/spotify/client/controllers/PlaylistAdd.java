package com.ap.spotify.client.controllers;

import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Playlist;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

public class PlaylistAdd {
    @FXML
    TextField titleTxt;
    @FXML
    TextArea descriptionTxt;
    @FXML
    RadioButton publicBtn, privateBtn;
    public void save(){
        Playlist playlist = new Playlist();
        playlist.setTitle(titleTxt.getText());
        playlist.setDescription(descriptionTxt.getText());
        playlist.setUser(StaticData.loggedInAccount.getId());
        if (publicBtn.isSelected()){
            playlist.setPublic(true);
        }

        Request request = new Request("newPlaylist");
        request.setJson(new Gson().toJson(playlist));

        try{
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Adding playlist!");
            alert.setHeaderText(response.getMessage());
            alert.show();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
