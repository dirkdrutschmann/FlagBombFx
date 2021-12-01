package de.bhtpaf.flagbomb.helper;

import com.google.gson.GsonBuilder;
import de.bhtpaf.flagbomb.FlagBomb;
import de.bhtpaf.flagbomb.helper.classes.User;
import de.bhtpaf.flagbomb.helper.classes.json.ExtendedItemJson;
import de.bhtpaf.flagbomb.helper.classes.json.BombermanJson;
import de.bhtpaf.flagbomb.helper.classes.json.ItemJson;
import de.bhtpaf.flagbomb.helper.classes.map.*;
import de.bhtpaf.flagbomb.helper.classes.map.items.Bomb;
import de.bhtpaf.flagbomb.helper.classes.map.items.Flag;
import de.bhtpaf.flagbomb.helper.classes.map.items.Gem;
import de.bhtpaf.flagbomb.helper.classes.map.items.Item;
import de.bhtpaf.flagbomb.helper.classes.webSocketData.WebSocketCommunicationObject;
import de.bhtpaf.flagbomb.helper.interfaces.eventListener.*;
import de.bhtpaf.flagbomb.helper.responses.PlayingPair;
import de.bhtpaf.flagbomb.services.Api;
import de.bhtpaf.flagbomb.services.WebsocketClient;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.*;

