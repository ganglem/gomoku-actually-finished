package screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.swapastack.gomoku.CustomLabel;
import io.swapastack.gomoku.Gomoku;
import io.swapastack.gomoku.SimpleClient;
import io.swapastack.gomoku.Tuple;
import io.swapastack.gomoku.shared.History;
import objects.GameBoard;
import objects.Player;
import objects.Stone;
import objects.Swap2Enum;

import java.awt.*;

/**
 * This is the GameScreen class.
 * This class can be used to implement your game logic.
 *
 * The current implementation provides a colorful grid and a leave game button.
 *
 * A good place to gather further information is here:
 * https://github.com/libgdx/libgdx/wiki/Input-handling
 * Input and Event handling is necessary to handle mouse and keyboard input.
 *
 * @author Dennis Jehle
 */
@SuppressWarnings("DuplicatedCode")
public class GameScreen implements Screen
{

    // grid dimensions
    public static final  int                grid_size_       = 15;
    private static final float              padding          = 100.f;
    private static final float              line_width       = 2.f;
    public static        SimpleClient       simpleClient;
    // server
    public static        boolean            historySaveError = false;
    private final        float              uiBorderLeft     = 350;
    private final        float              uiBorderRight    = 920;
    private final        float              uiBorderTop      = 72;
    private final        float              uiBorderBottom   = 645;
    // Screen pos
    private final        float              screen_width     = Gdx.graphics.getWidth();
    private final        float              screen_height    = Gdx.graphics.getHeight();
    private final        float              column_height    = screen_height-2.f*padding;
    private final        float              offset           = column_height/((float) grid_size_-1.f);
    private final        float              top_left_x       = screen_width/2.f-column_height/2.f;
    // reference to the parent object
    private final        Gomoku             parent_;
    // OrthographicCamera
    private final        OrthographicCamera camera_;
    // Viewport
    private final        Viewport           viewport_;
    // Stage
    private final        Stage              stage_;
    // SpriteBatch
    private final        SpriteBatch        sprite_batch_;
    // ShapeRenderer
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/glutils/ShapeRenderer.html
    private final        ShapeRenderer      shape_renderer_;
    // Skin
    private final        Skin               skin_;
    // UI elements
    private final        TextButton         black;
    private final        TextButton         white;
    private final        TextButton         both;
    private final        Texture            background_texture_;
    private final        ParticleEffect     particle_effect_;
    private final        Label              tie;
    private final        Label              errorLabel;
    // Players and Gameboard
    private final        Player[]           players          = new Player[2];
    private final        GameBoard          gameBoard;
    private final        CustomLabel        winTxt;
    private final        CustomLabel        playerTurn;
    private              boolean            insideBoard;
    // logic
    private              int                hotSeat          = 0;
    private              boolean            isIie            = false;
    private              Player             winner;
    private              Stone              lastPlaced       = new Stone(Color.BLACK, new Point(0, 0));
    private              Swap2Enum          swap2stage       = Swap2Enum.FIRST_PLAYER_TURN;
    private              int                counter          = 0;
    private              boolean            swap2            = true;


