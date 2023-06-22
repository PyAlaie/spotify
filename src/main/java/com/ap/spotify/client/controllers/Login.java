package com.ap.spotify.client.controllers;

import com.ap.spotify.client.HelloApplication;
import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Account;
import com.ap.spotify.shared.models.User;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class Login {
    @FXML
    private TextField usernameTxt;
    @FXML
    private TextField passwordTxt;
    @FXML
    private Label label;
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
        if (response.getStatusCode() == 200){
            String json = response.getJson();
            StaticData.isLogggedIn = true;
            StaticData.loggedInAccount = gson.fromJson(json, Account.class);
            StaticData.loggedInUser = gson.fromJson(json, User.class);
        }
        else {
            label.setText(response.getMessage());
        }

        System.out.println(response.getMessage());

        if(StaticData.isLogggedIn){
            if (StaticData.loggedInAccount.getRole().equals("user")){
                openStage("userHome.fxml");
                closeWindow(e);
            }
            else {
                openStage("artistPanel.fxml");
                closeWindow(e);
            }
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
}
