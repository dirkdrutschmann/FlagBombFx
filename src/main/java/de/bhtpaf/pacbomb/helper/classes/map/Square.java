package de.bhtpaf.pacbomb.helper.classes.map;

public class Square {

    public Coord upperLeft;
    public Coord downLeft;
    public Coord upperRight;
    public Coord downRight;

    public Square(int x, int y, int width){
        this.downLeft = new Coord(x, y);
        this.upperLeft = new Coord(x, y - width);
        this.downRight = new Coord(x + width, y);
        this.upperRight = new Coord(x + width, y - width);
    }

    public boolean compare(Square square)
    {
        if
        (
               downLeft.x == square.downLeft.x
            && downLeft.y == square.downLeft.y
        )
        {
            return true;
        }

        return false;
    }

    public boolean compare(Coord coord){

        if
        (
               downLeft.x <= coord.x
            && downRight.x >= coord.x
            && upperLeft.y <= coord.y
            && downLeft.y >= coord.y
        )
        {
            return true;
        }

        return false;
    }

}