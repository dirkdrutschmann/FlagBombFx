package de.bhtpaf.pacbomb.helper.classes.map.items;

import de.bhtpaf.pacbomb.helper.classes.map.Coord;
import de.bhtpaf.pacbomb.helper.classes.map.Square;

public class Item {
    public Square square;

    public Item (int x, int y, int width){
        this.square = new Square(x, y, width);
    }

    public Coord getMiddleCoord()
    {
        Coord middle = new Coord();

        middle.x = (this.square.downLeft.x + this.square.downRight.x) / 2;
        middle.y = (this.square.downLeft.y + this.square.upperLeft.y) / 2;

        return middle;
    }
}
