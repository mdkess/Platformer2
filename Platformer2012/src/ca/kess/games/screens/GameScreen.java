package ca.kess.games.screens;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import ca.kess.games.Constants;
import ca.kess.games.camera.GameCamera;
import ca.kess.games.camera.MarioCamera;
import ca.kess.games.entities.ActorEntity;
import ca.kess.games.entities.ChestEntity;
import ca.kess.games.entities.PhysicalEntity;
import ca.kess.games.graphics.GraphicsCache;
import ca.kess.games.input.EntityController;
import ca.kess.games.input.KeyboardInputHandler;
import ca.kess.games.input.SimpleAIController;
import ca.kess.games.ui.GameButton;
import ca.kess.games.weapons.MeleeWeapon;
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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

public class GameScreen extends PlatformerScreen {
    private SpriteBatch mSpriteBatch;
    private SpriteBatch mDebugSpriteBatch;
    private GameCamera mCamera;
    private WorldLevel mWorldLevel;
    private final ActorEntity mHero;
    private Random mRandom = new Random();
    private final Stage mStage;
    
    private final BitmapFont mFont;
    
    
    private final Button mLeftButton;
    
    private final Button mRightButton;
    private final Button mJumpButton;
    
    private final Button mInteractButton;
    private final Button mSuicideButton;
    private final Button mReviveButton;
    
    private final List<EntityController> mControllers = new LinkedList<EntityController>();
    private final List<KeyboardInputHandler> mKeyboardListeners = new LinkedList<KeyboardInputHandler>();
    public GameScreen(Game game) {
        super(game);
        mFont = new BitmapFont();
        
        Gdx.app.log(Constants.LOG, "GameScreen::GameScreen()");
        mSpriteBatch = new SpriteBatch();
        mDebugSpriteBatch = new SpriteBatch();
        mStage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true) {
            @Override
            public boolean keyDown(int keycode) {
                for(KeyboardInputHandler inputHandler : mKeyboardListeners) {
                    if(keycode == Keys.A || keycode == Keys.LEFT){
                        inputHandler.setLeftPressed(true);
                    } else if(keycode == Keys.D || keycode == Keys.RIGHT){
                        inputHandler.setRightPressed(true);
                    } else if(keycode == Keys.W || keycode == Keys.UP){
                        inputHandler.setJumpPressed(true);
                    } else if(keycode == Keys.E) {
                        inputHandler.setInteractPressed(true);
                    } else if(keycode == Keys.R) {
                        inputHandler.setResetPressed(true);
                    } else if(keycode == Keys.SPACE) {
                        inputHandler.setSuicidePressed(true);
                    }
                }
                return super.keyDown(keycode);
            }

            @Override
            public boolean keyUp(int keycode) {
                for(KeyboardInputHandler inputHandler : mKeyboardListeners) {
                    if(keycode == Keys.A || keycode == Keys.LEFT){
                        inputHandler.setLeftPressed(false);
                    } else if(keycode == Keys.D || keycode == Keys.RIGHT){
                        inputHandler.setRightPressed(false);
                    } else if(keycode == Keys.W || keycode == Keys.UP){
                        inputHandler.setJumpPressed(false);
                    } else if(keycode == Keys.E) {
                        inputHandler.setInteractPressed(false);
                    } else if(keycode == Keys.R) {
                        inputHandler.setResetPressed(false);
                    } else if(keycode == Keys.SPACE) {
                        inputHandler.setSuicidePressed(false);
                    }
                }
                return super.keyUp(keycode);
            }
        };

        mWorldLevel = new WorldLevel(this, "data/map.png");

        mHero = ActorEntity.GetActorEntity().initialize(mWorldLevel,
                2, 2,
                0, 0,
                1, 1,
                1,
                new Animation(0.0f, GraphicsCache.getChar(10,0)));
        registerKeyboardListener(new KeyboardInputHandler(mHero));
        mHero.equipWeapon(new MeleeWeapon());
        mHero.setDamagedOnCollision(true);
        mHero.setCanBeDamaged(true);
        mWorldLevel.addEntity(mHero);
        
        
        ActorEntity monster = ActorEntity.GetActorEntity().initialize(mWorldLevel,
                2, 2,
                0, 0,
                1, 1,
                1,
                new Animation(0.0f, GraphicsCache.getChar(8,8)));
        registerController(new SimpleAIController(monster));
        monster.setDamagesPlayerOnCollision(true);
        monster.setCanBeDamaged(true);
        mWorldLevel.addEntity(monster);
        
