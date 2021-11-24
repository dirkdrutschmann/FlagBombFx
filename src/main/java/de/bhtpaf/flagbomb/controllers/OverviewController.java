package de.bhtpaf.flagbomb.controllers;

import de.bhtpaf.flagbomb.FlagBomb;
import de.bhtpaf.flagbomb.helper.BomberMan;
import de.bhtpaf.flagbomb.helper.Game;
import de.bhtpaf.flagbomb.helper.Util;
import de.bhtpaf.flagbomb.helper.classes.User;
import de.bhtpaf.flagbomb.helper.classes.map.Grid;
import de.bhtpaf.flagbomb.helper.interfaces.eventListener.BomberManGeneratedListener;
import de.bhtpaf.flagbomb.helper.interfaces.eventListener.GameOverListener;
import de.bhtpaf.flagbomb.helper.interfaces.eventListener.LogoutEventListener;
import de.bhtpaf.flagbomb.helper.interfaces.eventListener.MapGeneratedListener;
import de.bhtpaf.flagbomb.helper.responses.PlayingPair;
import de.bhtpaf.flagbomb.helper.responses.PlayingPairStatus;
import de.bhtpaf.flagbomb.helper.responses.StdResponse;
import de.bhtpaf.flagbomb.services.Api;
import de.bhtpaf.flagbomb.services.WebsocketClient;
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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

public class OverviewController implements MapGeneratedListener, GameOverListener, BomberManGeneratedListener
{
    private boolean _StopAllThreads = false;
    private boolean _StopWaitingForPartnerThread = false;

    private List<LogoutEventListener> _logoutEventListeners = new ArrayList<>();

    private final int _stdGameSpeed = 250;
    private final int _stdGameWidth = 1000;
    private final int _stdGameSquareFactor = 30;
    private final int _stdGameBombs = 10;

    private Grid _mapGeneratedByWebSocket = null;
    private PlayingPair _playingPair = null;
    private List<BomberMan> _playersGeneratedByWebSocket = null;

    private User _user;
    private Stage _mainStage;
    private Api _api;
    private Scene _previousScene;

    private Timer _userListTimer = null;
    private Timer _incomingPlayRequestsTimer = null;
    private Timer _outgoingPlayRequestsTimer = null;

    private Thread _waitingForPartnerThread = null;

    private WebsocketClient _wsClient = null;

    @FXML
    public Label lb_user;

    @FXML
    public ImageView img_user;

    @FXML
    public BorderPane img_pane;

    @FXML
    public Button bt_logoff;

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

    @FXML
    public StackPane sp_overlay;

    @FXML
    public ImageView img_waiting_logo;

    @FXML
    public Label lb_waiting_text;

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

        _setFormLoading(true, "Spiel wird geladen...");

        int speed = _getValue(edt_game_speed, _stdGameSpeed);
        int width = _getValue(edt_game_width, _stdGameWidth);
        int squareFactor = _getValue(edt_game_squareFactor, _stdGameSquareFactor);
        int bombs = _getValue(edt_game_bombs, _stdGameBombs);

        Game game = new Game(_api, _wsClient, _mainStage, _user, speed, width, squareFactor, bombs, _mapGeneratedByWebSocket, _playersGeneratedByWebSocket, _playingPair);

        new Thread(() -> {
            try
            {
                game.init();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Util.showErrorMessageBox(e.getMessage());
                });
            }

            game.addOnGameOverListener(this::onGameOver);

