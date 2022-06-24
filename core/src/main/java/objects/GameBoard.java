package objects;

import com.badlogic.gdx.graphics.Color;
import screens.GameScreen;

/**
 * This is the GameBoard class that is used for Gomoku.
 *
 * Contains {@link #addStone(Stone)} to add a {@link Stone} to the board,
 * {@link #checkValid(GameScreen)} to check a valid stone placement,
 * {@link #checkWin(int, int, Color)} and {@link #checkWin(int, int, DirectionEnum, Color)}
 * to determine the winning condition.
 *
 * @author Emilija Kastratovic
 */
public class GameBoard
{

    public static int       counter = GameScreen.grid_size_*GameScreen.grid_size_;
    private final Stone[][] gameBoard;
    private       int       horizontal;
    private       int       vertical;
    private       int       crossUp;
    private       int       crossDwn;


    /**
     * Constructor for GameBoard. Initialized a twodimensional {@link Stone} array.
     *
     * @author Emilija Kastratovic
     */
    public GameBoard() {

        gameBoard = new Stone[GameScreen.grid_size_][GameScreen.grid_size_];
    }


    /**
     * Adds stone to Gameboard {@link Stone} array
     *
     * @param stone {@link Stone} that is added
     *
     * @author Emilija Kastratovic
     */
    public void addStone(Stone stone) {

        gameBoard[stone.getPosition().x][stone.getPosition().y] = stone;
    }


    /**
     * Checks whether the {@link Stone} array at the current mouse position is null.
     * Uses {@link GameScreen#boardPos()} for mouse position.
     *
     * @param gameScreen current {@link GameScreen}
     *
     * @return true if the position in the {@link Stone} array is null
     *
     * @author Emilija Kastratovic
     */
    public boolean checkValid(GameScreen gameScreen) {

        return gameBoard[gameScreen.boardPos().x][gameScreen.boardPos().y] == null;
    }


    /**
     * Used to check if the winning condition (5 {@link Stone} in a row) is fullfilled.
     * Calls recursive function {@link #checkWin(int, int, DirectionEnum, Color)} eight times.
     * {@link #horizontal}, {@link #vertical}, {@link #crossUp}, {@link #crossDwn} used to track streak in all possible directions.
     *
     * @param xPos  current {@link Stone} x Position
     * @param yPos  current {@link Stone} y Position
     * @param color current {@link Stone} color
     *
     * @return true if there is any winning arrangement of Stones
     *
     * @author Emilija Kastratovic
     */
    public boolean checkWin(int xPos, int yPos, Color color) {

        // 6 because when a stone is set, the methods for one direction are called twice.
        horizontal = 6;
        vertical = 6;
        crossUp = 6;
        crossDwn = 6;
        return (checkWin(xPos, yPos, DirectionEnum.UP, color) ||
                checkWin(xPos, yPos, DirectionEnum.DOWN, color) ||
                checkWin(xPos, yPos, DirectionEnum.LEFT, color) ||
                checkWin(xPos, yPos, DirectionEnum.RIGHT, color) ||
                checkWin(xPos, yPos, DirectionEnum.UP_RIGHT, color) ||
                checkWin(xPos, yPos, DirectionEnum.DOWN_LEFT, color) ||
                checkWin(xPos, yPos, DirectionEnum.UP_LEFT, color) ||
                checkWin(xPos, yPos, DirectionEnum.DOWN_RIGHT, color));

    }


    /**
     * Recursive function used to find a winning streak in any direction.
     * Called by {@link #checkWin(int, int, Color)} eight times.
     * Any of {@link #horizontal}, {@link #vertical}, {@link #crossUp}, {@link #crossDwn}
     * are decreased for each call if the next {@link Stone} is the same color.
     *
     * @param xPos  current {@link Stone} x Position
     * @param yPos  current {@link Stone} y Position
     * @param dir   {@link DirectionEnum} next direction
     * @param color current {@link Stone} color
     *
     * @return true if any {@link #horizontal}, {@link #vertical}, {@link #crossUp}, {@link #crossDwn} is exactly 0
     *
     * @throws IndexOutOfBoundsException if outside of gameboard
     * @author Emilija Kastratovic
     */
    public boolean checkWin(int xPos, int yPos, DirectionEnum dir, Color color) {

        try {
            if(gameBoard[xPos][yPos] != null) {
                Color currColor = gameBoard[xPos][yPos].getColor();
                if(currColor == color) {
                    switch(dir) {
                        case UP:
                            vertical--;
                            return checkWin(xPos, yPos+1, DirectionEnum.UP, currColor);

                        case DOWN:
                            vertical--;
                            return checkWin(xPos, yPos-1, DirectionEnum.DOWN, currColor);

                        case RIGHT:
                            horizontal--;
                            return checkWin(xPos+1, yPos, DirectionEnum.RIGHT, currColor);

                        case LEFT:
                            horizontal--;
                            return checkWin(xPos-1, yPos, DirectionEnum.LEFT, currColor);

                        case UP_RIGHT:
                            crossUp--;
                            return checkWin(xPos+1, yPos+1, DirectionEnum.UP_RIGHT, currColor);

                        case DOWN_LEFT:
                            crossUp--;
                            return checkWin(xPos-1, yPos-1, DirectionEnum.DOWN_LEFT, currColor);

                        case DOWN_RIGHT:
                            crossDwn--;
                            return checkWin(xPos+1, yPos-1, DirectionEnum.DOWN_RIGHT, currColor);

                        case UP_LEFT:
                            crossDwn--;
                            return checkWin(xPos-1, yPos+1, DirectionEnum.UP_LEFT, currColor);

                    }
                }
            } else
                return (horizontal == 0 || vertical == 0 || crossUp == 0 || crossDwn == 0);

        } catch(IndexOutOfBoundsException e) {
            return false;
        }

        return false;
    }


    public Stone getStone(int x, int y) {

        return gameBoard[x][y];
    }

}
