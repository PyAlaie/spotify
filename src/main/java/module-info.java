module com.ap.spotify {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.ap.spotify to javafx.fxml;
    exports com.ap.spotify;
    exports com.ap.spotify.client;
    opens com.ap.spotify.client to javafx.fxml;
}