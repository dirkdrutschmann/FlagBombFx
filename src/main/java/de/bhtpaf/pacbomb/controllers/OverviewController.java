package de.bhtpaf.pacbomb.controllers;

import de.bhtpaf.pacbomb.PacBomb;
import de.bhtpaf.pacbomb.helper.Game;
import de.bhtpaf.pacbomb.helper.Util;
import de.bhtpaf.pacbomb.helper.classes.User;
import de.bhtpaf.pacbomb.helper.interfaces.LogoutEventListener;
import de.bhtpaf.pacbomb.helper.responses.PlayingPair;
import de.bhtpaf.pacbomb.helper.responses.PlayingPairStatus;
import de.bhtpaf.pacbomb.helper.responses.StdResponse;
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
    private Timer _incomingPlayRequestsTimer = null;
    private Timer _outgoingPlayRequestsTimer = null;

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
    public ImageView img_logo;

    @FXML
    public Label lb_loading;

    @FXML
    public ListView lv_availablePlayers;

    @FXML
    public ListView lv_incomingRequest;

    @FXML
    public ListView lv_outgoingRequest;

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

        if (img_logo.getImage() == null)
        {
            img_logo.setImage(new Image(PacBomb.class.getResourceAsStream("icons/logo.png")));
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

        lv_incomingRequest.setCellFactory(param -> _getPlayRequestListCell(false));
        lv_outgoingRequest.setCellFactory(param -> _getPlayRequestListCell(true));

        // Click Event
        lv_availablePlayers.setOnMouseClicked(_getAvailablePlayersOnMouseClickedHandler());
        lv_incomingRequest.setOnMouseClicked(_getIncomingRequestOnMouseClickHandler());

        EventHandler currentHandle = _mainStage.getOnCloseRequest();

        _mainStage.setOnCloseRequest(ev -> {

            if (_userListTimer != null)
            {
                _userListTimer.cancel();
                _userListTimer.purge();

                System.out.println("UserList-Timer was stopped");
            }

            if (_incomingPlayRequestsTimer != null)
            {
                _incomingPlayRequestsTimer.cancel();
                _incomingPlayRequestsTimer.purge();

                System.out.println("IncomingRequest-Timer was stopped");
            }

            if (_outgoingPlayRequestsTimer != null)
            {
                _outgoingPlayRequestsTimer.cancel();
                _outgoingPlayRequestsTimer.purge();

                System.out.println("OutgoingRequest-Timer was stopped");
            }

            currentHandle.handle(ev);
        });

        // Start timer for current logged in users
        _startUserListTimer();

        // Start timer for incoming play request
        _startIncomingPlayRequestTimer();

        // Start timer for outgoing play request
        _startOutgoingPlayRequestTimer();

    }

    private EventHandler<MouseEvent> _getAvailablePlayersOnMouseClickedHandler()
    {
        return new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
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

                    Alert yesNoAlert = Util.getYesNoMessageBox("Soll eine Spieleanfrage an " + user.username + " gesendet werden?", "Spielanfrage senden?");
                    yesNoAlert.showAndWait().ifPresent(type ->
                    {
                        if (type.getButtonData() == ButtonBar.ButtonData.YES)
                        {
                            // Anfrage senden
                            System.out.println("Anfrage an UserId" + user.id + " senden");

                            new Thread(() -> {
                                StdResponse result = _api.sendPlayRequest(_user, user.id);

                                if (result.success == false)
                                {
                                    Platform.runLater(() -> {
                                        Util.showErrorMessageBox(result.message);
                                    });
                                }
                                else
                                {
                                    Platform.runLater(() -> {
                                        Util.showMessageBox(result.message);
                                    });
                                }
                            }).start();

                            yesNoAlert.close();
                        }
                    });
                }
            }
        };
    }

    private EventHandler<MouseEvent> _getIncomingRequestOnMouseClickHandler()
    {
        return new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                if(event.getButton() != MouseButton.PRIMARY || event.getClickCount() == 2)
                {
                    return;
                }

                if (lv_incomingRequest.getItems().size() == 0)
                {
                    return;
                }

                PlayingPair pair = (PlayingPair)lv_incomingRequest.getSelectionModel().getSelectedItem();

                if (pair == null)
                {
                    return;
                }

                if (pair.status == PlayingPairStatus.ACCEPTED)
                {
                    return;
                }

                List<ButtonType> buttons = new ArrayList<>();
                buttons.add(new ButtonType("Annehmen", ButtonBar.ButtonData.YES));
                buttons.add(new ButtonType("Ablehnen", ButtonBar.ButtonData.NO));
                buttons.add(new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE));

                Alert alert = Util.getCustomMessageBox("Anfrage von " + pair.requestingUser.username, "Eingehende Spieleanfrage", buttons);
                alert.showAndWait().ifPresent(type ->
                {
                    if (type.getButtonData() == ButtonBar.ButtonData.YES)
                    {
                        new Thread(() ->
                        {
                            StdResponse response = _api.acceptIncomingPlayRequest(_user, pair.requestingUser.id);

                            if (response.success)
                            {
                                Platform.runLater(() ->
                                {

                                    Util.showMessageBox(response.message);
                                });
                            }
                            else
                            {
                                Platform.runLater(() ->
                                {
                                    Util.showErrorMessageBox(response.message);
                                });
                            }
                        }).start();
                    }
                    else if (type.getButtonData() == ButtonBar.ButtonData.NO)
                    {
                        new Thread(() ->
                        {
                            StdResponse response = _api.rejectIncomingPlayRequest(_user, pair.requestingUser.id);

                            if (response.success)
                            {
                                Platform.runLater(() ->
                                {
                                    Util.showMessageBox(response.message);
                                });
                            }
                            else
                            {
                                Platform.runLater(() ->
                                {
                                    Util.showErrorMessageBox(response.message);
                                });
                            }
                        }).start();
                    }

                    alert.close();
                });
            }
        };
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

    private void _startIncomingPlayRequestTimer()
    {
        if (_incomingPlayRequestsTimer != null)
        {
            return;
        }

        _incomingPlayRequestsTimer = new Timer();

        _incomingPlayRequestsTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (_user == null)
                {
                    return;
                }

                List<PlayingPair> incomingRequest = _api.getIncomingPlayRequest(_user);

                if (incomingRequest == null)
                {
                    Platform.runLater(() -> {
                        lv_incomingRequest.getItems().clear();
                    });

                    return;
                }

                boolean found = false;
                for (PlayingPair pair : incomingRequest)
                {
                    found = false;
                    for (Object listPair : lv_incomingRequest.getItems())
                    {
                        if (((PlayingPair)listPair).id == pair.id)
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
                        lv_incomingRequest.getItems().add(pair);
                    });
                }

                Object[] items = lv_incomingRequest.getItems().toArray();
                for (int i = 0; i < items.length; i++)
                {
                    found = false;
                    for (PlayingPair pair : incomingRequest)
                    {
                        if (pair.id == ((PlayingPair)items[i]).id)
                        {
                            found = true;
                            break;
                        }
                    }

                    if (!found)
                    {
                        int finalI = i;
                        Platform.runLater(() -> {
                            lv_incomingRequest.getItems().remove(finalI);
                        });
                    }
                }
            }
        }, new Date(), 2500);
    }

    private void _startOutgoingPlayRequestTimer()
    {
        if (_outgoingPlayRequestsTimer != null)
        {
            return;
        }

        _outgoingPlayRequestsTimer = new Timer();

        _outgoingPlayRequestsTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (_user == null)
                {
                    return;
                }

                List<PlayingPair> outgoingRequest = _api.getOutgoingPlayRequest(_user);

                if (outgoingRequest == null)
                {
                    Platform.runLater(() -> {
                        lv_outgoingRequest.getItems().clear();
                    });

                    return;
                }

                boolean found = false;
                for (PlayingPair pair : outgoingRequest)
                {
                    found = false;
                    for (Object listPair : lv_outgoingRequest.getItems())
                    {
                        if (((PlayingPair)listPair).id == pair.id)
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
                        lv_outgoingRequest.getItems().add(pair);
                    });
                }

                Object[] items = lv_outgoingRequest.getItems().toArray();
                for (int i = 0; i < items.length; i++)
                {
                    found = false;
                    for (PlayingPair pair : outgoingRequest)
                    {
                        if (pair.id == ((PlayingPair)items[i]).id)
                        {
                            found = true;
                            break;
                        }
                    }

                    if (!found)
                    {
                        int finalI = i;
                        Platform.runLater(() -> {
                            lv_outgoingRequest.getItems().remove(finalI);
                        });
                    }
                }
            }
        }, new Date(), 2500);
    }

    private ListCell<PlayingPair> _getPlayRequestListCell(boolean outgoing)
    {
        return new ListCell<PlayingPair>() {
            @Override
            protected void updateItem(PlayingPair item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.id == null)
                {
                    setText(null);
                    setGraphic(null);
                }
                else
                {
                    String txt = "Anfrage von " + item.requestingUser.username + (item.status == PlayingPairStatus.ACCEPTED ? " (Angenommen)" : "");

                    if (outgoing)
                    {
                        txt = "Anfrage an " + item.requestedUser.username;
                    }

                    setText(txt);
                }
            }
        };
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
