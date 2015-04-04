package com.ff.ff2.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.lib.GraphicsManager;

public class FFSplash extends Sprite {
	// splash id
	public static final int SPLASH_TITLE = 0;
	public static final int SPLASH_NEXT_FIELD = 1;
	public static final int SPLASH_GAME_OVER = 2;
	public static final int SPLASH_CREATOR = 3;
	
	public Vector2 position = new Vector2();	
	private Texture texture;
	private float width;
	private float height;
	public int id;
	
	public FFSplash(Vector2 position, int id) {
		super();	
		this.position = position;
		this.id = id;
		this.width = FFScreen.splashSize;
		this.height = FFScreen.splashSize;
		loadTexture();
	}
	
	private void loadTexture() {		
		switch (id) {
		case SPLASH_TITLE:
			this.texture = GraphicsManager.getGraphicsManager().loaderTexture;
			break;
		case SPLASH_NEXT_FIELD:
			this.texture = GraphicsManager.getGraphicsManager().nextFieldTexture;
			break;
		case SPLASH_GAME_OVER:
			this.texture = GraphicsManager.getGraphicsManager().gameOverTexture;
			break;
		case SPLASH_CREATOR:
			this.texture = GraphicsManager.getGraphicsManager().createSplashTexture;
			break;
		}
	}
	
	public void draw(SpriteBatch spriteBatch) {
		spriteBatch.draw(texture, position.x, position.y, width, height);
	}
}