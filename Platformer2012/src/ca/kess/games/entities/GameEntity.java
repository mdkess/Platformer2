package ca.kess.games.entities;

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

public class GameEntity implements IRenderable, IUpdateable, Disposable {
    private Vector2 mPosition;
    private Vector2 mPreviousPosition;
    private Vector2 mVelocity;
    private Vector2 mMaximumSpeed = new Vector2(10, 10);
    
    private Animation mAnimation;
    private float mAnimationTime = 0.0f;
    
    private Vector2 mAABB;
    
    
    private WorldLevel mWorldLevel;
    
    private float mBounciness = 0;

    private float mAlpha;
    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }
    
    private float mMass;
    public float getMass() {
        return mMass;
    }
    public void setMass(float mass) {
        mMass = mass;
    }
    
    //TODO: Maybe my earlier game idea of having forces as objects
    // would be useful here, so that individual forces could be added
    // or removed?
    private Vector2 mAppliedForces;
    public void applyForce(float forceX, float forceY) {
        mAppliedForces.x += forceX;
        mAppliedForces.y += forceY;
    }

    public void resetForces() {
        mAppliedForces.set(0,0);
    }
    
    // These functions are called only once, when the event happens.
    
    //Whether the entity should be checked for collisions
    private boolean mCheckCollisions = true;
    public void checkCollisions(boolean checkCollisions) {
        mCheckCollisions = checkCollisions;
    }
    
    //Whether the entity is affected by gravity
    private boolean mHasGravity = true;
    public void hasGravity(boolean hasGravity) {
        mHasGravity = hasGravity;
    }
    
    private boolean mIsOnGround = false;
    private boolean mIsOnWallLeft = false;
    private boolean mIsOnWallRight = false;
    private boolean mIsOnRoof = false;
    
    public boolean isOnWallLeft() { return mIsOnWallLeft; }
    public boolean isOnWallRight() { return mIsOnWallRight; }
    public boolean isOnRoof() { return mIsOnRoof; }
    
    boolean mCanBeInteractedWith = true;
    public void canBeInteractedWith(boolean canBeInteractedWith) {
        mCanBeInteractedWith = canBeInteractedWith;
    }
    public boolean canBeInteractedWith() {
        return mCanBeInteractedWith;
    }
    
    public void onTouchWallLeft(Vector2 impactVelocity) {
        if(mInputHandler != null)
            System.out.println("onTouchWallLeft: " + impactVelocity);
    }
    public void onTouchWallRight(Vector2 impactVelocity) {
        if(mInputHandler != null)
            System.out.println("onTouchWallRight: " + impactVelocity);
    }
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
    
    public void onTouchRoof(Vector2 impactVelocity) {
        if(mInputHandler != null)
            System.out.println("onTouchRoof: " + impactVelocity);
    }
    
    public void remove() {
        mWorldLevel.getGameEntityPool().recycle(this);
    }
    
    public GameEntity() { }
    
    public GameEntity initialize(Animation animation, WorldLevel worldLevel, float bounciness) {
        mPosition = new Vector2();
        mPreviousPosition = new Vector2();
        mVelocity = new Vector2();
        mAnimation = animation;
        mAppliedForces = new Vector2();
        
        mAABB = new Vector2(1, 1);
        mWorldLevel = worldLevel;
        mBounciness = bounciness;
        mAlpha = 1.0f;
        
        mMass = 1.0f;
        
        return this;
    }
    
    private boolean mVisible = true;
    public boolean visible() { return mVisible; }
    public void setVisible(boolean visible) { mVisible = visible; }
    
    //Open a door, if it's in front of the entity
    //TODO: This is sort of hacky.

    public void openDoor() {
        Gdx.app.log(Constants.LOG, "GameEntity::openDoor");

        float x = mPosition.x;
        if(mFacingLeft) {
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
    
    
    public WorldLevel getWorld() { return mWorldLevel; }
    
    public Animation getAnimation() { return mAnimation; }
    public boolean isOnGround() { return mIsOnGround; }
    
    public void setVelocityX(float x) {
        mVelocity.x = x;
        if(x != 0) {
            mFacingLeft = x < 0;
        }
    }
    public void setVelocityY(float y) {
        mVelocity.y = y;
    }
    public float getVelocityX() { return mVelocity.x; }
    public float getVelocityY() { return mVelocity.y; }
    public Vector2 getPosition() {
        return mPosition;
    }
    
    public void setWidth(float width) {
        mAABB.x = width;
    }
    public float getWidth() { return mAABB.x; }
    
    public void setHeight(float height) {
        mAABB.y = height;
    }
    public float getHeight() { return mAABB.y; }
    
    private boolean mKilled = false;
    private boolean mDead = false;
    public void kill() {
        mKilled = true;
        mDead = true;
    }
    public void restore() {
        mKilled = false;
        mDead = false;
    }
    public boolean isDead() { return mDead; }
    
    public Vector2 getAABB() { return mAABB; }
    
    private boolean mFacingLeft = false;
    
    public void setAnimation(Animation animation) {
        mAnimation = animation;
    }
    
    @Override
    public void render(SpriteBatch b) {
        if(visible()) {
            b.setColor(1, 1, 1, mAlpha);
            b.draw(mAnimation.getKeyFrame(mAnimationTime, true), mPosition.x, mPosition.y, getWidth()/2, getHeight()/2, getWidth(), getHeight(), (mFacingLeft) ? -1 : 1, 1, 0);
        }
    }
    
    public void onInteraction(GameEntity other) {
        if(canBeInteractedWith()){
            mWorldLevel.killEntity(this);
            mWorldLevel.removeEntity(this);
        }
    }

    public InputHandler getInputHandler() {
        return mInputHandler;
    }
    
    
    public boolean collidesWith(GameEntity other) {
        return collidesWith(other.getPosition(), other.getAABB());
    }
    
    public boolean collidesWith(Vector2 pos, Vector2 aabb) {
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
    
    Vector2 mAcceleration = new Vector2();
    Vector2 mGravity = new Vector2();
    
    @Override
    public final void update(float delta) {
        mAnimationTime += delta;
        /*
        while(mAnimationTime > mAnimation.animationDuration) {
            mAnimationTime -= mAnimation.animationDuration;
        }*/
        resetForces();
        
        if(mInputHandler != null) { mInputHandler.update(); }
        
        if(mHasGravity) {
            Vector2 gravity = mWorldLevel.getGravity();
            mGravity.set(gravity);
        } else {
            mGravity.set(0,0);
        }
        applyForce(mGravity.x * mMass, mGravity.y * mMass);
        
        //Now apply friction
        float forceFriction = 0;
        if(mIsOnGround) {
            float u_k = mWorldLevel.getFriction(mPosition, mAABB);
            forceFriction = -u_k * mGravity.y * mMass;
            if(forceFriction < 0) {
                Gdx.app.error(Constants.LOG, "Error - friction " + forceFriction + " should not be negative");
                forceFriction = 0;
            }
        } else if(mApplyDrag) {
            //Now here, force of friction becomes drag, which is proportional to the square of the velocity.
            forceFriction = Constants.DRAG * mVelocity.x * mVelocity.x;
        }
        
        //F = m * a => a = F / m
        mAcceleration.set(mAppliedForces.x / mMass, mAppliedForces.y / mMass);
        
        mVelocity.x += mAcceleration.x * delta;
        mVelocity.y += mAcceleration.y * delta;
        
        //Now we have the velocity without friction. Apply friction.
        float frictionAcceleration = forceFriction * mMass;
        float frictionDeltaVelocity = frictionAcceleration * delta;
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
        
        float deltaXprev = mVelocity.x * delta;
        float deltaYprev = mVelocity.y * delta;

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
        if(mKilled) {
            mKilled = false;
            mWorldLevel.killEntity(this);
        }
        if(mVelocity.x != 0) {
            mFacingLeft = mVelocity.x < 0;
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
    
    public void onCollideTop(GameEntity other) {
        
    }
    public void onCollideBottom(GameEntity other) {
        
    }
    public void onCollideLeft(GameEntity other) {
        
    }
    public void onCollideRight(GameEntity other) {
        
    }
    
    private InputHandler mInputHandler = null;
    public void setInputHandler(InputHandler inputHandler) {
        mInputHandler = inputHandler;
    }
	@Override
	public void dispose() {
	    //Called when the entity is done with.
	}
	private boolean mApplyDrag = true;
    public void setApplyDrag(boolean applyDrag) {
        // TODO Auto-generated method stub
        mApplyDrag = applyDrag;
    }

}
