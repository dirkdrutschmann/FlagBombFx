package de.bhtpaf.flagbomb.helper.classes.map.items;

import de.bhtpaf.flagbomb.FlagBomb;
import de.bhtpaf.flagbomb.helper.classes.map.Square;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

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
        };


        public javafx.scene.paint.Color toJavaFXColor()
        {
            javafx.scene.paint.Color color;

            if (this == BLUE)
            {
                color = javafx.scene.paint.Color.BLUE;
            }
            else if (this == RED)
            {
                color = javafx.scene.paint.Color.RED;
            }
            else if (this == GREEN)
            {
                color = javafx.scene.paint.Color.GREEN;
            }
            else
            {
                color = javafx.scene.paint.Color.YELLOW;
            }

            return color;
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

    public Flag(int y, int x, int flagSize, Color color, String id)
    {
        super(x, y, flagSize);
        this._flagSize =  flagSize - 2;
        _color = color;
        this.itemId = id;
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

    public String getColor()
    {
        return this._color.toString();
    }

    public javafx.scene.paint.Color getJavaFXColor()
    {
        return this._color.toJavaFXColor();
    }

    public void respawn()
    {
        this.square = getInitPosition();
    }
}
