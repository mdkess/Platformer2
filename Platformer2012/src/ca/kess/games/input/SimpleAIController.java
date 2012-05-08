package ca.kess.games.input;

import ca.kess.games.Constants;
import ca.kess.games.entities.ActorEntity;
import ca.kess.games.entities.PhysicalEntity.Direction;

/**
 * A simple AI controller. The entity walks left until it hits a wall, then turns and
 * walks right, etc.
 */
public class SimpleAIController extends EntityController {
    private ActorEntity mEntity;
    public SimpleAIController(ActorEntity entity) {
        mEntity = entity;
    }
    
    @Override
    public void update() {
        float movementForce = Constants.HERO_MAX_FORCE * 0.7f;

        if(mEntity.isOnWallLeft()) {
            mEntity.setDirection(Direction.RIGHT);
        } else if(mEntity.isOnWallRight()) {
            mEntity.setDirection(Direction.LEFT);
        }
        
        if(mEntity.getDirection() == Direction.LEFT){
            movementForce = -movementForce;
        }
        mEntity.applyForce(movementForce, 0);
    }
}
