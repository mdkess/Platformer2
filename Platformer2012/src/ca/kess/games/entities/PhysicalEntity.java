package ca.kess.games.entities;

import java.util.LinkedList;
import java.util.Queue;

import ca.kess.games.Constants;
import ca.kess.games.input.InputHandler;
import ca.kess.games.interfaces.IRenderable;
import ca.kess.games.interfaces.IUpdateable;
import ca.kess.games.world.Tile;
import ca.kess.games.world.WorldLevel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

/**
 * A game entity is something within the game which can be updated, and interacts physically with the world.
 * Game entities are controlled by an InputHandler, which is responsible for applying forces and updating the
 * physical properties of the entity to cause it to move in interesting ways.
 * 
 * Do not construct entities directly, rather use the memory pool to create new objects. This is to avoid using
 * the garbage collector more than is necessary.
 * 
 * IMPORTANT: This class gets used a lot, and so it should never allocate or deallocate memory! Be really careful
 * about maintaining references.
 */
public class PhysicalEntity extends GameEntity implements IRenderable, IUpdateable, Disposable {
    /**
     * ENUMERATIONS AND HELPER DATA STRUCTURES
     */
    
    /**
     * This is the direction that the entity is facing. Used to set the facing direction of the sprite, as well
     * as to determine which direction the entity shoots, for example.
     */
    public enum Direction {
        LEFT(-1),
        RIGHT(1);
        
        private final int scaleX; //How much to scale the entity by in the X direction when rendering.
        
        Direction(int scaleX) {
            this.scaleX = scaleX;
        }
    }

    /**
     * PHYSICAL PROPERTIES/PHYSICAL STATE
     * 
     * These represent where the entity is within the world.
     */
    //The current position of the object. This is valid after update() has been called.
    private Vector2 mPosition;
    public float getPositionX() { return mPosition.x; }
    public float getPositionY() { return mPosition.y; }
    //WARNING: Be careful that you don't set to an invalid position.
    // Also - this doesn't update previous position, not sure if that is correct.
    public void setPosition(float x, float y) { mPosition.set(x, y); }

    //The position of the object, after the previous iteration of update() was called.
    private Vector2 mPreviousPosition;
    //The velocity of the entity, in world space (1 = 1 tile/second).
    private Vector2 mVelocity;
    public void setVelocityX(float x) { mVelocity.x = x; }
    public void setVelocityY(float y) { mVelocity.y = y; }
    public void setVelocity(float x, float y) { mVelocity.set(x, y); }
    public float getVelocityX() { return mVelocity.x; }
    public float getVelocityY() { return mVelocity.y; }
    
    //The absolute value of the maximum speed in either direction.
    private Vector2 mMaximumSpeed;
    
    // The width and height of the bounding box which contains the entity.
    private Vector2 mAABB;
    public void setWidth(float width) { mAABB.x = width; }
    public float getWidth() { return mAABB.x; }
    public void setHeight(float height) { mAABB.y = height; }
    public float getHeight() { return mAABB.y; }
    public void setSize(float width, float height) { mAABB.set(width, height); }
    
    // What portion of the entity's velocity should be restored when the object bounces off
    // of the ground. Should be between 0 and 1, probably. 0 - the object stops. 1 - the object
    // is perfectly bouncy, and should bounce to its original height. NOTE: There's no guarantee
    // that the object will bounce indefinitely - due to floating point misses, it may lose some
    // velocity (although I tried my best to make this not happen).
    private float mBounciness = 0;
    public void setBounciness(float bounciness) { mBounciness = bounciness; }
    public float getBounciness() { return mBounciness; }
    
    // How heavy the object is. In physical computing acceleration, the formula a = F / m is used.
    // As a point of reference, a human sized 1x1 tile character should have mass 1 (although this
    // is just a reference point, not a rule).
    private float mMass;
    public float getMass() { return mMass; }
    public void setMass(float mass) { mMass = mass; }

    // This is the sum of all of the applied forces on the entity (friction, gravity, movement, etc.).
    // This vector is used ONLY for storage during update, and it is cleared between updates. It is
    // therefore the InputHandler's job to apply forces every frame if it wants to maintain acceleration.
    private Vector2 mAppliedForces;
    // Apply a force to the object, for a single frame. See comment above for more info
    public void applyForce(float forceX, float forceY) { mAppliedForces.add(forceX, forceY); }
    public void resetForces() { mAppliedForces.set(0,0); }

