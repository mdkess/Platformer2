package ca.kess.games.camera;

import ca.kess.games.Constants;
import ca.kess.games.entities.PhysicalEntity;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class MarioCamera extends FixedCamera {
	/**
	 * The area of the camera, in screen space
	 */
	private final Rectangle mArea;
	public MarioCamera(OrthographicCamera camera, PhysicalEntity entity, Rectangle area) {
		super(camera, entity);
		mArea = area;
	}

	private Vector2 update_entityPos = new Vector2();
	@Override
	public void update() {
		Vector3 cameraPos = getCamera().position;
		update_entityPos.set(getTarget().getPositionX(), getTarget().getPositionY());

		float f = Constants.ZOOM_FACTOR * Constants.TILE_SIZE;
		//Transform entity position to screen space.
		float cameraX = cameraPos.x; //in world space (1 = 1 tile)
		float cameraY = cameraPos.y;

		//Convert to screen space.
		float offsetX = (update_entityPos.x - cameraPos.x)*f;
		float offsetY = (update_entityPos.y - cameraPos.y)*f;

		if(offsetX > mArea.width/2) {
			cameraX += (offsetX - (mArea.width/2))/f;
		} else if (offsetX < -mArea.width/2) {
			cameraX += (offsetX + (mArea.width/2))/f;
		}
		
		if(offsetY > mArea.height/2) {
			cameraY += (offsetY - (mArea.height/2))/f;
		} else if (offsetY < -mArea.height/2) {
			cameraY += (offsetY + (mArea.height/2))/f;
		}
		
		// Also restrict the camera to the visible level, forced to the bottom left if the camera views the entire world.
		
		getCamera().position.set(cameraX, cameraY, 0);
		getCamera().update();
	}
	
	@Override
	public void onResize(int width, int height) {
		super.onResize(width, height);
	}

}
