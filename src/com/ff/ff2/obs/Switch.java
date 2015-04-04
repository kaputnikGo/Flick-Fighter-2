package com.ff.ff2.obs;

//import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.lib.GraphicsManager;

public class Switch extends Obstacle {

	public Switch(Vector2 position) {	
		super(position, GraphicsManager.getGraphicsManager().switchoffTexture);
	}
	
	public void damage(float amount) {
		// indestructable
	}
	
	@Override
	public void swapTexture() {
		// TODO Auto-generated method stub	
	}
	
	public void swapTexture(Texture texture) {
		this.texture = texture;
		this.sprite = new Sprite(texture);
	}
	
	public void draw(SpriteBatch spriteBatch, float deltaTime) {
		super.draw(spriteBatch, deltaTime);
	}
}