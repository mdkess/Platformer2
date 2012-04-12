package ca.kess.games.util;

import ca.kess.games.entities.GameEntity;

import com.badlogic.gdx.math.Rectangle;

public class QuadTree <Key, Value> {
    private class Node {
        Value value;
        
        Rectangle position;
        
        Node NW;
        Node NE;
        Node SW;
        Node SE;
    }
    
    public void insert(GameEntity entity) {
        
    }
}
