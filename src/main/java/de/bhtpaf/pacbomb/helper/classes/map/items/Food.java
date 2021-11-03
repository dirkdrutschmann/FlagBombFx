package de.bhtpaf.pacbomb.helper.classes.map.items;

import de.bhtpaf.pacbomb.helper.classes.map.*;
import javafx.scene.paint.Color;
import java.util.Random;

public class Food extends Item
{
    public Color color;

    public int foodSize;

    public Food(int x, int y, int width, Color color)
    {
        super(x, y, width);
        this.foodSize =  width - 2;
        this.color = color;

    }

    public static Food getRandom(Grid grid)
    {
        Random rand = new Random();
        Color color = null;

        switch (rand.nextInt(5))
        {
            case 0:
            {
                color =  Color.PURPLE;
                break;
            }
            case 1:
            {
                color = Color.LIGHTBLUE;
                break;
            }
            case 2:
            {
                color = Color.YELLOW;
                break;
            }
            case 3:
            {
                color = Color.PINK;
                break;
            }
            case 4:
            {
                color = Color.ORANGE;
                break;
            }
        }

        if (color == null)
        {
            color = Color.WHITE;
        }

        Tile tile;

        while (true)
        {
            int x = rand.nextInt(950);
            int y = rand.nextInt(950);

            tile = grid.find(new Coord(x, y));

            if (tile.type != Type.wall)
            {
                break;
            }
        }

        return new Food(tile.downLeft.x, tile.downLeft.y, tile.width, color);
    }
}

