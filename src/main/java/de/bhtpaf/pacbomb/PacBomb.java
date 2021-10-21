package de.bhtpaf.pacbomb;

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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class PacBomb extends Application  {
    // variable
    static int speed = 20;
    static int bombs = 10;
    static int width = 500;
    static int height = 500;
    static int foodSize = 25;
    static Food food = new Food(width, height, foodSize);
    static int bomberManSize = 50;
    static int bombSize = 50;
    static int boomFactor = 2;
    static int step = 2;
    static Dir direction = Dir.right;
    static boolean gameOver = false;
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
    static BomberMan bomberMan = new BomberMan(height);
    static MediaPlayer gameOverPlayer = new MediaPlayer(gameOverMusic);
    //Upper Left of Image / Image is set 50x50

    static boolean bombIt = false;
    static List<Bomb> bombList = new ArrayList<Bomb>();
    static List<Food> foodList = new ArrayList<Food>(){{add(new Food(width, height, foodSize));}};
    public enum Dir {
        left, right, up, down
    }



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
            gc.setFill(Color.RED);
            gc.setFont(new Font("", 50));
            gc.fillText("GAME OVER", 100, 250);
            gameOverPlayer.play();
            return;
        }


        switch (direction) {
            case up:
                bomberMan.addY(-step);
                if (bomberMan.corner.downRight.y < 0) {
                    gameOver = true;
                }
                break;
            case down:
                bomberMan.addY(step);
                if (bomberMan.corner.upperRight.y > height) {
                    gameOver = true;
                }
                break;
            case left:
                bomberMan.addX(-step);
                if (bomberMan.corner.downLeft.x < 0) {
                    gameOver = true;
                }
                break;
            case right:
                bomberMan.addX(step);
                if (bomberMan.corner.upperRight.x > width) {
                    gameOver = true;
                }
                break;
        }

        // fill
        // background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width , height );

        // score
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("", 30));
        gc.fillText("BOMBS: " + bombs, 10, 30);


        gc.setFill(Color.RED);
        gc.fillRect(bomberMan.coord.x, bomberMan.coord.y, bomberManSize, bomberManSize);
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
            gc.fillOval(f.coord.x , f.coord.y , foodSize, foodSize);}
        }

        foodList.removeIf(f -> f.corner.compare(bomberMan.corner));
        Random rand = new Random();
        if(foodList.size() < 1){
            for (int i = 0; i < 1 + rand.nextInt(3); i++) {
                foodList.add(new Food(width, height, foodSize));
            }}

    }

    public static void addBomb(){
        if(bombs>0){
            bombs--;
            bombList.add(new Bomb(new Coord(bomberMan.coord.x, bomberMan.coord.y), bombSize));
        }else{
            MediaPlayer errorPlayer = new MediaPlayer(errorMusic);
            errorPlayer.play();
        }
    }



    public static class Food{
        Coord coord;
        Color color;
        Corner corner;

        public Food(int width, int height, int foodSize){
            Random rand = new Random();
            this.color = color(rand.nextInt(5));
           this.coord = new Coord( rand.nextInt(width-foodSize),  rand.nextInt(height-foodSize));

            this.corner = new Corner(this.coord, foodSize );
        }

        private Color color (int rand) {
            switch (rand) {
                case 0:
                    return Color.PURPLE;
                case 1:
                    return Color.LIGHTBLUE;
                case 2:
                    return Color.YELLOW;
                case 3:
                    return Color.PINK;
                case 4:
                    return Color.ORANGE;
            }
            return Color.WHITE;
        }
    }
    public static class Bomb {
        Coord coord;
        int state = 0;
        Corner corner;

        public Bomb(Coord coord, int bombSize) {
            this.coord = coord;
            this.corner = new Corner(this.coord, bombSize );
        }

    }
    public static class BomberMan{
         Coord coord;
         Corner corner;

         public BomberMan(int width){
             this.coord = new Coord(width / 2, width / 2 );
             this.corner = new Corner(this.coord, bomberManSize );
         }

         public void addX(int inc){
             this.coord.x += inc;
             this.corner = new Corner(this.coord, bomberManSize );
         }
         public void addY(int inc){
             this.coord.y += inc;
             this.corner = new Corner(coord, bomberManSize );
         }


    }

    public static class Corner{
        Coord upperLeft;
        Coord downLeft;
        Coord upperRight;
        Coord downRight;


        public Corner(Coord coord, int width){
            this.downLeft = coord;
            this.upperLeft = new Coord (coord.x, coord.y + width);
            this.downRight = new Coord (coord.x + width, coord.y);
            this.upperRight = new Coord (coord.x + width, coord.y + width);
        }

        public boolean compare(Corner corner){

                return (downLeft.x <= corner.upperRight.x) && (corner.downLeft.x <= upperRight.x) && (downLeft.y <= corner.upperRight.y) && (corner.downLeft.y <= upperRight.y);

        }

    }
    public static class Coord{
        int x;
        int y;

        public Coord(int x, int y){
            this.x = x;
            this.y = y;
        }



    }
    public static void main(String[] args) {
        launch(args);
    }

    }

