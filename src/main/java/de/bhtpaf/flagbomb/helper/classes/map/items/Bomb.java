package de.bhtpaf.flagbomb.helper.classes.map.items;

import de.bhtpaf.flagbomb.helper.classes.map.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

// Stellt eine Bombe dar
public class Bomb extends Item
{
    private final Image[] _stateImage;

    // Anzahl Bilder
    private final int _totalStates = 6;

    private final int _bombSize = square.downRight.x - square.downLeft.x;

    private final int _boomFactor = 2;

    private final MediaPlayer _boomSound;

    // Millisekunden bis Bombe explodiert
    private final long _bombTime = 1750;

    // Bombenstatus
    private int _state = 0;

    public Bomb(Square square, Image[] stateImages, Media boom)
    {
        super(square.downLeft.x, square.downLeft.y, square.downRight.x - square.downLeft.x);
        _stateImage = stateImages;
        _boomSound = new MediaPlayer(boom);
        _startBombTimer(_bombTime);
    }

    public Bomb(Square square, Image[] stateImages, long bombTime, Media boom)
    {
        super(square.downLeft.x, square.downLeft.y, square.downRight.x - square.downLeft.x);
        _stateImage = stateImages;
        _boomSound = new MediaPlayer(boom);
        _startBombTimer(bombTime);
    }

    public int getState()
    {
        return _state;
    }

    private void _startBombTimer(long explodesInMilliSeconds)
    {
        new Thread(() -> {
            long waitTime = explodesInMilliSeconds / _totalStates;

            for (int i = 0; i <= _totalStates; i++)
            {
                _state = i;
                try
                {
                    Thread.sleep(waitTime);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public List<ExtendedTile> getInfectedTiles(Grid grid)
    {
        List<ExtendedTile> infectedTiles = new ArrayList<>();

        IndexValues indexes = grid.getIndexValue(grid.find(getMiddleCoord()));

        Tile temp;

        boolean left = false, right = false, top = false, down = false;

        // links
        if (indexes.column > 0)
        {
            left = true;
            temp = grid.columns.get(indexes.column - 1).get(indexes.row);

            infectedTiles.add(
                new ExtendedTile(temp, new IndexValues(indexes.column - 1, indexes.row))
            );
        }

        // rechts
        if (indexes.column < grid.columnCount - 1)
        {
            right = true;
            temp = grid.columns.get(indexes.column + 1).get(indexes.row);

            infectedTiles.add(
                new ExtendedTile(temp, new IndexValues(indexes.column + 1, indexes.row))
            );
        }

        // oben
        if (indexes.row > 0)
        {
            top = true;
            temp = grid.columns.get(indexes.column).get(indexes.row - 1);

            infectedTiles.add(
                new ExtendedTile(temp, new IndexValues(indexes.column, indexes.row - 1))
            );
        }

        // unten
        if (indexes.row < grid.rowCount - 1)
        {
            down = true;
            temp = grid.columns.get(indexes.column).get(indexes.row + 1);

            infectedTiles.add(
                new ExtendedTile(temp, new IndexValues(indexes.column, indexes.row + 1))
            );
        }

        if (top && left)
        {
            temp = grid.columns.get(indexes.column - 1).get(indexes.row - 1);

            infectedTiles.add(
              new ExtendedTile(temp, new IndexValues(indexes.column - 1, indexes.row - 1))
            );
        }

        if (top && right)
        {
            temp = grid.columns.get(indexes.column + 1).get(indexes.row - 1);

            infectedTiles.add(
                    new ExtendedTile(temp, new IndexValues(indexes.column + 1, indexes.row - 1))
            );
        }

        if (down && left)
        {
            temp = grid.columns.get(indexes.column - 1).get(indexes.row + 1);

            infectedTiles.add(
                    new ExtendedTile(temp, new IndexValues(indexes.column - 1, indexes.row + 1))
            );
        }

        if (down && right)
        {
            temp = grid.columns.get(indexes.column + 1).get(indexes.row + 1);

            infectedTiles.add(
                    new ExtendedTile(temp, new IndexValues(indexes.column + 1, indexes.row + 1))
            );
        }

        return infectedTiles;
    }
    public void explode(){
        _boomSound.setVolume(0.5);
        _boomSound.play();
    }

    @Override
    public void draw(GraphicsContext gc)
    {
        if (_state >= 5)
        {
            gc.drawImage(_stateImage[_state], square.downLeft.x - (_boomFactor * _bombSize)/4, square.downLeft.y - (_boomFactor * _bombSize)/4, _boomFactor * _bombSize, _boomFactor * _bombSize);
        }
        else
        {
            gc.drawImage(_stateImage[_state], square.downLeft.x, square.downLeft.y, _bombSize, _bombSize);
        }
    }
}