package de.bhtpaf.pacbomb.helper;

import de.bhtpaf.pacbomb.helper.classes.map.Coord;
import de.bhtpaf.pacbomb.helper.classes.map.Grid;
import de.bhtpaf.pacbomb.helper.classes.map.Square;
import javafx.scene.paint.Color;
import java.util.Random;

public class Food{
    public Coord coord;
    public Color color;
    public Square square;
    public int foodSize;

    public Food(int width, int height, int foodSize, Grid grid)
    {
        Random rand = new Random();

        this.color = color(rand.nextInt(5));

        Coord work = grid.columns.get(rand.nextInt(grid.columns.size())).get(rand.nextInt(grid.columns.get(0).size())).downLeft;

        this.coord = new Coord(work.x + 1,work.y + 1);
        this.foodSize =  foodSize -2;
        this.square = new Square(this.coord.x, this.coord.y, foodSize - 2);
    }

    private Color color (int rand)
    {
        switch (rand)
        {
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

