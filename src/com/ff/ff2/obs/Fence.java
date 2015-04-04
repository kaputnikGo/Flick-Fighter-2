package com.ff.ff2.obs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.lib.GraphicsManager;

public class Fence extends Obstacle {	

	public Fence(Vector2 position) {
		super(position, GraphicsManager.getGraphicsManager().fenceTexture);
	}
	
	
	@Override
	public void swapTexture(Texture texture) {
		this.texture = texture;	
	}
	
	public void swapTexture() {
		this.texture = GraphicsManager.getGraphicsManager().fencehitTexture;
		this.sprite = new Sprite(this.texture);
	}
	
	public void damage(float amount) {
		hitPoints = Math.max(hitPoints - amount,  0);
		
		if (hitPoints <= 10) 
			swapTexture();
		if (hitPoints <= 0) 
			alive = false;
	}

	public void draw(SpriteBatch spriteBatch, float deltaTime) {
		super.draw(spriteBatch, deltaTime);
	}
}