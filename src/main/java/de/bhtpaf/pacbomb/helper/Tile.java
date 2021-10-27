package de.bhtpaf.pacbomb.helper;

public class Tile extends Corner{
    Type type;

    public Tile(Coord coord, int width, Type type){
        super(coord, width);
    this.type = type;
    }
}
