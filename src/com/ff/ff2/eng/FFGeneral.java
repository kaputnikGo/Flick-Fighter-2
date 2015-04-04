package com.ff.ff2.eng;

import android.util.Log;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ff.ff2.ail.AiUtils;
import com.ff.ff2.ent.Fighter;
import com.ff.ff2.ent.Turret;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.Collision;
import com.ff.ff2.nav.Navigator;
import com.ff.ff2.obs.Hanger;

/*
 * 
 *  FlickFighter 2 - General
 *  libGDX port
 *  
 *  min SDK 10 (2.3.3)
 *  
 *  this is the General class for enemy AI and control
 *  
 * 
 */

public class FFGeneral {
	private static final String TAG = FFGeneral.class.getSimpleName();
	
	public boolean alive;
	public Array<Fighter> squadron = new Array<Fighter>();
	public Array<Turret> turrets = new Array<Turret>();
	public Array<Hanger> hangers = new Array<Hanger>();
	
	public boolean turretsActive;
	public boolean turretsAlive;
	public boolean squadronAlive;
	
	// vectors of interest...
	public Vector2 powerupVector;
	public Vector2 switchVector;
	public Vector2 gatewayVector;
	
	private boolean gatewayActivated;
	private int updateCounter;
	private int defcon;
	
	public FFGeneral(boolean isPlayerControlled) {
		// isPlayerControlled not yet implemented
	}
	
	public void reset() {
		turrets.clear();
		squadron.clear();
		hangers.clear();
		
		alive = true;
		turretsAlive = true;
		squadronAlive = true;
		turretsActive = false;
		gatewayActivated = false;
		defcon = AiUtils.DEFCON_WAIT;
	}
	
/*****************************************************************/
	
	public void initWeapons() {
		// do not use array iterator here as update() and draw() do.
		squadron = FFModel.getFFModel().field.fortress.drones;
		squadron.shrink();
		turrets = FFModel.getFFModel().field.fortress.turrets;
		turrets.shrink();
		hangers = FFModel.getFFModel().field.fortress.hangers;
		hangers.shrink();
		
		powerupVector = new Vector2(FFModel.getFFModel().field.getPowerupPosition());
		switchVector = new Vector2(FFModel.getFFModel().field.getSwitchPosition());
		gatewayVector = new Vector2(FFModel.getFFModel().field.getGatewayPosition());
		
		gatewayActivated = false;
		defcon = AiUtils.DEFCON_CALM;

		Fighter drone;
		Turret turret;
		
		// stationary stuff first, drones can collide with turrets
		for (int t = 0; t < turrets.size; t++) {
			turret = turrets.get(t);
			if (turret.alive) {
				turret.loadAI();
				Navigator.turretPositions.add(turret.position);
				turret.gunner.initTactics(defcon, gatewayVector);
			}
		}
		
		for (int d = 0; d < squadron.size; d++) {
			drone = squadron.get(d);
			if (drone.alive) {
				drone.loadAI();
				Navigator.dronePositions.add(drone.position);
				drone.pilot.initTactics(defcon, powerupVector, switchVector);
			}
		}
		
		// find out if has a hanger here?
		// look last as it requires turret.positions
		if (hangers.size > 0) {
			if (FFScreen.DEBUG) Log.i(TAG, "hangers found: " + hangers.size);
			
			for (int h = 0; h < hangers.size; h++) {
				if (hangers.get(h).alive) {
					if (FFScreen.DEBUG) Log.i(TAG, "hanger " + h + " initHanger.");
					
					hangers.get(h).initHanger();
				}
			}
		}

		drone = null;
		turret = null;
	}

	public void update(float deltaTime, Vector2 enemyPosition) {
		// reset these:
		updateCounter = 0;
		updateDefcon();
		
		// fighters
		for (Fighter drone : squadron) {
			if (drone.alive && drone.pilot != null) {
				drone.pilot.updateAI(enemyPosition, deltaTime);
				
				drone.pilot.updateStrategy(defcon);

				Navigator.dronePositions.get(updateCounter).set(drone.position);
				drone.AIflight(deltaTime);
				Collision.collisionFighterCheck(drone);
			}
			updateCounter++;
		}
		
		// turrets
		updateCounter = 0;
		for (Turret turret : turrets) {
			if (turret.alive) {	
				turret.gunner.updateAI(enemyPosition, deltaTime);						
				turret.gunner.updateStrategy(defcon);	
				Navigator.turretPositions.get(updateCounter).set(turret.position);
			}
			updateCounter++;
		}
		
		// hangers
		if (hangers.size > 0) {
			for (Hanger hanger : hangers) {
				if (hanger.alive) {					
					// check conditions for launching a drone
					// rem'd gatewayActivated condition
					if (squadron.size <= FFScreen.engine.settings.MIN_DRONES || hanger.sqnldr.droneRequest) {
						// always check this
						if (squadron.size <= FFScreen.engine.settings.MAX_DRONES)
							hanger.sqnldr.readyDrone = true;					
					}
					
					hanger.sqnldr.updateAI(deltaTime);
					if (hanger.sqnldr.launched) {
						if (FFScreen.DEBUG) Log.i(TAG, "sqnldr launched drone, grab it, launchTactics");
						
						hanger.sqnldr.launcher.pilot.launchTactics(powerupVector, switchVector);
						
						// take over
						squadron.add(hanger.sqnldr.launcher);
						Navigator.dronePositions.add(hanger.sqnldr.launcher.position);
						hanger.sqnldr.launched = false;
					}
				}
			}
		}
	}
	
	public void powerUp(char type, Fighter drone) {
		// this could trigger an AI response...
		if (FFScreen.DEBUG) Log.i(TAG, "drone.powerup true");
		drone.swapTexture(type);
	}
		
	public void gatewayActivated() {
		// toggle
		gatewayActivated ^= true;
		// check for deactivation ?
		
		if (FFScreen.DEBUG) Log.i(TAG, "gatewayActivated: " + gatewayActivated);
	}
	
/*****************************************************************/
	
	
	private void updateDefcon() {
		// in order of importance
		if (gatewayActivated) defcon = AiUtils.DEFCON_WARNING;
		
		else if (turretsAlive == false) defcon = AiUtils.DEFCON_ALERT;
		
		else if (squadronAlive == false) defcon = AiUtils.DEFCON_ALERT;
		
		// as yet unused AiUtils.DEFCON_ATTACK - used to press the units into attacking
		// could be a threshold of unit remaining...
		
		// in case any of above have been set
		else defcon = AiUtils.DEFCON_CALM;
	}
	

/*****************************************************************/
	
	public void draw(SpriteBatch spriteBatch, float deltaTime) {
		for (Fighter fighter : squadron) {
			if (fighter.alive) fighter.draw(spriteBatch);
			else {
				FFModel.getFFModel().droneDestruct(fighter.position);
				squadron.removeValue(fighter, true);
				squadron.shrink();
				
				Navigator.dronePositions.removeValue(fighter.position, true);
				Navigator.dronePositions.shrink();
			}
		}
		squadronAlive = squadron.size > 0;

	    for (Turret turret : turrets) {
			if (turret.alive) turret.draw(spriteBatch);
			else {
				turrets.removeValue(turret, true);
				turrets.shrink();
				
				Navigator.turretPositions.removeValue(turret.position, true);
				Navigator.turretPositions.shrink();
			}
		}
	    turretsAlive = turrets.size > 0;
	}
	
}