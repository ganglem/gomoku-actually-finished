package io.swapastack.gomoku;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * This is the CustomLabel class.
 * This class was implemented because I didn't know that you could update the text in a {@link Label}.
 * Now it's too late to change it and it works so I kept it.
 *
 * @author Emilija Kastratovic
 */
public class CustomLabel extends com.badlogic.gdx.scenes.scene2d.ui.Label
{

    private String text;


    public CustomLabel(final CharSequence text, final Skin style) {

        super(text, style);
        this.text = text.toString();
    }


    @Override
    public void act(final float delta) {

        this.setText(text);
        super.act(delta);
    }


    /**
     * Updates {@link Label} text.
     * I didn't know that {@link Label#setText(int)} does the same thing.
     * It works so I kept it.
     *
     * @param text {@link String} to be updated
     *
     * @author Emilija Kastratovic
     */
    public void updateText(final String text) {

        this.text = text;
    }
}