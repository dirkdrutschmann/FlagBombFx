package de.bhtpaf.pacbomb.helper;

import de.bhtpaf.pacbomb.PacBomb;
import de.bhtpaf.pacbomb.helper.classes.User;
import de.bhtpaf.pacbomb.helper.classes.map.*;
import de.bhtpaf.pacbomb.helper.classes.map.items.Bombs;
import de.bhtpaf.pacbomb.helper.classes.map.items.Flag;
import de.bhtpaf.pacbomb.helper.classes.map.items.Food;
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

    private final Scene _previousScene;
    private final Stage _mainStage;
    private User _user = null;

    private int speed = 300;
    private int bombs = 10;
    private int width = 1000;
    private int height = width;

    private int squareFactor = 30;
    private int step = 1;
    private int bomberManSize = width / squareFactor;
    private int foodSize = width / squareFactor;
    private Grid _grid;

    private Dir _direction = Dir.right;
    private List<Dir> _forbiddenDirection = new ArrayList();

    private boolean gameOver = false;
    private int fontSizeTop = 20;

    //Creating an image


    private Media soundCollect = new Media(PacBomb.class.getResource("collect.wav").toString());
    private Media backgroundMusic = new Media(PacBomb.class.getResource("sound.wav").toString());
    private Media gameOverMusic = new Media(PacBomb.class.getResource("gameover.wav").toString());
    private Media errorMusic = new Media(PacBomb.class.getResource("empty.wav").toString());
    private MediaPlayer backgroundPlayer = new MediaPlayer(backgroundMusic);
    private BomberMan _bomberMan = null;
    private MediaPlayer gameOverPlayer = new MediaPlayer(gameOverMusic);

    private Bombs _bombList;
    private List<Food> foodList = new ArrayList();
    
    public Game(Stage stage, User user)
    {
        _previousScene = stage.getScene();
        _mainStage = stage;
        _user = user;

        try
        {
            _grid = new Grid (width, height, squareFactor);
            _grid.generateMap();

            bomberManSize = squareFactor;

            _bomberMan = new BomberMan(
                0,
                squareFactor,
                bomberManSize,
                new Flag(
                    _grid.columns.get(18).get(1).downLeft.x,
                    _grid.columns.get(18).get(1).downLeft.y,
                    bomberManSize,
                    Flag.Color.blue
                )
            );

            _bombList = new Bombs(width / squareFactor);

            backgroundPlayer.setAutoPlay(true);
            backgroundPlayer.setVolume(0.1);
            backgroundPlayer.play();
            VBox root = new VBox();
            Canvas c = new Canvas(width , height);
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

                    if (now - lastTick > 1000000000 / speed)
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
                        gc.fillText(string, height / 2 - factor, width / 2);

                        if (backgroundPlayer.getStatus() == MediaPlayer.Status.PLAYING)
                        {
                            backgroundPlayer.stop();
                        }

                        gameOverPlayer.play();

                        // Back to Overview in 5 Seconds
                        new Timer().schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
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

            Scene scene = new Scene(root, width , height);

            // control
            scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {

                if (key.getCode() == KeyCode.UP && !_forbiddenDirection.contains(Dir.up))
                {
                    _direction = Dir.up;
                }
                else if (key.getCode() == KeyCode.LEFT && !_forbiddenDirection.contains(Dir.left))
                {
                    _direction = Dir.left;
                }
                else if (key.getCode() == KeyCode.DOWN && !_forbiddenDirection.contains(Dir.down))
                {
                    _direction = Dir.down;
                }
                else if (key.getCode() == KeyCode.RIGHT && !_forbiddenDirection.contains(Dir.right))
                {
                    _direction = Dir.right;
                }
                else if (key.getCode() == KeyCode.SPACE)
                {
                    _addBomb();
                }
                else if (key.getCode() == KeyCode.ESCAPE)
                {
                    gameOver = true;
                }
            });

            //If you do not want to use css style, you can just delete the next line.
            //  scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

            _mainStage.setMinHeight(1039);
            _mainStage.setMaxHeight(1039);

            _mainStage.setMinWidth(1016);
            _mainStage.setMaxWidth(1016);

            _mainStage.setScene(scene);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void _tick(GraphicsContext gc)
    {
        if (backgroundPlayer.getStatus() != MediaPlayer.Status.PLAYING)
        {
            backgroundPlayer.play();
        }

        if (!_grid.hit(_bomberMan.square, _direction))
        {
            _bomberMan.doStep(_direction, step);
        }

        // fill
        // background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width , height );

        _grid.draw(gc);

        // score
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("", fontSizeTop));
        gc.fillText("BOMBS: " + bombs, 10, 20);
        //
        gc.setFill(Color.WHITE);

        gc.setFont(new Font("", fontSizeTop));

        //TO DO DYNAMIC WIDTH
        String string = "Player 1: " + (_user != null ? _user.username : "Anonymous");
        double factor = string.length() * fontSizeTop * 0.5;
        gc.fillText(string, width-factor, 20) ;

        _bomberMan.draw(gc);
        _bomberMan.getOwnedFlag().draw(gc);

        _bombList.updateBombs(gc, _grid);

        for (int i = 0; i < foodList.size(); i++)
        {
            Food f = foodList.get(i);

            Tile food = _grid.find(f.getMiddleCoord());
            Tile bm = _grid.find(_bomberMan.getMiddleCoord());

            if(food.compare(bm))
            {
                MediaPlayer collectPlayer = new MediaPlayer(soundCollect);
                collectPlayer.play();
                bombs++;

                foodList.remove(f);
            }
            else
            {
                f.draw(gc);
            }
        }

        Random rand = new Random();

        if (foodList.size() < 5)
        {
            for (int i = 0; i < 1 + rand.nextInt(6 - foodList.size()); i++)
            {
                foodList.add(Food.getRandom(_grid));
                foodList.get(foodList.size() - 1).draw(gc);
            }
        }

    }

    private void _addBomb()
    {
        if(bombs > 0)
        {
            int x = (_bomberMan.square.downLeft.x + _bomberMan.square.downRight.x) / 2;
            int y = (_bomberMan.square.upperLeft.y + _bomberMan.square.downLeft.y) / 2;

            if (_bombList.placeOnGrid(_grid, x, y) > 0)
            {
                bombs--;
            }
        }
        else
        {
            MediaPlayer errorPlayer = new MediaPlayer(errorMusic);
            errorPlayer.play();
        }
    }

}
