package ca.kess.games.entities;

import java.util.LinkedList;
import java.util.Queue;

import ca.kess.games.Constants;
import ca.kess.games.world.WorldLevel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;

/**
 * An ActorEntity is a ActorEntity that is designed to represent a creature within the game.
 */
public class ActorEntity extends PhysicalEntity {
    public ActorEntity() {
        super();
    }
    
    public ActorEntity initialize(
            WorldLevel worldLevel,
            float x, float y,
            float vx, float vy,
            float width, float height,
            float mass,
            float bounciness,
            Animation animation) {
        Gdx.app.log(Constants.LOG, "ActorEntity::initialize");
        initializeBase(worldLevel, x, y, vx, vy, width, height, mass, bounciness, animation);
        return this;
    }
    
    @Override
    public void onTouchWallLeft(Vector2 impactVelocity) {
        Gdx.app.log(Constants.LOG, "ActorEntity::onTouchWallLeft: " + impactVelocity);
    }
    

    @Override
    public void onTouchWallRight(Vector2 impactVelocity) {
        Gdx.app.log(Constants.LOG, "ActorEntity::onTouchWallRight: " + impactVelocity);
    }
    

    @Override
    public void onTouchGround(Vector2 impactVelocity) {
        Gdx.app.log(Constants.LOG, "ActorEntity::onTouchGround: " + impactVelocity);
        if(impactVelocity.y < -50.0) {
            kill();
            getWorld().killEntity(this);
            setVisible(false);
        }
    }
    
    @Override
    public void onTouchRoof(Vector2 impactVelocity) {
        Gdx.app.log(Constants.LOG, "ActorEntity::onTouchRoof: " + impactVelocity);
    }
    
    @Override
    protected void calculateFriction(final Vector2 frictionOut, final Vector2 gravity) {
        //If we're on a wall, and we're falling down, apply an upward
        //force on the entity. This represents the character grabbing
        //the wall on the way down.
        if((isOnWallLeft() || isOnWallRight()) && getVelocityY() < 0) {
            //Take the force as gravity, only against the wall.
            frictionOut.y = -Constants.WALL_FRICTION * gravity.y * getMass();
        }
        super.calculateFriction(frictionOut, gravity);
    }

    
    /**
     * Some boilerplate code for the internal object pool.
     */
    @Override
    public void recycle() {
        Gdx.app.log(Constants.LOG, "ActorEntity::recycle");
        this.setInitialized(false);
    
        ActorEntity.RecycleEntity(this);
    }
    
    // A list of all uninitialized game entities
    private static Queue<ActorEntity> sAvailableActors = new LinkedList<ActorEntity>();

    // Pre-allocate some game entities
    private static void CreateGameEntities(int num) {
        Gdx.app.log(Constants.LOG, "ActorEntity::GetActorEntity. Allocating " + num + " entities");
        for(int i=0; i < num; ++i) {
            sAvailableActors.add(new ActorEntity());
        }
    }
    
    // Get an uninitialized game entity. It is the caller's responsibility to initialize it.
    public static ActorEntity GetActorEntity() {
        Gdx.app.log(Constants.LOG, "ActorEntity::GetActorEntity. There are " + sAvailableActors.size() + " available.");
        if(sAvailableActors.size() == 0) {
            Gdx.app.log(Constants.LOG, "No available game entities, creating new ones");
            CreateGameEntities(100);
        }
        return sAvailableActors.remove();
    }
    
    // Put the entity back in the pool.
    private static void RecycleEntity(ActorEntity entity) {
        Gdx.app.log(Constants.LOG, "ActorEntity::RecycleEntity. There are now " + sAvailableActors.size() + 1 + " entities in the pool.");
        entity.getWorld().removeEntity(entity);
        sAvailableActors.add(entity);
    }
    
    // Dispose the object pool for the game entity
    public static void DisposeObjectPool() {
        Gdx.app.log(Constants.LOG, "ActorEntity::DisposeObjectPool");
        for(ActorEntity entity : sAvailableActors) {
            entity.dispose();
        }
    }
}
