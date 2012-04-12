package ca.kess.games;

import ca.kess.games.screens.GameScreen;

import com.badlogic.gdx.Game;

public class Platformer extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }
}
