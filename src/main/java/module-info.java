module com.ap.spotify {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires javafx.media;

    opens com.ap.spotify to javafx.fxml;
    exports com.ap.spotify;
    exports com.ap.spotify.client;
    opens com.ap.spotify.client to javafx.fxml;
    opens com.ap.spotify.shared.models to com.google.gson;
    exports com.ap.spotify.shared;
    opens com.ap.spotify.shared to javafx.fxml;
    exports com.ap.spotify.client.controllers;
    opens com.ap.spotify.client.controllers to javafx.fxml;
}