package ca.kess.games.entities;

import java.util.LinkedList;
import java.util.Queue;

import ca.kess.games.Constants;
import ca.kess.games.weapons.Weapon;
import ca.kess.games.world.WorldLevel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * An ActorEntity is a ActorEntity that is designed to represent a creature within the game.
 */
public class ActorEntity extends PhysicalEntity {
    private Weapon mEquippedWeapon = null;
    //Whether the entity is damaged on collision with another entity. True for the player
    private boolean mDamagedOnCollision = false;
    public void setDamagedOnCollision(boolean damagedOnCollision) {
    	mDamagedOnCollision = damagedOnCollision;
    }
    
    public ActorEntity() {
        super();
    }
    
    public ActorEntity initialize(
            WorldLevel worldLevel,
            float x, float y,
            float vx, float vy,
            float width, float height,
            float mass,
            Animation animation) {
        Gdx.app.log(Constants.LOG, "ActorEntity::initialize");
        initializeBase(worldLevel, x, y, vx, vy, width, height, mass, 0, animation);
        return this;
    }
    
    @Override
    public void onTouchWallLeft(Vector2 impactVelocity) {
        //Gdx.app.log(Constants.LOG, "ActorEntity::onTouchWallLeft: " + impactVelocity);
    }
    

    @Override
    public void onTouchWallRight(Vector2 impactVelocity) {
        //Gdx.app.log(Constants.LOG, "ActorEntity::onTouchWallRight: " + impactVelocity);
    }
    

    @Override
    public void onTouchGround(Vector2 impactVelocity) {
        //Gdx.app.log(Constants.LOG, "ActorEntity::onTouchGround: " + impactVelocity);
        if(impactVelocity.y < -50.0) {
            kill();
            getWorld().killEntity(this);
            setVisible(false);
        }
    }
    
    @Override
    public void onTouchRoof(Vector2 impactVelocity) {
        //Gdx.app.log(Constants.LOG, "ActorEntity::onTouchRoof: " + impactVelocity);
    }
    
    @Override
    protected void calculateFriction(final Vector2 frictionOut, final Vector2 gravity) {
        //If we're on a wall, and we're falling down, apply an upward
        //force on the entity. This represents the character grabbing
        //the wall on the way down.
        //We don't compute if the player is in a "tube", this is so that
        //the player can slide down easily.
        /* TODO: Removing this for now, until I can make it feel better
        if(getVelocityY() < 0) {
            boolean touchingWallLeft = mWorldLevel.getPenetrationDepthX(mPosition, -0.1f, mAABB) == 0;
            boolean touchingWallRight = mWorldLevel.getPenetrationDepthX(mPosition,  0.1f, mAABB) == 0;
            if(touchingWallLeft != touchingWallRight) {
                //Take the force as gravity, only against the wall.
                frictionOut.y = -Constants.WALL_FRICTION * gravity.y * getMass();
            }
        }*/
        super.calculateFriction(frictionOut, gravity);
    }

    /**
     * Use the equipped weapon to attack.
     */
    public void attack() {
        if(mEquippedWeapon != null) {
            mEquippedWeapon.attack(this);
        }
    }

    /**
     * For the ActorEntity, there are additional flags that the character can
     * enable.
     */
    @Override
    public void update() {
    	if(mInvincibilityCountdown > 0) {
    		mInvincibilityCountdown -= Constants.DELTA;
    	}
        super.update();
        if(mEquippedWeapon != null) {
            mEquippedWeapon.update();
        }
        if(mDamagedOnCollision && !isInvincible()) {
        	 for(PhysicalEntity entity : getWorld().getCollisions(this, true)) {
                 if(entity.damagesPlayerOnCollision()) {
                     mInvincibilityCountdown = Constants.INVINCIBILITY_TIME;
                     //Knock the entity back
                     setVelocity(10 * Math.signum(getPositionX() - entity.getPositionX()), 20);
                 }
             }
        }
    }
    
    // The entity can be invincible for some time after they are damaged.
    float mInvincibilityCountdown = 0.0f;
    public boolean isInvincible() {
    	return mInvincibilityCountdown > 0;
    }
    
    @Override
    public void render(SpriteBatch b) {
    	if(isInvincible()) {
    		float sineWave = (float) ((0.02 * (double) System.currentTimeMillis()) % (2. * Math.PI));
    		System.out.println(0.1 * (double)System.currentTimeMillis());
    		setAlpha((float) (0.5f + 0.5f * Math.sin(sineWave)));
    	} else {
    		setAlpha(1.0f);
    	}
    	super.render(b);
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

    public void equipWeapon(Weapon weapon) {
        Gdx.app.log(Constants.LOG, "Equipping weapon " + weapon);
        mEquippedWeapon = weapon;
    }
}
