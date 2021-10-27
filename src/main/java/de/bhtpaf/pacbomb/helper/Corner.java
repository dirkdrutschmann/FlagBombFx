package de.bhtpaf.pacbomb.helper;

public class Corner{
    public Coord upperLeft;
    public Coord downLeft;
    public Coord upperRight;
    public Coord downRight;
    public int width;


    public Corner(Coord coord, int width){
        this.width = width;
        this.downLeft = coord;
        this.upperLeft = new Coord(coord.x, coord.y + width);
        this.downRight = new Coord(coord.x + width, coord.y);
        this.upperRight = new Coord(coord.x + width, coord.y + width);
    }

    public boolean compare(Corner corner){
        return (downLeft.x <= corner.upperRight.x) && (corner.downLeft.x <= upperRight.x) && (downLeft.y <= corner.upperRight.y) && (corner.downLeft.y <= upperRight.y);
    }
    public boolean compare(Coord coord){
        return (downLeft.x <= coord.x) && (coord.x <= upperRight.x) && (downLeft.y <= coord.y) && (coord.y <= upperRight.y);
    }

}