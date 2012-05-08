package ca.kess.games.entities;

import java.util.LinkedList;
import java.util.Queue;

import ca.kess.games.Constants;
import ca.kess.games.timers.DeathFadeTimer;
import ca.kess.games.world.WorldLevel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;

public class ChestEntity extends PhysicalEntity {
    public enum ChestState {
        LOCKED,
        OPEN,
        CLOSED
    }
    
    Animation mOpenAnimation;
    Animation mClosedAnimation;
    ChestState mState;

    PhysicalEntity mContents;
    private ChestEntity() {
        
    }

    public ChestEntity initialize(WorldLevel worldLevel,
            float x, float y,
            float vx, float vy,
            float width, float height,
            float mass, float bounciness,
            Animation openAnimation, Animation closedAnimation, 
            PhysicalEntity contents) { 
        initializeBase(worldLevel,
                x, y,
                vx, vy,
                width, height,
                mass, bounciness,
                closedAnimation);
        // TODO Auto-generated constructor stub
        mOpenAnimation = openAnimation;
        mClosedAnimation = closedAnimation;
        mState = ChestState.CLOSED;
        mContents = contents;
        
        return this;
    }

    @Override
    public boolean onInteraction(PhysicalEntity other) {
        if(mState == ChestState.CLOSED) {
            mState = ChestState.OPEN;
            setVelocityY(15);
            mContents.setVelocityY(30);
            mContents.setPosition(getPositionX(), getPositionY());
            getWorld().addEntity(mContents);
            setAnimation(mOpenAnimation);
            getWorld().addTimer(new DeathFadeTimer(this, 1.0f));
            return true;
        }
        return false;
    }
    
    @Override
    public boolean canBeDamaged() {
        return false;
    }
    
    @Override
    public void recycle() {
        Gdx.app.log(Constants.LOG, "GameEntity::recycle");
        setInitialized(false);

        ChestEntity.RecycleEntity(this);
    }
    
    
    /**
     * Some boilerplate code for the internal object pool.
     */
    // A list of all uninitialized game entities
    private static Queue<ChestEntity> sAvailableEntities = new LinkedList<ChestEntity>();
    
    // Pre-allocate some game entities
    private static void CreateGameEntities(int num) {
        Gdx.app.log(Constants.LOG, "ChestEntity::GetGameEntity. Allocating " + num + " entities");
        for(int i=0; i < num; ++i) {
            sAvailableEntities.add(new ChestEntity());
        }
    }
    
    // Get an uninitialized game entity. It is the caller's responsibility to initialize it.
    public static ChestEntity GetChestEntity() {
        Gdx.app.log(Constants.LOG, "ChestEntity::GetChestEntity. There are " + sAvailableEntities.size() + " available.");
        if(sAvailableEntities.size() == 0) {
            Gdx.app.log(Constants.LOG, "No available game entities, creating new ones");
            CreateGameEntities(100);
        }
        return sAvailableEntities.remove();
    }
    
    // Put the entity back in the pool.
    private static void RecycleEntity(ChestEntity entity) {
        Gdx.app.log(Constants.LOG, "ChestEntity::RecycleEntity");
        entity.getWorld().removeEntity(entity);
        sAvailableEntities.add(entity);
    }
    
    // Dispose the object pool for the game entity
    public static void DisposeObjectPool() {
        Gdx.app.log(Constants.LOG, "ChestEntity::DisposeObjectPool");
        for(ChestEntity entity : sAvailableEntities) {
            entity.dispose();
        }
    }

}
