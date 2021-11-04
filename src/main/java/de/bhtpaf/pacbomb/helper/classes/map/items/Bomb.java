package de.bhtpaf.pacbomb.helper.classes.map.items;

import de.bhtpaf.pacbomb.helper.classes.map.*;

import java.util.ArrayList;
import java.util.List;

// Stellt eine Bombe dar
public class Bomb extends Item
{
    // Bombenstatus
    private int _state = 0;

    public Bomb(Square square) {
        super(square.downLeft.x, square.downLeft.y, square.downRight.x - square.downLeft.x);
    }

    public void setState(int state)
    {
        _state = state;
    }

    public int getState()
    {
        return _state;
    }

    public List<ExtendedTile> getInfectedTiles(Grid grid)
    {
        List<ExtendedTile> infectedTiles = new ArrayList<>();

        IndexValues indexes = grid.getIndexValue(grid.find(getMiddleCoord()));

        Tile temp;

        // links
        if (indexes.column > 0)
        {
            temp = grid.columns.get(indexes.column - 1).get(indexes.row);

            infectedTiles.add(
                new ExtendedTile(temp, new IndexValues(indexes.column - 1, indexes.row))
            );
        }

        // rechts
        if (indexes.column < grid.columnCount - 1)
        {
            temp = grid.columns.get(indexes.column + 1).get(indexes.row);

            infectedTiles.add(
                new ExtendedTile(temp, new IndexValues(indexes.column + 1, indexes.row))
            );
        }

        // oben
        if (indexes.row > 0)
        {
            temp = grid.columns.get(indexes.column).get(indexes.row - 1);

            infectedTiles.add(
                new ExtendedTile(temp, new IndexValues(indexes.column, indexes.row - 1))
            );
        }

        // unten
        if (indexes.row < grid.rowCount - 1)
        {
            temp = grid.columns.get(indexes.column).get(indexes.row + 1);

            infectedTiles.add(
                new ExtendedTile(temp, new IndexValues(indexes.column, indexes.row + 1))
            );
        }

        return infectedTiles;
    }
}