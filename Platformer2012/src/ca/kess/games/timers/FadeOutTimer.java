package ca.kess.games.timers;

import ca.kess.games.entities.PhysicalEntity;

public class FadeOutTimer extends Timer {

    public FadeOutTimer(PhysicalEntity entity, float duration) {
        super(entity, duration);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onFinish() {
        super.onFinish();
    }

    @Override
    public void onUpdate() {
        getEntity().setAlpha(1.0f - getPercentTimeElapsed());
    }

}
