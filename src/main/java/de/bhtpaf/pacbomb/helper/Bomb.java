package de.bhtpaf.pacbomb.helper;

import de.bhtpaf.pacbomb.helper.classes.map.Coord;
import de.bhtpaf.pacbomb.helper.classes.map.Square;

// Stellt eine Bombe dar
public class Bomb
{
    // Bombenstatus
    private int _state = 0;

    // Quadrat der Bombe
    public Square square;

    public Bomb(Square square) {
        this.square = square;
    }

    public void setState(int state)
    {
        _state = state;
    }

    public int getState()
    {
        return _state;
    }

    public Coord getCoord()
    {
        return square.downLeft;
    }
}