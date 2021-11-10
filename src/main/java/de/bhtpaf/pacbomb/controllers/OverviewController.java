package de.bhtpaf.pacbomb.controllers;

import de.bhtpaf.pacbomb.PacBomb;
import de.bhtpaf.pacbomb.helper.Game;
import de.bhtpaf.pacbomb.helper.Util;
import de.bhtpaf.pacbomb.helper.classes.User;
import de.bhtpaf.pacbomb.helper.interfaces.MessageHandler;
import de.bhtpaf.pacbomb.services.Api;
import de.bhtpaf.pacbomb.services.WebsocketClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URI;

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

    @FXML
    public Button bt_game_start;

    @FXML
    public ImageView img_loading;

    @FXML
    public Label lb_loading;

    public void logoutUser(ActionEvent event)
    {
        event.consume();
        _user = null;
        _mainStage.setScene(_previousScene);
    }

    public void startGame(ActionEvent event)
    {
        event.consume();

        /*final WebsocketClient wsClient = new WebsocketClient(URI.create(_api.getWebSocketUrl()));

        wsClient.addMessageHandler(new MessageHandler() {
            @Override
            public void handleMessage(String message) {
                System.out.println(message);
            }
        });*/

        bt_game_start.setDisable(true);
        edt_game_speed.setDisable(true);
        edt_game_bombs.setDisable(true);
        edt_game_width.setDisable(true);
        edt_game_squareFactor.setDisable(true);

        if (img_loading.getImage() == null)
        {
            img_loading.setImage(new Image(PacBomb.class.getResourceAsStream("loading.gif")));
        }

        img_loading.setVisible(true);
        lb_loading.setVisible(true);

        int speed = _getValue(edt_game_speed, _stdGameSpeed);
        int width = _getValue(edt_game_width, _stdGameWidth);
        int squareFactor = _getValue(edt_game_squareFactor, _stdGameSquareFactor);
        int bombs = _getValue(edt_game_bombs, _stdGameBombs);

        Game game = new Game(_api, _mainStage, _user, speed, width, squareFactor, bombs);

        new Thread(() -> {
            try
            {
                game.init();
                game.generateGrid();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Util.showErrorMessageBox(e.getMessage());
                });
            }

            Platform.runLater(() -> {
                img_loading.setVisible(false);
                lb_loading.setVisible(false);

                bt_game_start.setDisable(false);
                edt_game_speed.setDisable(false);
                edt_game_bombs.setDisable(false);
                edt_game_width.setDisable(false);
                edt_game_squareFactor.setDisable(false);

                try
                {
                    game.show();
                }
                catch (Exception e)
                {
                    Util.showErrorMessageBox(e.getMessage());
                }
            });
        }).start();
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
