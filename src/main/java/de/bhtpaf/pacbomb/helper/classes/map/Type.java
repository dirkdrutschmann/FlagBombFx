package de.bhtpaf.pacbomb.helper.classes.map;

import de.bhtpaf.pacbomb.PacBomb;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Random;

public enum Type {
    wall,
    free;

    private static final List<Type> VALUES = List.of(values());
    private static final int SIZE = VALUES.size();

    private static final Random RANDOM = new Random();
    private static final Image wallImage = new Image(PacBomb.class.getResourceAsStream("wall.png"));
    private static final Image blockImage = new Image(PacBomb.class.getResourceAsStream("stone.png"));
    private static final Image freeImage = new Image(PacBomb.class.getResourceAsStream("free.png"));

    public static Type random()
    {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

    public static void draw(GraphicsContext gc, Tile tile)
    {
        switch (tile.type) {
            case wall:
                if(((Wall)tile).isDestroyable)
                {
                    gc.drawImage(wallImage, tile.downLeft.x + 1, tile.downLeft.y + 1, tile.width - 2, tile.width - 2);
                }
                else
                {
                    gc.drawImage(blockImage, tile.downLeft.x + 1, tile.downLeft.y + 1, tile.width - 2, tile.width - 2);
                }

                break;
            case free:
                //gc.drawImage(freeImage, tile.downLeft.x + 1, tile.downLeft.y + 1, tile.width - 2, tile.width - 2);
                gc.setFill(Color.web("0x629E5E"));
                gc.fillRect(tile.downLeft.x + 1, tile.downLeft.y + 1, tile.width - 2, tile.width - 2);
                break;
        }
    }
}
