package com.ff.ff2.ent;


import android.util.Log;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.ail.Pilot;
import com.ff.ff2.eng.FFModel;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.CollisionUtils;
import com.ff.ff2.lib.GraphicsManager;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.lib.SoundManager;


public class Fighter extends Sprite {
	private static final String TAG = Fighter.class.getSimpleName();
	public Pilot pilot;
	
	public Vector2 position = new Vector2();
	public Vector2 velocity = new Vector2();
	public Rectangle boundBox = new Rectangle();
	
	private int hitAxis;
	private float radius;
	public float diameter;
	public float bulletDamage;
	public Vector2 cogOffset = new Vector2();
	private float bounceOffset;
	
	public int id;
	public boolean alive;
	public boolean AImoving;
	public char chargedUp;
	
	private int bounceCounter;
	private float bounceCounterTimer;
	public float hitPoints;
	private Texture texture;
	
	public Fighter(int id, Vector2 position) {
		super();
		this.id = id;
		this.position.set(position);
		
		this.velocity.set(0.f, 0.f);
		this.diameter = FFScreen.gameCellSize;
		this.radius = FFScreen.gameCellSize / 2;
		bounceOffset = radius / 2;
		this.cogOffset.set(position.x + radius, 
				position.y + radius);
		
		// shrink boundbox
		this.boundBox.set(position.x, position.y, diameter - 4, diameter - 4);
		
		this.hitAxis = IdManager.UNKNOWN;

		alive = true;
		AImoving = false;
		chargedUp = IdManager.EMPTY;
		bounceCounter = 0;
		bounceCounterTimer = 0;
		
		switch (id) {
			case IdManager.PRIVATE_ID:
				texture = GraphicsManager.getGraphicsManager().playerShipTexture;
				hitPoints = FFScreen.engine.settings.PRIVATE_HP;
				bulletDamage = FFScreen.engine.settings.PRIVATE_DAMAGE;
				break;
			case IdManager.GENERAL_ID:
				texture = GraphicsManager.getGraphicsManager().enemyShipTexture;
				hitPoints = FFScreen.engine.settings.DRONE_HP;
				bulletDamage = FFScreen.engine.settings.DRONE_DAMAGE;
				break;
		}
		
	}
	
/*****************************************************************/
	
	public void loadAI() {
		this.pilot = new Pilot(this);
	}
	
	public void AIflight(float deltaTime) {
		bounceCounterTimer += deltaTime;
		if (bounceCounterTimer >= 1.5) {
			bounceCounter = 0;
			bounceCounterTimer -= 1.5;
		}
		
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
		velocity.scl(1 - (0.98f * deltaTime));
		
		// slow and stop check
        if (velocity.len2() < 500.f) {
			AImoving = false;
			velocity.x = 0;
			velocity.y = 0;
		}
        position = CollisionUtils.withinScreen(position);
        updateBoundBox();
	}
	
	public Vector2 flick(Vector2 flickVelocity, float deltaTime) {
		bounceCounter = 0;		
		velocity.set(flickVelocity);		
		velocity.scl(0.9f);

		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
		
		// slow and stop check
	    if (velocity.len2() < 500.f) {
			velocity.x = 0;
			velocity.y = 0;
		}		
	    position = CollisionUtils.withinScreen(position);
	    updateBoundBox();	
		return velocity;
	}
	
	
	public void collidedPosition(Vector2 collider, float scale) {
		hitAxis = CollisionUtils.updateCollideAxis(position, collider);
		// 0 is for normal bounce
		if (scale == 0) scale = 0.5f;
		bounce(scale);
		if (pilot != null) {
			pilot.colliderCheck(collider);
		}
	}
	
/*****************************************************************/	
	
