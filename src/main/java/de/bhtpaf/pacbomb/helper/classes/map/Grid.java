package de.bhtpaf.pacbomb.helper.classes.map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.List;
import java.util.ArrayList;

public class Grid {
    public List<List<Tile>> rows= new ArrayList();
    public int width;
    public int height;

    public Grid(int width, int height, int squareFactor)
    {
        this.width = width / squareFactor;
        this.height = (height - 20) / squareFactor;

        List<Tile> column = null;

        for (int i = 0; i < this.width; i++)
        {
            column = new ArrayList();

            for (int k = 0; k < this.height; k++)
            {
                column.add(
                    new Tile(
                        new Coord(i * squareFactor, (k + 1) * squareFactor),
                        this.width,
                        Type.random()
                    )
                );
            }

            rows.add(column);
        }
    }

    public void draw (GraphicsContext gc)
    {
        this.rows.forEach((column) -> {
            column.forEach((tile)-> {
                gc.setFill(Color.BLACK);
                gc.fillRect(tile.downLeft.x, tile.downLeft.y, tile.width, tile.width);
                Type.draw(gc, tile);
           });
        });
    }

    public Square find (Coord coord)
    {
        for(List<Tile> list : this.rows)
        {
            for (Tile tile : list)
            {
                if(tile.compare(coord))
                {
                   return tile;
                }
            }
        }

        return null;
    }

    public boolean hit (Square square, Type type)
    {
        for(List<Tile> list : this.rows)
        {
            for (Tile tile : list)
            {
                if(tile.compare(square) && tile.type == type)
                {
                    return true;
                }
            }
        }
        return false;
    }

}
