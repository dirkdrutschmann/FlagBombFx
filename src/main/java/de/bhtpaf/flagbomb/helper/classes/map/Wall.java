package de.bhtpaf.flagbomb.helper.classes.map;

public class Wall extends Tile
{
    public boolean isDestroyable;

    public Wall(Coord coord, int width, boolean isDestroyable)
    {
        super(coord, width, Type.WALL);
        this.isDestroyable = isDestroyable;
    }
}
