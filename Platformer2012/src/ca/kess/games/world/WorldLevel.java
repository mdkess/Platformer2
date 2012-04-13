package ca.kess.games.world;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import ca.kess.games.Constants;
import ca.kess.games.entities.GameEntity;
import ca.kess.games.entities.GameEntityPool;
import ca.kess.games.interfaces.IRenderable;
import ca.kess.games.interfaces.IUpdateable;
import ca.kess.games.screens.GameScreen;
import ca.kess.games.timers.DeathFadeTimer;
import ca.kess.games.timers.Timer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class WorldLevel implements IUpdateable, Disposable {
	private TileSet mTileSet;
    private Tile[][] mTiles;
    private GameScreen mGame;
    private List<GameEntity> mGameEntities;
    private List<GameEntity> mEntitiesToAdd;
    private List<GameEntity> mEntitiesToRemove;
    
    private List<Timer> mTimers;
    private List<Timer> mFinishedTimers;
    private Vector2 mGravity;
    
    private GameEntityPool mGameEntityPool = new GameEntityPool(this);
    
    public GameEntityPool getGameEntityPool() {
        return mGameEntityPool;
    }
    
    public WorldLevel(GameScreen game, String mapLocation) {
        mGameEntities = new LinkedList<GameEntity>();
        mEntitiesToAdd = new LinkedList<GameEntity>();
        mEntitiesToRemove = new LinkedList<GameEntity>();
        mTimers = new LinkedList<Timer>();
        mFinishedTimers = new LinkedList<Timer>();

        mTileSet = new TileSet();

        mGame = game;
        Pixmap pixmap = new Pixmap(Gdx.files.internal(mapLocation));
        mTiles = new Tile[pixmap.getWidth()][pixmap.getHeight()];
        for(int y = 0; y < pixmap.getHeight(); ++y) {
            for(int x = 0; x < pixmap.getWidth(); ++x) {
                int pixelColor = pixmap.getPixel(x, y) >>> 8; //signed shift
                mTiles[x][pixmap.getHeight() - y - 1] = mTileSet.get(pixelColor);
                assert mTiles[x][y] != null : "Unknown tile color " + pixelColor;
            }
        }
        pixmap.dispose();
        mGravity = new Vector2(0, -Constants.GRAVITY);
        mCollisions = new ArrayList<GameEntity>(Constants.MAX_COLLISIONS); 
    }
    
    public Vector2 getGravity() { return mGravity; }
    
    public void addEntity(GameEntity entity) {
        mEntitiesToAdd.add(entity);
    }
    public void removeEntity(GameEntity entity) {
        mEntitiesToRemove.add(entity);
    }
    
    public void addTimer(Timer timer) {
        mTimers.add(timer);
    }
    public void removeTimer(Timer timer) {
        mFinishedTimers.add(timer);
    }
    
    private List<GameEntity> mCollisions;
    /**
     * Get up to Constants.MAX_COLLISIONS.
     * The list is reused per call, so be sure.
     * @param e
     * @return
     */
    public List<GameEntity> getCollisions(GameEntity e, boolean forInteraction) {
        mCollisions.clear();
        
        for(GameEntity entity : mGameEntities) {
            if(mCollisions.size() == Constants.MAX_COLLISIONS) break;
            if(entity != e) {
                if((!forInteraction || entity.canBeInteractedWith()) && e.collidesWith(entity)) {
                    mCollisions.add(entity);
                }
            }
        }
        System.out.println(mCollisions.size() + " collisions found");
        return mCollisions;
    }
    private Random mRandom = new Random();
    public void killEntity(GameEntity entity) {
        TextureRegion[][] pixels = entity.getAnimation().getKeyFrame(0.0f, true).split(1, 1);
        Vector2 pos = entity.getPosition();
        float deltaX = entity.getWidth() / pixels.length;
        float deltaY = entity.getHeight() / pixels[0].length;
        float particleMass = entity.getMass() / ((float)(pixels.length * pixels[0].length));
        for(int y=0; y < pixels.length; ++y) {
            for(int x = 0; x < pixels[y].length; ++x) {
                Animation animation = new Animation(0.0f, pixels[pixels.length -1 - y][x]);
                GameEntity particle = mGameEntityPool.getGameEntity().initialize(animation, this, 0.8f);
                particle.setWidth(entity.getWidth() / pixels.length);
                particle.setHeight(entity.getHeight() / pixels[x].length);
                particle.getPosition().set(pos.x + deltaX * x, pos.y + deltaY * y);
                particle.setVelocityX(entity.getVelocityX() + (mRandom.nextFloat() - 0.5f) * 10);
                particle.setVelocityY(20);
                particle.canBeInteractedWith(false);
                particle.setMass(particleMass);

                addEntity(particle);
                addTimer(new DeathFadeTimer(particle, 3.0f));
            }
        }
    }
    
    //private Vector2 tmp1 = new Vector2();
    //private Vector2 tmp2 = new Vector2();
    public void render(SpriteBatch b, int leftX, int bottomY, int rightX, int topY) {
        b.setColor(1,1,1,1);

        for(int x = leftX; x < rightX; ++x) {
            for(int y = bottomY; y < topY; ++y) {
                mTiles[x][y].render(b, x, y);
            }
        }
        for(GameEntity entity : mGameEntities) {
            entity.render(b);
        }
    }

    @Override
    public void update(float delta) {
        for(Timer timer : mFinishedTimers) {
            mTimers.remove(timer);
        }
        for(Timer timer : mTimers) {
            timer.update(delta);
        }
        mFinishedTimers.clear();
        
        for(GameEntity entity : mEntitiesToAdd) {
            mGameEntities.add(entity);
        }
        for(GameEntity entity : mEntitiesToRemove) {
            mGameEntities.remove(entity);
        }
        mEntitiesToAdd.clear();
        mEntitiesToRemove.clear();

        mTileSet.update(delta);
        for(GameEntity entity : mGameEntities) {
            entity.update(delta);
        }
    }
    
    public Tile getTile(int x, int y) {
        if(x < 0 || x >= mTiles.length) return null;
        if(y < 0 || y >= mTiles[x].length) return null;
        return mTiles[x][y];
    }
    
    /**
     * See how deep moving the rectangle at oldPosition would penetrate the world.
     * 
     *  
     * @param oldPosition
     * @param newPosition
     * @param AABB
     */
    //todo: debug render
    
    
    
    public float getPenetrationDepthX(Vector2 oldPosition, float deltaX, Vector2 AABB) {
        if(deltaX == 0) return 0;
        float xLeft, xRight, yBottom, yTop;
        yBottom = oldPosition.y;
        yTop = oldPosition.y + AABB.y;
        
        if(deltaX < 0) {
            xLeft = oldPosition.x + deltaX;
            xRight = oldPosition.x;
        } else {
            xLeft = oldPosition.x + AABB.x;
            xRight = oldPosition.x + AABB.x + deltaX;
        }
        
        
        //Figure out the bounds of the boxes that we are touching.
        int x0, x1;
        int y0, y1;
        
        x0 = (int) Math.floor(xLeft);
        x1 = (int) Math.floor(xRight);
        if(Math.floor(xRight) == xRight) {
            x1 -= 1;
        }
        
        y0 = (int) Math.floor(yBottom);
        y1 = (int) Math.floor(yTop);
        if(Math.floor(yTop) == yTop) {
            y1 -= 1;
        }
        
        
        boolean left = deltaX < 0;
        
        for(int x = x0; x <= x1; ++x) {
            for(int y = y0; y <= y1; ++y) {
                Tile tile = getTile(x,y);
                if(tile != null && tile.blocksMovement()) {
                    if(left) {
                        float newDelta = (x+1) - xRight;
                        deltaX = Math.max(deltaX, newDelta);
                    } else {
                        float newDelta = x - xLeft;
                        deltaX = Math.min(deltaX, newDelta);
                    }
                }
            }
        }
        
        return deltaX;
    }
    public float getPenetrationDepthY(Vector2 oldPosition, float deltaY, Vector2 AABB) {
        if(deltaY == 0) return 0;
        //Find the dimensions of the box carved out by the motion.
        float xLeft, xRight, yBottom, yTop;
        xLeft = oldPosition.x;
        xRight = oldPosition.x + AABB.x;
        
        if(deltaY < 0) {
            yBottom = oldPosition.y + deltaY;
            yTop = oldPosition.y;
        } else {
            yBottom = oldPosition.y + AABB.y;
            yTop = oldPosition.y + AABB.y + deltaY;
        }
        
        //Figure out the bounds of the boxes that we are touching.
        int x0, x1;
        int y0, y1;
        
        x0 = (int) Math.floor(xLeft);
        x1 = (int) Math.floor(xRight);
        if(Math.floor(xRight) == xRight) {
            x1 -= 1;
        }
        
        y0 = (int) Math.floor(yBottom);
        y1 = (int) Math.floor(yTop);
        if(Math.floor(yTop) == yTop) {
            y1 -= 1;
        }
        
        boolean down = deltaY < 0;
        for(int x = x0; x <= x1; ++x) {
            for(int y = y0; y <= y1; ++y) {
                Tile tile = getTile(x,y);
                if(tile != null && tile.blocksMovement()) {
                    if(down) {
                        float newDelta = (y+1) - yTop;
                        deltaY = Math.max(deltaY, newDelta);
                    } else {
                        float newDelta = y - yBottom;
                        deltaY = Math.min(deltaY, newDelta);
                    }
                }
            }
        }
        return deltaY;
    }

    // Returns the coefficient of friction for the entity, by taking the average
    // friction of the area beneath the entity.
    public float getFriction(Vector2 mPosition, Vector2 mAABB) {
        //TODO: implement me
        return Constants.DUMMY_FRICTION;
    }
    
	@Override
	public void dispose() {
		for(GameEntity entity : mGameEntities) {
			entity.dispose();
		}
	}

	public int getWidth() {
		return mTiles.length;
	}
	public int getHeight() {
		return mTiles[0].length;
	}


}
