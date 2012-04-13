package ca.kess.games;

public class Constants {
    public static final float MINIMUM_BOUNCE_VELOCITY = 4.0f;
    public static final float MINIMUM_GROUND_VELOCITY = 1.0f;
    public static final float MINIMUM_AIR_VELOCITY = 4.0f;
    public static final float AIR_RESISTENCE = 0.9f;
    public static final float FRICTION = 0.05f;
    public static final int MAX_COLLISIONS = 8;
    public static final int ZOOM_FACTOR = 3;
    public static final float DRAG = 1.7f;
    public static final float DUMMY_FRICTION = 0.8f;
    public static final float HERO_MAX_FORCE = 100.0f;
    public static final float HERO_JUMP_FORCE = 1000.0f;
    public static final int HERO_JUMP_THRUST_FRAMES = 1; //How many frames the player gets a boost for holding the jump key for.
    public static final float GRAVITY = 80.0f;

    public static final float PHYSICS_DELTA = 1.0f/60.0f;
    
    public static final String LOG = "mdkess-platformer";
    public static final int SKIP_FRAMES = 3;
	public static final int TILE_SIZE = 8;
}
