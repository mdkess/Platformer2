package ca.kess.games.entities;

import ca.kess.games.timers.DeathFadeTimer;
import ca.kess.games.world.WorldLevel;

import com.badlogic.gdx.graphics.g2d.Animation;

public class ChestEntity extends GameEntity {
    public enum ChestState {
        LOCKED,
        OPEN,
        CLOSED
    }
    
    Animation mOpenAnimation;
    Animation mClosedAnimation;
    ChestState mState;

    GameEntity mContents;
    public ChestEntity() {
        
    }
    
    public ChestEntity initialize(Animation openAnimation, Animation closedAnimation, WorldLevel worldLevel, float bounciness, GameEntity contents) { 
        super.initialize(closedAnimation, worldLevel, bounciness);
        // TODO Auto-generated constructor stub
        mOpenAnimation = openAnimation;
        mClosedAnimation = closedAnimation;
        mState = ChestState.CLOSED;
        mContents = contents;
        
        return this;
    }

    @Override
    public void onInteraction(GameEntity other) {
        if(mState == ChestState.CLOSED) {
            mState = ChestState.OPEN;
            setVelocityY(15);
            mContents.setVelocityY(30);
            mContents.getPosition().set(getPosition());
            getWorld().addEntity(mContents);
            setAnimation(mOpenAnimation);
            getWorld().addTimer(new DeathFadeTimer(this, 1.0f));
        }
    }
}
