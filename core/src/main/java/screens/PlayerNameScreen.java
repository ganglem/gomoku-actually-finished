package screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.swapastack.gomoku.Gomoku;
import io.swapastack.gomoku.SimpleClient;
import io.swapastack.gomoku.Tuple;
import objects.Player;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * This is the PlayerNameScreen class.
 * It is used to give the {@link Player}s their name.
 *
 * @author Emilija Kastratovic
 */
public class PlayerNameScreen implements Screen
{

    private final Gomoku             parent_;
    // OrthographicCamera
    private final OrthographicCamera camera_;
    // Viewport
    private final Viewport           viewport_;
    // Stage
    private final Stage              stage_;
    // SpriteBatch
    private final SpriteBatch        sprite_batch_;
    // ShapeRenderer
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/glutils/ShapeRenderer.html
    private final ShapeRenderer      shape_renderer_;
    // Skin
    private final Skin               skin_;
    public static TextField          playerOneNameTextField;
    public static TextField          playerTwoNameTextField;
    private final SpriteBatch        batch;
    // Texture
    private final Texture            background_texture_;
    private final Button                back_button_;


    /**
     * PlayerNameScreen Constructor
     *
     * @param parent {@link Gomoku}
     *
     * @author Emilija Kastratovic
     */
    public PlayerNameScreen(Gomoku parent) {


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
        // initialize background texture
        background_texture_ = new Texture("pics/city.jpg");

        // easier dimensions
        Tuple<Integer> dimensions = parent_.get_window_dimensions();

        // init batch
        batch = new SpriteBatch();

        // create switch to MainMenu button
        Button playButton = new TextButton("PLAY", skin_);
        playButton.setSize(200, 60);
        playButton.setPosition(dimensions.first/2f-(playButton.getWidth()/2f), dimensions.second/2f-(playButton.getHeight()/2f));

        // create player 1 text field
        playerOneNameTextField = new TextField("Player 1", skin_);
        playerOneNameTextField.setPosition(dimensions.first/2f-(playButton.getWidth()/2f)-250, dimensions.second/2f-(playButton.getHeight()/2f));
        playerOneNameTextField.setSize(200, 60);

        // create player 2 text field
        playerTwoNameTextField = new TextField("Player 2", skin_);
        playerTwoNameTextField.setPosition(dimensions.first/2f-(playButton.getWidth()/2f)+250, dimensions.second/2f-(playButton.getHeight()/2f));
        playerTwoNameTextField.setSize(200, 60);


        // add InputListener to Button, and close app if Button is clicked
        playButton.addListener(new InputListener()
        {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }


            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                try {
                    GameScreen.simpleClient = new SimpleClient(new URI(String.format("ws://%s:%d", MainMenuScreen.host, MainMenuScreen.port)));
                    GameScreen.simpleClient.connectBlocking();
                } catch(URISyntaxException | InterruptedException e) {
                    e.printStackTrace();
                }
                parent_.change_screen(ScreenEnum.GAME);
            }
        });


        back_button_ = new TextButton("BACK", skin_);
        back_button_.setSize(200, 60);
        back_button_.setPosition(25f, 25f);
        back_button_.addListener(new InputListener()
        {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }


            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                parent_.setScreen(BackScreen.back);
            }
        });

        stage_.addActor(back_button_);
        stage_.addActor(playerOneNameTextField);
        stage_.addActor(playerTwoNameTextField);
        stage_.addActor(playButton);
    }


    @Override
    public void show() {

        Gdx.input.setInputProcessor(stage_);
    }


    @Override
    public void render(float delta) {


        // clear the client area (Screen) with the clear color (black)
        Gdx.gl.glClearColor(100/255f, 100/255f, 100/255f, 100/255f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // update camera
        camera_.update();

        // update the current SpriteBatch
        sprite_batch_.setProjectionMatrix(camera_.combined);

        // draw background
        batch.begin();
        batch.draw(background_texture_, 0, 0, viewport_.getScreenWidth(), viewport_.getScreenHeight());
        batch.end();

        // update the Stage
        stage_.act(delta);
        // draw the Stage
        stage_.draw();
    }


    @Override
    public void resize(int width, int height) {

    }


    @Override
    public void pause() {

    }


    @Override
    public void resume() {

    }


    @Override
    public void hide() {

    }


    @Override
    public void dispose() {

        skin_.dispose();
        stage_.dispose();
        sprite_batch_.dispose();
    }
}
