package com.ap.spotify.client.controllers;

import com.ap.spotify.shared.Request;
import com.ap.spotify.shared.Response;
import com.ap.spotify.shared.models.Album;
import com.ap.spotify.shared.models.Music;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class Search {
    @FXML
    TextField searchTxt;
    @FXML
    ListView<Button> searchRes;

    public void search(ActionEvent event){
        String searchedExpression = searchTxt.getText();

        Request request = new Request("search");
        request.setJson(new Gson().toJson(searchedExpression));

        try {
            StaticData.objOut.writeObject(request);
            StaticData.objOut.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Response response = (Response) StaticData.objIn.readObject();

            Type type = new com.google.gson.reflect.TypeToken<HashMap<String,Object>>(){}.getType();
            HashMap<String, Object> map = new Gson().fromJson(response.getJson(), type);
            System.out.println(map);

            List<Music> musics = (List<Music>) map.get("musics");
            for(int i = 0; i < musics.size(); i++){
                Music music1 = new Gson().fromJson(new Gson().toJson(musics.get(i)), Music.class);
                // TODO: fill this function
                Button button = new Button(music1.getTitle());
                searchRes.getItems().add(button);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
