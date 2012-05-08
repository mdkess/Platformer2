package ca.kess.games;

import com.badlogic.gdx.backends.android.AndroidApplication;

public class PlatformerActivity extends AndroidApplication {
    @Override
    public void onCreate (android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize(new Platformer(), true);
    }

}