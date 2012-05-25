package ca.kess.games.weapons;

import ca.kess.games.Constants;
import ca.kess.games.entities.ActorEntity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * A weapon is something that can shoot. All weapons must implement this interface,
 * although for some weapons parts may be meaningful.
 */
public abstract class Weapon {

    private float mCooldownTimeRemaining = 0;
    /**
     * Returns whether the weapon is ready to fire. This is used to handle reloading, for example.
     */
    public boolean isReady() {
        return mCooldownTimeRemaining == 0;
    }
    
    /**
     * The total time that the weapon has to cool down for between shots.
     */
    public abstract float cooldownTime();
    
    /**
     * The amount of time remaining when the weapon is cooling down (0 if the weapon is ready).
     */
    public float remainingCooldownTime() {
        return mCooldownTimeRemaining;
    }
    
    /**
     * Whether the weapon is ready to fire. Can be overridden to make more interesting
     * checks (ie. a weapon that can only be fired underwater).
     */
    public boolean weaponIsReady() {
        return remainingCooldownTime() == 0;
    }
    
    public void update() {
        mCooldownTimeRemaining -= Constants.DELTA;
        if(mCooldownTimeRemaining < 0) mCooldownTimeRemaining = 0;
    }
    
    /**
     * FIRE ZE MISSILES! Checks whether the weapon is ready to attack, and does, 
     */
    public final boolean attack(ActorEntity entity) {
        if(weaponIsReady()) {
            mCooldownTimeRemaining = cooldownTime();
            fire(entity);
            return true;
        } else {
            Gdx.app.log(Constants.LOG, "Need to wait " + mCooldownTimeRemaining + "s to fire");
        }
        return false;
    }
    
    public void render(SpriteBatch b, ActorEntity owner) {
    	//do nothing
    }
    
    /**
     * This should be overridden in child classes.
     * Create the projectile and launch it.
     */
    protected abstract void fire(ActorEntity entity);
}
