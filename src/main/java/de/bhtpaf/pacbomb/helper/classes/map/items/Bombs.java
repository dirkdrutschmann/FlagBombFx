package de.bhtpaf.pacbomb.helper.classes.map.items;

import de.bhtpaf.pacbomb.helper.classes.map.Coord;
import de.bhtpaf.pacbomb.helper.classes.map.Grid;
import de.bhtpaf.pacbomb.helper.classes.map.Square;
import javafx.scene.media.MediaPlayer;

import java.util.*;

public class Bombs implements Collection<Bomb>
{
    List<Bomb> _bombs = new ArrayList<>();

    public int placeOnGrid(Grid grid, int x, int y)
    {
        Square pos = grid.find(new Coord(x, y));

        if (pos != null)
        {
            _bombs.add(new Bomb(pos));
            return size();
        }

        return -1;
    }

    @Override
    public int size()
    {
        return _bombs.size();
    }

    @Override
    public boolean isEmpty()
    {
        return _bombs.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return _bombs.contains(o);
    }

    @Override
    public Iterator<Bomb> iterator() {
        return _bombs.iterator();
    }

    @Override
    public Object[] toArray() {
        return _bombs.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return _bombs.toArray(a);
    }

    @Override
    public boolean add(Bomb bomb) {
        return _bombs.add(bomb);
    }

    @Override
    public boolean remove(Object o) {
        return _bombs.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Bomb> c) {
        return addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return retainAll(c);
    }

    @Override
    public void clear() {
        _bombs.clear();
    }
}
