package com.ap.spotify.client.controllers;

import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Account;
import com.ap.spotify.shared.models.Artist;
import com.ap.spotify.shared.models.User;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.io.IOException;

public class SignUp {
    @FXML
    RadioButton userRB;
    @FXML
    RadioButton artistRB;
    @FXML
    TextField usernameTxt;
    @FXML
    TextField passwordTxt;
    @FXML
    Label label;

    public void signUp(ActionEvent e) throws IOException, ClassNotFoundException {
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
        
        StaticData.objOut.writeObject(request);
        StaticData.objOut.flush();

        Response response = (Response) StaticData.objIn.readObject();
        label.setText(response.getMessage());
    }
}
