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
import javafx.stage.Stage;


import java.util.Random;


public class SnakeTest extends Application  {
    // variable
    static int speed = 10;
    static int bombs = -1;
    static int foodcolor = 0;
    static int width = 500;
    static int height = 500;
    static int foodX = 0;
    static int foodY = 0;
    static int cornersize = 25;
    static int step = 2;
    static Dir direction = Dir.right;
    static boolean gameOver = false;
    static Random rand = new Random();
    //Creating an image
    static Image image = new Image(SnakeTest.class.getResourceAsStream("bomberman.gif"));
    //Upper Left of Image / Image is set 50x50
    static int PacBombX = width / 2 ;
    static int PacBombY = height /2 ;

    public enum Dir {
        left, right, up, down
    }



    public void start(Stage primaryStage)  {
        try {
            newFood();

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
            return;
        }





        switch (direction) {
            case up:
                PacBombY -= step;
                if (PacBombY < 0) {
                    gameOver = true;
                }
                break;
            case down:
                PacBombY += step;
                if (PacBombY + 50 > height) {
                    gameOver = true;
                }
                break;
            case left:
                PacBombX -= step;
                if (PacBombX < 0) {
                    gameOver = true;
                }
                break;
            case right:
                PacBombX += step;
                if (PacBombX + 50 > width) {
                    gameOver = true;
                }
                break;

        }

        // eat food
        if (foodX >= PacBombX && foodX <= PacBombX+50 && foodY >= PacBombY && foodY <= PacBombY+50) {
            newFood();
        }



        // fill
        // background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width , height );

        // score
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("", 30));
        gc.fillText("BOMBS: " + bombs, 10, 30);

        // random foodcolor
        Color cc = Color.WHITE;

        switch (foodcolor) {
            case 0:
                cc = Color.PURPLE;
                break;
            case 1:
                cc = Color.LIGHTBLUE;
                break;
            case 2:
                cc = Color.YELLOW;
                break;
            case 3:
                cc = Color.PINK;
                break;
            case 4:
                cc = Color.ORANGE;
                break;
        }
        gc.setFill(cc);
        gc.fillOval(foodX , foodY , cornersize, cornersize);



        //Setting the image view
        gc.drawImage(image, PacBombX, PacBombY, 50, 50);

    }

    // food
    public static void newFood() {
        start: while (true) {
            bombs++;
            foodX = rand.nextInt(width);
            foodY = rand.nextInt(height);

            foodcolor = rand.nextInt(5);
            speed++;
            break;

        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    }

