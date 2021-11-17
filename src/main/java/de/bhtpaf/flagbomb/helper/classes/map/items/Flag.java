package de.bhtpaf.flagbomb.helper.classes.map.items;

import de.bhtpaf.flagbomb.FlagBomb;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Flag extends Item
{
    public enum Color
    {
        BLUE
        {
            @Override
            public String toString() {
                return "blue";
            }
        },
        RED
        {
            @Override
            public String toString() {
                return "red";
            }
        },
        GREEN
        {
            @Override
            public String toString() {
                return "green";
            }
        },
        YELLOW
        {
            @Override
            public String toString() {
                return "yellow";
            }
        }


    };

    private final Image _blueFlagImage = new Image(FlagBomb.class.getResourceAsStream("flag/flag_blue.png"));
    private final Image _redFlagImage = new Image(FlagBomb.class.getResourceAsStream("flag/flag_red.png"));
    private final Image _greenFlagImage = new Image(FlagBomb.class.getResourceAsStream("flag/flag_green.png"));
    private final Image _yellowFlagImage = new Image(FlagBomb.class.getResourceAsStream("flag/flag_yellow.png"));

    private final Color _color;
    private int _flagSize;

    public Flag(int y, int x, int flagSize, Color color)
    {
        super(x, y, flagSize);
        this._flagSize =  flagSize - 2;
        _color = color;
    }

    @Override
    public void draw(GraphicsContext gc)
    {
        Image flagImage = null;

        switch (_color)
        {
            case BLUE:
            {
                flagImage = _blueFlagImage;
                break;
            }
            case RED:
            {
                flagImage = _redFlagImage;
                break;
            }
            case GREEN:
            {
                flagImage = _greenFlagImage;
                break;
            }
            case YELLOW:
            {
                flagImage = _yellowFlagImage;
                break;
            }
        }

        gc.drawImage(flagImage, square.downLeft.x, square.downLeft.y, _flagSize, _flagSize);
    }
    public String getColor(){
        return this._color.toString();
    }
}
