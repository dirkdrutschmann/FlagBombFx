package de.bhtpaf.pacbomb.helper;

import javafx.scene.paint.Color;
import java.util.Random;

public class Food{
    public Coord coord;
    public Color color;
    public Corner corner;
    public int foodSize;

    public Food(int width, int height, int foodSize, Grid grid){
        Random rand = new Random();
        this.color = color(rand.nextInt(5));
        Coord work = grid.rows.get(rand.nextInt(grid.rows.size())).get(rand.nextInt(grid.rows.get(0).size())).downLeft;
        this.coord = new Coord(work.x+1,work.y+1);
        this.foodSize =  foodSize -2;
        this.corner = new Corner(this.coord, foodSize -2 );
    }

    private Color color (int rand) {
        switch (rand) {
            case 0:
                return Color.PURPLE;
            case 1:
                return Color.LIGHTBLUE;
            case 2:
                return Color.YELLOW;
            case 3:
                return Color.PINK;
            case 4:
                return Color.ORANGE;
        }
        return Color.WHITE;
    }


}

