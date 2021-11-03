package de.bhtpaf.pacbomb.helper.classes.map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Tile extends Square {
    public Type type;
    public int width;

    public Tile(Coord coord, int width, Type type)
    {
        super(coord.x, coord.y, width);
        this.type = type;
        this.width = width;
    }

    public void draw(GraphicsContext gc)
    {
        gc.setFill(Color.web("0x629E5E"));
        gc.fillRect(downLeft.x, downLeft.y, width, width);
        Type.draw(gc, this);
    }
}
