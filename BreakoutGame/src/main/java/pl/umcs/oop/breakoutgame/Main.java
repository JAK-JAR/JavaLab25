package pl.umcs.oop.breakoutgame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Pane root = new Pane();

        GameCanvas canvas = new GameCanvas(800, 600);
        root.getChildren().add(canvas);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Breakout Game");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}