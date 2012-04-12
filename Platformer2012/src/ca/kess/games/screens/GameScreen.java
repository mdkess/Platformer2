package ca.kess.games.screens;

import java.util.Random;

import ca.kess.games.Constants;
import ca.kess.games.entities.ChestEntity;
import ca.kess.games.entities.GameEntity;
import ca.kess.games.graphics.GraphicsCache;
import ca.kess.games.input.InputHandler;
import ca.kess.games.ui.GameButton;
import ca.kess.games.world.WorldLevel;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

public class GameScreen extends PlatformerScreen {
    private SpriteBatch mSpriteBatch;
    private SpriteBatch mDebugSpriteBatch;
    private OrthographicCamera mCamera;
    private WorldLevel mWorldLevel;
    private final GameEntity mHero;
    private Random mRandom = new Random();
    private final Stage mStage;
    
    private final BitmapFont mFont;
    
    
    private final Button mLeftButton;
    
    private final Button mRightButton;
    private final Button mJumpButton;
    
    private final Button mInteractButton;
    private final Button mSuicideButton;
    private final Button mReviveButton;
    
    //private List<Button> mButtons;
    public GameScreen(Game game) {
        super(game);
        mFont = new BitmapFont();
        
        Gdx.app.log(Constants.LOG, "GameScreen::GameScreen()");
        mSpriteBatch = new SpriteBatch();
        mDebugSpriteBatch = new SpriteBatch();
        mStage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true) {

            @Override
            public boolean keyDown(int keycode) {
                if(keycode == Keys.A || keycode == Keys.LEFT){
                    mHero.getInputHandler().setLeftPressed(true);
                }
                if(keycode == Keys.D || keycode == Keys.RIGHT){
                    mHero.getInputHandler().setRightPressed(true);
                }
                if(keycode == Keys.W || keycode == Keys.UP){
                    mHero.getInputHandler().setJumpPressed(true);
                }
                if(keycode == Keys.E){
                    mHero.getInputHandler().setInteractPressed(true);
                }
                if(keycode == Keys.R){
                    mHero.getInputHandler().setResetPressed(true);
                }
                if(keycode == Keys.SPACE) {
                    mHero.getInputHandler().setSuicidePressed(true);
                }
                
                return super.keyDown(keycode);
            }

            @Override
            public boolean keyUp(int keycode) {
                if(keycode == Keys.A || keycode == Keys.LEFT){
                    mHero.getInputHandler().setLeftPressed(false);
                }
                if(keycode == Keys.D || keycode == Keys.RIGHT){
                    mHero.getInputHandler().setRightPressed(false);
                }
                if(keycode == Keys.W || keycode == Keys.UP){
                    mHero.getInputHandler().setJumpPressed(false);
                }
                if(keycode == Keys.E){
                    mHero.getInputHandler().setInteractPressed(false);
                }
                if(keycode == Keys.R) {
                    mHero.getInputHandler().setResetPressed(false);
                }
                if(keycode == Keys.SPACE) {
                    mHero.getInputHandler().setSuicidePressed(false);
                }
                return super.keyUp(keycode);
            }

        };
        
        
        mCamera = new OrthographicCamera(Gdx.graphics.getWidth()/(8*Constants.ZOOM_FACTOR), Gdx.graphics.getHeight()/(8*Constants.ZOOM_FACTOR));
        mCamera.position.set(0, 0, 0);
        
        mWorldLevel = new WorldLevel(this, "data/map.png");
        
        mHero = mWorldLevel.getGameEntityPool().getGameEntity().initialize(new Animation(0.0f, GraphicsCache.getChar(10,0)), mWorldLevel, 0);
        //mHero.getAABB().set(2,2);
        mHero.getPosition().set(2, 2);
        InputHandler inputHandler = new InputHandler(mHero);
        mHero.setInputHandler(inputHandler);
        