    // Whether we should check for collisions with the world for this object. If this is set to false, the
    // object will be able to move through walls.
    private boolean mCheckCollisions = true;
    public boolean checkCollisions() { return mCheckCollisions; }
    public void setCheckCollisions(boolean checkCollisions) { mCheckCollisions = checkCollisions; }
    
    // Whether the object is affected by gravity. For ease of computation, set this flag instead of setting
    // the entity's mass to zero.
    private boolean mAffectedByGravity = true;
    public boolean isAffectedByGravity() { return mAffectedByGravity; }
    public void setAffectedByGravity(boolean affectedByGravity) { mAffectedByGravity = affectedByGravity; }
    
    // Whether drag should be applied when the entity is airborne.
    // Drag is a force in the opposite direction of the velocity, proportional to velocity squared.
    private boolean mApplyDrag = true;
    public void setApplyDrag(boolean applyDrag) { mApplyDrag = applyDrag; }
    
    /**
     * INTERACTIVE PROPERTIES/TRIGGER STATE
     * 
     * These cache physical properties of the entity - such as determining whether
     * they are touching parts of the world. 
     */
    //Whether the entity is touching the ground on its bottom side.
    private boolean mIsOnGround = false;
    public boolean isOnGround() { return mIsOnGround; }
    
    //Whether the entity is touching a wall on its left side.
    private boolean mIsOnWallLeft = false;
    public boolean isOnWallLeft() { return mIsOnWallLeft; }
    
    //Whether the entity is touching a wall on its right side.
    private boolean mIsOnWallRight = false;
    public boolean isOnWallRight() { return mIsOnWallRight; }
    
    //Whether the entity is touching a wall on its top side.
    private boolean mIsOnRoof = false;
    public boolean isOnRoof() { return mIsOnRoof; }

    // Trigger called when an entity touches a blocking tile on its left side
    // when they were not touching it previously.
    public void onTouchWallLeft(Vector2 impactVelocity) {
        if(mInputHandler != null)
            System.out.println("onTouchWallLeft: " + impactVelocity);
    }
    
    // Trigger called when an entity touches a blocking tile on its right side
    // when they were not touching it previously.
    public void onTouchWallRight(Vector2 impactVelocity) {
        if(mInputHandler != null)
            System.out.println("onTouchWallRight: " + impactVelocity);
    }
    
    // Trigger called when an entity touches a blocking tile on its bottom side
    // when they were not touching it previously.
    public void onTouchGround(Vector2 impactVelocity) {
        if(mInputHandler != null) {
            System.out.println("onTouchGround: " + impactVelocity);
            if(impactVelocity.y < -50.0) {
                kill();
                getWorld().killEntity(this);
                setVisible(false);
            }
        }
    }
    
    // Trigger called when an entity touches a blocking tile on its top side
    // when they were not touching it previously.
    public void onTouchRoof(Vector2 impactVelocity) {
        if(mInputHandler != null)
            System.out.println("onTouchRoof: " + impactVelocity);
    }

    // Whether the entity can be interacted with by other entities. Note that this is a one way rule - an
    // entity that can't be interacted with can still interact.
    boolean mCanBeInteractedWith = true;
    public void setCanBeInteractedWith(boolean canBeInteractedWith) {
        mCanBeInteractedWith = canBeInteractedWith;
    }
    public boolean canBeInteractedWith() {
        return mCanBeInteractedWith;
    }

    // Which direction the entity is facing. This is for animation and interaction.
    private Direction mDirection = Direction.RIGHT;
    public void setDirection(Direction direction) { mDirection = direction; }
    public Direction getDirection() { return mDirection; }
    
    // Whether the entity is alive. An entity that isn't alive is still part of the scene
    // graph, but isn't updated.
    private boolean mAlive;
    public void kill() {
        Gdx.app.log(Constants.LOG, "PhysicalEntity::kill");
        if(mAlive) {
            mAlive = false;
            mWorldLevel.killEntity(this);
        }
    }
    public void resurrect() {
        Gdx.app.log(Constants.LOG, "PhysicalEntity::resurrect");
        mAlive = true;
    }
    public boolean isAlive() { return mAlive; }
    /**
     * ANIMATION AND GRAPHICAL STATE
     * 
     * These don't affect the physics of the object directly.
     */

    // Whether the entity is visible (should be rendered)
    private boolean mVisible = true;
    public boolean isVisible() { return mVisible; }
    public void setVisible(boolean visible) { mVisible = visible; }


