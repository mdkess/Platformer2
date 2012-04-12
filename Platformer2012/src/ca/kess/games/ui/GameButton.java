package ca.kess.games.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

public class GameButton extends Button {
    private enum State {
        TOUCH_DOWN,
        TOUCH_UP
    }
    private State mState;
    public GameButton(TextureRegion region) {
        super(region);
        mState = State.TOUCH_UP;
    }
    @Override
    public boolean touchDown(float x, float y, int pointer) {
        System.out.println("Touch down on " + pointer);
        return super.touchDown(x, y, pointer);
    }
    @Override
    public void touchUp(float x, float y, int pointer) {
        System.out.println("Touch up on " + pointer);
        super.touchUp(x, y, pointer);
    }
    

}
