package ca.kess.games.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ItemBoxTile extends Tile {
    /**
     * An item box tile is a tile which creates an item when it is triggered. Think super mario coin box.
     * @param blocksMovement
     */
    ItemBoxTile(boolean blocksMovement, float staticFriction, float kineticFriction) {
        super(blocksMovement, staticFriction, kineticFriction);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void render(SpriteBatch b, int x, int y) {
        // TODO Auto-generated method stub

    }

}