public class Game implements GemGeneratedListener,
                             BombExplodedListener,
                             DirectionChangedListener,
                             BomberManChangedListener,
                             GemCollectedListener,
                             BombPlantedListener,
                             GameOverSetListener,
                             FlagCollectedListener,
                             FlagCapturedListener,
                             PlayerWonListener
{
    List<GameOverListener> _gameOverListeners = new ArrayList<>();

    private final Scene _previousScene;
    private final Stage _mainStage;
    private final Api _api;
    private final PlayingPair _playingPair;
    private final User _user;
    private final WebsocketClient _wsClient;


    private Scene _gameScene = null;

    private final int _speed;
    private final int fontSizeTop = 20;
    private final int _width;
    private final int _height;
    private final int _captureFlagCount;

    private BomberMan _myPlayer;
    private int _squareFactor;
    private int _bomberManSize;
    private int _bombs;
    private int _flags;
    private String _playersCaption;

    private Grid _grid = null;

    private Dir _direction = Dir.RIGHT;

    private boolean _gameOver = false;
    private BomberMan _playerWon = null;

    private Media soundCollect = new Media(FlagBomb.class.getResource("sounds/collect.wav").toString());
    private Media backgroundMusic = new Media(FlagBomb.class.getResource("sounds/sound.wav").toString());
    private Media gameOverMusic = new Media(FlagBomb.class.getResource("sounds/gameover.wav").toString());
    private Media errorMusic = new Media(FlagBomb.class.getResource("sounds/empty.wav").toString());
    private MediaPlayer backgroundPlayer = new MediaPlayer(backgroundMusic);
    private List<BomberMan> _players = null;
    private MediaPlayer gameOverPlayer = new MediaPlayer(gameOverMusic);

    private List<Gem> _gemList = new ArrayList();

    public Game(
            Api api,
            WebsocketClient wsClient,
            Stage stage,
            User user,
            int speed,
            int width,
            int squareFactor,
            int captureFlagCount,
            int bombs,
            Grid grid,
            List<BomberMan> players,
            PlayingPair pair
    ) {
        _api = api;
        _previousScene = stage.getScene();
        _mainStage = stage;
        _playingPair = pair;
        _user = user;
        _wsClient = wsClient;

        _speed = speed;
        _width = width;
        _squareFactor = squareFactor;
        _flags = 0;

        _grid = grid;
        _players = players;

        if (_grid == null || _wsClient == null)
        {
            _captureFlagCount = captureFlagCount;
            _bombs = bombs;
        }
        else
        {
            _captureFlagCount = _grid.captureFlagCount;
            _bombs = _grid.bombsAtStart;
        }

        _height = _width;
        _bomberManSize = _width / _squareFactor;


        javafx.event.EventHandler currentHandle = _mainStage.getOnCloseRequest();

        _mainStage.setOnCloseRequest(ev -> {
            _sendToWebSocket(null, "GameOverSet");
            for (GameOverListener listener : _gameOverListeners)
            {
                listener.onGameOver(_playingPair);
            }

            System.out.println("Game Over Events gestartet");

            currentHandle.handle(ev);
        });
    }

    public void init()
    {
        try
        {
            if (_grid == null)
            {
                _generateGrid();
            }

            if (_wsClient != null)
            {
                _wsClient.addGemGeneratedListener(this::onGemGenerated);
                _wsClient.addBomberManChangedListener(this::onBombermanChangedListener);
                _wsClient.addGemCollectedListener(this::onGemCollected);
                _wsClient.addBombPlantedListener(this::onBombPlanted);
                _wsClient.addGameOverSetListener(this::onGameOverSet);
                _wsClient.addFlagCollectedListener(this::onFlagCollected);
                _wsClient.addFlagCapturedListener(this::onFlagCaptured);
                _wsClient.addPlayerWonListener(this::onPlayerWon);
            }

            _bomberManSize = _squareFactor;

            if (_players == null)
            {
                _players = new ArrayList<>();

                // Spieler 1
                _players.add(
                        new BomberMan(
                                _grid.columns.get(0).get(_grid.rowCount / 2).downLeft.x,
                                _grid.columns.get(0).get(_grid.rowCount / 2).downLeft.y,
                                _bomberManSize,
                                new Flag(
                                        _grid.columns.get((_grid.columnCount / 2) + 1).get(1).downLeft.x,
                                        _grid.columns.get((_grid.columnCount / 2) + 1).get(1).downLeft.y,
                                        _bomberManSize,
                                        Flag.Color.BLUE
                                ),
                                _user.id,
                                _user.username
                        )
                );

                // Spieler zwei
                _players.add(
                        new BomberMan(
                                _grid.columns.get(_grid.columnCount - 1).get(_grid.rowCount / 2).downLeft.x,
                                _grid.columns.get(_grid.columnCount - 1).get(_grid.rowCount / 2).downLeft.y,
                                _bomberManSize,
                                new Flag(
                                    _grid.columns.get((_grid.columnCount / 2) + 1).get(_grid.rowCount - 3).downLeft.x,
                                    _grid.columns.get((_grid.columnCount / 2) + 1).get(_grid.rowCount - 3).downLeft.y,
                                    _bomberManSize,
                                    Flag.Color.RED
                                ),
                                _user.id + 1,
                                "Anonymous"
                        )
                );
            }

            if (_myPlayer == null)
            {
                for (BomberMan player : _players)
                {
                    if (player.userId == _user.id)
                    {
                        _myPlayer = player;
                        _myPlayer.addDirectionChangedListeners(this::onDirectionChanged);
                        break;
                    }
                }
            }

            _playersCaption = "";
            for (BomberMan player : _players)
            {
                if (!_playersCaption.isEmpty())
                {
                    _playersCaption += " vs. ";
                }

                _playersCaption += player.username;
            }

            backgroundPlayer.setAutoPlay(true);
            backgroundPlayer.setVolume(0.1);
            backgroundPlayer.play();

            VBox root = new VBox();
            Canvas c = new Canvas(_width, _height);
            GraphicsContext gc = c.getGraphicsContext2D();
            root.getChildren().add(c);

            new AnimationTimer()
            {
                long lastTick = 0;

                public void handle(long now)
                {
                    if (lastTick == 0)
                    {
                        lastTick = now;
                        _tick(gc);
                        return;
                    }

                    if (now - lastTick > 1000000000 / _speed)
                    {
                        lastTick = now;
                        _tick(gc);
                    }

                    if (_gameOver || _playerWon != null)
                    {
                        stop();

                        gc.setFill(Color.BLACK);
                        gc.setFont(new Font("", 50));

                        String string = "";

                        if (_gameOver)
                        {
                            string = "GAME OVER";
                        }
                        else
                        {
                            string = _playerWon.username + " hat gewonnen.";
                        }

                        double factor = string.length() * fontSizeTop * 0.5;
                        gc.fillText(string, _height / 2 - factor, _width / 2);

                        if (backgroundPlayer.getStatus() == MediaPlayer.Status.PLAYING)
                        {
                            backgroundPlayer.stop();
                        }

                        gameOverPlayer.setVolume(0.5);
                        gameOverPlayer.play();

                        // Back to Overview in 5 Seconds
                        new Timer().schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        // Raise GameOver-Event
                                        for (GameOverListener listener : _gameOverListeners)
                                        {
                                            listener.onGameOver(_playingPair);
                                        }

                                        Platform.runLater(() -> {
                                            _mainStage.setMinHeight(639);
                                            _mainStage.setMaxHeight(639);

                                            _mainStage.setMinWidth(1016);
                                            _mainStage.setMaxWidth(1016);
                                            _mainStage.setScene(_previousScene);
                                        });
                                    }
                                },
                                5000
                        );
                    }
                }

            }.start();

            _gameScene = new Scene(root, _width, _height);

            // control
            _gameScene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {

                if (key.getCode() == KeyCode.UP)
                {
                    _myPlayer.setDirection(Dir.UP);
                }
                else if (key.getCode() == KeyCode.LEFT)
                {
                    _myPlayer.setDirection(Dir.LEFT);
                }
                else if (key.getCode() == KeyCode.DOWN)
                {
                    _myPlayer.setDirection(Dir.DOWN);
                }
                else if (key.getCode() == KeyCode.RIGHT)
                {
                    _myPlayer.setDirection(Dir.RIGHT);
                }
                else if (key.getCode() == KeyCode.SPACE)
                {
                    _addBomb(_myPlayer);
                }
                else if (key.getCode() == KeyCode.ESCAPE)
                {
                    _gameOver = true;
                    _sendToWebSocket(null, "GameOverSet");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show() throws Exception
    {
        if (_grid == null)
        {
            throw new Exception("Grid wurde nicht generiert");
        }

        _mainStage.setMinHeight(_height + 39);
        _mainStage.setMaxHeight(_height + 39);

        _mainStage.setMinWidth(_width + 16);
        _mainStage.setMaxWidth(_width + 16);

        _mainStage.setScene(_gameScene);
    }

    public void addOnGameOverListener(GameOverListener listener)
    {
        _gameOverListeners.add(listener);
    }

    @Override
    public void onPlayerWon(BombermanJson player)
    {
        for (BomberMan p : _players)
        {
            if (player.userId == p.userId)
            {
                _playerWon = p;
                break;
            }
        }
    }

    public void onPlayerWon()
    {
        BombermanJson b = _playerWon.getForJson();
        _sendToWebSocket(b, "PlayerWon");
    }

    @Override
    public void onGemGenerated(Gem gem)
    {
        _gemList.add(gem);
    }

    @Override
    public void onBombExploded(Bomb bomb)
    {
        Tile middle = _grid.find(_myPlayer.getMiddleCoord());

        for (ExtendedTile tile : bomb.getInfectedTiles(_grid))
        {
            if (tile.tile().compare(middle))
            {
                _myPlayer.respawn();
                return;
            }
        }
    }

    @Override
    public void onBombPlanted(ExtendedItemJson bombJson)
    {
        for (BomberMan player : _players)
        {
            if (player.userId == bombJson.userId)
            {
                Bomb b = player.getBombs().placeOnGrid(bombJson.square, bombJson.itemId);
                b.addBombExplodedListener(this::onBombExploded);
                break;
            }
        }
    }

    public void onBombPlanted(Bomb bomb)
    {
        ExtendedItemJson b = new ExtendedItemJson();
        b.itemId = bomb.itemId;
        b.square = bomb.square;
        b.userId = _myPlayer.userId;

        _sendToWebSocket(b, "BombPlanted");
    }

    @Override
    public void onFlagCollected(ExtendedItemJson flagJson)
    {
        for (BomberMan player : _players)
        {
            // Sammelnden Spieler finden
            if (player.userId != flagJson.userId)
            {
                continue;
            }

            // Gesammelte Flag finden
            for (BomberMan playerFlag : _players)
            {
                if (!playerFlag.getOwnedFlag().itemId.equals(flagJson.itemId))
                {
                    continue;
                }

                player.capturedFlag = playerFlag.getOwnedFlag();
            }
        }
    }

    public void onFlagCollected(int userId, Flag flag)
    {
        ExtendedItemJson f = new ExtendedItemJson();
        f.itemId = flag.itemId;
        f.square = flag.square;
        f.userId = userId;

        _sendToWebSocket(f, "FlagCollected");
    }

    @Override
    public void onFlagCaptured(ExtendedItemJson flagJson)
    {
        for (BomberMan player : _players)
        {
            if (player.userId != flagJson.userId)
            {
                continue;
            }

            player.capturedFlag.respawn();
            player.capturedFlag = null;
            _flags--;
        }
    }

    public void onFlagCaptured(int userId, Flag flag)
    {
        ExtendedItemJson f = new ExtendedItemJson();
        f.itemId = flag.itemId;
        f.square = flag.square;
        f.userId = userId;

        _sendToWebSocket(f, "FlagCaptured");
    }

    @Override
    public void onDirectionChanged(BomberMan player, Dir oldDirection, Dir newDirection)
    {
        if (_wsClient == null)
        {
            return;
        }

        WebSocketCommunicationObject o = new WebSocketCommunicationObject();
        o.className = "BombermanChanged";
        o.objectValue = player.getForJson();

        _wsClient.sendMessage(o.toJson());
    }

    @Override
    public void onBombermanChangedListener(BombermanJson newBomberMan)
    {
        if (_wsClient == null)
        {
            return;
        }

        for (BomberMan player : _players)
        {
            if (newBomberMan.userId == player.userId)
            {
                player.setDirection(newBomberMan.currentDir, false);
                player.square = newBomberMan.currentSquare;
                break;
            }
        }
    }

    @Override
    public void onGemCollected(ItemJson gem)
    {
        if (_wsClient == null)
        {
            return;
        }

        for (Gem g : _gemList)
        {
            if (g.itemId.equals(gem.itemId))
            {
                _gemList.remove(g);
                break;
            }
        }
    }

    public void onGemCollected(Gem gem)
    {
        _sendItemToWebSocket(gem, "GemCollected");
    }

    @Override
    public void onGameOverSet()
    {
        _gameOver = true;
    }

    private void _tick(GraphicsContext gc)
    {
        if (backgroundPlayer.getStatus() != MediaPlayer.Status.PLAYING)
        {
            backgroundPlayer.setVolume(0.1);
            backgroundPlayer.play();
        }

        if (!_grid.hit(_myPlayer))
        {
            _myPlayer.doStep();
        }
        else
        {
            if (_myPlayer.getDirection() != Dir.STAND)
            {
                _myPlayer.setDirection(Dir.STAND);
            }
        }

        // Draw all other players
        for (BomberMan player : _players)
        {
            if (player.userId != _myPlayer.userId)
            {
                player.doStep();
            }
        }

        // fill
        // background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, _width, _height);

        _grid.draw(gc);

        // score
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("", fontSizeTop));
        String menuBar = "BOMBS: " + _bombs;
        menuBar += "   FLAGS: " + _flags + " / " + _captureFlagCount;

        gc.fillText(menuBar, 10, 20);
        gc.setFill(Color.WHITE);

        gc.setFont(new Font("", fontSizeTop));

        //TO DO DYNAMIC WIDTH
        double factor = _playersCaption.length() * fontSizeTop * 0.5;
        gc.fillText(_playersCaption, _width - factor, 20);

        for (BomberMan player : _players)
        {
            player.draw(gc);
            player.getBombs().updateBombs(gc, _grid);
            player.getOwnedFlag().draw(gc);
        }

        for (Gem gem : _gemList)
        {
            gem.draw(gc);
        }

        _collect();

        // Neue Items nur erstellen, wenn kein Multiplayer
        if (_wsClient == null)
        {
            Random rand = new Random();

            if (_gemList.size() < 5)
            {
                for (int i = 0; i < 1 + rand.nextInt(6 - _gemList.size()); i++)
                {
                    _gemList.add(Gem.getRandom(_grid));
                    _gemList.get(_gemList.size() - 1).draw(gc);
                }
            }
        }

    }

    private void _addBomb(BomberMan player)
    {
        if (_bombs > 0)
        {
            int x = (player.square.downLeft.x + player.square.downRight.x) / 2;
            int y = (player.square.upperLeft.y + player.square.downLeft.y) / 2;

            Bomb bomb = player.getBombs().placeOnGrid(_grid, x, y);

            if (bomb != null)
            {
                _bombs--;
                bomb.addBombExplodedListener(this::onBombExploded);
                onBombPlanted(bomb);
            }
        }
        else
        {
            MediaPlayer errorPlayer = new MediaPlayer(errorMusic);
            errorPlayer.setVolume(0.5);
            errorPlayer.play();
        }
    }

    private void _generateGrid() throws Exception
    {
        if (_grid != null)
        {
            return;
        }

        _grid = new Grid(_width, _height, _squareFactor, _captureFlagCount, _bombs);
        _grid = _api.getGrid(_grid, _user);
    }

    private void _collect()
    {
        List<Item> items = new ArrayList<Item>();
        items.addAll(_gemList);

        for (BomberMan player : _players)
        {
            items.add(player.getOwnedFlag());
        }

        for (Item item : items)
        {
            Tile tile = _grid.find(item.getMiddleCoord());
            Tile bm = _grid.find(_myPlayer.getMiddleCoord());

            if (tile == null || bm == null)
            {
                return;
            }

            if (tile.compare(bm))
            {
                if (item instanceof Gem)
                {
                    MediaPlayer collectPlayer = new MediaPlayer(soundCollect);
                    collectPlayer.setVolume(0.5);
                    collectPlayer.play();
                    _bombs++;

                    onGemCollected((Gem)item);
                    _gemList.remove(item);
                }
                // Fremde Flagge aufnehmen
                else if (item instanceof Flag && _myPlayer.getOwnedFlag() != (Flag)item && _myPlayer.capturedFlag == null)
                {
                    _myPlayer.capturedFlag = (Flag)item;
                    onFlagCollected(_myPlayer.userId, (Flag)item);
                }
                // Hit der eigenen Flagge
                else if (item instanceof Flag && _myPlayer.getOwnedFlag() == (Flag)item)
                {
                    // Eigene Fallge nicht am Spawn-Point
                    if (!item.square.compare(item.getInitPosition()))
                    {
                        _myPlayer.getOwnedFlag().respawn();
                    }
                    else if (_myPlayer.capturedFlag != null)
                    {
                        _myPlayer.capturedFlag.respawn();
                        onFlagCaptured(_myPlayer.userId, _myPlayer.capturedFlag);

                        _myPlayer.capturedFlag = null;
                        _flags++;

                        if (_flags >= _captureFlagCount) {
                            _playerWon = _myPlayer;
                            onPlayerWon();
                        }
                    }
                }

                for (BomberMan player : _players)
                {
                    if (tile.compare(_grid.find(player.getMiddleCoord())))
                    {
                        if (_myPlayer.capturedFlag == player.getOwnedFlag())
                        {
                            _myPlayer.capturedFlag.respawn();
                            _myPlayer.capturedFlag = null;
                        }

                        if (player.capturedFlag == _myPlayer.getOwnedFlag())
                        {
                            player.capturedFlag.respawn();
                            player.capturedFlag = null;
                        }
                    }
                }

            }

        }
    }

    private void _sendItemToWebSocket(Item item, String className)
    {
        _sendToWebSocket(item.getItemJson(), className);
    }

    private void _sendToWebSocket(Object object, String className)
    {
        if (_wsClient == null)
        {
            return;
        }

        WebSocketCommunicationObject o = new WebSocketCommunicationObject();
        o.className = className;
        o.objectValue = object;

        _wsClient.sendMessage(new GsonBuilder().create().toJson(o, WebSocketCommunicationObject.class));
    }
}
