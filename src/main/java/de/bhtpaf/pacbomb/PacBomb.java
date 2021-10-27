package de.bhtpaf.pacbomb;

import de.bhtpaf.pacbomb.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class PacBomb extends Application  {

    public void start(Stage primaryStage)  {

        try
        {
            FXMLLoader loader = new FXMLLoader(PacBomb.class.getResource("main.fxml"));
            Parent root = (Parent)loader.load();
            Scene scene = new Scene(root, 1000, 600);

            MainController mainController = (MainController)loader.getController();
            mainController.setMainStage(primaryStage);

            primaryStage.setTitle("PacBomb");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    }

