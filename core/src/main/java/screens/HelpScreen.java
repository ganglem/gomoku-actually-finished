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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.swapastack.gomoku.Gomoku;
import io.swapastack.gomoku.Tuple;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This is the HelpScreen class.
 * This class is used to display the help and rules.
 * Multiple buttons for interaction with the app.
 *
 * @author Emilija Kastratovic
 */
public class HelpScreen implements Screen
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
    private final Label                 rulez;
    private final ParticleEffect        particle_effect_;
    private       BufferedReader        buf;


    public HelpScreen(Gomoku parent) {

        // store reference to parent class
        parent_ = parent;
        // initialize OrthographicCamera with current screen size
        Tuple<Integer> dimensions = parent_.get_window_dimensions();
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
        String help_string = "Da Rules";
        bitmap_font_generator_ = new FreeTypeFontGenerator(Gdx.files.internal("fonts/streamster/streamster/Streamster.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter bitmap_font_parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bitmap_font_parameter.size = 50;
        bitmap_font_parameter.characters = help_string;
        bitmap_font_parameter.color = new Color(Color.ORANGE);
        bitmap_font_parameter.borderWidth = 1;
        bitmap_font_parameter.borderColor = Color.CORAL; // alternative enum color specification
        BitmapFont       japanese_latin_font        = bitmap_font_generator_.generateFont(bitmap_font_parameter);
        Label.LabelStyle japanese_latin_label_style = new Label.LabelStyle();
        japanese_latin_label_style.font = japanese_latin_font;
        Label help_label = new Label(help_string, japanese_latin_label_style);
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

        StringBuilder rules = new StringBuilder();

        try {
            buf = new BufferedReader(new FileReader("core/assets/rulez/rulez.txt"));
            String str;
            while((str = buf.readLine()) != null) {
                rules.append(str).append("\n");
            }
            buf.close();
        } catch(IOException e) {
            System.err.println();
        }

        // append text to a label
        rulez = new Label(rules, skin_);
        rulez.setFontScale(1);
        rulez.setPosition(dimensions.first/2f, dimensions.second/2f, Align.center);
        rulez.setVisible(true);

        stage_.addActor(rulez);
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