        for(int i = 0; i < 128; i += 16) {
            GameEntity obj = mWorldLevel.getGameEntityPool().getGameEntity().initialize(
                    new Animation(0.5f, GraphicsCache.getObject(mRandom.nextInt(14), mRandom.nextInt(14))), mWorldLevel, 0.5f);
                    //new Animation(0.1f,
                    //        GraphicsCache.getObject(0, 7),
                    //        GraphicsCache.getObject(1, 7),
                    //        GraphicsCache.getObject(2, 7),
                    //        GraphicsCache.getObject(1, 7))
                    //    , mWorldLevel, 1f);
            
            //TODO: Add chest to entity pool
            ChestEntity chest = mWorldLevel.getGameEntityPool().getChestEntity().initialize(
                    new Animation(0.0f, GraphicsCache.getObject(1, 0)),
                    new Animation(0.0f, GraphicsCache.getObject(2, 0)), mWorldLevel, 0.5f, obj);
            chest.getPosition().set(i, 100);
            mWorldLevel.addEntity(chest);
        }
        mWorldLevel.addEntity(mHero);
        TextureRegion flip = new TextureRegion(GraphicsCache.getInterface(5, 2));
        flip.flip(true, false);
        
        mLeftButton = new GameButton(flip) {
            @Override
            public boolean touchDown(float x, float y, int pointer) {
            	Gdx.app.log(Constants.LOG, "LeftButton::touchDown(" + pointer + ")");
                mHero.getInputHandler().setLeftPressed(true);
                return true;
                //return super.touchDown(x, y, pointer);
            }
            
            @Override
            public void touchUp(float x, float y, int pointer) {
            	Gdx.app.log(Constants.LOG, "LeftButton::touchUp(" + pointer + ")");
                mHero.getInputHandler().setLeftPressed(false);
                //super.touchUp(x, y, pointer);
            }
        };
        
        mRightButton = new GameButton(GraphicsCache.getInterface(5,2)) {
            public boolean touchDown(float x, float y, int pointer) {
                Gdx.app.log(Constants.LOG, "RightButton::touchDown(" + pointer + ")");
                mHero.getInputHandler().setRightPressed(true);
                return true;
                //return super.touchDown(x, y, pointer);
            }
            @Override
            public void touchUp(float x, float y, int pointer) {
            	Gdx.app.log(Constants.LOG, "RightButton::touchUp(" + pointer + ")");
                mHero.getInputHandler().setRightPressed(false);
                //super.touchUp(x, y, pointer);
            }
        };

        
        mJumpButton = new GameButton(GraphicsCache.getInterface(2, 1)) {
            public boolean touchDown(float x, float y, int pointer) {
                mHero.getInputHandler().setJumpPressed(true);
                return true;
                //return super.touchDown(x, y, pointer);
            }
            @Override
            public void touchUp(float x, float y, int pointer) {
                mHero.getInputHandler().setJumpPressed(false);
                //super.touchUp(x, y, pointer);
            }
        };

        mInteractButton = new GameButton(GraphicsCache.getInterface(2, 0)) {
            public boolean touchDown(float x, float y, int pointer) {
                mHero.getInputHandler().setInteractPressed(true);
                return true;
                //return super.touchDown(x, y, pointer);
            }
            @Override
            public void touchUp(float x, float y, int pointer) {
                mHero.getInputHandler().setInteractPressed(false);
                //super.touchUp(x, y, pointer);
            }
        };
        
        mSuicideButton = new GameButton(GraphicsCache.getObject(3, 1)) {
            public boolean touchDown(float x, float y, int pointer) {
                mHero.getInputHandler().setSuicidePressed(true);
                return true;
                //return super.touchDown(x, y, pointer);
            }
            @Override
            public void touchUp(float x, float y, int pointer) {
                mHero.getInputHandler().setSuicidePressed(false);
                //super.touchUp(x, y, pointer);
            }
        };
        mReviveButton = new GameButton(GraphicsCache.getInterface(9, 1)) {
            public boolean touchDown(float x, float y, int pointer) {
                mHero.getInputHandler().setResetPressed(true);
                return super.touchDown(x, y, pointer);
            }
            @Override
            public void touchUp(float x, float y, int pointer) {
                mHero.getInputHandler().setResetPressed(false);
                super.touchUp(x, y, pointer);
            }
        };

        
        initGUI();
        
        mStage.addActor(mLeftButton);
        mStage.addActor(mRightButton);
        mStage.addActor(mJumpButton);
        mStage.addActor(mInteractButton);
        mStage.addActor(mSuicideButton);
        mStage.addActor(mReviveButton);
        
