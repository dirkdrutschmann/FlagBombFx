package de.bhtpaf.flagbomb.helper;

import de.bhtpaf.flagbomb.FlagBomb;
import de.bhtpaf.flagbomb.helper.classes.User;
import de.bhtpaf.flagbomb.helper.classes.map.*;
import de.bhtpaf.flagbomb.helper.classes.map.items.Bomb;
import de.bhtpaf.flagbomb.helper.classes.map.items.Flag;
import de.bhtpaf.flagbomb.helper.classes.map.items.Gem;
import de.bhtpaf.flagbomb.helper.classes.map.items.Item;
import de.bhtpaf.flagbomb.helper.interfaces.BombExplodedListener;
import de.bhtpaf.flagbomb.helper.interfaces.GameOverListener;
import de.bhtpaf.flagbomb.helper.interfaces.GemGeneratedListener;
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

import java.sql.Array;
import java.util.*;

public class Game implements GemGeneratedListener, BombExplodedListener {
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

    private BomberMan _myPlayer;
    private int _squareFactor;
    private int _bomberManSize;
    private int _bombs;
    private int _flags;

    private Grid _grid = null;

    private Dir _direction = Dir.right;

    private boolean gameOver = false;
    private boolean _hit = false;

    private Media soundCollect = new Media(FlagBomb.class.getResource("sounds/collect.wav").toString());
    private Media backgroundMusic = new Media(FlagBomb.class.getResource("sounds/sound.wav").toString());
    private Media gameOverMusic = new Media(FlagBomb.class.getResource("sounds/gameover.wav").toString());
    private Media errorMusic = new Media(FlagBomb.class.getResource("sounds/empty.wav").toString());
    private MediaPlayer backgroundPlayer = new MediaPlayer(backgroundMusic);
    private List<BomberMan> _players = null;
    private MediaPlayer gameOverPlayer = new MediaPlayer(gameOverMusic);

    private List<Gem> gemList = new ArrayList();

    public Game(
            Api api,
            WebsocketClient wsClient,
            Stage stage,
            User user,
            int speed,
            int width,
            int squareFactor,
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
        _bombs = bombs;
        _flags = 0;

        _height = _width;
        _bomberManSize = _width / _squareFactor;

        _grid = grid;
        _players = players;
    }

