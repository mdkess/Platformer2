package ca.kess.games.timers;
import ca.kess.games.Constants;
import ca.kess.games.entities.PhysicalEntity;
import ca.kess.games.interfaces.IUpdateable;


public abstract class Timer implements IUpdateable {
    private final float mDuration;
    private float mElapsedTime;
    private boolean mRunning;
    private final PhysicalEntity mEntity;
    public Timer(PhysicalEntity entity, float duration) {
        mDuration = duration;
        mRunning = true;
        mElapsedTime = 0.0f;
        mEntity = entity;
    }
    
    public PhysicalEntity getEntity() { return mEntity; }
    public float getElapsedTime() { return mElapsedTime; }
    public final float getDuration() { return mDuration; }

    public float getPercentTimeElapsed() { return mElapsedTime / mDuration; }

    @Override
    public final void update() {
        if(!mRunning) {
            return;
        }
        
        mElapsedTime += Constants.DELTA;
        if(mElapsedTime >= mDuration) {
            mElapsedTime = mDuration;
            onUpdate();
            onFinish();
            mRunning = false;
        } else {
            onUpdate();
        }
        
    }
    
    public final void start() {
        mRunning = true;
    }
    public final void stop() {
        mRunning = false;
    }
    
    public void onFinish() {
        mEntity.getWorld().removeTimer(this);
    }
    public abstract void onUpdate();
}
