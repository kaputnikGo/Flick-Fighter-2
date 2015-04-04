package com.ff.ff2.obs;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.lib.GraphicsManager;

public class Bombup extends Obstacle {
	
	public Bombup(Vector2 position) {	
		super(position, GraphicsManager.getGraphicsManager().bombupTexture);
	}
	
	@Override
	public void swapTexture(Texture texture) {
		this.texture = texture;	
	}
	
	public void swapTexture() {
		// TODO Auto-generated method stub		
	}
	
	public void damage(float amount) {
		// currently is indestructable
		// but will disappear upon fly-over
		// for powering up player
	}
	
	public void draw(SpriteBatch spriteBatch, float deltaTime) {
		super.draw(spriteBatch, deltaTime);
	}
}