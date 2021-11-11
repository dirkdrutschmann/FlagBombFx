package de.bhtpaf.pacbomb.helper.classes.map.items;

import de.bhtpaf.pacbomb.PacBomb;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Flag extends Item
{
    public enum Color
    {
        blue{
            @Override
            public String toString() {
                return "blue";
            }
        },
        red{
            @Override
            public String toString() {
                return "red";
            }
        },
        green{
            @Override
            public String toString() {
                return "green";
            }
        },
        yellow{
            @Override
            public String toString() {
                return "yellow";
            }
        }


    };

    private final Image _blueFlagImage = new Image(PacBomb.class.getResourceAsStream("flag/flag_blue.png"));
    private final Image _redFlagImage = new Image(PacBomb.class.getResourceAsStream("flag/flag_red.png"));
    private final Image _greenFlagImage = new Image(PacBomb.class.getResourceAsStream("flag/flag_green.png"));
    private final Image _yellowFlagImage = new Image(PacBomb.class.getResourceAsStream("flag/flag_yellow.png"));

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
            case blue:
            {
                flagImage = _blueFlagImage;
                break;
            }
            case red:
            {
                flagImage = _redFlagImage;
                break;
            }
            case green:
            {
                flagImage = _greenFlagImage;
                break;
            }
            case yellow:
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
