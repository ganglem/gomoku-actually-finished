package objects;

import com.badlogic.gdx.graphics.Color;

import java.awt.*;

/**
 * This is the Stone class.
 * Contains {@link #getPosition()} and {@link #getColor()}.
 */
public class Stone
{

    private final Color color;
    private final Point position;


    public Stone(Color color, Point position) {

        this.color = color;
        this.position = position;
    }


    public Point getPosition() {

        return this.position;
    }


    public Color getColor() {

        return color;
    }

}
