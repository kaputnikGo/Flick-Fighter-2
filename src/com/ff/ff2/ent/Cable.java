package com.ff.ff2.ent;


//import android.util.Log;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.eng.FFModel;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.lib.SoundManager;

public class Cable extends Weapon {
	//private static final String TAG = Cable.class.getSimpleName(); 
	
	public Cable(int id, Vector2 position, Vector2 target) {
		super(id, position, target, 10); // damage = 10
		if (id == IdManager.MISSILE_BOMB) damage = 40;
		this.diameter = FFScreen.gameCellSize / 8;
		// used to offset weapon shooting from entity, so as not to shoot self
		this.buffer = FFScreen.gameCellSize;
		startFlight();
		
	}
	
/*****************************************************************/
		
	public void updateBoundBox() {
		boundBox.set(position.x, position.y, diameter, diameter);
	}
	
	public void collidedPosition(Vector2 collider) {
		updateCollideAxis(collider);
		bounce();
	}
	
	
/*****************************************************************/	
	
	private void startFlight() {
		velocity.set(target.x - position.x, 
				target.y - position.y).nor().scl(Math.min(position.dst(target.x, target.y), speed));		
		direction.set(target.x - position.x, target.y - position.y).nor();
		SoundManager.playCable(id);
		position.set(position.x + (buffer * direction.x), 
				position.y + (buffer * direction.y));
		updateBoundBox();
	}
	
	private void bounce() {
		//collideAxis refers to axis component collided with
		if (hitAxis.equals("t")) {
			// hit top
			position.y -= diameter;
			velocity.y *= -1;
			direction.y *= -1;
		}
		else if (hitAxis.equals("b")) {
			// hit bottom
			position.y += diameter;
			velocity.y *= -1;
			direction.y *= -1;
		}
		else if (hitAxis.equals("l")) {
			// hit left
			position.x -= diameter;
			velocity.x *= -1;
			direction.x *= -1;
		}
		else if (hitAxis.equals("r")) {
			// hit right
			position.x += diameter;
			velocity.x *= -1;
			direction.x *= -1;
		}
		else {
			// uh-oh, is n?
		}	
	}
	
	private void updateCollideAxis(Vector2 collider) {	
		// center the positions to the COG
		// for hopefully accurate theta...
		float deltaX = (position.x) - (collider.x + 16);
		float deltaY = (position.y) - (collider.y - 16);

		float theta = (float)(180.0 / Math.PI * Math.atan2(deltaX, deltaY));
		
		if (theta <= 45 && theta > -45) {
			// hit bottom
			hitAxis = "b";
		}
		else if (theta > 45 && theta < 135) {
			// hit right
			hitAxis = "r";
		}
		else if (theta <= -45 && theta > -135) {
			// hit left
			hitAxis = "l";
		}
		else {
			// hit top, hopefully
			hitAxis = "t";
		}
	}
	
	@Override
	public void flight(float deltaTime) {
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);	
		velocity.scl(1 - (0.98f * deltaTime));
		// add wobble here
		position.x += MathUtils.random(0, 20 * direction.x);
		position.y += MathUtils.random(0, 20 * direction.y);
		
		updateBoundBox();
		// kill at edge of screen
		if (position.x <= 0 || position.y <= 0
				|| position.x >= FFScreen.gameWidth || position.y >= FFScreen.gameHeight) {
			alive = false;
		}
		else {
			// add sparks for the flight
			FFModel.getFFModel().particleEmitter.scaledPowerupCharge(position, 4.f, id);
		}
	}
	
/*****************************************************************/
	
	public void draw(SpriteBatch spriteBatch, float deltaTime) {
		if (alive == false) 
			return;
		else {
			flight(deltaTime);
			spriteBatch.draw(sprite, position.x, position.y, diameter, diameter);
		}	
	}
}