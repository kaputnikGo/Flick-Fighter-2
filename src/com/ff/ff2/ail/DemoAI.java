package com.ff.ff2.ail;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ff.ff2.eng.FFModel;
import com.ff.ff2.ent.Bullet;
import com.ff.ff2.ent.Cable;
import com.ff.ff2.ent.Fighter;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.nav.Navigator;

public class DemoAI {
	private static final String TAG = DemoAI.class.getSimpleName();
	private Array<Vector2> moves;
	private int currentMoveNumber;
	private int maxMoves;
	private int setMovesNum;
	private int gatewayMoves;
	private final float MOVEBUFFER = FFScreen.gameCellSize * 4;
	private final float OFFSETBUFFER = FFScreen.gameCellSize;
	private final float MINRANGE = FFScreen.gameCellSize * 2;
	private final int MOVES_SIZE = 20;
	
	public Vector2 switchPosition;
	public Vector2 gatewayPosition;
	public Vector2 powerupPosition;
	public Vector2 nextTarget;
	private Vector2 nullTarget;
	private Vector2 evasiveVector;
	public Vector2 nextWaypoint;
	
	private Fighter fighter;
	
	public DemoAI() {	
		nullTarget = new Vector2();
		nullTarget.set(Navigator.nullTarget);
		
		nextTarget = new Vector2();
		evasiveVector = new Vector2();
		nextWaypoint = new Vector2();
		
		switchPosition = new Vector2();
		gatewayPosition = new Vector2();
		powerupPosition = new Vector2();
		
		moves = new Array<Vector2>(MOVES_SIZE);
	}
	
	public void initDemoAI(Fighter fighter) {
		this.fighter = fighter;
		
		if (moves != null) 
			moves.clear();
		
		nextTarget.set(nullTarget);
		gatewayMoves = 0;
		
		nextTarget.set(nullTarget);
		evasiveVector.set(nullTarget);
		nextWaypoint.set(nullTarget);
		
		loadMoves();
		initKeyPositions();	
	}
	
	public void destroy() {
		if (moves != null) moves = null;
		if (fighter != null) fighter = null;
	}
	
/*********************************************************************/	
	
	public void updateIntel() {
		// reset for stale enemy positions
		nextTarget.set(nullTarget);
		// first go for drones
		if (FFModel.getFFModel().fortressGeneral.squadronAlive) {
			nextTarget.set(Navigator.findClosestDronePosition(fighter.position));
		}
		// then for turrets
		else if (FFModel.getFFModel().fortressGeneral.turretsAlive) {
			nextTarget.set(Navigator.findClosestTurretPosition(fighter.position));
		}
		else {
			// nothing left to shoot
			nextTarget.set(powerupPosition); // try clear any obs
		}		
	}
	
	public void updatePosition(boolean gatewayActivated) {	
		if (fighter.chargedUp == IdManager.POWERUP) {
			
			nextTarget.set(switchPosition);
			nextWaypoint.set(switchPosition.x - MOVEBUFFER, 
				switchPosition.y - OFFSETBUFFER);
		}
		else if (gatewayActivated) {
			// goto gateway
			nextMoveToGateway();
		}
		else {
			// normal flight
			nextWaypoint();
		}
		fighter.velocity.set(AiUtils.setVelocity(fighter.position, nextWaypoint, false));
	}

	public void fireWeapon() {
		if (nextTarget.equals(nullTarget)) {
			// skip
		}
		else {
			// add an offset to ensure not shooting self
			if (fighter.chargedUp == IdManager.POWERUP) {
				nextTarget.set(switchPosition); // make sure is shooting at correct target...
				fighter.swapTexture(IdManager.NULL_ID); // reset it
				FFModel.getFFModel().weapons.add(new Cable(
						IdManager.POWER_CABLE, 
						fighter.cogOffset, 
						nextTarget));	
			}
			else if (fighter.chargedUp == IdManager.BOMBUP) {
				fighter.swapTexture(IdManager.NULL_ID); // reset it
				FFModel.getFFModel().weapons.add(new Cable(
						IdManager.MISSILE_BOMB,
						fighter.cogOffset,
						nextTarget));			
			}
			else {
				FFModel.getFFModel().weapons.add(new Bullet(
						fighter.id + 2, 
						fighter.cogOffset, 
						nextTarget, 
						fighter.bulletDamage));
			}
		}
	}
	
/*********************************************************************/
	
	private void nextWaypoint() {
		if (nextTarget.equals(powerupPosition)) {
			// has no more enemies to shoot:
			// set move to the powerup
			// goto powerup
			if (setMovesNum >= 1) {
				setMovesNum = 0;
				nextWaypoint.set(powerupPosition.x - MOVEBUFFER, 
						powerupPosition.y - OFFSETBUFFER);
			}
			else {
				setMovesNum++;
				nextWaypoint.set(powerupPosition.x + MOVEBUFFER,
						powerupPosition.y + OFFSETBUFFER);
			}
		}
		else {
			// normal flight
			if (enemyTooClose()) {
				if (FFScreen.DEBUG) Log.i(TAG, "evasive move.");
				nextWaypoint.set(AiUtils.evasiveVector(fighter.position, nextTarget));
			}
			setNextMoveWaypoint();
		}
	}

	private void nextMoveToGateway() {
		if (gatewayMoves == 0) {
			gatewayMoves++;
			nextWaypoint.set(gatewayPosition.x - MOVEBUFFER, 
					gatewayPosition.y - OFFSETBUFFER);
		}
		else {
			gatewayMoves = 0;
			nextWaypoint.set(gatewayPosition.x + MOVEBUFFER,
					gatewayPosition.y + OFFSETBUFFER);
		}
	}
	/*
	public Vector2 moveToSwitch() {
		// position to left of switch for clear shot
		nextTarget.set(switchPosition);
		return nextMoveVector.set(switchPosition.x - MOVEBUFFER, 
				switchPosition.y - OFFSETBUFFER);
	}
	*/

/*********************************************************************/
	
	private void initKeyPositions() {
		switchPosition.set(FFModel.getFFModel().field.getSwitchPosition());
		gatewayPosition.set(FFModel.getFFModel().field.getGatewayPosition());
		powerupPosition.set(FFModel.getFFModel().field.getPowerupPosition());
	}
	
	private void setNextMoveWaypoint() {
		currentMoveNumber++;
		if (currentMoveNumber < maxMoves) {
			nextWaypoint.set(moves.get(currentMoveNumber));
		}
		else {
			currentMoveNumber = 0;
			// trigger off new random vectors
			loadMoves();
			nextWaypoint.set(moves.get(currentMoveNumber));
		}			
	}
	
	private boolean enemyTooClose() {
		return (fighter.position.dst(nextTarget) <= MINRANGE);
	}

	private void loadMoves() {
		// reset
		setMovesNum = 0;
		gatewayMoves = 0;
		currentMoveNumber = 0;
		moves.clear();
		
		moves = AiUtils.getRandomMoves();
		
		maxMoves = moves.size;
	}
	
}