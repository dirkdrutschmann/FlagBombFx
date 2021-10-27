package de.bhtpaf.pacbomb.helper;

public class BomberMan{
    public Coord coord;
    public Corner corner;
    public int bomberManSize;

    public BomberMan(int width, int bomberManSize){
        this.bomberManSize = bomberManSize;
        this.coord = new Coord(width / 2, width / 2 );
        this.corner = new Corner(this.coord, bomberManSize );
    }

    public void addX(int inc){
        this.coord.x += inc;
        this.corner = new Corner(this.coord, this.bomberManSize );
    }
    public void addY(int inc){
        this.coord.y += inc;
        this.corner = new Corner(coord, this.bomberManSize );
    }


}