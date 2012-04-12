package ca.kess.games.timers;

import ca.kess.games.entities.GameEntity;

public class DeathFadeTimer extends FadeOutTimer {
    public DeathFadeTimer(GameEntity entity, float duration) {
        super(entity, duration);
    }

    @Override
    public void onFinish() {
        getEntity().remove();
        super.onFinish();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }

}