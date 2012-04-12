package ca.kess.games.entities;

import java.util.LinkedList;
import java.util.Queue;

import ca.kess.games.Constants;
import ca.kess.games.world.WorldLevel;

import com.badlogic.gdx.Gdx;

/**
 * A pool of game entities. Instead of calling new, claim them from here to
 * avoid the evils of the garbage collector.
 * 
 * The pool will automatically resize when it runs out of capacity
 * @author mar.000
 *
 */
public class GameEntityPool {
    //The list of active entities. These are claimed 
    private Queue<GameEntity> mAvailableGameEntities = new LinkedList<GameEntity>();
    private Queue<ChestEntity> mAvailableChestEntities = new LinkedList<ChestEntity>();
    private WorldLevel mWorldLevel;
    
    public GameEntityPool(WorldLevel worldLevel) {
        mWorldLevel = worldLevel;
        createGameEntities(100);
        createChestEntities(100);
    }
    
    private void createGameEntities(int num) {
        for(int i=0; i < num; ++i) {
            mAvailableGameEntities.add(new GameEntity());
        }
    }
    private void createChestEntities(int num) {
        for(int i=0; i < num; ++i) {
            mAvailableChestEntities.add(new ChestEntity());
        }
    }
    
    public GameEntity getGameEntity() {
        if(mAvailableGameEntities.size() == 0) {
            Gdx.app.log(Constants.LOG, "No available game entities, creating new ones");
            createGameEntities(100);
        }
        return mAvailableGameEntities.remove();
    }
    public ChestEntity getChestEntity() {
        if(mAvailableChestEntities.size() == 0) {
            Gdx.app.log(Constants.LOG, "No available chest entities, creating new ones");
            createChestEntities(100);
        }
        return mAvailableChestEntities.remove();
    }
    
    public void recycle(GameEntity entity) {
        Gdx.app.log(Constants.LOG, "Recycling GameEntity");
        entity.dispose();
        mWorldLevel.removeEntity(entity);
        mAvailableGameEntities.add(entity);
    }
    
    public void recycle(ChestEntity chestEntity) {
        Gdx.app.log(Constants.LOG, "Recycling ChesetEntity");
        chestEntity.dispose();
        mWorldLevel.removeEntity(chestEntity);
        mAvailableChestEntities.add(chestEntity);
    }
}
