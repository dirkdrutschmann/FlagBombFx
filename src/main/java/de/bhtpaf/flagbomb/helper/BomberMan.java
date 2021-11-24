package de.bhtpaf.flagbomb.helper;

import de.bhtpaf.flagbomb.FlagBomb;
import de.bhtpaf.flagbomb.helper.classes.json.BombermanJson;
import de.bhtpaf.flagbomb.helper.classes.map.items.Bombs;
import de.bhtpaf.flagbomb.helper.classes.map.items.Flag;
import de.bhtpaf.flagbomb.helper.classes.map.items.Item;
import de.bhtpaf.flagbomb.helper.interfaces.eventListener.DirectionChangedListener;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

public class BomberMan extends Item
{
    private final Image _bomberMan;

    private List<DirectionChangedListener> _directionChangedListeners = new ArrayList<>();

    public int width;

    private Dir _direction;

    private final Flag _ownedFlag;

    public Flag capturedFlag = null;

    private Bombs _bombs;

    public final int userId;

    public final String username;

    public BomberMan(int x, int y, int width, Flag ownedFlag, int id, String username)
    {
        super(x, y, width);
        this.width = width;
        _ownedFlag = ownedFlag;
        _bomberMan = new Image(FlagBomb.class.getResourceAsStream("bomb/"+ _ownedFlag.getColor() +"/bomberman.gif"));
        _bombs = new Bombs(width, _ownedFlag.getColor());
        _direction = Dir.STAND;
        this.userId = id;
        this.username = username;
    }

    @Override
    public void draw(GraphicsContext gc)
    {
        gc.drawImage(_bomberMan, square.downLeft.x, square.downLeft.y, width, width);
    }

    public Flag getOwnedFlag()
    {
        return _ownedFlag;
    }

    public Bombs getBombs()
    {
        return _bombs;
    }

    public Dir getDirection()
    {
        return _direction;
    }

    public void setDirection(Dir direction)
    {
        setDirection(direction, true);
    }

    public void setDirection(Dir direction, boolean raiseEvent)
    {
        if (direction != _direction)
        {
            Dir oldDirection = _direction;
            _direction = direction;

            if (raiseEvent)
            {
                for (DirectionChangedListener listener : _directionChangedListeners)
                {
                    listener.onDirectionChanged(this, oldDirection, _direction);
                }
            }

        }
    }

    public void addX(int inc)
    {
        this.square.downLeft.x += inc;
        this.square.downRight.x += inc;
        this.square.upperLeft.x += inc;
        this.square.upperRight.x += inc;
    }

    public void addY(int inc)
    {
        this.square.downLeft.y += inc;
        this.square.downRight.y += inc;
        this.square.upperLeft.y += inc;
        this.square.upperRight.y += inc;
    }

    public void doStep()
    {
        switch (_direction)
        {
            case UP:
                addY(-1);
                break;
            case DOWN:
                addY(1);
                break;
            case LEFT:
                addX(-1);
                break;
            case RIGHT:
                addX(1);
                break;
        }
        if(capturedFlag != null)
        {
            capturedFlag.square = this.square;
        }
    }

    public void respawn()
    {
        this.square = getInitPosition();

        capturedFlag = null;

        Media gameOverMusic = new Media(FlagBomb.class.getResource("sounds/gameover.wav").toString());
        MediaPlayer hitPlayer = new MediaPlayer(gameOverMusic);
        hitPlayer.play();

        Dir oldDirection = _direction;
        setDirection(Dir.STAND, false);

        for (DirectionChangedListener listener : _directionChangedListeners)
        {
            listener.onDirectionChanged(this, oldDirection, _direction);
        }
    }

    public void addDirectionChangedListeners(DirectionChangedListener listener)
    {
        _directionChangedListeners.add(listener);
    }

    public BombermanJson getForJson()
    {
        BombermanJson json = new BombermanJson();
        json.userId = userId;
        json.currentSquare = square;
        json.currentDir = _direction;

        return json;
    }
}