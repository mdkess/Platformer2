package ca.kess.games.camera;

import ca.kess.games.Constants;
import ca.kess.games.entities.GameEntity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class MarioCamera extends FixedCamera {
	/**
	 * The area of the camera, in screen space
	 */
	private final Rectangle mArea;
	public MarioCamera(OrthographicCamera camera, GameEntity entity, Rectangle area) {
		super(camera, entity);
		mArea = area;
	}

	@Override
	public void update() {
		Vector3 cameraPos = getCamera().position;
		Vector2 entityPos = getTarget().getPosition();
		
		float f = Constants.ZOOM_FACTOR * Constants.TILE_SIZE;
		//Transform entity position to screen space.
		float cameraX = cameraPos.x; //in world space (1 = 1 tile)
		float cameraY = cameraPos.y;
		
		//Convert to screen space.
		float offsetX = (entityPos.x - cameraPos.x)*f;
		float offsetY = (entityPos.y - cameraPos.y)*f;
		
		
		
		//Camera is always in the middle of the screen
		float mx = Gdx.graphics.getWidth()/2;
		float my = Gdx.graphics.getHeight()/2;
		
		Gdx.app.log(Constants.LOG, offsetX + " " + offsetY + " " + mx + " " + my);
		
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
		
		//only move the camera if the entity is outside of the area.
		/*
		if(targetY < mArea.y) {
			targetY = mArea.y;
		} else if(targetY > mArea.y + mArea.height) {
			targetY = mArea.y + mArea.height;
		}
		
		if(targetX < mArea.x) {
			targetX = mArea.x;
		} else if(targetX > mArea.x + mArea.width) {
			targetX = mArea.x + mArea.width;
		}*/
		
		
        getCamera().position.set(cameraX, cameraY, 0);
		//getCamera().position.set(offsetX, offsetY, 0);
		//getCamera().position.set(entityPos.x, entityPos.y, 0);
        getCamera().update();
	}
	
	@Override
	public void onResize(int width, int height) {
		super.onResize(width, height);
	}

}
