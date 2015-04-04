package com.ff.ff2.obs;

//import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.lib.GraphicsManager;

public class Mine extends Obstacle {

	public Mine(Vector2 position) {	
		super(position, GraphicsManager.getGraphicsManager().mineTexture);
	}
	
	public void damage(float amount) {
		hitPoints = Math.max(hitPoints - amount,  0);
		
		if (hitPoints <= 20) 
			swapTexture();
		if (hitPoints <= 0) {
			alive = false;
		}
	}
	
	@Override
	public void swapTexture() {
		this.texture = GraphicsManager.getGraphicsManager().minehitTexture;
		this.sprite = new Sprite(this.texture);
	}
	
	public void swapTexture(Texture texture) {
		this.texture = texture;
	}
	
	public void draw(SpriteBatch spriteBatch, float deltaTime) {
		super.draw(spriteBatch, deltaTime);
	}
}