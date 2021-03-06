package ca.kess.games.world;

import ca.kess.games.entities.PhysicalEntity;

import com.badlogic.gdx.graphics.g2d.Animation;

public class DoorTile extends AnimatedTile {
    private enum State {
        OPEN,
        CLOSED,
    }
    private State mState;
    private Animation mOpenAnimation;
    private Animation mClosedAnimation;
    
    public DoorTile(Animation closedAnimation, Animation openAnimation, float staticFriction, float kineticFriction) {
        super(closedAnimation, true, staticFriction, kineticFriction);
        mState = State.CLOSED;
        mClosedAnimation = closedAnimation;
        mOpenAnimation = openAnimation;
    }
    
    public void open() {
        mState = State.OPEN;
        setBlocksMovement(false);
        setAnimation(mOpenAnimation);
    }
    
    private void toggleState() {
        if(mState == State.OPEN) {
            close();
        } else {
            open();
        }
    }
    
    @Override
    public boolean onInteraction(PhysicalEntity entity) {
        toggleState();
        return true;
    }
    
    public void close(){
        mState = State.CLOSED;
        setBlocksMovement(true);
        setAnimation(mClosedAnimation);
    }

}
