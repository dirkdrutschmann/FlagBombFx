package de.bhtpaf.flagbomb.helper.classes.map;

import de.bhtpaf.flagbomb.FlagBomb;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Random;

public enum Type {
    WALL,
    FREE;

    private static final List<Type> VALUES = List.of(values());
    private static final int SIZE = VALUES.size();

    private static final Random RANDOM = new Random();
    private static final Image wallImage = new Image(FlagBomb.class.getResourceAsStream("grid/wall.png"));
    private static final Image blockImage = new Image(FlagBomb.class.getResourceAsStream("grid/stone.png"));
    private static final Image freeImage = new Image(FlagBomb.class.getResourceAsStream("grid/free.png"));

    public static Type random()
    {
        Type t = VALUES.get(RANDOM.nextInt(SIZE));

        if (t == FREE && RANDOM.nextInt(500) % 2 == 0)
        {
            return WALL;
        }

        return t;
    }

    public static void draw(GraphicsContext gc, Tile tile)
    {
        switch (tile.type) {
            case WALL:
                if(((Wall)tile).isDestroyable)
                {
                    gc.drawImage(wallImage, tile.downLeft.x + 1, tile.downLeft.y + 1, tile.width - 2, tile.width - 2);
                }
                else
                {
                    gc.drawImage(blockImage, tile.downLeft.x + 1, tile.downLeft.y + 1, tile.width - 2, tile.width - 2);
                }

                break;
            case FREE:
                //gc.drawImage(freeImage, tile.downLeft.x + 1, tile.downLeft.y + 1, tile.width - 2, tile.width - 2);
                gc.setFill(Color.web("0x629E5E"));
                gc.fillRect(tile.downLeft.x + 1, tile.downLeft.y + 1, tile.width - 2, tile.width - 2);
                break;
        }
    }
}
