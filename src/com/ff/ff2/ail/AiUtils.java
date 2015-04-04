package com.ff.ff2.ail;

import android.util.Log;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.CollisionUtils;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.nav.Navigator;
import com.ff.ff2.nav.NavigatorUtils;


public class AiUtils {
	private static final String TAG = AiUtils.class.getSimpleName();
	private static final Vector2 nullTarget = new Vector2(Navigator.nullTarget);
	private static final int cellSize = FFScreen.gameCellSize;
	private static final int MOVES_SIZE = 20;
	private static final float MAX_VECTOR_X = FFScreen.gameWidth - 100;
	private static final float MAX_VECTOR_Y = FFScreen.gameHeight - 100;
	
	// UNIT AI STRATEGY
	public static final int AI_WAITING = 0; // standing by, waiting...
	public static final int AI_DEFENSIVE = 1; // holding pattern
	public static final int AI_OFFENSIVE = 2; // go seek enemy 
	public static final int AI_ORDERED = 3; // goto gateway asap
	public static final int AI_BESERK = 4; // go beserk, shoot everything
	
	// GENERAL'S DEFCON STATE (match the AI unit's int)
	public static final int DEFCON_WAIT = 0; // wait for update state
	public static final int DEFCON_CALM = 1; // normal state
	public static final int DEFCON_ATTACK = 2; // hunt enemy
	public static final int DEFCON_WARNING = 3; // gateway activated
	public static final int DEFCON_ALERT = 4; // sqaudron/turrets dead

	
/*********************************************************************/
	
	public static Vector2 getPatrolWaypoint2(Vector2 start, int direction) {
		// always this
		Vector2 waypoint2 = new Vector2();
		
		// will need to account for angular directions soon
		
		if (direction == IdManager.NORTH) {
			// from fighter.position go up patrol range
			waypoint2.set(start.x, 
					start.y - FFScreen.engine.settings.PATROL_RANGE_MAX);
		}
		else if (direction == IdManager.SOUTH) {
			waypoint2.set(start.x, 
					start.y + FFScreen.engine.settings.PATROL_RANGE_MAX);
		}
		else if (direction == IdManager.WEST) {
			waypoint2.set(start.x - FFScreen.engine.settings.PATROL_RANGE_MAX, 
					start.y);
		}
		else if (direction == IdManager.EAST) {
			waypoint2.set(start.x + FFScreen.engine.settings.PATROL_RANGE_MAX, 
					start.y);
		}
		else {
			// stuck cos yet to code angles?
			if (FFScreen.DEBUG) Log.i(TAG, "sestting waypoint2 to nullTarget");
			waypoint2.set(nullTarget);
		}
		
		return CollisionUtils.withinScreen(waypoint2);
	}
	
	public static Vector2 extendVectorWaypoint(Vector2 source, Vector2 target) {
		// buffer could be cellSize * 2
		// or dst(source, target) * 2...this gets a bit wild
		Vector2 waypoint = new Vector2();
		
		waypoint.set(
				target.x + ((cellSize * 2) * NavigatorUtils.getDirection(source.x, target.x)), 
				target.y + ((cellSize * 2) * NavigatorUtils.getDirection(source.y, target.y)));
		
		return CollisionUtils.withinScreen(waypoint);		
	}
	
	public static Array<Vector2> getRandomMoves() {
		Array<Vector2> moves = new Array<Vector2>();
		
		// currently a random array
		// it gets reloaded after the twenty moves
		for (int i = 0; i < MOVES_SIZE; i++) {
			moves.add(randomMoveVector());
		}
		return moves;
	}
		
/*********************************************************************/
	
	public static Vector2 setVelocity(Vector2 position, Vector2 waypoint, boolean slow) {
		Vector2 velocity = new Vector2();
		
		// distance check overrides for velocity to next waypoint
		if (position.dst(waypoint) <= FFScreen.engine.settings.PATROL_RANGE_MIN) {
			velocity.set(waypoint.x - position.x, 
					waypoint.y - position.y).nor().scl(Math.min(
							position.dst(waypoint.x, waypoint.y), cellSize));
		}
		else if (position.dst(waypoint) <= FFScreen.engine.settings.PATROL_RANGE_MAX) {
			velocity.set(waypoint.x - position.x, 
					waypoint.y - position.y).nor().scl(Math.min(
							position.dst(waypoint.x, waypoint.y), 
							FFScreen.engine.settings.VELOCITY_SLOW));
		}
		// 
		else if (slow) {
			velocity.set(waypoint.x - position.x, 
					waypoint.y - position.y).nor().scl(Math.min(
							position.dst(waypoint.x, waypoint.y), 
							FFScreen.engine.settings.VELOCITY_NORM));
		}
		else {
			velocity.set(waypoint.x - position.x, 
					waypoint.y - position.y).nor().scl(Math.min(
							position.dst(waypoint.x, waypoint.y), 
							FFScreen.engine.settings.VELOCITY_FAST));
		}
		return velocity;
	}
	
	public static Vector2 evasiveVector(Vector2 position, Vector2 target) {
		// work out where enemy is in relation to currentPosition
		// then add diff based on closest axis
		Vector2 evasive = new Vector2();
		evasive.set(position);
		
		// is on left side, move to left
        if (position.x <= target.x) {
        	evasive.x = target.x - position.x;
        }
        // is on right side, move to right
        else if (position.x >= target.x) {
        	evasive.x += position.x - target.x;
        }
        // is above, move up
        if (position.y <= target.y) {
        	evasive.y -= target.y - position.y;
        }
        // is below, move down
        else if (position.y >= target.y) {
        	evasive.y += (position.y - target.y);
        }  
        // scale to make a good get away
        evasive.scl(1.5f);
		return evasive;
	}
	
	public static Vector2 randomMoveVector() {
		return new Vector2(MathUtils.random(100, MAX_VECTOR_X),
				MathUtils.random(100, MAX_VECTOR_Y));
	}
	
	public static Vector2 randomTargetVector(Vector2 pivot) {
		// should be from pivot to edge of screen
		Vector2 shooter = new Vector2();
		shooter.set(pivot);
		shooter.x += (MathUtils.random(-MAX_VECTOR_X, MAX_VECTOR_X));
		
		shooter.y += (MathUtils.random(-MAX_VECTOR_Y, MAX_VECTOR_Y));
		
		return CollisionUtils.withinScreen(shooter);
	}
}