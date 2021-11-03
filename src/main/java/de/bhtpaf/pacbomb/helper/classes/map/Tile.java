package de.bhtpaf.pacbomb.helper.classes.map;

public class Tile extends Square {
    public Type type;
    public int width;

    public Tile(Coord coord, int width, Type type)
    {
        super(coord.x, coord.y, width);
        this.type = type;
        this.width = width;
    }
}
