package de.bhtpaf.pacbomb.controllers;

import de.bhtpaf.pacbomb.helper.Game;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainController {
    private Stage _mainStage;

    @FXML
    public TextField edt_username;

    @FXML
    public TextField edt_password;

    @FXML
    public void loginUser(ActionEvent event)
    {
        event.consume();
        Alert msg = new Alert(Alert.AlertType.INFORMATION);
        msg.setContentText("Hello World");
        msg.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK)
            {
                Game game = new Game(_mainStage);
            }
        });
    }

    public void setMainStage(Stage stage)
    {
        _mainStage = stage;
    }
}