            Platform.runLater(() -> {
                _setFormLoading(false);

                try
                {
                    _stopAllTimers();
                    game.show();
                }
                catch (Exception e)
                {
                    Util.showErrorMessageBox(e.getMessage());
                }
            });
        }).start();
    }

    public void stopWaiting(ActionEvent event)
    {
        event.consume();
        _StopWaitingForPartnerThread = true;
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
            img_logo.setImage(new Image(FlagBomb.class.getResourceAsStream("icons/logo.png")));
        }

        if (img_waiting_logo.getImage() == null)
        {
            img_waiting_logo.setImage(new Image(FlagBomb.class.getResourceAsStream("icons/logo.png")));
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
        lv_outgoingRequest.setOnMouseClicked(_getOutgoingRequestOnMouseClickHandler());

        EventHandler currentHandle = _mainStage.getOnCloseRequest();

        _mainStage.setOnCloseRequest(ev -> {
            _stopAllTimers();

            _StopAllThreads = true;
            System.out.println("StopAllThreads was set to true.");

            if (_wsClient != null && _wsClient.isOpen())
            {
                _wsClient.close();
                System.out.println("WebSocket was closed");
            }

            currentHandle.handle(ev);
        });

        _startAllTimers();

    }

    @Override
    public void onMapGenerated(Grid map)
    {
        _mapGeneratedByWebSocket = map;
    }

    @Override
    public void onGameOver(PlayingPair pair)
    {
        if (_wsClient != null)
        {
            if (_wsClient.isOpen())
            {
                _wsClient.close();
                _wsClient = null;
            }

            _api.setGameOverStatus(_user, pair);

            _mapGeneratedByWebSocket = null;
            _playersGeneratedByWebSocket = null;
        }

        _startAllTimers();
    }

    @Override
    public void onBomberManGenerated(BomberMan bomberMan)
    {
        if (_playersGeneratedByWebSocket == null)
        {
            _playersGeneratedByWebSocket = new ArrayList<>();
        }

        _playersGeneratedByWebSocket.add(bomberMan);
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
                            System.out.println("Anfrage an UserId " + user.id + " senden");

                            new Thread(() -> {
                                Grid mapConfig = new Grid(
                                        _getValue(edt_game_width, _stdGameWidth),
                                        _getValue(edt_game_width, _stdGameWidth),
                                        _getValue(edt_game_squareFactor, _stdGameSquareFactor)
                                );

                                StdResponse result = _api.sendPlayRequest(_user, user.id, mapConfig);

                                if (result.success)
                                {
                                    Platform.runLater(() -> {
                                        Util.showMessageBox(result.message);
                                    });


                                }
                                else
                                {
                                    Platform.runLater(() -> {
                                        Util.showErrorMessageBox(result.message);
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
                                _connectToWebSocket(pair);
                                _checkPartnerConnectionAsync(pair);
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

    private EventHandler<MouseEvent> _getOutgoingRequestOnMouseClickHandler()
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

                if (lv_outgoingRequest.getItems().size() == 0)
                {
                    return;
                }

                PlayingPair pair = (PlayingPair)lv_outgoingRequest.getSelectionModel().getSelectedItem();

                if (pair == null)
                {
                    return;
                }

                if (pair.status != PlayingPairStatus.ACCEPTED)
                {
                    return;
                }

                List<ButtonType> buttons = new ArrayList<>();
                buttons.add(new ButtonType("Spiel starten", ButtonBar.ButtonData.YES));
                buttons.add(new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE));

                Alert gameStartAlert = Util.getCustomMessageBox("Spiel mit " + pair.requestedUser.username + " starten?", "Spiel starten?", buttons);
                gameStartAlert.showAndWait().ifPresent(type ->
                {
                    if (type.getButtonData() == ButtonBar.ButtonData.YES)
                    {
                        _connectToWebSocket(pair);
                        _checkPartnerConnectionAsync(pair);
                    }
                    gameStartAlert.close();
                });
            }
        };
    }

    private void _checkPartnerConnectionAsync(PlayingPair pair)
    {
        _StopWaitingForPartnerThread = false;

        _waitingForPartnerThread = new Thread(() -> {
            boolean isConnected = false;

            Platform.setImplicitExit(false);

            Platform.runLater(() -> {
                _disableForm(true, true);
                sp_overlay.visibleProperty().set(true);
            });

            String points = "";
            int i = 0;

            while (!isConnected && i < 60 && !_StopWaitingForPartnerThread)
            {
                // Exit Thread
                if (_StopAllThreads)
                {
                    return;
                }

                try
                {
                    for (int k = 0; k <= i % 5; k++)
                    {
                        if (k == 0)
                        {
                            points = ".";
                        }
                        else
                        {
                            points += ".";
                        }
                    }

                    String finalPoints = points;
                    Platform.runLater(() -> {
                        lb_waiting_text.textProperty().set("Warten auf Spieler" + finalPoints);
                    });

                    isConnected = _api.isPlayingPartnerConnected(_user, pair);

                    i++;

                    Thread.sleep(1000);
                }
                catch (Exception e)
                {
                    Platform.runLater(() -> {
                        sp_overlay.visibleProperty().set(false);
                        _disableForm(false, true);
                        return;
                    });
                }
            }

            if (isConnected)
            {
                // Max 30 Sekunden auf Map warten
                i = 0;
                while
                (
                       (_mapGeneratedByWebSocket == null || _playersGeneratedByWebSocket == null)
                    && i < 60
                )
                {
                    try
                    {
                        i++;
                        Thread.sleep(500);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                Platform.runLater(() -> {
                    sp_overlay.visibleProperty().set(false);
                    _disableForm(false, true);

                    if (_mapGeneratedByWebSocket != null)
                    {
                        _playingPair = pair;
                        startGame(new ActionEvent());
                    }
                    else
                    {
                        Util.showErrorMessageBox("Map konnte nicht geladen werden!");
                    }
                });
            }

            Platform.setImplicitExit(true);
        });

        _waitingForPartnerThread.setDaemon(true);
        _waitingForPartnerThread.start();
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

                boolean found;
                for (User user : loggedInUsers)
                {
                    found = false;
                    int i = 0;
                    for (Object listUser : lv_availablePlayers.getItems())
                    {
                        if (((User)listUser).id == user.id)
                        {
                            found = true;
                            int finalI = i;
                            Platform.runLater(() ->
                            {
                                lv_availablePlayers.getItems().set(finalI, user);
                            });

                            break;
                        }
                        i++;
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
                    int i = 0;
                    for (Object listPair : lv_incomingRequest.getItems())
                    {
                        if (((PlayingPair)listPair).id.equals(pair.id))
                        {
                            found = true;
                            int finalI = i;
                            Platform.runLater(()->
                            {
                                lv_incomingRequest.getItems().set(finalI, pair);
                            });
                            break;
                        }

                        i++;
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
                        if (pair.id.equals(((PlayingPair)items[i]).id))
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

                boolean found;
                for (PlayingPair pair : outgoingRequest)
                {
                    found = false;
                    int i = 0;
                    for (Object listPair : lv_outgoingRequest.getItems())
                    {
                        if (((PlayingPair)listPair).id.equals(pair.id))
                        {
                            found = true;

                            int finalI = i;
                            Platform.runLater(() ->
                            {
                                lv_outgoingRequest.getItems().set(finalI, pair);
                            });
                            break;
                        }

                        i++;
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
                        if (pair.id.equals(((PlayingPair)items[i]).id))
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

    private void _startAllTimers()
    {
        // Start timer for current logged in users
        _startUserListTimer();

        // Start timer for incoming play request
        _startIncomingPlayRequestTimer();

        // Start timer for outgoing play request
        _startOutgoingPlayRequestTimer();
    }

    private void _stopAllTimers()
    {
        if (_userListTimer != null)
        {
            _userListTimer.cancel();
            _userListTimer.purge();
            _userListTimer = null;

            System.out.println("UserList-Timer was stopped");
        }

        if (_incomingPlayRequestsTimer != null)
        {
            _incomingPlayRequestsTimer.cancel();
            _incomingPlayRequestsTimer.purge();
            _incomingPlayRequestsTimer = null;

            System.out.println("IncomingRequest-Timer was stopped");
        }

        if (_outgoingPlayRequestsTimer != null)
        {
            _outgoingPlayRequestsTimer.cancel();
            _outgoingPlayRequestsTimer.purge();
            _outgoingPlayRequestsTimer = null;

            System.out.println("OutgoingRequest-Timer was stopped");
        }
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
                        txt = "Anfrage an " + item.requestedUser.username + (item.status == PlayingPairStatus.ACCEPTED ? " (Angenommen)" : "");
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
        _disableForm(startLoading, false);

        if (startLoading && img_loading.getImage() == null)
        {
            img_loading.setImage(new Image(FlagBomb.class.getResourceAsStream("icons/loading.gif")));
        }

        img_loading.setVisible(startLoading);

        if (startLoading)
        {
            lb_loading.textProperty().set(loadingText);
        }

        lb_loading.setVisible(startLoading);
    }

    private void _disableForm(boolean isDisabled, boolean withLogoffButton)
    {
        bt_game_start.setDisable(isDisabled);
        edt_game_speed.setDisable(isDisabled);
        edt_game_bombs.setDisable(isDisabled);
        edt_game_width.setDisable(isDisabled);
        edt_game_squareFactor.setDisable(isDisabled);

        if (withLogoffButton)
        {
            bt_logoff.setDisable(isDisabled);
        }
    }

    private void _connectToWebSocket(PlayingPair pair)
    {
        if (_wsClient != null)
        {
            _wsClient.close();
        }

        _wsClient = new WebsocketClient(URI.create(_api.getWebSocketUrl(pair.id, _user.id)));
        _wsClient.addMapGeneratedListener(this::onMapGenerated);
        _wsClient.addBomberManGeneratedListener(this::onBomberManGenerated);
    }
}
