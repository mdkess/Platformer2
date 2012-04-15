package ca.kess.games.timers;

import ca.kess.games.entities.PhysicalEntity;

public class DeathFadeTimer extends FadeOutTimer {
    public DeathFadeTimer(PhysicalEntity entity, float duration) {
        super(entity, duration);
    }

    @Override
    public void onFinish() {
        getEntity().recycle();
        super.onFinish();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }

}
