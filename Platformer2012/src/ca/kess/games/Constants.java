package ca.kess.games;

/**
 * A list of constants that affect the world.
 */
public class Constants {
    /**
     * The minimum velocity for something to bounce. This is so that objects don't jitter when bouncing at low velocities.
     * If after a bounce their velocity would be less than this, it is set to zero.
     */
    public static final float MINIMUM_BOUNCE_VELOCITY = 4.0f;
    
    /**
     * The minimum velocity for something to travel at. This is so that things that are decelerating don't slide slowly.
     */
    public static final float MINIMUM_GROUND_VELOCITY = 1.0f;
    
    /**
     * The minimum velocity for traveling through the air.
     */
    public static final float MINIMUM_AIR_VELOCITY = 4.0f;
    
    /**
     * The maximum number of collisions returned when getting all collisions.
     */
    public static final int MAX_COLLISIONS = 8;
    
    /**
     * How far the camera is zoomed in. ZOOM_FACTOR * TILE_SIZE determines the relative size of a pixel.
     */
    public static final int ZOOM_FACTOR = 2;
    
    /**
     * The size of a tile, in pixels.
     */
    public static final int TILE_SIZE = 8;
    
    /**
     * The drag coefficient. The force of drag is determined by DRAG * velocity^2.
     * In the future, it might be useful for this to be a per entity or even a per tile
     * property.
     */
    public static final float DRAG = 1.7f;
    
    /**
     * A dummy friction value for all blocks. This is a placeholder until the physics code
     * is more fleshed out.
     */
    public static final float DUMMY_FRICTION = 0.8f;

    /**
     * The friction on a wall. This will be replaced with an actual calculation
     * eventually.
     */
    public static final float WALL_FRICTION = 1.5f;
    /**
     * The maximum force that the a game entity can have affecting it in the X direction. This should be a per entity property.
     */
    public static final float HERO_MAX_FORCE = 100.0f;
    
    /**
     * How much thrust is applied when the hero is jumping.
     */
    public static final float HERO_JUMP_FORCE = 1000.0f;
    
    /**
     * How many (physics) frames the hero can jump for. This should be part of a degrading thrust function
     * to allow for variable height jumps.
     */
    public static final int HERO_JUMP_THRUST_FRAMES = 1; //How many frames the player gets a boost for holding the jump key for.
    
    /**
     * The force of gravity, in the -Y direction.
     */
    public static final float GRAVITY = 80.0f;

    /**
     * The timestep for updating physics.
     */
    public static final float DELTA = 1.0f/60.0f;
    
    /**
     * How many frames we should skip on. In the update loop, the world physics is updated at a constant rate of 
     * PHYSICS_DELTA, and then renders. If the frame rate is less than 1 / (SKIP_FRAMES * PHYSICS_DELTA) the game will
     * stop updating and render, so the game will not freeze. This will cause the game to slow down however.
     */
    public static final int SKIP_FRAMES = 3;
    
    public static final String LOG = "mdkess-platformer";

}