    /**
     * LINKED/REFERENCED OBJECTS
     * 
     * These are the objects that the PhysicalEntity needs to access to do its good work.
     */
    private WorldLevel mWorldLevel;
    public WorldLevel getWorld() { return mWorldLevel; }

    // The entity's animation. Updated in update.
    private Animation mAnimation;
    private float mAnimationTime = 0.0f;
    public Animation getAnimation() { return mAnimation; }
    public void setAnimation(Animation animation) { mAnimation = animation; }
    
    // Alpha for the entity. Probably should be used as a color mask, but for now can
    // be used to make the entity less visible.
    private float mAlpha;
    public void setAlpha(float alpha) { mAlpha = alpha; }

    // Whether the entity has been initialized.
    private boolean mInitialized;
    public boolean isInitialized() { return mInitialized; }
    public void setInitialized(boolean initialized) { mInitialized = initialized; }
    

    // Private constructor. Should be constructed through static game entity pool methods.
    // Allocate any memory that you need to allocate here!
    protected PhysicalEntity() {
        Gdx.app.log(Constants.LOG, "PhysicalEntity::PhysicalEntity");
        mPosition = new Vector2();
        mPreviousPosition = new Vector2();
        mVelocity = new Vector2();
        mAnimation = null;
        mAppliedForces = new Vector2();
        
        mAABB = new Vector2(1, 1);
        mWorldLevel = null;
        mBounciness = 0;
        mAlpha = 1.0f;
        mMass = 1.0f;
        
        mMaximumSpeed = new Vector2(10, 10);
        
        mInitialized = false;
        
        mAlive = false;
    }

    
    //Initialize the entity as if it was new.
    public PhysicalEntity initialize(
            WorldLevel worldLevel,
            float x, float y,
            float vx, float vy,
            float width, float height,
            float mass,
            float bounciness,
            Animation animation) {
        assert !mInitialized : "Trying to initialize an already initialized entity!";
        mPosition.set(x, y);
        mPreviousPosition.set(x, y);
        mVelocity.set(vx, vy);
        mAABB.set(width, height);
        
        mAppliedForces.set(0, 0);
        mWorldLevel = worldLevel;
        mBounciness = bounciness;
        
        mAnimation = animation;
        mAlpha = 1.0f;
        mMass = mass;
        mAlive = true;
        return this;
    }

    //Recycle the entity. This should dispose any objects that need to be disposed
    public void recycle() {
        Gdx.app.log(Constants.LOG, "PhysicalEntity::recycle");
        mInitialized = false;

        PhysicalEntity.RecycleEntity(this);
    }
    
    
    //Open a door, if it's in front of the entity
    //TODO: This should really be an "Interact with tile" sort of function.
    public void openDoor() {
        Gdx.app.log(Constants.LOG, "PhysicalEntity::openDoor");

        float x = mPosition.x;
        if(mDirection == Direction.LEFT) {
            x -= 0.1;
        } else {
            x += mAABB.x + 0.1;
        }
        float y = mAABB.y * 0.5f + mPosition.y;

        Tile tile = mWorldLevel.getTile((int)Math.floor(x), (int)Math.floor(y));
        if(tile != null) {
        	tile.onInteraction(this);
        }
    }
    


    
    

    
    @Override
    public void render(SpriteBatch b) {
        if(this.isVisible()) {
            b.setColor(1, 1, 1, mAlpha);
            b.draw(mAnimation.getKeyFrame(mAnimationTime, true), mPosition.x, mPosition.y, getWidth()/2, getHeight()/2, getWidth(), getHeight(), mDirection.scaleX, 1, 0);
        }
    }
    
    public void onInteraction(PhysicalEntity other) {
        if(this.canBeInteractedWith()){
            mWorldLevel.killEntity(this);
            mWorldLevel.removeEntity(this);
        }
    }

    public InputHandler getInputHandler() {
        return mInputHandler;
    }

    public boolean collidesWith(PhysicalEntity other) {
        return collidesWith(other.mPosition, other.mAABB);
    }
    
    // Return whether this entity overlaps the position and location
    protected boolean collidesWith(Vector2 pos, Vector2 aabb) {
        float ax1 = mPosition.x;
        float ax2 = mPosition.x + getWidth();
        float ay1 = mPosition.y;
        float ay2 = mPosition.y + getHeight();
        
        float bx1 = pos.x;
        float bx2 = pos.x + aabb.x;
        float by1 = pos.y;
        float by2 = pos.y + aabb.y;
        
        //Check for a separating axis:
        
        //x-axis intersects
        return(ax1 < bx2 && ax2 >= bx1 && ay1 < by2 && ay2 >= by1);
    }


