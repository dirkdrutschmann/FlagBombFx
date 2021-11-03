package de.bhtpaf.pacbomb.controllers;

import de.bhtpaf.pacbomb.helper.Game;
import de.bhtpaf.pacbomb.helper.Util;
import de.bhtpaf.pacbomb.helper.classes.User;
import de.bhtpaf.pacbomb.services.Api;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class OverviewController {
    private User _user;
    private Stage _mainStage;
    private Api _api;
    private Scene _previousScene;

    @FXML
    public Label lb_user;

    @FXML
    public ImageView img_user;

    @FXML
    public BorderPane img_pane;

    public void logoutUser(ActionEvent event)
    {
        event.consume();
        _user = null;
        _mainStage.setScene(_previousScene);
    }

    public void startGame(ActionEvent event)
    {
        event.consume();
        new Game(_mainStage, _user);
    }

    public void setUser(User user)
    {
        _user = user;
    }

    public void setMainStage(Stage stage)
    {
        _mainStage = stage;
        _previousScene = _mainStage.getScene();
    }

    public void setApi(Api api)
    {
        _api = api;
    }

    public void init()
    {
        if (_user != null)
        {
            img_user.setImage(new Image(Util.getImageOfBase64String(_user.userImageBase64)));
            img_pane.setCenter(img_user);

            lb_user.textProperty().set("Hallo " + _user.prename + "!");
        }
    }
}
