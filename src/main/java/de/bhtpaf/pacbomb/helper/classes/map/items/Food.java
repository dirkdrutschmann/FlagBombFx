package de.bhtpaf.pacbomb.helper.classes.map.items;

import de.bhtpaf.pacbomb.helper.classes.map.*;
import javafx.scene.canvas.GraphicsContext;
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

        Tile tile = grid.columns.get(grid.columnCount - 1).get(grid.rowCount - 1);

        int xBound = tile.downLeft.x;
        int yBound = tile.downLeft.y;

        while (true)
        {
            int x = rand.nextInt(xBound);
            int y = rand.nextInt(yBound);

            tile = grid.find(new Coord(x, y));

            if (tile.type != Type.wall)
            {
                break;
            }
        }

        return new Food(tile.downLeft.x, tile.downLeft.y, tile.width, color);
    }

    @Override
    public void draw(GraphicsContext gc)
    {
        gc.setFill(color);
        gc.fillOval(square.downLeft.x , square.downLeft.y , foodSize, foodSize);
    }
}

