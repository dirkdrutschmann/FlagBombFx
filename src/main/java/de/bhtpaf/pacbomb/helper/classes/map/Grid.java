package de.bhtpaf.pacbomb.helper.classes.map;

import de.bhtpaf.pacbomb.helper.Dir;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.List;
import java.util.ArrayList;

public class Grid {

    public List<List<Tile>> columns = new ArrayList();
    public int columnCount;
    public int rowCount;

    public Grid(int width, int height, int squareFactor)
    {
        this.columnCount = width / squareFactor;
        this.rowCount = (height - 20) / squareFactor;

        List<Tile> row;

        for (int i = 0; i < this.columnCount; i++)
        {
            row = new ArrayList();

            for (int k = 0; k < this.rowCount; k++)
            {
                row.add(
                    new Tile(
                        new Coord(i * squareFactor, (k + 1) * squareFactor),
                        squareFactor,
                        Type.random()
                    )
                );
            }

            columns.add(row);
        }
    }

    public void draw (GraphicsContext gc)
    {
        this.columns.forEach((column) -> {
            column.forEach((tile)-> {
                gc.setFill(Color.BLACK);
                gc.fillRect(tile.downLeft.x, tile.downLeft.y, tile.width, tile.width);
                Type.draw(gc, tile);
           });
        });
    }

    public Tile find (Coord coord)
    {
        for(List<Tile> rows : this.columns)
        {
            for (Tile tile : rows)
            {
                if(tile.compare(coord))
                {
                   return tile;
                }
            }
        }



        return null;
    }

    public IndexValues getIndexValue(Tile tile)
    {
        for(int i = 0; i < this.columnCount; i++)
        {
            for (int k = 0; k < this.rowCount; k++)
            {
                if(columns.get(i).get(k).compare(tile))
                {
                    return new IndexValues(i, k);
                }
            }
        }

        return null;
    }

    public boolean hit (Square myself, Dir direction)
    {
        Tile current = null;
        Tile middle = find(new Coord((myself.upperLeft.x + myself.downRight.x) / 2, (myself.upperLeft.y + myself.downRight.y) / 2));

        switch (direction)
        {
            case up:
            {
                current = find(new Coord(myself.downLeft.x, myself.downLeft.y - 1));
                break;
            }
            case down:
            {
                current = find(new Coord(myself.upperLeft.x, myself.upperLeft.y + 1));
                break;
            }
            case left:
            {
                current = find(new Coord(myself.downRight.x - 1, myself.downRight.y));
                break;
            }
            case right:
            {
                current = find(new Coord(myself.downLeft.x + 1, myself.downLeft.y));
                break;
            }
        }

        if (current == null)
        {
            return true;
        }

        IndexValues indexes = getIndexValue(current);
        IndexValues indexesMiddle = getIndexValue(middle);

        Tile refTile = null;

        switch (direction)
        {
            case up:
            {
                // Erste Zeile erreicht
                if (indexes.row == 0)
                {
                    return true;
                }

                if (indexesMiddle.row > 0)
                {
                    refTile = columns.get(indexesMiddle.column).get(indexesMiddle.row - 1);
                }

                break;
            }
            case down:
            {
                // Letzte Zeile erreicht
                if (indexes.row + 1 == rowCount)
                {
                    return true;
                }

                if (indexesMiddle.row < rowCount)
                {
                    refTile = columns.get(indexesMiddle.column).get(indexesMiddle.row + 1);
                }

                break;
            }
            case left:
            {
                // Linker Rand
                if (indexes.column == 0)
                {
                    return true;
                }

                if (indexesMiddle.column > 0)
                {
                    refTile = columns.get(indexesMiddle.column - 1).get(indexesMiddle.row);
                }

                break;
            }
            case right:
            {
                // Rechter Rand
                if (indexes.column + 1 == columnCount)
                {
                    return true;
                }

                if (indexesMiddle.column < columnCount)
                {
                    refTile = columns.get(indexesMiddle.column + 1).get(indexesMiddle.row);
                }

                break;
            }
        }

        if (refTile != null && refTile.type == Type.wall)
        {
            switch (direction)
            {
                case up:
                {
                    if(myself.upperLeft.y - 1 <= refTile.downLeft.y)
                    {
                        return true;
                    }

                    break;
                }
                case down:
                {
                    if(myself.downLeft.y + 1 >= refTile.upperLeft.y)
                    {
                        return true;
                    }

                    break;
                }
                case left:
                {
                    if(myself.downLeft.x - 1 <= refTile.downRight.x)
                    {
                        return true;
                    }

                    break;
                }
                case right:
                {
                    if(myself.downRight.x + 1 >= refTile.downLeft.x)
                    {
                        return true;
                    }

                    break;
                }
            }
        }

        return false;
    }

}
