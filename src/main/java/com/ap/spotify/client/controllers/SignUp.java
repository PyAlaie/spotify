package com.ap.spotify.client.controllers;

import com.ap.spotify.Test;
import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Account;
import com.ap.spotify.shared.models.Artist;
import com.ap.spotify.shared.models.User;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignUp implements Initializable {
    @FXML
    RadioButton userRB;
    @FXML
    RadioButton artistRB;
    @FXML
    TextField usernameTxt;
    @FXML
    PasswordField passwordTxt;
    @FXML
    Label label;
    @FXML
    ImageView logo;

    public void signUp(ActionEvent e){
        String username = usernameTxt.getText();
        String password = passwordTxt.getText();

        Request request = null;
        if(userRB.isSelected()){
            request = new Request("newUser");
            Gson gson = new Gson();
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            request.setJson(gson.toJson(user));
        }
        else if(artistRB.isSelected()){
            request = new Request("newArtist");
            Gson gson = new Gson();
            Artist artist = new Artist();
            artist.setUsername(username);
            artist.setPassword(password);
            request.setJson(gson.toJson(artist));
        }

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();

            Response response = (Response) StaticData.objIn.readObject();
            label.setText(response.getMessage());
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image image = new Image(Test.class.getResource("cloud/logo.jpg").toExternalForm());
        logo.setImage(image);
    }
}
