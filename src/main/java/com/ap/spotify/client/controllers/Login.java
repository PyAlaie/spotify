package com.ap.spotify.client.controllers;

import com.ap.spotify.Test;
import com.ap.spotify.client.HelloApplication;
import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Account;
import com.ap.spotify.shared.models.Artist;
import com.ap.spotify.shared.models.User;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Login implements Initializable {
    @FXML
    private TextField usernameTxt;
    @FXML
    private PasswordField passwordTxt;
    @FXML
    private Label label;
    @FXML
    ImageView logo;
    public void login(ActionEvent e) throws IOException, ClassNotFoundException {
        String username = usernameTxt.getText();
        String password =passwordTxt.getText();

        Request request = new Request("login");
        Gson gson = new Gson();
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        request.setJson(gson.toJson(account));
        StaticData.objOut.writeObject(request);
        StaticData.objOut.flush();

        System.out.println("Login request sent!");

        Response response = (Response) StaticData.objIn.readObject();
        if (response.getStatusCode() == 200) {
            String json = response.getJson();
            StaticData.isLogggedIn = true;
            StaticData.loggedInAccount = gson.fromJson(json, Account.class);

            System.out.println(response.getMessage());

            if (StaticData.isLogggedIn) {
                if (StaticData.loggedInAccount.getRole().equals("user")) {
                    StaticData.loggedInUser = gson.fromJson(json, User.class);
                    openStage("userHome.fxml");
                    closeWindow(e);
                } else {
                    StaticData.loggedInArtist = gson.fromJson(json, Artist.class);
                    openStage("artistPanel.fxml");
                    closeWindow(e);
                }
            }
        }
        else{
            label.setText(response.getMessage());
        }
    }

    public void signUp(ActionEvent e) throws IOException {
        openStage("signUp.fxml");
    }

    public void openStage(String stageName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(stageName));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    public void closeWindow(ActionEvent actionEvent){
        Node source = (Node)  actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image image = new Image(Test.class.getResource("cloud/logo.jpg").toExternalForm());
        logo.setImage(image);
    }
}
