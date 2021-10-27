package de.bhtpaf.pacbomb.helper;

public class Bomb {
    public Coord coord;
    public int state = 0;
    public Corner corner;

    public Bomb(Coord coord, int bombSize) {
        this.coord = coord;
        this.corner = new Corner(this.coord, bombSize );
    }

}