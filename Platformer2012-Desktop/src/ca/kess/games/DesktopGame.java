package ca.kess.games;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class DesktopGame {
    public static void main(String[] args) {
        new LwjglApplication(new Platformer(), "Dungeonpants", 800, 608, true);
    }
}
