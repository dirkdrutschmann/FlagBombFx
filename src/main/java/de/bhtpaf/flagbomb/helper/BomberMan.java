package de.bhtpaf.flagbomb.helper;

import de.bhtpaf.flagbomb.FlagBomb;
import de.bhtpaf.flagbomb.helper.classes.map.Square;
import de.bhtpaf.flagbomb.helper.classes.map.items.Bombs;
import de.bhtpaf.flagbomb.helper.classes.map.items.Flag;
import de.bhtpaf.flagbomb.helper.classes.map.items.Item;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.skin.TextInputControlSkin;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class BomberMan extends Item
{
    private final Image _bomberMan;

    public int _width;

    public Dir direction = Dir.right;

    private final Flag _ownedFlag;

    public Flag capturedFlag = null;

    private Bombs _bombs;

    public final int userId;

    public BomberMan(int x, int y, int width, Flag ownedFlag, int id)
    {
        super(x, y, width);
        _width = width;
        _ownedFlag = ownedFlag;
        _bomberMan = new Image(FlagBomb.class.getResourceAsStream("bomb/"+ _ownedFlag.getColor() +"/bomberman.gif"));
        _bombs = new Bombs(width, _ownedFlag.getColor());
        this.userId = id;
    }

    @Override
    public void draw(GraphicsContext gc)
    {
        gc.drawImage(_bomberMan, square.downLeft.x, square.downLeft.y, _width, _width);
    }

    public Flag getOwnedFlag()
    {
        return _ownedFlag;
    }

    public Bombs getBombs()
    {
        return _bombs;
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
        switch (direction)
        {
            case up:
                addY(-1);
                break;
            case down:
                addY(1);
                break;
            case left:
                addX(-1);
                break;
            case right:
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
    }


}