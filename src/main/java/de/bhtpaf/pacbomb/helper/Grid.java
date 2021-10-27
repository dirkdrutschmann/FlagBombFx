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
                {column.forEach((corner)-> {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(corner.downLeft.x, corner.downLeft.y, this.width, this.height);
                    switch (corner.type) {
                        case wall:
                            gc.setFill(Color.DARKCYAN);
                            gc.fillRect(corner.downLeft.x + 1, corner.downLeft.y + 1 , this.width-2, this.height-2);
                            break;
                        case block:
                            gc.setFill(Color.CHOCOLATE);
                            gc.fillRect(corner.downLeft.x + 1, corner.downLeft.y + 1 , this.width-2, this.height-2);
                            break;
                        case free:
                            gc.setFill(Color.LIGHTGRAY);
                            gc.fillRect(corner.downLeft.x + 1, corner.downLeft.y + 1 , this.width-2, this.height-2);
                            break;
                    }});
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

}
