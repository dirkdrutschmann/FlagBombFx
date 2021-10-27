package de.bhtpaf.pacbomb.helper;

import de.bhtpaf.pacbomb.PacBomb;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Type {
    wall,block,free,;


    private static final List<Type> VALUES =
            List.of(values());
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();
    private static Image wallImage = new Image(PacBomb.class.getResourceAsStream("wall.gif"));
    public static Type random()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

    public static void draw(GraphicsContext gc, Tile tile) {
        switch (tile.type) {
            case wall:
                gc.drawImage(wallImage, tile.downLeft.x + 1, tile.downLeft.y + 1, tile.width - 2, tile.width - 2);
                break;
            case block:
                gc.setFill(Color.CHOCOLATE);
                gc.fillRect(tile.downLeft.x + 1, tile.downLeft.y + 1, tile.width - 2, tile.width - 2);
                break;
            case free:
                gc.setFill(Color.LIGHTGRAY);
                gc.fillRect(tile.downLeft.x + 1, tile.downLeft.y + 1, tile.width - 2, tile.width - 2);
                break;
        }
    }
}