        mCamera = new MarioCamera(new OrthographicCamera(Gdx.graphics.getWidth()/(Constants.TILE_SIZE*Constants.ZOOM_FACTOR), Gdx.graphics.getHeight()/(Constants.TILE_SIZE*Constants.ZOOM_FACTOR)), mHero
            , new Rectangle(300, 200, Gdx.graphics.getWidth() - 600, Gdx.graphics.getHeight() - 400));

        for(int i = 0; i < 128; i += 16) {
            PhysicalEntity obj = ActorEntity.GetActorEntity().initialize(mWorldLevel, 0, 0, 0, 0, 1, 1, 1,
                    new Animation(0.1f,
                            GraphicsCache.getObject(0, 7),
                            GraphicsCache.getObject(1, 7),
                            GraphicsCache.getObject(2, 7),
                            GraphicsCache.getObject(1, 7)));
            
            ChestEntity chest = ChestEntity.GetChestEntity().initialize(mWorldLevel,
                    i, 100, 0, 0, 1, 1, 1, 0.5f, 
                    new Animation(0.0f, GraphicsCache.getObject(1, 0)),
                    new Animation(0.0f, GraphicsCache.getObject(2, 0)), obj);
            mWorldLevel.addEntity(chest);
        }

        TextureRegion flip = new TextureRegion(GraphicsCache.getInterface(5, 2));
        flip.flip(true, false);
        
        mLeftButton = new GameButton(flip) {
            @Override
            public boolean touchDown(float x, float y, int pointer) {
                Gdx.app.log(Constants.LOG, "LeftButton::touchDown(" + pointer + ")");
                for(KeyboardInputHandler inputHandler : mKeyboardListeners) {
                    inputHandler.setLeftPressed(true);
                }
                return true;
            }
            
            @Override
            public void touchUp(float x, float y, int pointer) {
                Gdx.app.log(Constants.LOG, "LeftButton::touchUp(" + pointer + ")");
                for(KeyboardInputHandler inputHandler : mKeyboardListeners) {
                    inputHandler.setLeftPressed(false);
                }
            }
        };
        
        mRightButton = new GameButton(GraphicsCache.getInterface(5,2)) {
            @Override
            public boolean touchDown(float x, float y, int pointer) {
                Gdx.app.log(Constants.LOG, "RightButton::touchDown(" + pointer + ")");
                for(KeyboardInputHandler inputHandler : mKeyboardListeners) {
                    inputHandler.setRightPressed(true);
                }
                return true;
                //return super.touchDown(x, y, pointer);
            }
            @Override
            public void touchUp(float x, float y, int pointer) {
                Gdx.app.log(Constants.LOG, "RightButton::touchUp(" + pointer + ")");
                for(KeyboardInputHandler inputHandler : mKeyboardListeners) {
                    inputHandler.setRightPressed(false);
                }
            }
        };

        mJumpButton = new GameButton(GraphicsCache.getInterface(2, 1)) {
            @Override
            public boolean touchDown(float x, float y, int pointer) {
                Gdx.app.log(Constants.LOG, "JumpButton::touchDown(" + pointer + ")");
                for(KeyboardInputHandler inputHandler : mKeyboardListeners) {
                    inputHandler.setJumpPressed(true);
                }
                return true;
            }
            @Override
            public void touchUp(float x, float y, int pointer) {
                Gdx.app.log(Constants.LOG, "JumpButton::touchUp(" + pointer + ")");
                for(KeyboardInputHandler inputHandler : mKeyboardListeners) {
                    inputHandler.setJumpPressed(false);
                }
            }
        };

