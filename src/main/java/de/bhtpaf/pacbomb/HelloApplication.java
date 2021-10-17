package de.bhtpaf.pacbomb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setTitle("Pac-Bomb!");
        stage.setScene(scene);
        stage.show();
        scene.setOnKeyPressed(e -> System.out.println(e.getCode()));
    }

    public static void main(String[] args) {
        launch();
    }
}