        Gdx.input.setInputProcessor(mStage);

    }
    
    private void initGUI() {
    	Gdx.app.log(Constants.LOG, "PlatformerScreen::initGUI");
        int size = 128;
        
        mLeftButton.x = 8;
        mLeftButton.y = 8;
        mLeftButton.width = size;
        mLeftButton.height = size;
        
        mRightButton.x = 8 + size + 16;
        mRightButton.y = 8;
        mRightButton.width = size;
        mRightButton.height = size;
        
        mJumpButton.x = Gdx.graphics.getWidth() - size - 8;
        mJumpButton.y = 8;
        mJumpButton.width = size;
        mJumpButton.height = size;
        
        mInteractButton.x = Gdx.graphics.getWidth() - size - 8;
        mInteractButton.y = 8 + size;
        mInteractButton.width = size;
        mInteractButton.height = size;
        
        mSuicideButton.x = 8;
        mSuicideButton.y = Gdx.graphics.getHeight() - size - 8;
        mSuicideButton.width = size;
        mSuicideButton.height = size;
        
        mReviveButton.x = Gdx.graphics.getWidth() - size - 8;
        mReviveButton.y = Gdx.graphics.getHeight() - size - 8;
        mReviveButton.width = size;
        mReviveButton.height = size;
    }
        
        
    public OrthographicCamera getCamera() { return mCamera; }
    
    private float mTotalTime = 0.0f;
    private float mAccumulator = 0.0f;
    @Override
    public void render(float delta) {
        delta = (float) Math.min(delta, 1.0/30.0f);
        mAccumulator += delta;
        //for(Gdx.input.n)
        int iters = 0;
        while(mAccumulator >= Constants.PHYSICS_DELTA) {
            ++iters;
            mWorldLevel.update(Constants.PHYSICS_DELTA);
            mAccumulator -= Constants.PHYSICS_DELTA;
            if(iters > Constants.SKIP_FRAMES) {
                Gdx.app.error(Constants.LOG, "Skipped after " + iters + " updates. Delta: " + delta + " Accumulator: " + mAccumulator);
                mAccumulator = 0;
            }
        }
        
        mStage.act(delta);
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
        Vector2 pos = mHero.getPosition();
        mCamera.position.set(pos.x, pos.y, 0);
        
        mCamera.update();
        
        mTotalTime += Gdx.graphics.getDeltaTime();
        
        mSpriteBatch.setProjectionMatrix(mCamera.combined);
        mSpriteBatch.begin();
        mWorldLevel.render(mSpriteBatch);
        mSpriteBatch.end();
        
        mStage.draw();
        
        drawDebugInfo();

    }
    private void drawDebugInfo() {
        //Draw some debug info.
        Runtime rt = Runtime.getRuntime();
        mDebugSpriteBatch.begin();
        mFont.draw(mDebugSpriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 200);
        mFont.draw(mDebugSpriteBatch, "Free mem: " + rt.freeMemory(), 10, 220);
        mFont.draw(mDebugSpriteBatch, "Total mem: " + rt.totalMemory(), 10, 240);
        long usedMem = rt.totalMemory() - rt.freeMemory();
        mFont.draw(mDebugSpriteBatch, "Used mem: " + (usedMem), 10, 260);
        mFont.draw(mDebugSpriteBatch, "Used mem: " + String.format("%.5g%n%%", ((float)usedMem / (float)rt.totalMemory())*100.0), 10, 280);
        mFont.draw(mDebugSpriteBatch, "Speed: " + mHero.getVelocityX(), 10, 300);
        mFont.draw(mDebugSpriteBatch, "On Ground: " + mHero.isOnGround(), 10, 320);
        mDebugSpriteBatch.end();
    }
    
    private void drawUserInterface() {
        /*
        mGUISpriteBatch.begin();
        for(Button button : mButtons) {
            button.render(mGUISpriteBatch);
        }
        mGUISpriteBatch.end();
        */
    }
    
    @Override
    public void resize(int width, int height) {
        Gdx.app.log(Constants.LOG, "GameScreen::resise(" + width + ", " + height + ")");
        mStage.setViewport(width, height, true);
        initGUI();
        mCamera.setToOrtho(false, Gdx.graphics.getWidth()/(8*Constants.ZOOM_FACTOR), Gdx.graphics.getHeight()/(8*Constants.ZOOM_FACTOR));
    }
    
    @Override
    public void dispose() {
        Gdx.app.log(Constants.LOG, "GameScreen::dispose");
        super.dispose();
        mStage.dispose();
        mSpriteBatch.dispose();
        GraphicsCache.clear();
        mWorldLevel.dispose();
    }
    
    @Override
    public void show() {
    	Gdx.app.log(Constants.LOG, "GameScreen::show");
    }

    @Override
    public void hide() {
    	Gdx.app.log(Constants.LOG, "GameScreen::hide");
        dispose();
    }


}
