package de.bhtpaf.flagbomb.helper;

import de.bhtpaf.flagbomb.FlagBomb;
import de.bhtpaf.flagbomb.helper.classes.User;
import de.bhtpaf.flagbomb.helper.classes.map.*;
import de.bhtpaf.flagbomb.helper.classes.map.items.Bombs;
import de.bhtpaf.flagbomb.helper.classes.map.items.Flag;
import de.bhtpaf.flagbomb.helper.classes.map.items.Gem;
import de.bhtpaf.flagbomb.helper.interfaces.GameOverListener;
import de.bhtpaf.flagbomb.helper.responses.PlayingPair;
import de.bhtpaf.flagbomb.services.Api;
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

public class Game
{
    List<GameOverListener> _gameOverListeners = new ArrayList<>();

    private final Scene _previousScene;
    private final Stage _mainStage;
    private final Api _api;
    private final PlayingPair _playingPair;
    private final User _user;

    private Scene _gameScene = null;

    private final int _speed;
    private final int fontSizeTop = 20;
    private final int _width;
    private final int _height;

    private int _squareFactor;
    private int _bomberManSize;
    private int _bombs;

    private Grid _grid = null;

    private Dir _direction = Dir.right;

    private boolean gameOver = false;

    private Media soundCollect = new Media(FlagBomb.class.getResource("sounds/collect.wav").toString());
    private Media backgroundMusic = new Media(FlagBomb.class.getResource("sounds/sound.wav").toString());
    private Media gameOverMusic = new Media(FlagBomb.class.getResource("sounds/gameover.wav").toString());
    private Media errorMusic = new Media(FlagBomb.class.getResource("sounds/empty.wav").toString());
    private MediaPlayer backgroundPlayer = new MediaPlayer(backgroundMusic);
    private List<BomberMan> _players = null;
    private MediaPlayer gameOverPlayer = new MediaPlayer(gameOverMusic);

    private Bombs _bombList;
    private List<Gem> gemList = new ArrayList();
    
    public Game(Api api, Stage stage, User user, int speed, int width, int squareFactor, int bombs, Grid grid, PlayingPair pair)
    {
        _api = api;
        _previousScene = stage.getScene();
        _mainStage = stage;
        _playingPair = pair;
        _user = user;

        _speed = speed;
        _width = width;
        _squareFactor = squareFactor;
        _bombs = bombs;

        _height = _width;
        _bomberManSize = _width / _squareFactor;

        _grid = grid;
    }

    public void init()
    {
        try
        {
            _bomberManSize = _squareFactor;

            _players = new ArrayList<>();
            _players.add(
                new BomberMan(
                    0,
                    _squareFactor,
                    _bomberManSize,
                    new Flag(
                        _grid.columns.get((_grid.rowCount / 2) + 1).get(1).downLeft.x,
                        _grid.columns.get((_grid.rowCount / 2) + 1).get(1).downLeft.y,
                        _bomberManSize,
                        Flag.Color.blue
                    ),
                    _user.id
                )
            );

            _bombList = new Bombs(_width / _squareFactor);

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

                    if (gameOver)
                    {
                        stop();

                        gc.setFill(Color.BLACK);
                        gc.setFont(new Font("", 50));
                        String string = "GAME OVER";
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
                    _direction = Dir.up;
                }
                else if (key.getCode() == KeyCode.LEFT)
                {
                    _direction = Dir.left;
                }
                else if (key.getCode() == KeyCode.DOWN)
                {
                    _direction = Dir.down;
                }
                else if (key.getCode() == KeyCode.RIGHT)
                {
                    _direction = Dir.right;
                }
                else if (key.getCode() == KeyCode.SPACE)
                {
                    for (BomberMan player: _players)
                    {
                        if (player.id == _user.id)
                        {
                            _addBomb(player);
                        }
                    }
                }
                else if (key.getCode() == KeyCode.ESCAPE)
                {
                    gameOver = true;
                }
            });

            //If you do not want to use css style, you can just delete the next line.
            //  scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void generateGrid() throws Exception
    {
        if (_grid != null)
        {
            return;
        }

        if (_gameScene == null)
        {
            throw new Exception("Game nicht initialisiert");
        }

        _grid = new Grid (_width, _height, _squareFactor);
        _grid = _api.getGrid(_grid, _user);
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

    private void _tick(GraphicsContext gc)
    {
        if (backgroundPlayer.getStatus() != MediaPlayer.Status.PLAYING)
        {
            backgroundPlayer.setVolume(0.1);
            backgroundPlayer.play();
        }

        for (BomberMan player: _players)
        {
            if (!_grid.hit(player.square, _direction))
            {
                player.doStep(_direction);
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
        gc.fillText("BOMBS: " + _bombs, 10, 20);
        //
        gc.setFill(Color.WHITE);

        gc.setFont(new Font("", fontSizeTop));

        //TO DO DYNAMIC WIDTH
        String string = "Player 1: " + (_user != null ? _user.username : "Anonymous");
        double factor = string.length() * fontSizeTop * 0.5;
        gc.fillText(string, _width -factor, 20);

        for (BomberMan player: _players)
        {
            player.draw(gc);
            player.getOwnedFlag().draw(gc);
        }

        _bombList.updateBombs(gc, _grid);

        boolean collected = false;
        for (int i = 0; i < gemList.size(); i++)
        {
            collected = false;
            Gem f = gemList.get(i);
            Tile gem = _grid.find(f.getMiddleCoord());

            for (BomberMan player : _players)
            {
                Tile bm = _grid.find(player.getMiddleCoord());

                if (gem.compare(bm)) {
                    MediaPlayer collectPlayer = new MediaPlayer(soundCollect);
                    collectPlayer.setVolume(0.5);
                    collectPlayer.play();
                    _bombs++;

                    gemList.remove(f);

                    collected = true;
                }
            }

            if (!collected)
            {
                f.draw(gc);
            }
        }

        Random rand = new Random();

        if (gemList.size() < 5)
        {
            for (int i = 0; i < 1 + rand.nextInt(6 - gemList.size()); i++)
            {
                gemList.add(Gem.getRandom(_grid));
                gemList.get(gemList.size() - 1).draw(gc);
            }
        }

    }

    private void _addBomb(BomberMan player)
    {
        if(_bombs > 0)
        {
            int x = (player.square.downLeft.x + player.square.downRight.x) / 2;
            int y = (player.square.upperLeft.y + player.square.downLeft.y) / 2;

            if (_bombList.placeOnGrid(_grid, x, y, player) > 0)
            {
                _bombs--;
            }
        }
        else
        {
            MediaPlayer errorPlayer = new MediaPlayer(errorMusic);
            errorPlayer.setVolume(0.5);
            errorPlayer.play();
        }
    }

}
