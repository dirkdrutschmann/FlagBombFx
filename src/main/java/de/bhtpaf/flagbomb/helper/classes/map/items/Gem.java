package de.bhtpaf.flagbomb.helper.classes.map.items;

import de.bhtpaf.flagbomb.FlagBomb;
import de.bhtpaf.flagbomb.helper.classes.json.ItemJson;
import de.bhtpaf.flagbomb.helper.classes.map.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;


import java.util.Random;

public class Gem extends Item
{

    private static final Image[] _gemImages = new Image[]{
            new Image(FlagBomb.class.getResourceAsStream("food/1.png")),
            new Image(FlagBomb.class.getResourceAsStream("food/2.png")),
            new Image(FlagBomb.class.getResourceAsStream("food/3.png"))
    };
    private final int _selectedImage;
    private final int _gemSize;

    public Gem(int x, int y, int width , int selection)
    {
        super(x, y, width);
        _gemSize =  width - 2;
        _selectedImage = selection;
    }

    public Gem(int x, int y, int width , int selection, String itemId)
    {
        super(x, y, width, itemId);
        _gemSize =  width - 2;
        _selectedImage = selection;
    }

    public static Gem getRandom(Grid grid)
    {
        Random rand = new Random();
        int selection = rand.nextInt(_gemImages.length);


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
        gc.drawImage( _gemImages[_selectedImage], square.downLeft.x , square.downLeft.y , _gemSize, _gemSize);
    }
}

