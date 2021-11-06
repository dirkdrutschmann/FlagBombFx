package de.bhtpaf.pacbomb.controllers;

import de.bhtpaf.pacbomb.helper.Game;
import de.bhtpaf.pacbomb.helper.Util;
import de.bhtpaf.pacbomb.helper.classes.User;
import de.bhtpaf.pacbomb.services.Api;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class OverviewController {

    private final int _stdGameSpeed = 250;
    private final int _stdGameWidth = 1000;
    private final int _stdGameSquareFactor = 30;
    private final int _stdGameBombs = 10;

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

    @FXML
    public TextField edt_game_speed;

    @FXML
    public TextField edt_game_bombs;

    @FXML
    public TextField edt_game_width;

    @FXML
    public TextField edt_game_squareFactor;

    public void logoutUser(ActionEvent event)
    {
        event.consume();
        _user = null;
        _mainStage.setScene(_previousScene);
    }

    public void startGame(ActionEvent event)
    {
        event.consume();

        int speed = _getValue(edt_game_speed, _stdGameSpeed);
        int width = _getValue(edt_game_width, _stdGameWidth);
        int squareFactor = _getValue(edt_game_squareFactor, _stdGameSquareFactor);
        int bombs = _getValue(edt_game_bombs, _stdGameBombs);

        new Game(_mainStage, _user, speed, width, squareFactor, bombs);
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

        edt_game_speed.textProperty().set(Integer.toString(_stdGameSpeed));
        edt_game_width.textProperty().set(Integer.toString(_stdGameWidth));
        edt_game_squareFactor.textProperty().set(Integer.toString(_stdGameSquareFactor));

        edt_game_bombs.textProperty().set(Integer.toString(_stdGameBombs));
    }

    private int _getValue(TextField edt, int stdValue)
    {
        String val = edt.textProperty().get();
        int returnVal = stdValue;
        try
        {
            returnVal = Integer.parseInt(val);
            edt.styleProperty().set("");
        }
        catch (NumberFormatException e)
        {
            edt.styleProperty().set("-fx-text-box-border: red;");
        }

        return returnVal;
    }
}
