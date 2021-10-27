package de.bhtpaf.pacbomb;

import de.bhtpaf.pacbomb.helper.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.scene.text.TextAlignment;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class PacBomb extends Application  {
    // variable
    static int speed = 20;
    static int bombs = 10;
    static int width = 1000;
    static int height = width;



    static int boomFactor = 2;
    static int squareFactor = 30;
    static int step = 3;
    static int bomberManSize = width/squareFactor;
    static int bombSize = width/squareFactor;
    static int foodSize = width/squareFactor;
    static Grid grid = new Grid (width, height,squareFactor );
    static Dir direction = Dir.right;
    static boolean gameOver = false;
    static int fontSizeTop = 20;
    //Creating an image
    static Image pacMan = new Image(PacBomb.class.getResourceAsStream("bomberman.gif"));
    static Image[] bombImages = new Image[] {new Image(PacBomb.class.getResourceAsStream("bomb1.gif")),
            new Image(PacBomb.class.getResourceAsStream("bomb2.gif")),
            new Image(PacBomb.class.getResourceAsStream("bomb3.gif")),
            new Image(PacBomb.class.getResourceAsStream("bomb4.gif")),
            new Image(PacBomb.class.getResourceAsStream("bomb5.gif")),
            new Image(PacBomb.class.getResourceAsStream("bomb6.gif"))};
    static Media soundBoom = new Media(PacBomb.class.getResource("boom.mp3").toString());
    static Media soundCollect = new Media(PacBomb.class.getResource("collect.wav").toString());
    static Media backgroundMusic = new Media(PacBomb.class.getResource("sound.wav").toString());
    static Media gameOverMusic = new Media(PacBomb.class.getResource("gameover.wav").toString());
    static Media errorMusic = new Media(PacBomb.class.getResource("empty.wav").toString());
    static MediaPlayer backgroundPlayer = new MediaPlayer(backgroundMusic);
    static BomberMan bomberMan = new BomberMan(height, bomberManSize);
    static MediaPlayer gameOverPlayer = new MediaPlayer(gameOverMusic);
    //Upper Left of Image / Image is set 50x50

    static boolean bombIt = false;
    static List<Bomb> bombList = new ArrayList<Bomb>();
    static List<Food> foodList = new ArrayList<Food>(){{add(new Food(width, height, foodSize, grid));}};



    public void start(Stage primaryStage)  {
        try {
            backgroundPlayer.setAutoPlay(true);
            backgroundPlayer.setVolume(0.1);
            backgroundPlayer.play();
            VBox root = new VBox();
            Canvas c = new Canvas(width , height );
            GraphicsContext gc = c.getGraphicsContext2D();
            root.getChildren().add(c);


            new AnimationTimer() {
                long lastTick = 0;

                public void handle(long now) {
                    if (lastTick == 0) {
                        lastTick = now;
                        tick(gc);
                        return;
                    }

                    if (now - lastTick > 1000000000 / speed) {
                        lastTick = now;
                        tick(gc);
                    }
                }

            }.start();

            Scene scene = new Scene(root, width , height);

            // control
            scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
                if (key.getCode() == KeyCode.UP) {
                    direction = Dir.up;
                }
                if (key.getCode() == KeyCode.LEFT) {
                    direction = Dir.left;
                }
                if (key.getCode() == KeyCode.DOWN) {
                    direction = Dir.down;
                }
                if (key.getCode() == KeyCode.RIGHT) {
                    direction = Dir.right;
                }
                if (key.getCode() == KeyCode.SPACE){
                    addBomb();
                }

            });

            //If you do not want to use css style, you can just delete the next line.
          //  scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Pac-Bomb");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // tick
    public static void tick(GraphicsContext gc) {
        if (gameOver) {

            gc.setFill(Color.BLACK);
            gc.setFont(new Font("", 50));
            String string="GAME OVER";
            double factor = string.length()*fontSizeTop*0.5;
            gc.fillText(string, height/2-factor, width/2);
            gameOverPlayer.play();
            return;
        }



        switch (direction) {
            case up:
                    bomberMan.addY(-step);
                    if (bomberMan.corner.downRight.y < grid.rows.get(0).get(0).downRight.y) {
                        gameOver = true;
                    }
                break;
            case down:
                bomberMan.addY(step);
                if (bomberMan.corner.upperRight.y > grid.rows.get(grid.rows.size()-1).get(grid.rows.get(0).size()-1).upperRight.y) {
                    gameOver = true;
                }
                break;
            case left:
                bomberMan.addX(-step);
                if (bomberMan.corner.downLeft.x < grid.rows.get(0).get(0).downLeft.x) {
                    gameOver = true;
                }
                break;
            case right:
                bomberMan.addX(step);
                if (bomberMan.corner.upperRight.x > grid.rows.get(grid.rows.size()-1).get(grid.rows.get(0).size()-1).upperRight.x) {
                    gameOver = true;
                }
                break;
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
        String string="Player 1: Manuel";
        double factor = string.length()*fontSizeTop*0.5;
        gc.fillText(string, width-factor, 20) ;


        gc.drawImage(pacMan, bomberMan.coord.x, bomberMan.coord.y, bomberManSize, bomberManSize);

        Iterator<Bomb> iterBomb = bombList.iterator();

        while (iterBomb.hasNext()) {
            Bomb b = iterBomb.next();

            if (b.state == 60) {
                iterBomb.remove();
                return;
            }   else if (b.state >= 50) {
                gc.drawImage(bombImages[b.state / 10], b.coord.x-(boomFactor * bombSize)/4, b.coord.y-(boomFactor * bombSize)/4, boomFactor * bombSize, boomFactor * bombSize);
                b.state++;
            }else {
                gc.drawImage(bombImages[b.state / 10], b.coord.x, b.coord.y, bombSize, bombSize);
                b.state++;
            }
            if (b.state == 52) {
                MediaPlayer boomPlayer = new MediaPlayer(soundBoom);
                boomPlayer.play();
            }
        }

        Iterator<Food> iterFood = foodList.iterator();

        while (iterFood.hasNext()) {
            Food f = iterFood.next();
            if(f.corner.compare(bomberMan.corner)) {
                MediaPlayer collectPlayer = new MediaPlayer(soundCollect);
                collectPlayer.play();
                bombs++;
                speed++;

            }else{
            gc.setFill(f.color);
            gc.fillOval(f.coord.x , f.coord.y , f.foodSize, f.foodSize);}
        }

        foodList.removeIf(f -> f.corner.compare(bomberMan.corner));
        Random rand = new Random();
        if(foodList.size() < 1){
            for (int i = 0; i < 1 + rand.nextInt(3); i++) {
                foodList.add(new Food(width, height, foodSize, grid));
            }}

    }

    public static void addBomb(){
        if(bombs>0){
            bombs--;
            bombList.add(new Bomb(grid.find(new Coord(bomberMan.coord.x+bomberMan.bomberManSize/2, bomberMan.coord.y+bomberMan.bomberManSize/2)), bombSize));
        }else{
            MediaPlayer errorPlayer = new MediaPlayer(errorMusic);
            errorPlayer.play();
        }
    }








    public static void main(String[] args) {
        launch(args);
    }

    }