    /**
     * This is the main update function, it is called once per frame.
     * Delta is expected to be constant, this invariant should be enforced once the
     * update loop is more completely fleshed out.
     * 
     * At a high level, this function checks the sum of forces F on the entity.
     * It then computes acceleration with a = F / m, and updates the entity's
     * velocity accordingly. Then it tries to move the entity as far on the X
     * axis as it needs to, resolving collisions. Then it tries to move the entity
     * as far ion the Y axis as it can.
     * 
     * This also updates the trigger state of the entity.
     * 
     * Most state variables within the class are in flux within the update() function,
     * so be careful bout what you call!
     */
    Vector2 update_Acceleration = new Vector2();  //LOCAL VARIABLE: Only to be used in update()!
    Vector2 update_Gravity = new Vector2();       //LOCAL VARIABLE: Only to be used in update()!
    @Override
    public final void update() {
        mAnimationTime += Constants.DELTA;
        /*
        while(mAnimationTime > mAnimation.animationDuration) {
            mAnimationTime -= mAnimation.animationDuration;
        }*/
        resetForces();
        
        if(mInputHandler != null) { mInputHandler.update(); }
        
        if(mAffectedByGravity) {
            Vector2 gravity = mWorldLevel.getGravity();
            update_Gravity.set(gravity);
        } else {
            update_Gravity.set(0,0);
        }
        applyForce(update_Gravity.x * mMass, update_Gravity.y * mMass);
        
        //Now apply friction
        float forceFriction = 0;
        if(mIsOnGround) {
            float u_k = mWorldLevel.getFriction(mPosition, mAABB);
            forceFriction = -u_k * update_Gravity.y * mMass;
            if(forceFriction < 0) {
                Gdx.app.error(Constants.LOG, "Error - friction " + forceFriction + " should not be negative");
                forceFriction = 0;
            }
        } else if(mApplyDrag) {
            //Now here, force of friction becomes drag, which is proportional to the square of the velocity.
            forceFriction = Constants.DRAG * mVelocity.x * mVelocity.x;
        }
        
        //F = m * a => a = F / m
        update_Acceleration.set(mAppliedForces.x / mMass, mAppliedForces.y / mMass);
        
        mVelocity.x += update_Acceleration.x * Constants.DELTA;
        mVelocity.y += update_Acceleration.y * Constants.DELTA;
        
        //Now we have the velocity without friction. Apply friction.
        float frictionAcceleration = forceFriction * mMass;
        float frictionDeltaVelocity = frictionAcceleration * Constants.DELTA;
        if(mVelocity.x > 0) {
            mVelocity.x -= frictionDeltaVelocity;
            if(mVelocity.x < 0) {
                mVelocity.x = 0;
            }
        } else {
            mVelocity.x += frictionDeltaVelocity;
            if(mVelocity.x > 0) {
                mVelocity.x = 0;
            }
        }
        
        //Now try to move
        float deltaX;
        float deltaY;
        
        mPreviousPosition.x = mPosition.x;
        mPreviousPosition.y = mPosition.y;
        
        float deltaXprev = mVelocity.x * Constants.DELTA;
        float deltaYprev = mVelocity.y * Constants.DELTA;

        if(!mCheckCollisions) {
            mPosition.x += deltaXprev;
            mPosition.y += deltaYprev;
        } else {
            deltaX = mWorldLevel.getPenetrationDepthX(mPosition, deltaXprev, mAABB);
            mPosition.x += deltaX;
            
            //mIsOnWallLeft = mIsOnWallRight = false;
            
            if(deltaX != deltaXprev) {
                if(deltaXprev < 0) {
                    if(!mIsOnWallLeft) {
                        onTouchWallLeft(mVelocity);
                    }
                    mIsOnWallLeft = true;
                    mIsOnWallRight = false;
                    
                } else {
                    if(!mIsOnWallRight) {
                        onTouchWallRight(mVelocity);
                    }
                    mIsOnWallRight = true;
                    mIsOnWallLeft = false;
                    
                }
                mVelocity.x = 0;
            } else if (deltaXprev != 0) {
                mIsOnWallLeft = mIsOnWallRight = false;
            }
            
            
            
            deltaY = mWorldLevel.getPenetrationDepthY(mPosition, deltaYprev, mAABB);
            mPosition.y += deltaY;
            //TODO: This can probably be simplified
            //mIsOnGround = mIsOnRoof = false;
            if(deltaY != deltaYprev) {
                if(deltaY < 0) {
                    mIsOnGround = true;
                    onTouchGround(mVelocity);
                    //System.out.println("On the ground - hit at " + mVelocity.y + " tiles/second");
                    //Apply bounciness
                    float deltaDelta = deltaY - deltaYprev;  //how much we overshot by
                    mVelocity.y = -mVelocity.y * mBounciness;
                    
                    if(mVelocity.y > Constants.MINIMUM_BOUNCE_VELOCITY) {
                        float newDeltaY = mWorldLevel.getPenetrationDepthY(mPosition, deltaDelta, mAABB);
                        mPosition.y += newDeltaY;
                        //And stop. If the object is between two objects, we don't want to loop forever.
                        if(newDeltaY != deltaDelta) {
                            //mIsOnRoof = true;
                            //onTouchRoof(mVelocity);
                            mVelocity.y = 0;
                        }
                    } else {
                        mVelocity.y = 0;
                    }
                    //TODO: Update the position again
                } else if (deltaY == 0) {
                    mIsOnGround = true;
                    mVelocity.y = 0;
                } else {
                    //mIsOnRoof = true;
                    //onTouchRoof(mVelocity);
                    mVelocity.y = 0;
                }
            } else {
                //System.out.println("Not on the ground");
                mIsOnGround = false;
            }
        }

        //Cap velocity.
        if(mVelocity.x > mMaximumSpeed.x) {
            mVelocity.x = mMaximumSpeed.x;
        } else if (mVelocity.x < -mMaximumSpeed.x) {
            mVelocity.x = -mMaximumSpeed.x;
        }
        
        if(mIsOnGround && mApplyDrag) {
            if(mVelocity.x > -Constants.MINIMUM_GROUND_VELOCITY && mVelocity.x < Constants.MINIMUM_GROUND_VELOCITY) {
                mVelocity.x = 0;
            }
        }
    }
    
