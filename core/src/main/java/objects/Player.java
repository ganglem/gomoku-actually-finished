package objects;

import com.badlogic.gdx.graphics.Color;
import screens.GameScreen;

import java.awt.*;

/**
 * This is the Player class.
 * It contains getters and setters {@link #getColor()}
 * {@link #setColor(Color)} and
 * {@link #getName()},
 * and {@link #placeStone(GameScreen, Point)} to place a {@link Stone} in the {@link GameBoard}.
 */
public class Player
{

    private final String name;
    private       Color  color;


    /**
     * Constructor for {@link Player}.
     *
     * @param name {@link Player} name
     *
     * @author Emilija Kastratovic
     */
    public Player(String name) {

        this.name = name;
    }


    public Color getColor() {

        return color;
    }


    public void setColor(Color color) {

        this.color = color;
    }


    public String getName() {

        return name;
    }


    /**
     * Creates a new {@link Stone} with current position and {@link Player} color.
     * Used to add Stone to {@link GameBoard}.
     *
     * @param position   {@link Stone} position with {@link GameScreen#boardPos()}
     *
     * @return new {@link Stone}
     *
     * @author Emilija Kastratovic
     */
    public Stone placeStone(Point position) {

        return new Stone(this.color, position);
    }

}
