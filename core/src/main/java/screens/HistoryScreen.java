package screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.swapastack.gomoku.CustomLabel;
import io.swapastack.gomoku.Gomoku;
import io.swapastack.gomoku.Tuple;
import io.swapastack.gomoku.shared.History;

import java.util.ArrayList;

/**
 * This is the HistoryScreen class.
 * This class is used to display the game history of the current server session.
 * Multiple buttons for interaction with the app.
 *
 * @author Emilija Kastratovic
 */
public class HistoryScreen implements Screen
{

    // reference to the parent object
    // the reference is used to call methods of the parent object
    // e.g. parent_.get_window_dimensions()
    // the 'parent' object has nothing to do with inheritance in the accustomed manner
    // it is called 'parent' because the Gomoku class extends com.badlogic.gdx.Game
    // and each Game can have multiple classes which implement the com.badlogic.gdx.Screen
    // interface, so in this special case the Game is the parent of a Screen
    private final Gomoku                parent_;
    // see: https://github.com/libgdx/libgdx/wiki/Orthographic-camera
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/OrthographicCamera.html
    private final OrthographicCamera    camera_;
    // see: https://github.com/libgdx/libgdx/wiki/Viewports
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/utils/viewport/ScreenViewport.html
    private final Viewport              viewport_;
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/scenes/scene2d/Stage.html
    private final Stage                 stage_;
    // see: https://github.com/libgdx/libgdx/wiki/Spritebatch,-Textureregions,-and-Sprites
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/g2d/SpriteBatch.html
    private final SpriteBatch           sprite_batch_;
    // see: https://github.com/libgdx/libgdx/wiki/2D-ParticleEffects
    // see: https://github.com/libgdx/libgdx/wiki/2D-Particle-Editor
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/g2d/ParticleEffect.html
    // see: https://github.com/libgdx/libgdx/wiki/Skin
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/scenes/scene2d/ui/Skin.html
    // see: https://github.com/czyzby/gdx-skins (!!! other skins available here)
    private final Skin                  skin_;
    // see: https://libgdx.info/basic-label/
    private final FreeTypeFontGenerator bitmap_font_generator_;
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/Texture.html
    private final Texture               background_texture_;
    // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/audio/Music.html
    private final Button                back_button_;
    private final ParticleEffect        particle_effect_;


    public HistoryScreen(Gomoku parent) {

        // store reference to parent class
        parent_ = parent;
        // initialize OrthographicCamera with current screen size
        final Tuple<Integer> dimensions = parent_.get_window_dimensions();
        camera_ = new OrthographicCamera((float) dimensions.first, (float) dimensions.second);
        // initialize ScreenViewport with the OrthographicCamera created above
        viewport_ = new ScreenViewport(camera_);
        // initialize SpriteBatch
        sprite_batch_ = new SpriteBatch();
        // initialize the Stage with the ScreenViewport created above
        stage_ = new Stage(viewport_, sprite_batch_);
        // initialize the Skin
        skin_ = new Skin(Gdx.files.internal("quantum-horizon/skin/quantum-horizon-ui.json"));

        particle_effect_ = new ParticleEffect();
        particle_effect_.load(Gdx.files.internal("slowbuzz.p"), Gdx.files.internal(""));
        particle_effect_.start();
        particle_effect_.setPosition(640.f, 460.f);

        // create string for BitmapFont and Label creation
        final String history_string = "Game History";
        bitmap_font_generator_ = new FreeTypeFontGenerator(Gdx.files.internal("fonts/streamster/streamster/Streamster.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter bitmap_font_parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bitmap_font_parameter.size = 50;
        bitmap_font_parameter.characters = history_string;
        bitmap_font_parameter.color = new Color(Color.ORANGE);
        bitmap_font_parameter.borderWidth = 1;
        bitmap_font_parameter.borderColor = Color.CORAL; // alternative enum color specification
        BitmapFont       japanese_latin_font        = bitmap_font_generator_.generateFont(bitmap_font_parameter);
        Label.LabelStyle japanese_latin_label_style = new Label.LabelStyle();
        japanese_latin_label_style.font = japanese_latin_font;
        Label help_label = new Label(history_string, japanese_latin_label_style);
        help_label.setFontScale(1f, 1f);
        help_label.setPosition(
                (float) dimensions.first/2.f-help_label.getWidth()/2.f
                , (float) dimensions.second-help_label.getHeight()-25f
                              );

        // load background texture
        background_texture_ = new Texture("pics/sunzoom.jpg");

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

        // history button
        final Button seeHistory = new TextButton("SEE HISTORY", skin_);
        seeHistory.setSize(200, 60);
        seeHistory.setPosition(dimensions.first/2f-seeHistory.getWidth()/2f, 25f);
        seeHistory.addListener(new InputListener()
        {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }


            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                GameScreen.simpleClient.requestAllHistory();
                while(!GameScreen.simpleClient.isHistoryReceived()) {
                    try {
                        Thread.sleep(2);
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                ArrayList<History> historyMessage = GameScreen.simpleClient.getHistory();
                String             result         = "";
                for(History h : historyMessage) {
                    result += h.playerOneName+": "+(h.playerOneWinner ? "won" : "lost")+"  |  "+h.playerTwoName+": "+(h.playerTwoWinner ? "won" : "lost")+"\n";
                }
                final CustomLabel historyText = new CustomLabel(result, skin_);
                historyText.setFontScale(1);
                historyText.setVisible(true);
                historyText.setPosition(dimensions.first/2f-historyText.getWidth()/2f, dimensions.second/2f-historyText.getHeight()/2f);
                historyText.updateText(result);
                stage_.addActor(historyText);
            }
        });

        stage_.addActor(seeHistory);

        stage_.addActor(back_button_);
        stage_.addActor(help_label);
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

        // clear the client area (Screen) with the clear color (black)
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // update camera
        camera_.update();

        // update the current SpriteBatch
        sprite_batch_.setProjectionMatrix(camera_.combined);

        sprite_batch_.begin();
        sprite_batch_.draw(background_texture_, 0, 0, viewport_.getScreenWidth(), viewport_.getScreenHeight());
        sprite_batch_.end();

        // particles
        sprite_batch_.begin();
        if(particle_effect_.isComplete())
            particle_effect_.reset();
        particle_effect_.draw(sprite_batch_, delta);
        sprite_batch_.end();

        // update the Stage
        stage_.act(delta);
        // draw the Stage
        stage_.draw();
    }


    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     *
     * @author Dennis Jehle
     */
    @Override
    public void show() {
        // this command is necessary that the stage receives input events
        // e.g. mouse click on exit button
        // see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/Input.html
        Gdx.input.setInputProcessor(stage_);
    }


    /**
     * This method gets called after a window resize.
     *
     * @param width  new window width
     * @param height new window height
     *
     * @author Dennis Jehle
     * @see ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {

    }


    /**
     * This method gets called if the application lost focus.
     *
     * @author Dennis Jehle
     * @see ApplicationListener#pause()
     */
    @Override
    public void pause() {

    }


    /**
     * This method gets called if the application regained focus.
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

        //background_music_.stop();
    }


    /**
     * Called when this screen should release all resources.
     *
     * @author Dennis Jehle
     */
    @Override
    public void dispose() {

        //background_music_.dispose();
        background_texture_.dispose();
        bitmap_font_generator_.dispose();
        skin_.dispose();
        stage_.dispose();
        sprite_batch_.dispose();
    }
}
