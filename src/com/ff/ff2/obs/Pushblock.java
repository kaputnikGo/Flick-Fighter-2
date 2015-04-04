package com.ff.ff2.obs;

//import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.Collision;
import com.ff.ff2.lib.CollisionUtils;
import com.ff.ff2.lib.GraphicsManager;
import com.ff.ff2.lib.IdManager;

public class Pushblock extends Obstacle {
	//private static final String TAG = Pushblock.class.getSimpleName();
	private int hitAxis;
	private float range;
	private Vector2 oldPosition = new Vector2();

	public Pushblock(Vector2 position) {
		super(position, GraphicsManager.getGraphicsManager().pushblockTexture);
		this.hitAxis = IdManager.UNKNOWN;
		range = FFScreen.gameCellSize;
	}
	
	
	@Override
	public void swapTexture(Texture texture) {
		//	
	}
	
	public void swapTexture() {
		//
	}
	
	public void damage(float amount) {
		// no damage, but moves in direction of shot, n number of cells
	}
	
/*********************************************************************/
	
	public void collidedFighter(Vector2 collider) {
		// snapshot of position in case of collide obs
		oldPosition.set(position);
		
		hitAxis = CollisionUtils.updateCollideAxis(position, collider);
		//hitAxis refers to axis component collided with
		if (hitAxis == IdManager.NORTH) {
			// hit top
			position.y -= range;
		}
		else if (hitAxis == IdManager.SOUTH) {
			// hit bottom
			position.y += range;
		}
		else if (hitAxis == IdManager.WEST) {
			// hit left
			position.x -= range;
		}
		else if (hitAxis == IdManager.EAST) {
			// hit right
			position.x += range;
		}
		else {
			// uh-oh, is n?
		}
		// update boundbox for collide check
		boundBox.set(position.x, position.y, width, height);
		//check obs
		if (Collision.collisionObstacleCheck(this)) {
			// revert to old position
			position.set(oldPosition);
			boundBox.set(position.x, position.y, width, height);
			hitAxis = IdManager.UNKNOWN;
		}
		else {
			// check edge
			position = CollisionUtils.withinScreen(position);
			// reset hitAxis here, adjust boundBox
			boundBox.set(position.x, position.y, width, height);
			hitAxis = IdManager.UNKNOWN;		
		}
	}

/*********************************************************************/
	
	public void draw(SpriteBatch spriteBatch, float deltaTime) {
		super.draw(spriteBatch, deltaTime);
	}
}