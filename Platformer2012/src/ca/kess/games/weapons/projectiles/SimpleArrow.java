package ca.kess.games.weapons.projectiles;

import java.util.LinkedList;
import java.util.Queue;

import ca.kess.games.Constants;
import ca.kess.games.entities.ActorEntity;
import ca.kess.games.entities.PhysicalEntity;
import ca.kess.games.world.WorldLevel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;

/**
 * This arrow just travels in a straight line until it hits something
 */
public class SimpleArrow extends Projectile {
    private ActorEntity mOwner;
    
    public SimpleArrow initialize(
            ActorEntity owner,
            WorldLevel worldLevel,
            float x, float y,
            float vx, float vy,
            float width, float height,
            float mass,
            Animation animation) {
        initializeBase(worldLevel, x, y, vx, vy, width, height, mass, 0, animation);
        setAffectedByGravity(false);
        setApplyDrag(false);
        mOwner = owner;
        return this;
    }
    
    @Override
    public void handleCollision(PhysicalEntity entity) {
        if(entity != mOwner) {
            entity.damage(1);

            setVelocity(0, 0);
            kill();
            recycle();
        }
    }
    
    @Override
    public void onTouchWallLeft(Vector2 impactVelocity) {
        setVelocity(0, 0);
        kill();
        recycle();
    }
    
    @Override
    public void onTouchWallRight(Vector2 impactVelocity) {
        setVelocity(0, 0);
        kill();
        recycle();
    }
    
    /**
     * Some boilerplate code for the internal object pool.
     */
    @Override
    public void recycle() {
        Gdx.app.log(Constants.LOG, "SimpleArrow::recycle");
        this.setInitialized(false);
    
        SimpleArrow.RecycleEntity(this);
    }
    
    // A list of all uninitialized game entities
    private static Queue<SimpleArrow> sAvailableActors = new LinkedList<SimpleArrow>();

    // Pre-allocate some game entities
    private static void CreateGameEntities(int num) {
        Gdx.app.log(Constants.LOG, "SimpleArrow::GetActorEntity. Allocating " + num + " entities");
        for(int i=0; i < num; ++i) {
            sAvailableActors.add(new SimpleArrow());
        }
    }
    
    // Get an uninitialized game entity. It is the caller's responsibility to initialize it.
    public static SimpleArrow GetActorEntity() {
        Gdx.app.log(Constants.LOG, "SimpleArrow::GetActorEntity. There are " + sAvailableActors.size() + " available.");
        if(sAvailableActors.size() == 0) {
            Gdx.app.log(Constants.LOG, "No available game entities, creating new ones");
            CreateGameEntities(100);
        }
        return sAvailableActors.remove();
    }
    
    // Put the entity back in the pool.
    private static void RecycleEntity(SimpleArrow entity) {
        Gdx.app.log(Constants.LOG, "SimpleArrow::RecycleEntity. There are now " + sAvailableActors.size() + 1 + " entities in the pool.");
        entity.getWorld().removeEntity(entity);
        sAvailableActors.add(entity);
    }
    
    // Dispose the object pool for the game entity
    public static void DisposeObjectPool() {
        Gdx.app.log(Constants.LOG, "SimpleArrow::DisposeObjectPool");
        for(SimpleArrow entity : sAvailableActors) {
            entity.dispose();
        }
    }
}
