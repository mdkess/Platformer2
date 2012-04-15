package ca.kess.games.weapons;

/**
 * A weapon is something that can shoot. All weapons must implement this interface,
 * although for some weapons parts may be meaningful.
 */
public abstract class Weapon {

    /**
     * Returns whether the weapon is ready to fire. This is used to handle reloading, for example.
     */
    public abstract boolean isReady();
    
    /**
     * The total time that the weapon has to cool down for between shots.
     */
    public abstract int cooldownTime();
    
    /**
     * The amount of time remaining when the weapon is cooling down (0 if the weapon is ready).
     */
    public abstract int remainingCooldownTime();
    
    /**
     * Whether the weapon is ready to fire. Can be overridden to make more interesting
     * checks (ie. a weapon that can only be fired underwater).
     */
    public boolean weaponIsReady() {
        return remainingCooldownTime() == 0;
    }
    
    /**
     * FIRE ZE MISSILES! Checks whether the weapon is ready to attack, and does, 
     */
    public final boolean attack() {
        if(weaponIsReady()) {
            fire();
            return true;
        }
        return false;
    }
    
    /**
     * This should be overridden in child classes.
     * Create the projectile and launch it.
     */
    protected abstract void fire();
}
