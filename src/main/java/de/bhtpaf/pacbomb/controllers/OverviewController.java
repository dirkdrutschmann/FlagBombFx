package de.bhtpaf.pacbomb.controllers;

import de.bhtpaf.pacbomb.PacBomb;
import de.bhtpaf.pacbomb.helper.Game;
import de.bhtpaf.pacbomb.helper.Util;
import de.bhtpaf.pacbomb.helper.classes.User;
import de.bhtpaf.pacbomb.helper.interfaces.LogoutEventListener;
import de.bhtpaf.pacbomb.services.Api;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.*;

public class OverviewController
{

    private List<LogoutEventListener> _logoutEventListeners = new ArrayList<>();

    private final int _stdGameSpeed = 250;
    private final int _stdGameWidth = 1000;
    private final int _stdGameSquareFactor = 30;
    private final int _stdGameBombs = 10;

    private User _user;
    private Stage _mainStage;
    private Api _api;
    private Scene _previousScene;

    private Timer _userListTimer = null;

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

    @FXML
    public ListView lv_availablePlayers;

    public void logoutUser(ActionEvent event)
    {
        event.consume();

        _setFormLoading(true, "Abmeldung wird ausgeführt");

        new Thread(() -> {
            if (_api.logoutUser(_user))
            {
                _logoutEventListeners.forEach((listener) -> listener.onUserLoggedOut(_user));
                _user = null;
            }
            else
            {
                Platform.runLater(() -> {
                    Util.showErrorMessageBox("Fehler bei der Abmeldung.");
                });
            }

            Platform.runLater(() -> {
                _setFormLoading(false);
                _mainStage.setScene(_previousScene);
            });
        }).start();
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

        _setFormLoading(true, "Spiel wird geladen...");

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
                _setFormLoading(false);

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

    public void addLogoutEventListener(LogoutEventListener listener)
    {
        _logoutEventListeners.add(listener);
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

        lv_availablePlayers.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty)
            {
                super.updateItem(item, empty);

                if (empty || item == null || item.username == null)
                {
                    setText(null);
                    setGraphic(null);
                }
                else
                {
                    setText(item.username + " (Login: " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(item.lastLogon) + ")");
                }
            }
        });

        lv_availablePlayers.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2)
                {
                    if (lv_availablePlayers.getItems().size() == 0)
                    {
                        return;
                    }

                    User user = (User)lv_availablePlayers.getSelectionModel().getSelectedItem();

                    if (user == null)
                    {
                        return;
                    }

                    Util.showMessageBox(user.prename + " " + user.lastname + " (UserId: " + Integer.toString(user.id) + ")");
                }
            }
        });

        EventHandler currentHandle = _mainStage.getOnCloseRequest();

        _mainStage.setOnCloseRequest(ev -> {

            if (_userListTimer != null)
            {
                _userListTimer.cancel();
                _userListTimer.purge();

                System.out.println("UserList-Timer stopped");
            }

            currentHandle.handle(ev);
        });


        _startUserListTimer();
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

    private void _startUserListTimer()
    {
        if (_userListTimer != null)
        {
            return;
        }

        // Current logged in users
        _userListTimer = new Timer();

        _userListTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (_user == null)
                {
                    return;
                }

                List<User> loggedInUsers = _api.getLoggedInUsers(_user);

                // Eigener Nutzer nicht in Liste
                loggedInUsers.removeIf(u -> u.id == _user.id);

                if (loggedInUsers == null)
                {
                    Platform.runLater(() -> {
                        lv_availablePlayers.getItems().clear();
                    });

                    return;
                }

                boolean found = false;
                for (User user : loggedInUsers)
                {
                    found = false;
                    for (Object listUser : lv_availablePlayers.getItems())
                    {
                        if (((User)listUser).id == user.id)
                        {
                            found = true;
                            break;
                        }
                    }

                    if (found)
                    {
                        continue;
                    }

                    Platform.runLater(() -> {
                        lv_availablePlayers.getItems().add(user);
                    });
                }

                Object[] items = lv_availablePlayers.getItems().toArray();
                for (int i = 0; i < items.length; i++)
                {
                    found = false;
                    for (User user : loggedInUsers)
                    {
                        if (user.id == ((User)items[i]).id)
                        {
                            found = true;
                            break;
                        }
                    }

                    if (!found)
                    {
                        int finalI = i;
                        Platform.runLater(() -> {
                            lv_availablePlayers.getItems().remove(finalI);
                        });
                    }
                }
            }
        }, new Date(), 5000);
    }

    private void _setFormLoading(boolean startLoading)
    {
        _setFormLoading(startLoading, "");
    }

    private void _setFormLoading(boolean startLoading, String loadingText)
    {
        bt_game_start.setDisable(startLoading);
        edt_game_speed.setDisable(startLoading);
        edt_game_bombs.setDisable(startLoading);
        edt_game_width.setDisable(startLoading);
        edt_game_squareFactor.setDisable(startLoading);

        if (startLoading && img_loading.getImage() == null)
        {
            img_loading.setImage(new Image(PacBomb.class.getResourceAsStream("icons/loading.gif")));
        }

        img_loading.setVisible(startLoading);

        if (startLoading)
        {
            lb_loading.textProperty().set(loadingText);
        }

        lb_loading.setVisible(startLoading);
    }
}
