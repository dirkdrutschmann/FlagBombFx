package de.bhtpaf.pacbomb.helper.classes.map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import de.bhtpaf.pacbomb.helper.Dir;
import de.bhtpaf.pacbomb.helper.classes.json.JsonDeserializerWithInheritance;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Grid {

    public int squareFactor;
    public int height;
    public int width;

    public List<List<Tile>> columns = new ArrayList();
    public int columnCount;
    public int rowCount;

    public Grid ()
    {}

    public Grid(int width, int height, int squareFactor)
    {
        this.columnCount = width / squareFactor;
        this.rowCount = (height - 20) / squareFactor;

        this.width = width;
        this.height = height;
        this.squareFactor = squareFactor;
    }

    public void draw (GraphicsContext gc)
    {
        this.columns.forEach((column) -> {
            column.forEach((tile)-> {
                tile.draw(gc);
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

                if (indexesMiddle.row + 1 < rowCount)
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

                if (indexesMiddle.column + 1 < columnCount)
                {
                    refTile = columns.get(indexesMiddle.column + 1).get(indexesMiddle.row);
                }

                break;
            }
        }

        if (refTile != null && refTile instanceof Wall)
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

    public void generateMap()
    {
        Random rand = new Random();
        List<Tile> row;
        for (int i = 0; i < this.columnCount; i++)
        {
            row = new ArrayList();

            for (int k = 0; k < this.rowCount; k++)
            {

                // Erste und letzte Spalte und erste und letzte Zeile immer frei
                if
                (
                       i == 0
                    || i == this.columnCount - 1
                    || k == 0
                    || k == this.rowCount - 1
                    || (k == rowCount / 2 && (i == 1 || i == 2 || i == columnCount - 2 || i == columnCount - 3))
                )
                {
                    row.add(
                        new Tile(
                            new Coord(i * squareFactor, (k + 1) * squareFactor),
                            squareFactor,
                            Type.FREE
                        )
                    );
                }

                // Einrandung Flagge
                else if
                (
                       (k == (rowCount / 2) - 1 || k == (rowCount / 2) + 1 && (i == 1 || i == 2 || i == 3 || i == columnCount - 2 || i == columnCount - 3 || i == columnCount - 4))
                    || (k == rowCount / 2 && (i == 3 || i == columnCount - 4))
                )
                {
                    row.add(
                        new Wall(
                            new Coord(i * squareFactor, (k + 1) * squareFactor),
                            squareFactor,
                            false
                        )
                    );
                }
                // Rest zufällig
                else
                {
                    Type t = Type.random();

                    if (t == Type.WALL)
                    {
                        row.add(
                            new Wall(
                                new Coord(i * squareFactor, (k + 1) * squareFactor),
                                squareFactor,
                                rand.nextInt(500) %  2 == 0
                            )
                        );
                    }
                    else
                    {
                        row.add(
                            new Tile(
                                new Coord(i * squareFactor, (k + 1) * squareFactor),
                                squareFactor,
                                t
                            )
                        );
                    }
                }
            }

            columns.add(row);
        }
    }

    public String toJson()
    {
        return new Gson().toJson(this);
    }

    public static Grid getFromJson(String json)
    {
        return new GsonBuilder().registerTypeAdapter(Tile.class, new JsonDeserializerWithInheritance<Tile>()).create().fromJson(json, Grid.class);
    }
}
