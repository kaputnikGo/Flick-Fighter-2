package com.ff.ff2.ail;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.eng.FFModel;
import com.ff.ff2.ent.Bullet;
import com.ff.ff2.ent.Fighter;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.nav.Navigator;

public class SqnLdr {
	private static final String TAG = SqnLdr.class.getSimpleName();
	
	private float AIpause;
	private float AIautoTime;
	
	private Vector2 position;
	public boolean readyDrone;
	public boolean droneRequest; // request drone launch
	public boolean launched;
	public Fighter launcher; // temp, launched from hanger, then controlled by general
	
	private Vector2 nullTarget;
	public Vector2 runwayStart;
	public Vector2 runwayExit;
	private int runwayLength;
	private int runwayOffset;
	private int runway;
	
	private Vector2 obsTarget;
	
	public SqnLdr(Vector2 position) {
		this.position = position;
		AIpause = FFScreen.engine.settings.DEPLOY_PAUSE;
		AIautoTime = 0;
		readyDrone = false;
		droneRequest = false;
		launched = false;
		
		nullTarget = new Vector2();
		nullTarget.set(Navigator.nullTarget);
		
		runwayExit = new Vector2();
		runwayExit.set(nullTarget);
		runwayStart = new Vector2();
		runwayStart.set(nullTarget);
		runwayLength = FFScreen.gameCellSize * 3; //end of runway 3 cells from hanger in runway direction
		runwayOffset = FFScreen.gameCellSize; // next free cell to hanger
		runway = -1;
		
		// limited use
		obsTarget = new Vector2();
		obsTarget.set(nullTarget);
		
		launcher = null;
		if (FFScreen.DEBUG) Log.i(TAG, "positionXY: " + position.x + ", " + position.y);
	}
	
	public void destroy() {
		// clean up
	}
	
	public boolean initRunway() {
		// this is doing full scan including angles -
		runway = Navigator.hangerRunwayDirection(position);
		if (runway == IdManager.UNKNOWN) {
			if (FFScreen.DEBUG) Log.i(TAG, "runway unknown.");
			// can't find empty start to runway
			// can shoot way out?
			if (unblockRunwayStart() == false) {
				// uh -oh
				return false;
			}
		}
		
		// plots start and exit for runway
		plotRunway(runway);
		// all good, carry on
		if (Navigator.hangerRunwayExitScan(runwayStart, runwayExit) == false) {
			// has blocked runway, check for shootables
			if (Navigator.hangerRunwayExitShootable(runwayStart, runwayExit)) {
				unblockRunwayExit();
			}
			else {
				if (FFScreen.DEBUG) Log.i(TAG, "exit shootable false.");
				return false;
			}
		}
		return true;
	}
	
/*********************************************************************/	

	private void plotRunway(int runway) {
		// using position go along runway direction 3 * cellSize
		switch (runway) {
			case IdManager.NORTH:
				// goes up
				if (FFScreen.DEBUG) Log.i(TAG, "runway facing north.");
				runwayExit.set(position.x, position.y - runwayLength);
				runwayStart.set(position.x, position.y - runwayOffset);
				break;
			case IdManager.EAST:
				//  goes right
				if (FFScreen.DEBUG) Log.i(TAG, "runway facing east.");
				runwayExit.set(position.x + runwayLength, position.y);
				runwayStart.set(position.x + runwayOffset, position.y);
				break;
			case IdManager.SOUTH:
				// goes down
				if (FFScreen.DEBUG) Log.i(TAG, "runway facing south.");
				runwayExit.set(position.x, position.y + runwayLength);
				runwayStart.set(position.x, position.y + runwayOffset);
				break;
			case IdManager.WEST:
				// goes left
				if (FFScreen.DEBUG) Log.i(TAG, "runway facing west.");
				runwayExit.set(position.x - runwayLength, position.y);
				runwayStart.set(position.x - runwayOffset, position.y);
				break;
			default:
				// is on angle?
				if (FFScreen.DEBUG) Log.i(TAG, "error: runway on angle");
				break;					
		}
	}
	
	private boolean unblockRunwayStart() {
		if (FFScreen.DEBUG) Log.i(TAG, "clearing runway start.");
		obsTarget.set(nullTarget);
		obsTarget.set(Navigator.findClosestTarget(position));
		
		if (obsTarget.equals(nullTarget)) {
			if (FFScreen.DEBUG) Log.i(TAG, "no shootable found, self destruct.");
			return false;
		}
		
		breakout(obsTarget);
		return true;
	}
	
	private void unblockRunwayExit() {
		if (FFScreen.DEBUG) Log.i(TAG, "unblock runway exit.");
		breakout(runwayExit);
	}
		
/*********************************************************************/
	
// UPDATE FUNCTIONS
		
	public void updateAI(float deltaTime) {
		AIautoTime += deltaTime;
		
		if (AIautoTime >= AIpause) {
			// Reset timer (not set to 0)
			AIautoTime -= AIpause;
			if (readyDrone) {
				readyDrone = false;
				droneRequest = false;
				if (FFScreen.DEBUG) Log.i(TAG, "update >= DEPLOY_PAUSE, launch drone.");
				launchDrone();
			}
		}
	}
	
	public void underAttack() {
		// this gets called every time hanger gets hit from hp <= 60
		
		//immediately
		if (FFScreen.DEBUG) Log.i(TAG, "underAttack, request drone.");
		droneRequest = true;
	}
	
	
/*********************************************************************/
	
	private void launchDrone() {
		// create new drone and add to general's squadrons
		launcher = new Fighter(IdManager.GENERAL_ID, runwayStart);
		launcher.loadAI();
		// give it flightpath out of hanger		
		launcher.pilot.launchDrone(runwayStart, runway);
		launched = true;
		if (FFScreen.DEBUG) Log.i(TAG, "drone launched");
	}

	private void breakout(Vector2 target) {
		if (FFScreen.DEBUG) Log.i(TAG, "breakout fire.");
		// offset vectors
		target.x = target.x + 16;
		target.y = target.y + 16;
		// sqnldrs and hangers do not yet have full fireWeapon abilities,
		// this function serves only to allow hanger to clear runway of shootable obs
		// faking a drone bullet...
		Bullet bullet = new Bullet(IdManager.DRONE_BULLET, 
				runwayStart, 
				target,
				100);
		
		FFModel.getFFModel().weapons.add(bullet);
	}	
}