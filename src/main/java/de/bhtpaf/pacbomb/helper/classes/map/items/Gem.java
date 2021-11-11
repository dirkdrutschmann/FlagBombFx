package de.bhtpaf.pacbomb.helper.classes.map.items;

import de.bhtpaf.pacbomb.PacBomb;
import de.bhtpaf.pacbomb.helper.classes.map.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;


import java.util.Random;

public class Gem extends Item
{

    private final Image[] _foodImages = new Image[]{
            new Image(PacBomb.class.getResourceAsStream("food/1.png")),
            new Image(PacBomb.class.getResourceAsStream("food/2.png")),
            new Image(PacBomb.class.getResourceAsStream("food/3.png"))
    };
    private final int _selectedImage;
    private final int _foodSize;

    public Gem(int x, int y, int width , int selection)
    {
        super(x, y, width);
        _foodSize =  width - 2;
        _selectedImage = selection;
    }

    public static Gem getRandom(Grid grid)
    {
        Random rand = new Random();
        int selection = rand.nextInt(3);


        Tile tile = grid.columns.get(grid.columnCount - 1).get(grid.rowCount - 1);

        int xBound = tile.downLeft.x;
        int yBound = tile.downLeft.y;

        while (true)
        {
            int x = rand.nextInt(xBound);
            int y = rand.nextInt(yBound);

            tile = grid.find(new Coord(x, y));

            if (tile.type != Type.WALL)
            {
                break;
            }
        }

        return new Gem(tile.downLeft.x, tile.downLeft.y, tile.width, selection);
    }



    @Override
    public void draw(GraphicsContext gc)
    {
        gc.drawImage( _foodImages[_selectedImage], square.downLeft.x , square.downLeft.y , _foodSize, _foodSize);
    }
}

