package ca.kess.games.ui;

import ca.kess.games.interfaces.IRenderable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Button extends UIElement implements IRenderable{
    public enum State {
        DOWN,
        UP
    }
    private State mState;
    private TextureRegion mTextureRegion;
    private Rectangle mBoundingBox;
    private String mName;
    
    public Button(String name, TextureRegion texture, int x, int y, int width, int height) {
        mTextureRegion = texture;
        mBoundingBox = new Rectangle(x, y, width, height);
        mName = name;
    }
    
    private void onTouchEvent(float x, float y) {
        if(mBoundingBox.contains(x, y)) {
            if(mState == State.UP){
                mState = State.DOWN;
                onPress();
            }
        }
    }
    private void onPress() {
        System.out.println("On press");
    }
    
    private void onRelease() {
        
    }

    @Override
    public void render(SpriteBatch b) {
        b.draw(mTextureRegion, mBoundingBox.x, mBoundingBox.y, 0, 0, mBoundingBox.width, mBoundingBox.height, 1, 1, 0);
    }
}
