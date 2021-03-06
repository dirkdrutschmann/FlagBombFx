package de.bhtpaf.flagbomb.helper.classes.map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.bhtpaf.flagbomb.helper.BomberMan;
import de.bhtpaf.flagbomb.helper.classes.json.JsonDeserializerWithInheritance;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Grid {

    public int squareFactor;
    public int height;
    public int width;
    public int captureFlagCount;
    public int bombsAtStart;

    public List<List<Tile>> columns = new ArrayList();
    public int columnCount;
    public int rowCount;

    public Grid ()
    {}

    public Grid(int width, int height, int squareFactor, int captureFlagCount, int bombsAtStart)
    {
        this.columnCount = width / squareFactor;
        this.rowCount = (height - 20) / squareFactor;

        this.width = width;
        this.height = height;
        this.squareFactor = squareFactor;
        this.captureFlagCount = captureFlagCount;
        this.bombsAtStart = bombsAtStart;
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

    public boolean hit (BomberMan player)
    {
        Tile current = null;
        Tile middle = find(new Coord((player.square.upperLeft.x + player.square.downRight.x) / 2, (player.square.upperLeft.y + player.square.downRight.y) / 2));

        switch (player.getDirection())
        {
            case UP:
            {
                current = find(new Coord(player.square.downLeft.x, player.square.downLeft.y - 1));
                break;
            }
            case DOWN:
            {
                current = find(new Coord(player.square.upperLeft.x, player.square.upperLeft.y + 1));
                break;
            }
            case LEFT:
            {
                current = find(new Coord(player.square.downRight.x - 1, player.square.downRight.y));
                break;
            }
            case RIGHT:
            {
                current = find(new Coord(player.square.downLeft.x + 1, player.square.downLeft.y));
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

        switch (player.getDirection())
        {
            case UP:
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
            case DOWN:
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
            case LEFT:
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
            case RIGHT:
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
            switch (player.getDirection())
            {
                case UP:
                {
                    if(player.square.upperLeft.y - 1 <= refTile.downLeft.y)
                    {
                        return true;
                    }

                    break;
                }
                case DOWN:
                {
                    if(player.square.downLeft.y + 1 >= refTile.upperLeft.y)
                    {
                        return true;
                    }

                    break;
                }
                case LEFT:
                {
                    if(player.square.downLeft.x - 1 <= refTile.downRight.x)
                    {
                        return true;
                    }

                    break;
                }
                case RIGHT:
                {
                    if(player.square.downRight.x + 1 >= refTile.downLeft.x)
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
                // Rest zuf??llig
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
