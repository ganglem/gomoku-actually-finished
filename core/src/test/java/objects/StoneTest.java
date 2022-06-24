package objects;

import com.badlogic.gdx.graphics.Color;
import junit.framework.TestCase;

import java.awt.*;

/**
 * Tests for {@link Stone}.
 */
public class StoneTest extends TestCase
{

    Player player = new Player("Emilija");
    Point  pos    = new Point(1, 5);
    Stone  stone = new Stone(player.getColor(), pos);


    public void testGetPosition() {
        
        assertEquals(pos, stone.getPosition());
    }


    public void testGetColor() {

        player.setColor(Color.BLACK);
        stone = new Stone(player.getColor(), pos);
        assertEquals(Color.BLACK, stone.getColor());
    }
}