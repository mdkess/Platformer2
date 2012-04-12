package ca.kess.games.timers;
import ca.kess.games.entities.GameEntity;
import ca.kess.games.interfaces.IUpdateable;


public abstract class Timer implements IUpdateable {
    private final float mDuration;
    private float mElapsedTime;
    private boolean mRunning;
    private final GameEntity mEntity;
    public Timer(GameEntity entity, float duration) {
        mDuration = duration;
        mRunning = true;
        mElapsedTime = 0.0f;
        mEntity = entity;
    }
    
    public GameEntity getEntity() { return mEntity; }
    public float getElapsedTime() { return mElapsedTime; }
    public final float getDuration() { return mDuration; }

    public float getPercentTimeElapsed() { return mElapsedTime / mDuration; }

    @Override
    public final void update(float delta) {
        if(!mRunning) {
            return;
        }
        
        mElapsedTime += delta;
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
