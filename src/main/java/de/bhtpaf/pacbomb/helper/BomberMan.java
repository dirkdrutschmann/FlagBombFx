package de.bhtpaf.pacbomb.helper;

import de.bhtpaf.pacbomb.helper.classes.map.Coord;
import de.bhtpaf.pacbomb.helper.classes.map.Square;

public class BomberMan
{
    public Coord coord;
    public Square square;
    public int bomberManSize;

    public BomberMan(int width, int bomberManSize)
    {
        this.bomberManSize = bomberManSize;
        //this.coord = new Coord(width / 2, width / 2 );
        this.coord = new Coord(0, 33);
        this.square = new Square(this.coord.x, this.coord.y, bomberManSize );
    }

    public void addX(int inc)
    {
        this.coord.x += inc;
        this.square = new Square(this.coord.x, this.coord.y, this.bomberManSize );
    }

    public void addY(int inc)
    {
        this.coord.y += inc;
        this.square = new Square(coord.x, coord.y, this.bomberManSize);
    }


}