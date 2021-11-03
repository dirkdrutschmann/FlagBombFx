package de.bhtpaf.pacbomb.helper;

import de.bhtpaf.pacbomb.helper.classes.map.Coord;
import de.bhtpaf.pacbomb.helper.classes.map.Square;
import de.bhtpaf.pacbomb.helper.classes.map.items.Item;

public class BomberMan extends Item
{
    public int width;

    public BomberMan(int x, int y, int width)
    {
        super(x, y, width);
    }

    public void addX(int inc)
    {
        this.square.downLeft.x += inc;
        this.square.downRight.x += inc;
        this.square.upperLeft.x += inc;
        this.square.upperRight.x += inc;
    }

    public void addY(int inc)
    {
        this.square.downLeft.y += inc;
        this.square.downRight.y += inc;
        this.square.upperLeft.y += inc;
        this.square.upperRight.y += inc;
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