package ca.kess.games.weapons;

import ca.kess.games.entities.ActorEntity;
import ca.kess.games.entities.PhysicalEntity.Direction;
import ca.kess.games.graphics.GraphicsCache;
import ca.kess.games.weapons.projectiles.SimpleArrow;

import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * The bow fires arrows in a straight line. The arrows stick into the wall that they hit.
 */
public class Bow extends Weapon {

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public float cooldownTime() {
        return 0.1f;
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    protected void fire(ActorEntity entity) {
        //Create an arrow projectile.
        float px = entity.getPositionX();
        float py = entity.getPositionY();
        float vx = entity.getDirection() == Direction.LEFT ? -20 : 20;
        float vy = 20;
        SimpleArrow arrow = SimpleArrow.GetActorEntity().initialize(entity, entity.getWorld(), px, py, vx, 0, 1, 1, 1, new Animation(1.0f, GraphicsCache.getObject(12, 5)));
        //GravityArrow arrow = GravityArrow.GetActorEntity().initialize(entity, entity.getWorld(), px, py, vx, vy, 1, 1, 1, new Animation(1.0f, GraphicsCache.getObject(12, 5)));
        arrow.setDirection(entity.getDirection());
        arrow.setMaximumSpeed(20, 20);
        entity.getWorld().addEntity(arrow);
    }

}
