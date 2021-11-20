package de.bhtpaf.flagbomb.helper.classes.map.items;

import de.bhtpaf.flagbomb.helper.classes.map.Coord;
import de.bhtpaf.flagbomb.helper.classes.map.Square;
import javafx.scene.canvas.GraphicsContext;

public abstract class Item
{
    public Square square;

    private final Square _initSquare;

    public Item (int x, int y, int width){
        this.square = new Square(x, y, width);
        this._initSquare = square;
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

    public abstract void draw(GraphicsContext gc);

}
