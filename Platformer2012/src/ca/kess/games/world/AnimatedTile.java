package ca.kess.games.world;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class AnimatedTile extends Tile {
    private Animation mAnimation;
    private float mElapsedTime = 0.0f;
    public AnimatedTile(Animation animation, boolean blocksMovement, float staticFriction, float kineticFriction) {
        super(blocksMovement, staticFriction, kineticFriction);
        mAnimation = animation;
    }
    
    public void setAnimation(Animation animation) {
        mAnimation = animation;
    }
    
    @Override
    public void render(SpriteBatch b, int x, int y) {
        b.draw(mAnimation.getKeyFrame(mElapsedTime, true), x, y, 0, 0, 1, 1, 1, 1, 0);
    }
    
    @Override
    public void update(float delta) {
        mElapsedTime += delta;
    }
}
