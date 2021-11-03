package de.bhtpaf.pacbomb.helper;

import de.bhtpaf.pacbomb.PacBomb;
import de.bhtpaf.pacbomb.helper.classes.User;
import de.bhtpaf.pacbomb.helper.classes.map.*;
import de.bhtpaf.pacbomb.helper.classes.map.items.Bomb;
import de.bhtpaf.pacbomb.helper.classes.map.items.Bombs;
import de.bhtpaf.pacbomb.helper.classes.map.items.Food;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
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

    private Scene _previousScene;
    private Stage _mainStage;
    private User _user = null;

    private int speed = 300;
    private int bombs = 10;
    private int width = 1000;
    private int height = width;

    private int boomFactor = 2;
    private int squareFactor = 30;
    private int step = 1;
    private int bomberManSize = width / squareFactor;
    private int bombSize = width / squareFactor;
    private int foodSize = width / squareFactor;
    private Grid grid = new Grid (width, height, squareFactor);

    private Dir _direction = Dir.right;
    private List<Dir> _forbiddenDirection = new ArrayList();

    private boolean gameOver = false;
    private int fontSizeTop = 20;

    //Creating an image
    private Image pacMan = new Image(PacBomb.class.getResourceAsStream("bomberman.gif"));

    private Image[] bombImages = new Image[]
    {
        new Image(PacBomb.class.getResourceAsStream("bomb1.gif")),
        new Image(PacBomb.class.getResourceAsStream("bomb2.gif")),
        new Image(PacBomb.class.getResourceAsStream("bomb3.gif")),
        new Image(PacBomb.class.getResourceAsStream("bomb4.gif")),
        new Image(PacBomb.class.getResourceAsStream("bomb5.gif")),
        new Image(PacBomb.class.getResourceAsStream("bomb6.gif"))
    };

    private Media soundBoom = new Media(PacBomb.class.getResource("boom.mp3").toString());
    private Media soundCollect = new Media(PacBomb.class.getResource("collect.wav").toString());
    private Media backgroundMusic = new Media(PacBomb.class.getResource("sound.wav").toString());
    private Media gameOverMusic = new Media(PacBomb.class.getResource("gameover.wav").toString());
    private Media errorMusic = new Media(PacBomb.class.getResource("empty.wav").toString());
    private MediaPlayer backgroundPlayer = new MediaPlayer(backgroundMusic);
    private BomberMan bomberMan = null;
    private MediaPlayer gameOverPlayer = new MediaPlayer(gameOverMusic);

    private boolean bombIt = false;
    private Bombs _bombList = new Bombs();
    private List<Food> foodList = new ArrayList();
    
    public Game(Stage stage, User user)
    {
        _previousScene = stage.getScene();
        _mainStage = stage;
        _user = user;

        try
        {
            bomberManSize = squareFactor;
            bomberMan = new BomberMan(0, squareFactor, bomberManSize);

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
        if (!grid.hit(bomberMan.square, _direction))
        {
            bomberMan.doStep(_direction, step);
        }

        // fill
        // background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width , height );

        grid.draw(gc);

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


        gc.drawImage(pacMan, bomberMan.square.downLeft.x, bomberMan.square.downLeft.y, bomberManSize, bomberManSize);
        // gc.setFill(Color.BLUE);
        // gc.fillRect(bomberMan.coord.x, bomberMan.coord.y, bomberManSize, grid.columns.get(0).get(0).downLeft.y);

        Iterator<Bomb> iterBomb = _bombList.iterator();

        while (iterBomb.hasNext())
        {
            Bomb b = iterBomb.next();
            int bombState = b.getState();
            if (bombState == 60)
            {
                iterBomb.remove();
                return;
            }
            else if (bombState >= 50)
            {
                gc.drawImage(bombImages[bombState / 10], b.square.downLeft.x - (boomFactor * bombSize)/4, b.square.downLeft.y - (boomFactor * bombSize)/4, boomFactor * bombSize, boomFactor * bombSize);
                b.setState(++bombState);
            }
            else
            {
                gc.drawImage(bombImages[bombState / 10], b.square.downLeft.x, b.square.downLeft.y, bombSize, bombSize);
                b.setState(++bombState);

            }

            // Bombe explodiert
            if (b.getState() == 52) {
                MediaPlayer boomPlayer = new MediaPlayer(soundBoom);
                boomPlayer.play();

                List<ExtendedTile> infected = b.getInfectedTiles(grid);

                for (ExtendedTile field : infected)
                {
                    if (field.tile() instanceof Wall && ((Wall)field.tile()).isDestroyable)
                    {
                        Tile freeTile = new Tile(field.tile().downLeft, field.tile().width, Type.free);
                        freeTile.draw(gc);

                        grid.columns.get(field.index().column).set(field.index().row, freeTile);
                    }
                }


            }
        }

        for (int i = 0; i < foodList.size(); i++)
        {
            Food f = foodList.get(i);

            Tile food = grid.find(f.getMiddleCoord());
            Tile bm = grid.find(bomberMan.getMiddleCoord());

            if(food.compare(bm))
            {
                MediaPlayer collectPlayer = new MediaPlayer(soundCollect);
                collectPlayer.play();
                bombs++;

                foodList.remove(f);
            }
            else
            {
                gc.setFill(f.color);
                gc.fillOval(f.square.downLeft.x , f.square.downLeft.y , f.foodSize, f.foodSize);
            }
        }

        Random rand = new Random();

        if(foodList.size() < 3)
        {
            for (int i = 0; i < 1 + rand.nextInt(5 - foodList.size()); i++)
            {
                foodList.add(Food.getRandom(grid));
            }
        }

    }

    private void _addBomb()
    {
        if(bombs > 0)
        {
            int x = (bomberMan.square.downLeft.x + bomberMan.square.downRight.x) / 2;
            int y = (bomberMan.square.upperLeft.y + bomberMan.square.downLeft.y) / 2;

            if (_bombList.placeOnGrid(grid, x, y) > 0)
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
