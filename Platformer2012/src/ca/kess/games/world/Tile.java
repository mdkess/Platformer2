package ca.kess.games.world;

import ca.kess.games.Constants;
import ca.kess.games.entities.PhysicalEntity;
import ca.kess.games.interfaces.IUpdateable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Tile implements IUpdateable {
    private boolean mBlocksMovement;
    private float mStaticFriction;
    private float mKineticFriction;
    Tile(boolean blocksMovement, float staticFriction, float kineticFriction) {
        mBlocksMovement = blocksMovement;
        mStaticFriction = staticFriction;
        mKineticFriction = kineticFriction;
    }
    
    public abstract void render(SpriteBatch b, int x, int y);
    
    public boolean blocksMovement() { return mBlocksMovement; }
    public void setBlocksMovement(boolean blocksMovement) {
        mBlocksMovement = blocksMovement;
    }
    
    public boolean onInteraction(PhysicalEntity entity) {
        Gdx.app.log(Constants.LOG, "Tile::onInteraction");
        return false;
    }
    
    @Override
    public void update() { }
}
