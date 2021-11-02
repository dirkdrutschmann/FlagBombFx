package de.bhtpaf.pacbomb.helper;

import de.bhtpaf.pacbomb.helper.classes.map.Coord;
import de.bhtpaf.pacbomb.helper.classes.map.Square;

public class BomberMan
{
    public Coord coord;
    public Square square;
    public int width;

    public BomberMan(int width)
    {
        this.width = width;
        this.coord = new Coord(0, width);
        this.square = new Square(this.coord.x, this.coord.y, width);
    }

    public void addX(int inc)
    {
        this.coord.x += inc;
        this.square.downLeft.x += inc;
        this.square.downRight.x += inc;
        this.square.upperLeft.x += inc;
        this.square.upperRight.x += inc;
    }

    public void addY(int inc)
    {
        this.coord.y += inc;
        this.square.downLeft.y += inc;
        this.square.downRight.y += inc;
        this.square.upperLeft.y += inc;
        this.square.upperRight.y += inc;
        // this.square = new Square(coord.x, coord.y, this.bomberManSize);
    }

    public void doStep(Dir direction, int step)
    {
        switch (direction)
        {
            case up:
                addY(-step);
                break;
            case down:
                addY(step);
                break;
            case left:
                addX(-step);
                break;
            case right:
                addX(step);
                break;
        }
    }


}