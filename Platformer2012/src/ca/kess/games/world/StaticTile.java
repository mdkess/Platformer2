package ca.kess.games.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class StaticTile extends Tile {
    private TextureRegion mTextureRegion;
    public StaticTile(TextureRegion textureRegion, boolean blocksMovement, float staticFriction, float kineticFriction) {
        super(blocksMovement, staticFriction, kineticFriction);
        mTextureRegion = textureRegion;
    }
    public void setTextureRegion(TextureRegion textureRegion) {
        mTextureRegion = textureRegion;
    }
    @Override
    public void render(SpriteBatch b, int x, int y) {
        b.draw(mTextureRegion, x, y, 0, 0, 1, 1, 1, 1, 0);
    }

}