    //These methods are called when two entities collide.
    
    public void onCollideTop(PhysicalEntity other) {
        
    }
    public void onCollideBottom(PhysicalEntity other) {
        
    }
    public void onCollideLeft(PhysicalEntity other) {
        
    }
    public void onCollideRight(PhysicalEntity other) {
        
    }
    
    private InputHandler mInputHandler = null;
    public void setInputHandler(InputHandler inputHandler) {
        mInputHandler = inputHandler;
    }
    
    
    // Dipose the entity. This should clean up any references to libgdx objects
    // that need disposing. Note that this can be called on an initialized or
    // uninitialized object. Don't worry about state, the object will be garbage
    // collected and must be reconstructed after this.
    @Override
    public void dispose() {
    }

    /**
     * Some boilerplate code for the internal object pool.
     */
    // A list of all uninitialized game entities
    private static Queue<PhysicalEntity> sAvailableGameEntities = new LinkedList<PhysicalEntity>();

    // Pre-allocate some game entities
    private static void CreateGameEntities(int num) {
        Gdx.app.log(Constants.LOG, "PhysicalEntity::GetPhysicalEntity. Allocating " + num + " entities");
        for(int i=0; i < num; ++i) {
            sAvailableGameEntities.add(new PhysicalEntity());
        }
    }
    
    // Get an uninitialized game entity. It is the caller's responsibility to initialize it.
    public static PhysicalEntity GetPhysicalEntity() {
        Gdx.app.log(Constants.LOG, "PhysicalEntity::GetPhysicalEntity. There are " + sAvailableGameEntities.size() + " available.");
        if(sAvailableGameEntities.size() == 0) {
            Gdx.app.log(Constants.LOG, "No available game entities, creating new ones");
            CreateGameEntities(100);
        }
        return sAvailableGameEntities.remove();
    }
    
    // Put the entity back in the pool.
    private static void RecycleEntity(PhysicalEntity entity) {
        Gdx.app.log(Constants.LOG, "PhysicalEntity::RecycleEntity. There are now " + sAvailableGameEntities.size() + 1 + " entities in the pool.");
        entity.getWorld().removeEntity(entity);
        sAvailableGameEntities.add(entity);
    }
    
    // Dispose the object pool for the game entity
    public static void DisposeObjectPool() {
        Gdx.app.log(Constants.LOG, "PhysicalEntity::DisposeObjectPool");
        for(PhysicalEntity entity : sAvailableGameEntities) {
            entity.dispose();
        }
    }
}