    public void init() {
        try {
            if (_grid == null) {
                _generateGrid();
            }

            if (_wsClient != null) {
                _wsClient.addGemGeneratedListener(this::onGemGenerated);
            }

            _bomberManSize = _squareFactor;

            if (_players == null) {
                _players = new ArrayList<>();

                // Spieler 1
                _players.add(
                        new BomberMan(
                                0,
                                _squareFactor,
                                _bomberManSize,
                                new Flag(
                                        _grid.columns.get((_grid.columnCount / 2) + 1).get(1).downLeft.x,
                                        _grid.columns.get((_grid.columnCount / 2) + 1).get(1).downLeft.y,
                                        _bomberManSize,
                                        Flag.Color.BLUE
                                ),
                                _user.id
                        )
                );

                // Spieler zwei
                _players.add(
                        new BomberMan(
                                _grid.columns.get(_grid.columnCount - 1).get(_grid.rowCount - 1).downLeft.x,
                                _grid.columns.get(_grid.columnCount - 1).get(_grid.rowCount - 1).downLeft.y,
                                _bomberManSize,
                                new Flag(
                                        _grid.columns.get((_grid.columnCount / 2) + 1).get(_grid.rowCount - 3).downLeft.x,
                                        _grid.columns.get((_grid.columnCount / 2) + 1).get(_grid.rowCount - 3).downLeft.y,
                                        _bomberManSize,
                                        Flag.Color.RED
                                ),
                                _user.id + 1
                        )
                );
            }

            if (_myPlayer == null) {
                for (BomberMan player : _players) {
                    if (player.userId == _user.id) {
                        _myPlayer = player;
                    }
                }
            }

            backgroundPlayer.setAutoPlay(true);
            backgroundPlayer.setVolume(0.1);
            backgroundPlayer.play();

            VBox root = new VBox();
            Canvas c = new Canvas(_width, _height);
            GraphicsContext gc = c.getGraphicsContext2D();
            root.getChildren().add(c);

            new AnimationTimer() {
                long lastTick = 0;

                public void handle(long now) {
                    if (lastTick == 0) {
                        lastTick = now;
                        _tick(gc);
                        return;
                    }

                    if (now - lastTick > 1000000000 / _speed) {
                        lastTick = now;
                        _tick(gc);
                    }

                    if (gameOver) {
                        stop();

                        gc.setFill(Color.BLACK);
                        gc.setFont(new Font("", 50));
                        String string = "GAME OVER";
                        double factor = string.length() * fontSizeTop * 0.5;
                        gc.fillText(string, _height / 2 - factor, _width / 2);

                        if (backgroundPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
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
                                        for (GameOverListener listener : _gameOverListeners) {
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

                if (key.getCode() == KeyCode.UP) {
                    _myPlayer.direction = Dir.up;
                } else if (key.getCode() == KeyCode.LEFT) {
                    _myPlayer.direction = Dir.left;
                } else if (key.getCode() == KeyCode.DOWN) {
                    _myPlayer.direction = Dir.down;
                } else if (key.getCode() == KeyCode.RIGHT) {
                    _myPlayer.direction = Dir.right;
                } else if (key.getCode() == KeyCode.SPACE) {
                    _addBomb(_myPlayer);
                } else if (key.getCode() == KeyCode.ESCAPE) {
                    gameOver = true;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show() throws Exception {
        if (_grid == null) {
            throw new Exception("Grid wurde nicht generiert");
        }

        _mainStage.setMinHeight(_height + 39);
        _mainStage.setMaxHeight(_height + 39);

        _mainStage.setMinWidth(_width + 16);
        _mainStage.setMaxWidth(_width + 16);

        _mainStage.setScene(_gameScene);
    }

    public void addOnGameOverListener(GameOverListener listener) {
        _gameOverListeners.add(listener);
    }

    @Override
    public void onGemGenerated(Gem gem) {
        gemList.add(gem);
    }

    private void _tick(GraphicsContext gc) {
        if (backgroundPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            backgroundPlayer.setVolume(0.1);
            backgroundPlayer.play();
        }

        if (!_grid.hit(_myPlayer)) {
            _myPlayer.doStep();
        }
        if (_hit) {
            _hit = false;
            gc.setFill(Color.BLACK);
            gc.setFont(new Font("", 50));
            String string = "You got hit!";
            double factor = string.length() * fontSizeTop * 0.5;
            gc.fillText(string, _height / 2 - factor, _width / 2);
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
        if (_flags > 0) {
            menuBar += "        FLAGS: " + _flags;
        }
        gc.fillText(menuBar, 10, 20);
        //
        gc.setFill(Color.WHITE);

        gc.setFont(new Font("", fontSizeTop));

        //TO DO DYNAMIC WIDTH
        String string = "Player 1: " + (_user != null ? _user.username : "Anonymous");
        double factor = string.length() * fontSizeTop * 0.5;
        gc.fillText(string, _width - factor, 20);

        for (BomberMan player : _players) {
            player.getOwnedFlag().draw(gc);
        }
        for (BomberMan player : _players) {
            player.draw(gc);
            player.getBombs().updateBombs(gc, _grid);
        }


        for (Gem gem : gemList) {
            gem.draw(gc);
        }

        _collect();

        // Neue Items nur erstellen, wenn kein Multiplayer
        if (_wsClient == null) {
            Random rand = new Random();

            if (gemList.size() < 5) {
                for (int i = 0; i < 1 + rand.nextInt(6 - gemList.size()); i++) {
                    gemList.add(Gem.getRandom(_grid));
                    gemList.get(gemList.size() - 1).draw(gc);
                }
            }
        }

    }

    private void _addBomb(BomberMan player) {
        if (_bombs > 0) {
            int x = (player.square.downLeft.x + player.square.downRight.x) / 2;
            int y = (player.square.upperLeft.y + player.square.downLeft.y) / 2;
            Bomb bomb = player.getBombs().placeOnGrid(_grid, x, y);
            if (bomb != null) {
                _bombs--;
                bomb.addBombExplodedListener(this::onBombExploded);
            }
        } else {
            MediaPlayer errorPlayer = new MediaPlayer(errorMusic);
            errorPlayer.setVolume(0.5);
            errorPlayer.play();
        }
    }

    private void _generateGrid() throws Exception {
        if (_grid != null) {
            return;
        }

        _grid = new Grid(_width, _height, _squareFactor);
        _grid = _api.getGrid(_grid, _user);
    }

    private void _collect() {
        List<Item> items = new ArrayList<Item>();
        items.addAll(gemList);
        for (BomberMan player : _players) {
            items.add(player.getOwnedFlag());
            items.addAll(player.getBombs());
        }

        for (Item item : items) {

            Tile tile = _grid.find(item.getMiddleCoord());
            Tile bm = _grid.find(_myPlayer.getMiddleCoord());

            if (tile.compare(bm)) {
                if (item instanceof Gem) {
                    MediaPlayer collectPlayer = new MediaPlayer(soundCollect);
                    collectPlayer.setVolume(0.5);
                    collectPlayer.play();
                    _bombs++;

                    gemList.remove(item);
                } else if (item instanceof Flag && _myPlayer.getOwnedFlag() != (Flag) item && _myPlayer.capturedFlag == null) {
                    _myPlayer.capturedFlag = (Flag) item;
                } else if (item instanceof Flag && _myPlayer.getOwnedFlag() == (Flag) item && _myPlayer.capturedFlag != null) {
                    _myPlayer.capturedFlag.respawn();
                    _myPlayer.capturedFlag = null;
                    _flags++;
                }
                for (BomberMan player : _players) {
                    if (bm.compare(_grid.find(player.getMiddleCoord()))) {
                        if (_myPlayer.capturedFlag == player.getOwnedFlag()) {
                            _myPlayer.capturedFlag.respawn();
                            _myPlayer.capturedFlag = null;
                            _myPlayer.respawn();
                        }
                        if (player.capturedFlag == _myPlayer.getOwnedFlag()) {
                            player.capturedFlag.respawn();
                            player.capturedFlag = null;
                            player.respawn();
                        }
                    }
                }

            }

        }
    }


    @Override
    public void onBombExploded(Bomb bomb) {

        Tile middle = _grid.find(_myPlayer.getMiddleCoord());
        for (ExtendedTile tile : bomb.getInfectedTiles(_grid)) {
            if (tile.tile().compare(middle)) {
                _myPlayer.respawn();
                return;
            }
        }
    }
}
