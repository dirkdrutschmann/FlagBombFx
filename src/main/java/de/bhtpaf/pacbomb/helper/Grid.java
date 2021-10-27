package de.bhtpaf.pacbomb.helper;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.List;
import java.util.ArrayList;

public class Grid {
    public List<List<Tile>> rows= new ArrayList();
    public int width;
    public int height;

    public Grid(int width, int height,int squareFactor ){
        this.width = width/squareFactor;
        this.height = height/squareFactor;
        for(int i = this.width; i < width-this.width; i = i + width/squareFactor){
            List<Tile> column = new ArrayList();



            for (int j = this.height; j < height - this.height; j = j + height/squareFactor){

                    column.add(new Tile(new Coord(i, j), this.width, Type.random()));


            }
            rows.add(column);
        }
    }



    public void draw (GraphicsContext gc){
        this.rows.forEach((column)  ->
                {column.forEach((tile)-> {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(tile.downLeft.x, tile.downLeft.y, tile.width, tile.width);
                    Type.draw(gc, tile);
               });
                });
    }

    public Coord find (Coord coord){
        for(List<Tile> list : this.rows){
            for (Tile corner : list){
                if(corner.compare(coord)){
                   return corner.downLeft;
                }
            }
        }
        return coord;
    }
    public boolean hit (Corner corner, Type type){
        for(List<Tile> list : this.rows){
            for (Tile tile : list){
                if(tile.compare(corner) && tile.type == type){
                    return true;
                }
            }
        }
        return false;
    }

}
