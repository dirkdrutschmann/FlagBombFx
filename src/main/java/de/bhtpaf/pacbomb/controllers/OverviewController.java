package de.bhtpaf.pacbomb.controllers;

import de.bhtpaf.pacbomb.helper.Game;
import de.bhtpaf.pacbomb.helper.classes.User;
import de.bhtpaf.pacbomb.services.Api;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class OverviewController {
    private User _user;
    private Stage _mainStage;
    private Api _api;

    @FXML
    public Label lb_user;

    public void logoutUser(ActionEvent event)
    {
        event.consume();
    }

    public void startGame(ActionEvent event)
    {
        event.consume();
        Game game = new Game(_mainStage);
    }

    public void setUser(User user)
    {
        _user = user;
    }

    public void setMainStage(Stage stage)
    {
        _mainStage = stage;
    }

    public void setApi(Api api)
    {
        _api = api;
    }

    public void init()
    {
        if (_user != null)
        {
            lb_user.textProperty().set("Hallo " + _user.prename + "!");
        }
    }
}
