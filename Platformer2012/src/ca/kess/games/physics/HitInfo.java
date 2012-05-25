package ca.kess.games.physics;

import ca.kess.games.entities.ActorEntity;

/**
 * Encapsulates information about a collision
 */
public class HitInfo {
	public boolean CollidesWithWallLeft;
	public boolean CollidesWithWallRight;
	public boolean CollidesWithRoof;
	public boolean CollidesWithGround;
	public ActorEntity CollidesWithEntity;
	public float FinalPositionX;
	public float FinalPositionY;
	
}
