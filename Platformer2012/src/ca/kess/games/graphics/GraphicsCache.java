package ca.kess.games.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GraphicsCache {
    private static Texture sCharTexture = new Texture(Gdx.files.internal("data/lofi_char.png"));
    private static Texture sEnvironmentTexture = new Texture(Gdx.files.internal("data/lofi_environment.png"));
    private static Texture sObjectTexture = new Texture(Gdx.files.internal("data/lofi_obj.png"));
    private static Texture sInterfaceTexture = new Texture(Gdx.files.internal("data/lofi_interface.png"));
    
    public static TextureRegion getEnvironment(int x, int y) {
    	if(sEnvironmentTexture == null) {
    		sEnvironmentTexture = new Texture(Gdx.files.internal("data/lofi_environment.png"));
    	}
    	return new TextureRegion(sEnvironmentTexture, 8*x, 8*y, 8, 8);
    }
    
    public static TextureRegion getObject(int x, int y) {
    	if(sObjectTexture == null) {
    		sObjectTexture = new Texture(Gdx.files.internal("data/lofi_obj.png"));
    	}
    	return new TextureRegion(sObjectTexture, 8*x, 8*y, 8, 8);
    }
    public static TextureRegion getInterface(int x, int y) {
    	if(sInterfaceTexture == null) {
    		sInterfaceTexture = new Texture(Gdx.files.internal("data/lofi_interface.png"));
    	}
    	return new TextureRegion(sInterfaceTexture, 8*x, 8*y, 8, 8);
    }

    public static TextureRegion getChar(int x, int y) {    	
    	return getChar(x, y, 1, 1);
    }
    public static TextureRegion getChar(int x, int y, int w, int h) {
    	if(sCharTexture == null) {
    		sCharTexture = new Texture(Gdx.files.internal("data/lofi_char.png"));
    	}
        return new TextureRegion(sCharTexture, 8*x, 8*y, 8*w, 8*h);
    }
    
    public static void clear() {
    	if(sCharTexture != null)
    		sCharTexture.dispose();
    	if(sEnvironmentTexture != null)
    		sEnvironmentTexture.dispose();
    	if(sObjectTexture != null)
    		sObjectTexture.dispose();
    	if(sInterfaceTexture != null)
    		sInterfaceTexture.dispose();
    	
    	sCharTexture = sEnvironmentTexture = sObjectTexture = sInterfaceTexture = null;
    }
    
    private GraphicsCache() { }
}
