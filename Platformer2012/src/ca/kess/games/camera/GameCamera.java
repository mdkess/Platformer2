package ca.kess.games.camera;

import ca.kess.games.world.WorldLevel;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

public abstract class GameCamera {
	private final OrthographicCamera mCamera;

	protected final OrthographicCamera getCamera() {
		return mCamera;
	}
	
	public GameCamera(OrthographicCamera camera) {
		mCamera = camera;
	}
	
	/**
	 * Do any pre-processing logic for the camera
	 */
	public abstract void update();
	
	/**
	 * Get the MVP matrix for the camera
	 */
	public abstract Matrix4 getCombined();
	
	/**
	 * Called when the screen is resized/reoriented.
	 */
	public abstract void onResize(int width, int height);

	/**
	 * Render pandas!
	 */
	public abstract void render(SpriteBatch b, WorldLevel worldLevel);

}
