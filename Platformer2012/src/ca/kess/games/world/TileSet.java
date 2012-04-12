package ca.kess.games.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.kess.games.graphics.GraphicsCache;
import ca.kess.games.interfaces.IUpdateable;

import com.badlogic.gdx.graphics.g2d.Animation;

public class TileSet implements IUpdateable {
    //Map of the static tiles
	private Map<Integer, Tile> mTileCache = new HashMap<Integer, Tile>();
	private List<Tile> mInstanceTiles = new ArrayList<Tile>();
	public TileSet() {
	    
	    Animation torchAnimation = new Animation(.7f, GraphicsCache.getEnvironment(1,0), GraphicsCache.getEnvironment(2,0));
        Animation lavaAnimation = new Animation(1.2f, GraphicsCache.getEnvironment(0,7), GraphicsCache.getEnvironment(0,8));
        Animation waterAnimation = new Animation(.5f, GraphicsCache.getEnvironment(2,7), GraphicsCache.getEnvironment(2,8));
        
	    //Ground
		mTileCache.put(Integer.valueOf(0x000000), new StaticTile(GraphicsCache.getEnvironment(0, 0), true, 1.0f, 1.0f));
		//Air
		mTileCache.put(Integer.valueOf(0xFFFFFF), new StaticTile(GraphicsCache.getEnvironment(0, 5), false, 1.0f, 1.0f));
		//Water
		mTileCache.put(Integer.valueOf(0x0000FF), new AnimatedTile(waterAnimation, false, 1.0f, 1.0f));
		//Lava
		mTileCache.put(Integer.valueOf(0xFF0000), new AnimatedTile(lavaAnimation, false, 1.0f, 1.0f));
		//Grass
		mTileCache.put(Integer.valueOf(0x00FF00), new StaticTile(GraphicsCache.getEnvironment(6, 4), true, 1.0f, 1.0f));
		//Torch
		mTileCache.put(Integer.valueOf(0xFFFF00), new AnimatedTile(torchAnimation, true, 1.0f, 1.0f));
		//Door
		
		
	}

	@Override
	public void update(float delta) {
	    for(Tile tile : mTileCache.values()) {
	        tile.update(delta);
	    }
	    for(Tile tile : mInstanceTiles) {
	        tile.update(delta);
	    }
	}
    private Animation mDoorClosedAnimation = new Animation(1f, GraphicsCache.getEnvironment(0,  13));
    private Animation mDoorOpenAnimation = new Animation(1f, GraphicsCache.getEnvironment(1,  13));
	public Tile get(int color) {
		Tile tile = mTileCache.get(Integer.valueOf(color));
		if(tile != null)
		    return tile;
		
		if(color == 0xFF8000) {
		    tile = new DoorTile(mDoorClosedAnimation, mDoorOpenAnimation, 1.0f, 1.0f);
		    mInstanceTiles.add(tile);
		    return tile;
		    
		}
		return null;
	}
}