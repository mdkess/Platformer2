package ca.kess.games.screens;

import ca.kess.games.Constants;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

public abstract class PlatformerScreen implements Screen {
    private Game mGame;
    public PlatformerScreen(Game game) {
        mGame = game;
    }
    
    public Game getGame() { return mGame; }

    @Override
    public void render(float delta) {
        // TODO Auto-generated method stub
    	Gdx.app.log(Constants.LOG, "PlatformerScreen::render");
        
    }

    @Override
    public void resize(int width, int height) {
    	Gdx.app.log(Constants.LOG, "PlatformerScreen::resize(" + width + "," + height + ")");
        // TODO Auto-generated method stub
        
    }

    @Override
    public void show() {
    	Gdx.app.log(Constants.LOG, "PlatformerScreen::show");
        // TODO Auto-generated method stub
        
    }

    @Override
    public void hide() {
    	Gdx.app.log(Constants.LOG, "PlatformerScreen::hide");
        // TODO Auto-generated method stub
        
    }

    @Override
    public void pause() {
    	Gdx.app.log(Constants.LOG, "PlatformerScreen::pause");
        // TODO Auto-generated method stub
        
    }

    @Override
    public void resume() {
    	Gdx.app.log(Constants.LOG, "PlatformerScreen::resume");
        // TODO Auto-generated method stub
        
    }

    @Override
    public void dispose() {
    	Gdx.app.log(Constants.LOG, "PlatformerScreen::dispose");
        // TODO Auto-generated method stub
        
    }
}