        mInteractButton = new GameButton(GraphicsCache.getInterface(2, 0)) {
            @Override
            public boolean touchDown(float x, float y, int pointer) {
                Gdx.app.log(Constants.LOG, "InteractButton::touchDown(" + pointer + ")");
                for(KeyboardInputHandler inputHandler : mKeyboardListeners) {
                    inputHandler.setInteractPressed(true);
                }
                return true;
            }
            @Override
            public void touchUp(float x, float y, int pointer) {
                Gdx.app.log(Constants.LOG, "InteractButton::touchUp(" + pointer + ")");
                for(KeyboardInputHandler inputHandler : mKeyboardListeners) {
                    inputHandler.setInteractPressed(false);
                }
            }
        };
        
        mSuicideButton = new GameButton(GraphicsCache.getObject(3, 1)) {
            @Override
            public boolean touchDown(float x, float y, int pointer) {
                Gdx.app.log(Constants.LOG, "SuicideButton::touchDown(" + pointer + ")");
                for(KeyboardInputHandler inputHandler : mKeyboardListeners) {
                    inputHandler.setSuicidePressed(true);
                }
                return true;
            }
            @Override
            public void touchUp(float x, float y, int pointer) {
                Gdx.app.log(Constants.LOG, "SuicideButton::touchUp(" + pointer + ")");
                for(KeyboardInputHandler inputHandler : mKeyboardListeners) {
                    inputHandler.setSuicidePressed(false);
                }
            }
        };
        mReviveButton = new GameButton(GraphicsCache.getInterface(9, 1)) {
            @Override
            public boolean touchDown(float x, float y, int pointer) {
                Gdx.app.log(Constants.LOG, "ReviveButton::touchDown(" + pointer + ")");
                for(KeyboardInputHandler inputHandler : mKeyboardListeners) {
                    inputHandler.setResetPressed(true);
                }
                return super.touchDown(x, y, pointer);
            }
            @Override
            public void touchUp(float x, float y, int pointer) {
                Gdx.app.log(Constants.LOG, "ReviveButton::touchUp(" + pointer + ")");
                for(KeyboardInputHandler inputHandler : mKeyboardListeners) {
                    inputHandler.setResetPressed(false);
                };
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
    
    private void registerKeyboardListener(KeyboardInputHandler inputHandler) {
        mKeyboardListeners.add(inputHandler); 
    }

    private void registerController(EntityController controller) {
        mControllers.add(controller);
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
        
    private float mTotalTime = 0.0f;
    private float mAccumulator = 0.0f;
    @Override
    public void render(float delta) {
        delta = (float) Math.min(delta, 1.0/30.0f);
        mAccumulator += delta;
        //for(Gdx.input.n)
        int iters = 0;
        while(mAccumulator >= Constants.DELTA) {
            ++iters;
            for(KeyboardInputHandler inputHandler : mKeyboardListeners) {
                inputHandler.update();
            }
            for(EntityController controller : mControllers) {
                controller.update();
            }
            mWorldLevel.update();
            mAccumulator -= Constants.DELTA;
            if(iters > Constants.SKIP_FRAMES) {
                Gdx.app.error(Constants.LOG, "Skipped after " + iters + " updates. Delta: " + delta + " Accumulator: " + mAccumulator);
                mAccumulator = 0;
            }
        }
        
        mStage.act(delta);
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
        
        mCamera.update();
        
        mTotalTime += Gdx.graphics.getDeltaTime();
        
        mSpriteBatch.setProjectionMatrix(mCamera.getCombined());
        mSpriteBatch.begin();
        mCamera.render(mSpriteBatch, mWorldLevel);
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
        mFont.draw(mDebugSpriteBatch, "On Wall(L): " + mHero.isOnWallLeft(), 10, 340);
        mFont.draw(mDebugSpriteBatch, "On Wall(R): " + mHero.isOnWallRight(), 10, 360);
        mDebugSpriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log(Constants.LOG, "GameScreen::resise(" + width + ", " + height + ")");
        mStage.setViewport(width, height, true);
        initGUI();
        mCamera.onResize(width, height);
        
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
