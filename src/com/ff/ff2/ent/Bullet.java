package com.ff.ff2.ent;


//import android.util.Log;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.lib.SoundManager;

public class Bullet extends Weapon {
	//private static final String TAG = Bullet.class.getSimpleName();
	
	public Bullet(int id, Vector2 position, Vector2 target, float damage) {
		super(id, position, target, damage);
		speed += speed;
		this.damage = damage;
		this.diameter = FFScreen.gameCellSize / 4;
		// used to offset weapon shooting from entity, so as not to shoot self
		// weapon starts at fighter/turret cog
		this.buffer = FFScreen.gameCellSize;
		startFlight();
	}
	
/*****************************************************************/
	
	public void updateBoundBox() {
		boundBox.set(position.x, position.y, diameter, diameter);
	}
	
	@Override
	public void collidedPosition(Vector2 collider) {
		// TODO Auto-generated method stub
		
	}
	
/*****************************************************************/
	
	private void startFlight() {
		/*
         * Get the normalized direction vector from position to target. 
         * Then scale that value to desired speed. 
         * Use the distance of the target 
         * from the current position to determine how fast to
         * shoot the bullet, and limiting to a maximum speed. 
         * We will apply velocity in the fight method.
         */
		if (id == IdManager.TURRET_BULLET) {
			//slow, limited range for turret
			velocity.set(target.x - position.x, 
				target.y - position.y).nor().scl(Math.min(position.dst(target.x, target.y), speed));
		}
		else { 
			// standard for drone, private, mine
			direction.set(target).sub(position).nor();
			velocity.set(direction).scl(speed);

		}
		SoundManager.playBullet(id);
		position.set(position.x + (buffer * direction.x), 
				position.y + (buffer * direction.y));
		
		updateBoundBox();
	}
	
	public void flight(float deltaTime) {
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
		velocity.scl(1 - (0.98f * deltaTime));
		updateBoundBox();
		
		// kill if stopped moving
		if (velocity.len2() < 1000.f) {
			alive = false;
		}
		// kill at edge of screen
		if (position.x <= 0 || position.y <= 0
				|| position.x >= FFScreen.gameWidth || position.y >= FFScreen.gameHeight) {
			alive = false;
		}
		
		if (!alive) {
			//TODO audio
			//SoundManager.playCrash(); // need better
		}
	}
	
/*****************************************************************/
	
	public void draw(SpriteBatch spriteBatch, float deltaTime) {
		if (!alive) {
			return;
		}
		else {
			flight(deltaTime);
			spriteBatch.draw(sprite, position.x, position.y, diameter, diameter);
		}	
	}
}