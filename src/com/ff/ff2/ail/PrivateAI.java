package com.ff.ff2.ail;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ff.ff2.eng.FFModel;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.nav.Navigator;

public class PrivateAI {
	/*****
	 * 
	 * 
	 * 
	 * 
	 * 			NOT IMPLEMENTED
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	private static final String TAG = PrivateAI.class.getSimpleName();
	private Array<Vector2> moves;
	private Vector2 generalPosition;
	private Vector2 currentPosition;
	private int currentMoveNumber;
	private int maxMoves;
	private int setMovesNum;
	private int gatewayMoves;
	private final float MOVEBUFFER = FFScreen.gameCellSize * 4;
	private final float OFFSETBUFFER = FFScreen.gameCellSize;
	private final float MINRANGE = FFScreen.gameCellSize * 2;
	
	private Vector2 switchPosition;
	private Vector2 gatewayPosition;
	private Vector2 powerupPosition;
	
	public PrivateAI() {
		//
	}
	
	public void initPrivateAI() {
		if (moves != null) moves.clear();
		generalPosition = new Vector2(0, 0);
		gatewayMoves = 0;
		
		loadMoves();
		loadKeyPositions();	
	}
	
	public void destroy() {
		//
	}
	
/*********************************************************************/	
	
	public void updateIntel(Vector2 currentPositionIn) {
		currentPosition = currentPositionIn;
		// reset for stale enemy positions
		if (generalPosition != null) generalPosition.set(0, 0);
		// first go for drones
		if (FFModel.getFFModel().fortressGeneral.squadronAlive) {
			generalPosition.set(Navigator.findClosestDronePosition(currentPosition));
		}
		// then for turrets
		else if (FFModel.getFFModel().fortressGeneral.turretsAlive) {
			generalPosition.set(Navigator.findClosestTurretPosition(currentPosition));
		}
		else {
			// nothing left to shoot
			generalPosition = null;
		}		
	}
	
	public Vector2 nextMoveVector() {
		if (generalPosition == null) {
			// set move to the powerup
			// goto powerup
			// this can cause bounce loop if obs in way...
			if (setMovesNum >= 1) {
				setMovesNum = 0;
				return new Vector2(powerupPosition.x - MOVEBUFFER, 
						powerupPosition.y - OFFSETBUFFER);
			}
			setMovesNum++;
			return new Vector2(powerupPosition.x + MOVEBUFFER,
					powerupPosition.y + OFFSETBUFFER);
		}
		else {
			if (enemyTooClose()) {
				if (FFScreen.DEBUG) Log.i(TAG, "evasive move.");
				return evasiveVector();
			}
			
		}
		return getNextMoveVector();
	}

	public Vector2 moveToGateway() {
		if (gatewayMoves == 0) {
			gatewayMoves++;
			return new Vector2(gatewayPosition.x - MOVEBUFFER, 
					gatewayPosition.y - OFFSETBUFFER);
		}
		else {
			gatewayMoves = 0;
			return new Vector2(gatewayPosition.x + MOVEBUFFER,
					gatewayPosition.y + OFFSETBUFFER);
		}
	}

	public Vector2 moveToSwitch() {
		// position to left of switch for clear shot
		return new Vector2(switchPosition.x - MOVEBUFFER, 
				switchPosition.y - OFFSETBUFFER);
	}
	
	public Vector2 targetSwitch() {
		return switchPosition;
	}

/*********************************************************************/
	
	private void loadKeyPositions() {
		switchPosition = new Vector2(FFModel.getFFModel().field.getSwitchPosition());
		gatewayPosition = new Vector2(FFModel.getFFModel().field.getGatewayPosition());
		powerupPosition = new Vector2(FFModel.getFFModel().field.getPowerupPosition());
	}
	
	private Vector2 getRandomVector() {
		return AiUtils.randomMoveVector();
	} 
	
	private Vector2 evasiveVector() {
		// work out where enemy is in relation to currentPosition
		// then add diff based on closest axis
		Vector2 evasiveVector = new Vector2(currentPosition);
		
		// is on left side, move to left
        if (currentPosition.x <= generalPosition.x) {
        	evasiveVector.x = generalPosition.x - currentPosition.x;
        }
        // is on right side, move to right
        else if (currentPosition.x >= generalPosition.x) {
        	evasiveVector.x += currentPosition.x - generalPosition.x;
        }
        // is above, move up
        if (currentPosition.y <= generalPosition.y) {
        	evasiveVector.y -= generalPosition.y - currentPosition.y;
        }
        // is below, move down
        else if (currentPosition.y >= generalPosition.y) {
        	evasiveVector.y += (currentPosition.y - generalPosition.y);
        }  
        // scale to make a good get away
        evasiveVector.scl(1.5f);
		return evasiveVector;
	}
	
	
	private Vector2 getNextMoveVector() {
		currentMoveNumber++;
		if (currentMoveNumber < maxMoves) {
			return moves.get(currentMoveNumber);
		}
		else {
			currentMoveNumber = 0;
			// trigger off new random vectors
			loadMoves();
			return moves.get(currentMoveNumber);
		}			
	}
	
	private boolean enemyTooClose() {
		float distance = -1;
		distance = currentPosition.dst(generalPosition);
		
		if (distance <= MINRANGE) {
			return true;
		}
		else
			return false;
	}
	/*
	private boolean enemyInRange() {
		// value for maxRange/minRange should be modifiable		
		float maxRange = FFScreen.gameWidth;;
		float distance = -1;
		distance = currentPosition.dst(enemyPosition);
		
		if (distance <= maxRange) {
			return true;
		}
		else 
			return false;
	}
	*/
	private void loadMoves() {
		setMovesNum = 0;
		gatewayMoves = 0;
		moves = new Array<Vector2>();
		// currently a random array
		// it gets reloaded after the twenty moves
		for (int i = 0; i < 20; i++) {
			moves.add(getRandomVector());
		}
		currentMoveNumber = 0;
		maxMoves = moves.size;
	}
}