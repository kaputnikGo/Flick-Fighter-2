package com.ff.ff2.obs;

//import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.lib.GraphicsManager;

public class Wordblock extends Obstacle {
	public int wordNum;

	public Wordblock(int wordNum, Vector2 position) {	
		super(position, GraphicsManager.getGraphicsManager().getIDWordTexture(wordNum, false));
		this.wordNum = wordNum;
		
		// need to override the sizes of default obstacle
		this.width = texture.getWidth();
		this.height = texture.getHeight();
		this.boundBox.set(position.x, position.y, width, height);
	}
	
	public void damage(float amount) {
		hitPoints = Math.max(hitPoints - amount,  0);
		if (hitPoints <= 10) 
			swapTexture();
		if (hitPoints <= 0) 
			alive = false;
	}
	
	@Override
	public void swapTexture() {	
		this.texture = GraphicsManager.getGraphicsManager().getIDWordTexture(this.wordNum, true);
		this.sprite = new Sprite(texture);
	}
	
	public void swapTexture(Texture newTexture) {
		this.texture = newTexture;
	}
	
	public void draw(SpriteBatch spriteBatch, float deltaTime) {
		super.draw(spriteBatch, deltaTime);
	}
}