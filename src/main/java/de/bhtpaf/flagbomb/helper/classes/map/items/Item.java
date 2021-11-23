package de.bhtpaf.flagbomb.helper.classes.map.items;

import com.google.gson.annotations.Expose;
import de.bhtpaf.flagbomb.helper.classes.json.ItemJson;
import de.bhtpaf.flagbomb.helper.classes.map.Coord;
import de.bhtpaf.flagbomb.helper.classes.map.Square;
import javafx.scene.canvas.GraphicsContext;

import java.util.UUID;

public abstract class Item
{
    @Expose
    public String itemId;

    @Expose
    public Square square;

    private final Square _initSquare;

    public Item (int x, int y, int width)
    {
        this.square = new Square(x, y, width);
        this._initSquare = new Square(x, y, width);
        this.itemId = UUID.randomUUID().toString();
    }

    public Item (int x, int y, int width, String itemId)
    {
        this.square = new Square(x, y, width);
        this._initSquare = new Square(x, y, width);
        this.itemId = itemId;
    }

    public Coord getMiddleCoord()
    {
        Coord middle = new Coord();

        middle.x = (this.square.downLeft.x + this.square.downRight.x) / 2;
        middle.y = (this.square.downLeft.y + this.square.upperLeft.y) / 2;

        return middle;
    }

    protected Square getInitPosition()
    {
        return _initSquare;
    }

    public ItemJson getItemJson()
    {
        ItemJson json = new ItemJson();
        json.itemId = itemId;
        json.square = square;

        return json;
    }

    public abstract void draw(GraphicsContext gc);

}
