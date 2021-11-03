package de.bhtpaf.pacbomb.helper.classes.map.items;

import de.bhtpaf.pacbomb.helper.classes.map.Grid;
import de.bhtpaf.pacbomb.helper.classes.map.Square;

public class Item {
    public Square square;

    public Item (int x, int y, int width){
        this.square = new Square(x, y, width);
    }
}
