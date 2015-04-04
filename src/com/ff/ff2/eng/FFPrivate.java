package com.ff.ff2.eng;

import android.util.Log;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
//import com.ff.ff2.ail.PrivateAI;
import com.ff.ff2.ent.Fighter;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.Collision;
import com.ff.ff2.lib.DemoManager;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.lib.SoundManager;

/*
 * 
 *  FlickFighter 2 - Private
 *  libGDX port
 *  
 *  min SDK 10 (2.3.3)
 *  
 *  this is the private class to help Model control private fighter
 *  or fortress...coming soon
 *  
 * 
 */

public class FFPrivate {
	private static final String TAG = FFPrivate.class.getSimpleName();
	
	public boolean alive;
	//public PrivateAI privateAI;
	private boolean AIcontrol;
	public Fighter fighter;	
	
	private Vector2 flickVelocity;
	private Vector2 flickTarget;
	
	public boolean flicking;
	public boolean demoControl;
	public boolean bouncer;
	public boolean hitGateway;
	
	public FFPrivate(boolean isPlayerControlled) {
		// isPlayerControlled not yet implemented
	}
	
/*****************************************************************/
	
	public void initFighter(Vector2 startVector) {
		fighter = new Fighter(
				IdManager.PRIVATE_ID, 
				startVector);
		alive = true;
	}
	
	public void reset() {
		alive = true;
		flicking = false;
		demoControl = false;
		AIcontrol = false;
		bouncer = false;
		hitGateway = false;
		flickVelocity = new Vector2();
		flickTarget = new Vector2();
		//if (privateAI != null) privateAI = null;
	}
	
	
	public void loadAI() {
		//this.privateAI = new PrivateAI();
		AIcontrol = true;
	}

	public void powerUp(char type) {
		fighter.swapTexture(type);
	}
	
	public void flicking(float velocityX, float velocityY) {
		SoundManager.playFly();
		flickTarget.set(velocityX, velocityY);

		flickVelocity.set(flickTarget.x - fighter.position.x, 
				flickTarget.y - fighter.position.y).nor().scl(
				Math.min(fighter.position.dst(flickTarget.x, flickTarget.y), 1200.f));

		flicking = true;
	}
	
	public void update(float deltaTime) {
		// auto move player
		if (demoControl) {
			DemoManager.getDemoManager().autoMoveFighter(deltaTime);
		}
		else if (AIcontrol) {
			
		}
		// touch player move
		else if (flicking) {
			flickVelocity = fighter.flick(flickVelocity, deltaTime);			
			
			if (Collision.collisionFighterCheck(fighter))  {
				// update flickVelocity due to bounce
				bouncer = true;
				flickVelocity.set(fighter.velocity);
			}
			
			if (flickVelocity.len2() <= 0 ) {
				flicking = false;
				bouncer = false;
			}	
		}
	}
	
/*****************************************************************/
	
	public void fireWeapon(float targetX, float targetY) {
		fighter.fireWeapon(targetX, targetY);
	}
	
	public void fighterHit() {
		if (fighter.hitPoints <= 0) {
			fighter.alive = false;
			alive = false;
			if (FFScreen.DEBUG) Log.i(TAG, "fighter die!");
		}
	}
	
/*****************************************************************/
	
	public void draw(SpriteBatch spriteBatch) {
		// pass draw to fighter
		if (fighter.alive) fighter.draw(spriteBatch);
	}
}