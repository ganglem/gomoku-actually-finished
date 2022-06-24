package objects;

import com.badlogic.gdx.graphics.Color;
import junit.framework.TestCase;

import java.awt.*;

/**
 * Tests for all {@link Player} methods.
 */
public class PlayerTest extends TestCase
{

    Player player = new Player("Emilija");
    Point  pos    = new Point(5, 5);


    public void testSetColor() {

        player.setColor(Color.BLACK);
        assertEquals(Color.BLACK, player.getColor());
    }


    public void testGetColor() {

        player.setColor(Color.BLACK);
        Color color = player.getColor();
        assertEquals(Color.BLACK, color);
    }


    public void testTestGetName() {

        assertEquals("Emilija", player.getName());
    }


    public void testPlaceStone() {

        GameBoard board = new GameBoard();
        Stone     stone = new Stone(Color.BLACK, pos);
        board.addStone(stone);

        player.setColor(Color.BLACK);
        player.placeStone(pos);

        assertEquals(board.getStone(5, 5), stone);
    }
}