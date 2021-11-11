package de.bhtpaf.pacbomb.helper.classes.map.items;

import de.bhtpaf.pacbomb.PacBomb;
import de.bhtpaf.pacbomb.helper.BomberMan;
import de.bhtpaf.pacbomb.helper.classes.map.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.*;

public class Bombs implements Collection<Bomb>
{

    private HashMap<String, Image[]> _bombImages = init();

    private final Media _soundBoom = new Media(PacBomb.class.getResource("boom.mp3").toString());
    private final int _bombSize;
    private final int _boomFactor = 2;

    private List<Bomb> _bombs = new ArrayList<>();

    public Bombs (int bombSize)
    {
        _bombSize = bombSize;
    }

    public void updateBombs(GraphicsContext gc, Grid grid)
    {
        for (int i = 0; i < _bombs.size(); i++)
        {
            Bomb bomb = _bombs.get(i);
            int bombState = bomb.getState();

            // Bombe entfernen
            if (bombState == 6)
            {
                _bombs.remove(i);
                return;
            }
            else
            {
                bomb.draw(gc);
            }


            // Bombe explodiert
            if (bombState == 5) {
                MediaPlayer boomPlayer = new MediaPlayer(_soundBoom);
                boomPlayer.play();

                List<ExtendedTile> infected = bomb.getInfectedTiles(grid);

                for (ExtendedTile field : infected)
                {
                    if (field.tile() instanceof Wall && ((Wall)field.tile()).isDestroyable)
                    {
                        Tile freeTile = new Tile(field.tile().downLeft, field.tile().width, Type.FREE);
                        freeTile.draw(gc);

                        grid.columns.get(field.index().column).set(field.index().row, freeTile);
                    }
                }
            }
        }
    }

    public int placeOnGrid(Grid grid, int x, int y, BomberMan player)
    {
        Square pos = grid.find(new Coord(x, y));

        if (pos != null)
        {
            System.out.println(player.getOwnedFlag().toString());
            _bombs.add(new Bomb(pos, _bombImages.get(player.getOwnedFlag().getColor())));
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

    private HashMap<String, Image[]> init (){
        HashMap<String, Image[]> bombImages = new HashMap<String, Image[]>();
        for(Flag.Color color : Flag.Color.values()){
            bombImages.put(color.toString(),  new Image[]
                    {
                            new Image(PacBomb.class.getResourceAsStream("bomb/"+color.toString()+"/bomb1.png")),
                            new Image(PacBomb.class.getResourceAsStream("bomb/"+color.toString()+"/bomb2.png")),
                            new Image(PacBomb.class.getResourceAsStream("bomb/"+color.toString()+"/bomb3.png")),
                            new Image(PacBomb.class.getResourceAsStream("bomb/"+color.toString()+"/bomb4.png")),
                            new Image(PacBomb.class.getResourceAsStream("bomb/"+color.toString()+"/bomb5.png")),
                            new Image(PacBomb.class.getResourceAsStream("bomb/"+color.toString()+"/bomb6.png"))
                    });
        }
        return bombImages;
    }

}
