package ca.kess.games.camera;

import ca.kess.games.Constants;
import ca.kess.games.entities.PhysicalEntity;
import ca.kess.games.world.WorldLevel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

public class FixedCamera extends GameCamera {
    private PhysicalEntity mGameEntity;

    public FixedCamera(OrthographicCamera camera, PhysicalEntity entity) {
        super(camera);
        mGameEntity = entity;
    }

    public final PhysicalEntity getTarget() {
        return mGameEntity;
    }

    @Override
    public void update() {
        getCamera().update();
    }

    @Override
    public void render(SpriteBatch b, WorldLevel worldLevel) {
        float mx = getCamera().position.x;
        float my = getCamera().position.y;
        float width = Gdx.graphics.getWidth()/(8*Constants.ZOOM_FACTOR);
        float height = Gdx.graphics.getWidth()/(8*Constants.ZOOM_FACTOR);
        int x0 = Math.max((int) (mx - width/2), 0);
        int x1 = Math.min((int) (1+mx + width/2), worldLevel.getWidth());
        int y0 = Math.max((int) (my - height/2), 0);
        int y1 = Math.min((int) (my + height/2), worldLevel.getHeight());
        
        worldLevel.render(b, x0, y0, x1, y1);
    }

    @Override
    public void onResize(int width, int height) {
        getCamera().setToOrtho(false, width/(Constants.TILE_SIZE*Constants.ZOOM_FACTOR), height/(Constants.TILE_SIZE*Constants.ZOOM_FACTOR));
    }

    @Override
    public Matrix4 getCombined() {
        return getCamera().combined;
    }
}
