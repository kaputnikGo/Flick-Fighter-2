package com.ff.ff2.obs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.lib.GraphicsManager;

public class Scaffold extends Obstacle {	
	public boolean alive = true;
	public float hitPoints;

	public Scaffold(Vector2 position) {	
		super(position, GraphicsManager.getGraphicsManager().scaffoldTexture);
	}
	
	@Override
	public void swapTexture(Texture texture) {
		this.texture = texture;	
	}
	
	public void swapTexture() {	
		// no swap
	}
	
	public void damage(float amount) {
		// is indestructable
	}
}