	private void bounce(float scale) {
		// uses collider as a position vector
		bounceCounter++;

		//hitAxis refers to axis component collided with
		switch (hitAxis) {
			case IdManager.NORTH:
				// hit top
				position.y -= bounceOffset;
				velocity.y *= -1;
				break;
			case IdManager.EAST:
				// hit right
				position.x += bounceOffset;
				velocity.x *= -1;
				break;
			case IdManager.SOUTH:
				// hit bottom
				position.y += bounceOffset;
				velocity.y *= -1;
				break;
			case IdManager.WEST:
				// hit left
				position.x -= bounceOffset;
				velocity.x *= -1;
				break;
			default:
				// uh -oh...
		}
		// reset hitAxis here
		hitAxis = IdManager.UNKNOWN;	
		
		// scale rebound velocity?
		velocity.scl(scale / bounceCounter);

	    // final check
        position.set(CollisionUtils.withinScreen(position));
        
	    if (bounceCounter == 5) {
	    	SoundManager.playBounce(id);
	    	if (pilot != null) {
	    		pilot.unstuckUnit(); 	
	    	}
	    	bounceCounter = 0;
	    	velocity.set(0, 0);
	    }
	}	
	
/*****************************************************************/	
	
	public void swapOrderedTexture(boolean ordered) {
		if (ordered) 
			this.texture = GraphicsManager.getGraphicsManager().enemyShipOrderedTexture;
		else 
			this.texture = GraphicsManager.getGraphicsManager().enemyShipTexture;
	}
	
	public void swapTexture(char type) {
		this.chargedUp = type;
		if (type == IdManager.POWERUP) {
			if (this.id == IdManager.PRIVATE_ID)
				this.texture = GraphicsManager.getGraphicsManager().playerShipPowerupTexture;
			else if (this.id == IdManager.GENERAL_ID) 
				this.texture = GraphicsManager.getGraphicsManager().enemyShipPowerupTexture;
		}
		else if (type == IdManager.BOMBUP) {
			if (this.id == IdManager.PRIVATE_ID)
				this.texture = GraphicsManager.getGraphicsManager().playerShipBombupTexture;
			else if (this.id == IdManager.GENERAL_ID)
				this.texture = GraphicsManager.getGraphicsManager().enemyShipBombupTexture;
		}
		else {
			if (this.id == IdManager.PRIVATE_ID)
				this.texture = GraphicsManager.getGraphicsManager().playerShipTexture;
			else if (this.id == IdManager.GENERAL_ID)
				this.texture = GraphicsManager.getGraphicsManager().enemyShipTexture;
		}
	}
	
	public void updateBoundBox() {
		boundBox.setPosition(position.x, position.y);
		cogOffset.set(position.x + radius, 
				position.y + radius);
	}

	
/*****************************************************************/
	
	public void damage(float amount) {
		this.hitPoints = Math.max(hitPoints - amount,  0);
		if (hitPoints <= 0) {
			alive = false;
			if (FFScreen.DEBUG) Log.i(TAG, "fighter damaged and dead.");
		}
	}
	
	public void fireWeapon(float targetX, float targetY) {
		if (chargedUp == IdManager.POWERUP) {
			swapTexture(IdManager.NULL_ID); // reset it	
			FFModel.getFFModel().weapons.add(new Cable(
					IdManager.POWER_CABLE, 
					cogOffset, 
					new Vector2(targetX, targetY)));
		}
		
		else if (chargedUp == IdManager.BOMBUP) {
			swapTexture(IdManager.NULL_ID); // reset it	
			FFModel.getFFModel().weapons.add(new Cable(
					IdManager.MISSILE_BOMB,
					cogOffset,
					new Vector2(targetX, targetY)));
		}
		else {
			FFModel.getFFModel().weapons.add(new Bullet(
					id + 2, 
					cogOffset, 
					new Vector2(targetX, targetY), 
					bulletDamage));
		}	
	}
	
/*****************************************************************/
	
	public void draw(SpriteBatch spriteBatch) {
		spriteBatch.draw(texture, position.x, position.y, diameter, diameter);
	}
}