    /**
     * Constructor for GameScreen class.
     * This is where all the {@link Label}s and {@link Button}s are initialized.
     *
     * @param parent Gomoku
     */
    public GameScreen(Gomoku parent) {

        // store reference to parent class
        parent_ = parent;
        // initialize OrthographicCamera with current screen size
        Tuple<Integer> client_area_dimensions = parent_.get_window_dimensions();
        camera_ = new OrthographicCamera((float) client_area_dimensions.first, (float) client_area_dimensions.second);
        // initialize ScreenViewport with the OrthographicCamera created above
        viewport_ = new ScreenViewport(camera_);
        // initialize SpriteBatch
        sprite_batch_ = new SpriteBatch();
        // initialize the Stage with the ScreenViewport created above
        stage_ = new Stage(viewport_, sprite_batch_);
        // initialize ShapeRenderer
        shape_renderer_ = new ShapeRenderer();
        // initialize the Skin
        skin_ = new Skin(Gdx.files.internal("quantum-horizon/skin/quantum-horizon-ui.json"));
        // init background
        background_texture_ = new Texture("pics/board.jpg");
        // initialize and configure ParticleEffect
        particle_effect_ = new ParticleEffect();
        particle_effect_.load(Gdx.files.internal("slowbuzz.p"), Gdx.files.internal(""));
        particle_effect_.start();
        particle_effect_.setPosition(640.f, 460.f);

        // create switch to MainMenu button
        Button menu_screen_button = new TextButton("LEAVE GAME", skin_);
        menu_screen_button.setPosition(25.f, 25.f);
        menu_screen_button.addListener(new InputListener()
        {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }


            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                simpleClient.disconnect();
                MainMenuScreen.background_music_.stop();
                parent_.change_screen(ScreenEnum.MENU);
            }
        });

        // resets game
        Button reset_game_button = new TextButton("RESET GAME", skin_);
        reset_game_button.setPosition(25.f, 100.f);
        reset_game_button.addListener(new InputListener()
        {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer,
                                     int button) {

                return true;
            }


            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer,
                                int button) {

                // counter needs to be reset to initial value
                GameBoard.counter = grid_size_*grid_size_;
                parent_.change_screen(ScreenEnum.GAME);
            }
        });

        // create color buttons
        black = new TextButton("Black", skin_);
        black.setPosition(25.f, 270.f);
        white = new TextButton("White", skin_);
        white.setPosition(25.f, 350.f);
        both = new TextButton("Both", skin_);
        both.setPosition(25.f, 430.f);
        black.setVisible(false);
        white.setVisible(false);
        both.setVisible(false);
        white.addListener(new InputListener()
        {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }


            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }
        });
        black.addListener(new InputListener()
        {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer,
                                     int button) {

                return true;
            }


            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer,
                                int button) {

            }
        });
        both.addListener(new InputListener()
        {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer,
                                     int button) {

                return true;
            }


            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer,
                                int button) {

            }
        });

        // init gameboard and players
        gameBoard = new GameBoard();
        players[0] = new Player(PlayerNameScreen.playerOneNameTextField.getText());
        players[1] = new Player(PlayerNameScreen.playerTwoNameTextField.getText());
        players[0].setColor(Color.BLACK);
        players[1].setColor(Color.WHITE);

        // tie label
        tie = new Label("TIE!", skin_);
        tie.setFontScale(1);
        tie.setColor(Color.BLUE);
        tie.setPosition(screen_width/2f-tie.getWidth()/2f, screen_height-45);
        tie.setVisible(false);

        // player turn label
        playerTurn = new CustomLabel("Player X's turn", skin_);
        playerTurn.setFontScale(1);
        playerTurn.setPosition(screen_width/2f-playerTurn.getWidth()/2f, screen_height-45);
        playerTurn.setVisible(true);

        // winner label
        winTxt = new CustomLabel("", skin_);
        winTxt.setFontScale(1);
        winTxt.setPosition(screen_width/2f-playerTurn.getWidth()/2f, screen_height-45);
        winTxt.setVisible(false);

        // rules button
        Texture     daRulez       = new Texture("rulez/rulez.png");
        ImageButton daRulezButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(daRulez)));
        daRulezButton.setSize(200, 200);
        daRulezButton.setPosition(client_area_dimensions.first-225f, 25f);
        daRulezButton.addListener(new InputListener()
        {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }


            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                setBack();
                parent_.change_screen(ScreenEnum.HELP);
            }
        });

        // game history button
        Button historyButton = new TextButton("HISTORY", skin_);
        historyButton.setSize(200, 60);
        historyButton.setPosition(client_area_dimensions.first-225f, client_area_dimensions.second-85f);
        historyButton.setVisible(true);
        historyButton.addListener(new InputListener()
        {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }


            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                setBack();
                parent_.change_screen(ScreenEnum.HISTORY);
            }
        });

        // error Label
        errorLabel = new Label("HistoryNotSaved:\nan unexpected\nerror occurred", skin_);
        errorLabel.setPosition(client_area_dimensions.first-225f, client_area_dimensions.second-145f);
        errorLabel.setVisible(false);

        historySaveError = false;

        // add everything to stage
        stage_.addActor(errorLabel);
        stage_.addActor(historyButton);
        stage_.addActor(daRulezButton);
        stage_.addActor(winTxt);
        stage_.addActor(playerTurn);
        stage_.addActor(menu_screen_button);
        stage_.addActor(reset_game_button);
        stage_.addActor(tie);
        stage_.addActor(black);
        stage_.addActor(white);
        stage_.addActor(both);
    }


    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     *
     * @author Dennis Jehle
     */
    @Override
    public void render(float delta) {

        insideBoard = Gdx.input.getX()>=uiBorderLeft && Gdx.input.getX()<=uiBorderRight &&
                      Gdx.input.getY()>=uiBorderTop && Gdx.input.getY()<=uiBorderBottom;

        // clear the client area (Screen) with the clear color (black)
        Gdx.gl.glClearColor(100/255f, 100/255f, 100/255f, 100/255f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // update camera
        camera_.update();
        // update the current SpriteBatch
        sprite_batch_.setProjectionMatrix(camera_.combined);
        sprite_batch_.begin();
        sprite_batch_.draw(background_texture_, 0, 0, viewport_.getScreenWidth(), viewport_.getScreenHeight());
        sprite_batch_.end();

        drawGrid();
        if(winner == null ^ isIie) {
            showPlayerTurn();
            logic();
        }
        drawStone();

        // particles
        sprite_batch_.begin();
        if(particle_effect_.isComplete())
            particle_effect_.reset();
        particle_effect_.draw(sprite_batch_, delta);
        sprite_batch_.end();

        showHistorySaveError();

        stage_.act(delta);
        stage_.draw();
    }


    /**
     * Determines closest grid cross. Used for {@link Stone} placement.
     *
     * @return Integer Point that has an x and y value
     */
    public Point boardPos() {

        int    mouseX          = Gdx.input.getX();
        int    mouseY          = Gdx.input.getY();
        double minimalDistance = Math.sqrt((mouseX-top_left_x)*(mouseX-top_left_x)+(mouseY-padding)*(mouseY-padding));
        int    minX            = 0;
        int    minY            = 0;
        for(int i = 0; i<grid_size_; i++) {
            for(int k = 0; k<grid_size_; k++) {
                double xDistance = mouseX-(top_left_x+i*offset);
                double yDistance = mouseY-(padding+k*offset);
                double distance  = Math.sqrt(xDistance*xDistance+yDistance*yDistance);
                if(distance<minimalDistance) {
                    minimalDistance = distance;
                    minX = i;
                    minY = k;
                }
            }
        }
        Point pos = new Point();
        pos.x = minX;
        pos.y = minY;
        return pos;
    }


    /**
     * Draws gameboard grid.
     * Taken from {@link #render(float)} function.
     */
    private void drawGrid() {

        shape_renderer_.begin(ShapeType.Filled);
        for(int i = 0; i<grid_size_; i++) {
            float fraction = (float) (i+1)/(float) grid_size_;
            shape_renderer_.rectLine(
                    top_left_x+i*offset, padding+column_height
                    , top_left_x+i*offset, padding
                    , line_width
                    , mix(fraction),
                    mix(fraction));
            shape_renderer_.rectLine(
                    top_left_x, padding+column_height-i*offset
                    , top_left_x+column_height, padding+column_height-i*offset
                    , line_width
                    , mix(fraction)
                    , mix(fraction));
        }
        shape_renderer_.end();
    }


    /**
     * Draws the {@link Stone} on the gameboard.
     * Draws a green or red hover stone if position is valid or invalid respectively.
     * Draws black and white stone when positioned on valid place.4
     *
     * @author Emilija Kastratovic
     */
    private void drawStone() {

        shape_renderer_.begin(ShapeType.Filled);
        for(int x = 0; x<grid_size_; x++) {
            for(int y = 0; y<grid_size_; y++) {
                Stone currentStone = gameBoard.getStone(x, grid_size_-y-1);
                if(currentStone != null) {
                    shape_renderer_.setColor(currentStone.getColor());
                    shape_renderer_.circle(top_left_x+x*offset, padding+y*offset, 16);
                }
            }
        }
        Point hover = boardPos();
        if(winner == null ^ isIie) {
            if(insideBoard) {
                if(gameBoard.checkValid(this)) {
                    shape_renderer_.setColor(Color.GREEN);
                } else {
                    shape_renderer_.setColor(Color.RED);
                }
                shape_renderer_.circle(top_left_x+hover.x*offset, padding+(grid_size_-hover.y-1)*offset, 16);
            }
        }
        shape_renderer_.end();
    }


    /**
     * Game logic that includes {@link #swap2()} and the {@link #normal()} game afterwards.
     * Checks if there is a {@link #winner} or a {@link #tie}
     *
     * @author Emilija Kastratovic
     */
    private void logic() {

        // add stone
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if(swap2) {
                if(gameBoard.checkValid(this)) {
                    swap2();
                    if(insideBoard) {
                        counter++;
                    }
                }
            } else {
                if(insideBoard) {
                    normal();
                }
            }
        }
        // check winner
        if(winner != null) showWinner();

        // check tie
        if(GameBoard.counter == 0) {
            sprite_batch_.begin();
            isIie = true;
            playerTurn.setVisible(false);
            tie.setVisible(true);
            sprite_batch_.end();
        }
    }


    /**
     * Whole game logic after {@link #swap2()} is over.
     * Checks whether {@link Player} is allowed to place a {@link Stone} at wished position.
     * Player turn is swapped after valid stone placement.
     * Checks if there is a winner after valid stone placement.
     *
     * @author Emilija Kastratovic
     */
    private void normal() {

        if(gameBoard.checkValid(this)) {
            setLastPlaced(hotSeat);
            if(GameBoard.counter != 0 && winner == null) {
                GameBoard.counter--;
            }
            if(winner == null) {
                if(lastPlaced != null) {
                    if(gameBoard.checkWin(lastPlaced.getPosition().x, lastPlaced.getPosition().y, players[hotSeat].getColor())) {
                        winner = players[hotSeat];
                        sendWinner();
                    }
                }
            }
            if(hotSeat == 0) hotSeat = 1;
            else hotSeat = 0;
        }
    }


    /**
     * Opening rule for Gomoku.
     *
     * The first {@link Player} starts by placing 2 black and 1 white {@link Stone} ({@link Swap2Enum#FIRST_PLAYER_TURN}).
     * The second player then selects one of three options ({@link Swap2Enum#SECOND_PLAYER_TURN}):
     * play as black ({@link Swap2Enum#CHOICE_BLACK}),
     * play as white and place another white stone ({@link Swap2Enum#CHOICE_WHITE}),
     * or place two more stones, one white and one black, and let the first player choose the color ({@link Swap2Enum#PLAYER_PASS}).
     *
     * @author Emilija Kastratovic
     */
    private void swap2() {

        switch(swap2stage) {

            case FIRST_PLAYER_TURN:

                black.setVisible(false);
                white.setVisible(false);
                if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && insideBoard && gameBoard.checkValid(this)) {
                    switch(counter) {
                        case 0:
                            players[0].setColor(Color.BLACK);
                            setLastPlaced(0);
                            break;
                        case 1:
                            setLastPlaced(0);
                            players[0].setColor(Color.WHITE);
                            break;
                        case 2:
                            setLastPlaced(0);
                            black.setVisible(true);
                            white.setVisible(true);
                            both.setVisible(true);
                            hotSeat = 1;
                            swap2stage = Swap2Enum.SECOND_PLAYER_TURN;
                            break;
                    }
                }
                break;

            case SECOND_PLAYER_TURN:

                black.setVisible(true);
                white.setVisible(true);
                both.setVisible(true);

                if(black.isPressed()) {
                    players[0].setColor(Color.WHITE);
                    players[1].setColor(Color.BLACK);
                    black.setVisible(false);
                    white.setVisible(false);
                    both.setVisible(false);
                    hotSeat = 0;
                    swap2 = false;
                    break;
                }
                if(white.isPressed()) {
                    players[1].setColor(Color.WHITE);
                    players[0].setColor(Color.BLACK);
                    black.setVisible(false);
                    white.setVisible(false);
                    both.setVisible(false);
                    swap2stage = Swap2Enum.CHOICE_WHITE;
                    break;
                }
                if(both.isPressed()) {
                    swap2stage = Swap2Enum.PLAYER_PASS;
                    both.setVisible(false);
                    black.setVisible(false);
                    white.setVisible(false);
                    counter = 0;
                    break;
                }
                break;

            case CHOICE_WHITE:

                if(insideBoard) {
                    setLastPlaced(1);
                    hotSeat = 0;
                    swap2 = false;
                    break;
                }

            case PLAYER_PASS:

                switch(counter) {

                    case 0:
                        if(insideBoard) {
                            players[1].setColor(Color.BLACK);
                            setLastPlaced(1);
                            break;
                        }

                    case 1:
                        players[1].setColor(Color.WHITE);
                        if(insideBoard) {
                            setLastPlaced(1);
                            hotSeat = 0;
                            black.setVisible(true);
                            white.setVisible(true);
                            break;
                        }

                    case 2:
                        if(black.isPressed()) {
                            players[0].setColor(Color.BLACK);
                            black.setVisible(false);
                            white.setVisible(false);
                            players[1].setColor(Color.WHITE);
                            hotSeat = 0;
                            swap2 = false;
                            break;
                        }
                        if(white.isPressed()) {
                            players[0].setColor(Color.WHITE);
                            black.setVisible(false);
                            white.setVisible(false);
                            players[1].setColor(Color.BLACK);
                            hotSeat = 0;
                            swap2 = false;
                            break;
                        }
                }
                break;
        }
    }


    /**
     * Sets {@link #lastPlaced} {@link Stone} and adds it to current {@link GameBoard}.
     *
     * @param index current Player
     *
     * @author Emilija Kastratovic
     */
    private void setLastPlaced(int index) {

        if(insideBoard) {
            lastPlaced = players[index].placeStone(boardPos());
            gameBoard.addStone(lastPlaced);
        }
    }


    /**
     * Shows current player turn and color.
     *
     * @author Emilija Kastratovic
     */
    private void showPlayerTurn() {

        sprite_batch_.begin();
        playerTurn.setColor(players[hotSeat].getColor());
        playerTurn.updateText(players[hotSeat].getName()+"'s turn");
        sprite_batch_.end();
    }


    /**
     * Called when there is a winner.
     *
     * @author Emilija Kastratovic
     */
    private void showWinner() {

        sprite_batch_.begin();
        playerTurn.setVisible(false);
        winTxt.setColor(winner.getColor());
        winTxt.updateText(winner.getName()+" won!");
        winTxt.setVisible(true);
        sprite_batch_.end();
    }


    /**
     * Method created just so it can be called from a {@link Button}.
     *
     * @author Emilija Kastratovic
     */
    private void setBack() {

        BackScreen.back = this;
    }


    /**
     * sends winner to {@link SimpleClient}
     *
     * @author Emilija Kastratovic
     */
    public void sendWinner() {

        boolean playerOneWinner = winner == players[0];
        boolean playerTwoWinner = winner == players[1];

        History history = new History(players[0].getName(), players[1].getName(),
                                      playerOneWinner, playerTwoWinner);

        simpleClient.sendWinner(history);
    }


    /**
     * shows error message when {@link History} is not saved.
     *
     * @author Emilija Kastratovic
     */
    private void showHistorySaveError() {

        if(historySaveError) {
            errorLabel.setVisible(true);
        }
    }


    /**
     * Interpolate between RGB(A) values.
     * Inspired by: https://stackoverflow.com/a/21010385/5380008
     *
     * @param fraction percentage 0.f-1.f
     *
     * @return {@link Color} mixed color
     *
     * @author Dennis Jehle
     */
    private Color mix(float fraction) {
        // calculated the mixed RGB values
        float r = (Color.CORAL.r-Color.MAGENTA.r)*fraction+Color.MAGENTA.r;
        float g = (Color.CORAL.g-Color.MAGENTA.g)*fraction+Color.MAGENTA.g;
        float b = (Color.CORAL.b-Color.MAGENTA.b)*fraction+Color.MAGENTA.b;
        // return the mixed color
        return new Color(r, g, b, 1.f);
    }


    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     *
     * @author Dennis Jehle
     */
    @Override
    public void show() {
        // InputProcessor for Stage
        Gdx.input.setInputProcessor(stage_);
    }


    /**
     * This method is called if the window gets resized.
     *
     * @param width  new window width
     * @param height new window height
     *
     * @author Dennis Jehle
     * @see ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {
        // could be ignored because you cannot resize the window at the moment
    }


    /**
     * This method is called if the application lost focus.
     *
     * @author Dennis Jehle
     * @see ApplicationListener#pause()
     */
    @Override
    public void pause() {

    }


    /**
     * This method is called if the application regained focus.
     *
     * @author Dennis Jehle
     * @see ApplicationListener#resume()
     */
    @Override
    public void resume() {

    }


    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     *
     * @author Dennis Jehle
     */
    @Override
    public void hide() {

    }


    /**
     * Called when this screen should release all resources.
     *
     * @author Dennis Jehle
     */
    @Override
    public void dispose() {

        skin_.dispose();
        stage_.dispose();
        sprite_batch_.dispose();
    }
}
