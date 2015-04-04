package com.ff.ff2.obs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.eng.FFModel;
import com.ff.ff2.lib.GraphicsManager;

public class Gateway extends Obstacle {
	private static final float PAUSE = 0.25f;
	private boolean flip = false;
	private float autoTime = 0;
	public boolean activated = false;


	public Gateway(Vector2 position) {	
		super(position, GraphicsManager.getGraphicsManager().gatewayoffTexture);
	}
	
	public void damage(float amount) {
		// is indestructable
	}
	
	@Override
	public void swapTexture(Texture texture) {
		this.texture = texture;	
		this.sprite = new Sprite(texture);
		this.activated = FFModel.getFFModel().field.gatewayActivated;
	}
	
	public void swapTexture() {
		//
	}
	
/*****************************************************************/
	
	private void autoAnimate(float deltaTime) {
		autoTime += deltaTime;
	    if (autoTime >= PAUSE) {
	    	animate();
	        // Reset timer (not set to 0)
	        autoTime -= PAUSE;
	    }
	}
	
	private void animate() {
		if (flip) {
			this.texture = GraphicsManager.getGraphicsManager().gatewayoffTexture;
			this.sprite = new Sprite(this.texture);
		}
		else {
			this.texture = GraphicsManager.getGraphicsManager().gatewayonTexture;
			this.sprite = new Sprite(this.texture);
		}
		flip ^= true;
	}
	
/*****************************************************************/
	
	public void draw(SpriteBatch spriteBatch, float deltaTime) {
		if (this.activated) {
			autoAnimate(deltaTime);
		}
		super.draw(spriteBatch, deltaTime);
	}
}