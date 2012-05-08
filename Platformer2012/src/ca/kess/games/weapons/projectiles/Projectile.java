package ca.kess.games.weapons.projectiles;

import ca.kess.games.entities.PhysicalEntity;

public abstract class Projectile extends PhysicalEntity {
    @Override
    public void update() {
        super.update();
        for(PhysicalEntity entity : getWorld().getCollisions(this, true)) {
            if(entity.canBeDamaged()) {
                handleCollision(entity);
            }
        }
    }
    
    public abstract void handleCollision(PhysicalEntity entity);
}
