package ca.kess.games.input;

import ca.kess.games.Constants;
import ca.kess.games.entities.ActorEntity;
import ca.kess.games.entities.PhysicalEntity;

import com.badlogic.gdx.Gdx;

public class KeyboardInputHandler extends EntityController {
    private ActorEntity mEntity;
    public KeyboardInputHandler(ActorEntity entity) {
        mEntity = entity;
    }
    
    //keep track of the mini state machien for how the player can jump
    private enum JumpState {
        CAN_JUMP,           //Entity hasn't jumped yet
        JUMPED,             //Entity has jumped
        CAN_DOUBLE_JUMP,    //Entity can double jump
        DOUBLE_JUMPED,      //Entity has jumped and double jumped
    }

    private JumpState mJumpState = JumpState.CAN_JUMP;
    
    private boolean hasInteracted = false;
    private int mJumpFrames = 0;
    
    private boolean mLeftPressed = false;

    private boolean mRightPressed = false;
    
    private boolean mJumpPressed = false;
    private boolean mInteractPressed = false;
    private boolean mResetPressed = false;
    private boolean mSuicidePressed = false;
    
    public void setLeftPressed(boolean pressed) {
    	Gdx.app.log(Constants.LOG, "InputHandler::setLeftPressed(" + pressed + ")");
        mLeftPressed = pressed;
    }
    public void setRightPressed(boolean pressed) {
    	Gdx.app.log(Constants.LOG, "InputHandler::setRightPressed(" + pressed + ")");
        mRightPressed = pressed;
    }
    public void setJumpPressed(boolean pressed) {
    	Gdx.app.log(Constants.LOG, "InputHandler::setJumpPressed(" + pressed + ")");
        mJumpPressed = pressed;
    }
    public void setInteractPressed(boolean pressed) {
    	Gdx.app.log(Constants.LOG, "InputHandler::setInteractPressed(" + pressed + ")");
        mInteractPressed = pressed;
    }
    public void setResetPressed(boolean pressed) {
    	Gdx.app.log(Constants.LOG, "InputHandler::setResetPressed(" + pressed + ")");
        mResetPressed = pressed;
    }
    public void setSuicidePressed(boolean pressed) {
    	Gdx.app.log(Constants.LOG, "InputHandler::setSuicidePressed(" + pressed + ")");
        mSuicidePressed = pressed;
    }
    
    @Override
    public void update() {
        if(mResetPressed && !mEntity.isAlive()) {
            mEntity.resurrect();
            mEntity.setVisible(true);
            mResetPressed = false;
        }

        if(!mEntity.isAlive()) return;
        float maximumForce = Constants.HERO_MAX_FORCE;
        
        float forceX = 0;
        //if(Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) {
        //    vx = mEntity.getVelocityX();
        //}
        //float vy = mEntity.getVelocityY();
        float forceY = 0;
        
        if(mEntity.isOnGround()) {
            mJumpState = JumpState.CAN_JUMP;
            mJumpFrames = 0;
        }
        
        if(mLeftPressed) {
            forceX -= maximumForce;
        }
        if(mRightPressed) {
            forceX += maximumForce;
        }

        
        if(mJumpPressed) { 
            if(mEntity.isOnGround() && mJumpState == JumpState.CAN_JUMP) {
                forceY += Constants.HERO_JUMP_FORCE;
                mJumpFrames = Constants.HERO_JUMP_THRUST_FRAMES;
                mJumpState = JumpState.JUMPED;
            } else if(mJumpState == JumpState.CAN_DOUBLE_JUMP) {
                Gdx.app.log(Constants.LOG, "Double jumped!");
                mJumpState = JumpState.DOUBLE_JUMPED;
                mJumpFrames = Constants.HERO_JUMP_THRUST_FRAMES;
                forceY += Constants.HERO_JUMP_FORCE;
                mEntity.setVelocityY(0);
            } else if(mJumpFrames > 0) {
                --mJumpFrames;
                forceY += Constants.HERO_JUMP_FORCE;
                
            }
            /*else {
                
                if(mEntity.isOnWallLeft()) {
                    vy = Constants.HERO_JUMP_FORCE;
                    vx = Constants.HERO_JUMP_FORCE;
                } else if(mEntity.isOnWallRight()) {
                    vx = Constants.HERO_JUMP_FORCE;
                    vx = Constants.HERO_JUMP_FORCE;
                }
            }
            */
            /*
            } else if(!hasDoubleJumped) {
                //hasDoubleJumped = true;
                //mEntity.setVelocityY(0);
                //vy = Constants.HERO_JUMP_FORCE;
            }*/ 
            /*else if(!hasWallJumped) {
                if(mEntity.isOnWallLeft()) {
                    vy += maximumSpeed * 3;
                    vx += 10;
                    hasWallJumped = true;
                } else if(mEntity.isOnWallRight()) {
                    vy = maximumSpeed * 3;
                    vx -= 10;
                    hasWallJumped = true;
                }
            }*/
        } else {
            mJumpFrames = 0; // use them or lose them
            if(mJumpState == JumpState.JUMPED) {
                mJumpState = JumpState.CAN_DOUBLE_JUMP;
            }
        }

        
        if(mInteractPressed) {
            mInteractPressed = false;
            if(!hasInteracted) {
                for(PhysicalEntity entity : mEntity.getWorld().getCollisions(mEntity, true)) {
                    hasInteracted = hasInteracted || entity.onInteraction(mEntity);
                }
                if(!hasInteracted) {
                    hasInteracted = mEntity.openDoor();
                }
                if(!hasInteracted) {
                    mEntity.attack();
                }
            }
        } else {
            hasInteracted = false;
        }
        
        mEntity.applyForce(forceX, forceY);

        if(mLeftPressed || mRightPressed || mJumpPressed) {
            mEntity.setApplyDrag(false);
        } else {
            mEntity.setApplyDrag(true);
        }
        
        if(mLeftPressed) {
            mEntity.setDirection(PhysicalEntity.Direction.LEFT);
        } else if(mRightPressed) {
            mEntity.setDirection(PhysicalEntity.Direction.RIGHT);
        }
        
        if(mSuicidePressed) {
            mSuicidePressed = false;
            if(mEntity.isAlive()) {
                mEntity.kill();
                mEntity.setVisible(false);
            }
        }

    }
}
