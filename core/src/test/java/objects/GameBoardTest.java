package objects;

import com.badlogic.gdx.graphics.Color;
import junit.framework.TestCase;
import screens.GameScreen;

import java.awt.*;

/**
 * Tests for all {@link GameBoard} methods.
 *
 * {@link GameBoard.checkValid(GameScreen)} cannot be tested as it requires a mouse input.
 */
public class GameBoardTest extends TestCase
{

    GameBoard gameBoard = new GameBoard();

    // should work
    Stone     bStone1   = new Stone(Color.BLACK, new Point(4, 5));
    Stone     bStone2    = new Stone(Color.BLACK, new Point(5, 5));
    Stone     bStone3    = new Stone(Color.BLACK, new Point(6, 5));
    Stone     bStone4    = new Stone(Color.BLACK, new Point(7, 5));
    Stone     bStone5    = new Stone(Color.BLACK, new Point(8, 5));

    // should not work
    Stone     wStone1    = new Stone(Color.WHITE, new Point(1, 1));
    Stone     wStone2    = new Stone(Color.WHITE, new Point(1, 2));
    Stone     wStone3    = new Stone(Color.WHITE, new Point(1, 3));
    Stone     wStone4    = new Stone(Color.WHITE, new Point(1, 4));
    Stone     wStone5    = new Stone(Color.WHITE, new Point(1, 5));
    Stone     wStone6    = new Stone(Color.WHITE, new Point(1, 6));


    public void testAddStone() {

        gameBoard.addStone(bStone2);
        assertEquals(gameBoard.getStone(5, 5), bStone2);
    }


    public void testCheckWinRecursive() {

        gameBoard.addStone(bStone1);
        gameBoard.addStone(bStone2);
        gameBoard.addStone(bStone3);
        gameBoard.addStone(bStone4);
        gameBoard.addStone(bStone5);

        gameBoard.addStone(wStone1);
        gameBoard.addStone(wStone2);
        gameBoard.addStone(wStone3);
        gameBoard.addStone(wStone4);
        gameBoard.addStone(wStone5);
        gameBoard.addStone(wStone6);

        assertTrue(gameBoard.checkWin(bStone1.getPosition().x, bStone1.getPosition().y, bStone1.getColor()));
        assertFalse(gameBoard.checkWin(wStone3.getPosition().x, wStone3.getPosition().y, wStone3.getColor()));
    }


    public void testGetStone() {

        gameBoard.addStone(bStone2);
        assertEquals(bStone2, gameBoard.getStone(5, 5));
    }
}