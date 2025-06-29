package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Główna klasa aplikacji klienta, inicjalizacja interfejsu i uruchomienie aplikacji
public class ClientMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Ładowanie struktury interfejsu z pliku FXML
        Parent root = FXMLLoader.load(getClass().getResource("view.fxml"));

        // Konfiguracja okna głównego
        primaryStage.setTitle("Klient");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Uruchomienie aplikacji
    }
}
