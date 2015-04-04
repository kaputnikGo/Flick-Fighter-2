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

public class Pilot {
	private static final String TAG = Pilot.class.getSimpleName();
	
	private Fighter fighter;
	private int strategy;
	
	private float AIpause;
	private float AIautoTime;
	
	private float responseTimer;
	private float responseTimerMax;

	private Array<Vector2> flightPath;
	private int currentWaypoint;
	private int maxWaypoints;
	
	private boolean objectiveReached;
	
	private Vector2 nextWaypoint;
	private Vector2 patrolWaypoint1;
	private Vector2 patrolWaypoint2;
	private Vector2 powerupPosition;
	private Vector2 switchPosition;
	
	private Vector2 nullTarget;
	public Vector2 nextTarget;	
	private Vector2 enemyPosition;
	
	public Vector2 collider;

	
	public Pilot(Fighter fighter) {
		this.fighter = fighter;
		strategy = AiUtils.AI_WAITING;
		// stop creating and re-use
		nullTarget = new Vector2();
		nullTarget.set(Navigator.nullTarget);
		collider = new Vector2();
		collider.set(nullTarget);
		
		nextWaypoint = new Vector2();
		patrolWaypoint1 = new Vector2();
		patrolWaypoint2 = new Vector2();
		
		nextTarget = new Vector2();
		enemyPosition = new Vector2();
		powerupPosition = new Vector2();
		switchPosition = new Vector2();		
	}
	
	public void destroy() {
		flightPath = null;
	}
		
// INITIAL STRATEGY
	public void initTactics(int defcon, Vector2 powerupPosition, Vector2 switchPosition) {
		resetPilot();
		
		this.powerupPosition.set(powerupPosition);
		this.switchPosition.set(switchPosition);	
	
		Array<Vector2> waypoints = new Array<Vector2>();
		waypoints = Navigator.patrolScan(fighter.position, 3, powerupPosition);
		
		if (waypoints != null) {
			// 3 cells range, not pixels
			patrolWaypoint1.set(waypoints.get(0));
			patrolWaypoint2.set(waypoints.get(1));
			waypoints = null;
		}
		else {
			if (FFScreen.DEBUG) Log.i(TAG, "no patrolScan, loading adhoc.");
			loadAdhocPatrol();
		}
		updateStrategy(defcon);
	}
	
	public void launchTactics(Vector2 powerupPosition, Vector2 switchPosition) {
		// drone just launched, has a patrol (but even if not) allow to clear runway
		if (FFScreen.DEBUG) Log.i(TAG, "launchTactics");
		this.powerupPosition.set(powerupPosition);
		this.switchPosition.set(switchPosition);
		executeResponse();
	}
	
/*********************************************************************/
	
	private void resetPilot() {
		AIpause = FFScreen.engine.settings.SLOW_PAUSE;
		AIautoTime = 0;
		
		nextWaypoint.set(nullTarget);
		nextTarget.set(nullTarget);
		enemyPosition.set(nullTarget);
		collider.set(nullTarget);
		
		patrolWaypoint1.set(nullTarget);
		patrolWaypoint2.set(nullTarget);
		
		if (flightPath != null) flightPath = null;
		strategy = AiUtils.AI_WAITING;
		
		currentWaypoint = 0;
		objectiveReached = false;
		
		responseTimer = 0;
		responseTimerMax = 0;
		
		fighter.swapOrderedTexture(false);
	}
	
/*********************************************************************/	
	
	public void launchDrone(Vector2 start, int runway) {
		resetPilot();
		nextWaypoint.set(AiUtils.getPatrolWaypoint2(start, runway));
		executeResponse();
		if (FFScreen.DEBUG) Log.i(TAG, "pilot launching.");	
	}
	
	public void colliderCheck(Vector2 collider) {
		//TODO
		this.collider.set(collider);
		
		if (strategy != AiUtils.AI_ORDERED) { 
			// is not on flightpath mission, check patrolWaypoints
			if (Navigator.targetVectorInline(fighter.position, nextWaypoint, collider)) {
				checkPatrolRange();
			}
		}
	}
	
	public void unstuckUnit() {
		if (FFScreen.DEBUG) Log.i(TAG, "unstuckUnit.");
		// slow things down a bit...
		fighter.velocity.set(0, 0);
		AIpause = FFScreen.engine.settings.SLOW_PAUSE;
		AIautoTime = 0;
		
		nextTarget.set(nullTarget);
		
		// look for blocking obs target
		if (Navigator.shootableTarget(collider)) {
			if (FFScreen.DEBUG) Log.i(TAG, "unstuckUnit shootable collider.");
			nextTarget.set(collider);
			collider.set(nullTarget);
		}
		else {
			nextTarget = Navigator.findClosestTarget(fighter.position);
			if (FFScreen.DEBUG) Log.i(TAG, "unstuckUnit findclosest target.");
		}
		
		// if has target
		if (nextTarget.equals(nullTarget)) {
			if (FFScreen.DEBUG) Log.i(TAG, "found non-shootable collider/closestTarget, try axis scan patrol...");
			// do a axisscan in case there a way out right under your nose
			loadPatrolWaypoints(Navigator.getLocalScanEmpty(fighter.position));
		}
		else {
			
			fireWeapon();
		}	
	}
	
/*********************************************************************/
	
// UPDATE FUNCTIONS
	
	public void updateAI(Vector2 enemyPosition, float deltaTime) {	
		AIautoTime += deltaTime;
		this.enemyPosition.set(enemyPosition);
		
		if (strategy == AiUtils.AI_ORDERED) {
			responseTimer += deltaTime;
		}	
	}
	
	public void updateStrategy(int defcon) {
		if (AIautoTime >= AIpause) {
			// Reset timer (not set to 0)
			AIautoTime -= AIpause;
			
			// check yo'self
			setStrategy(defcon);
			
			switch (strategy) {
				case AiUtils.AI_WAITING:
					// try stay still
					nextWaypoint.set(fighter.position);
					targetEnemyRandom();
					break;
					
				case AiUtils.AI_DEFENSIVE:
					nextWaypoint.set(getNextPatrolWaypoint());
					nextTarget.set(nullTarget);
					break;
					
				case AiUtils.AI_OFFENSIVE:
					nextWaypoint.set(enemyPosition);
					targetEnemyRandom();
					break;
	
				case AiUtils.AI_BESERK:
					nextWaypoint.set(getRandomVector());
					targetEnemyRandom();
					break;
		
				case AiUtils.AI_ORDERED:
					executeOrders();
					break;
					
				default:
					nextWaypoint.set(getRandomVector());
					nextTarget.set(nullTarget);
					break;
			}
			executeResponse();
		}
	}
	
/*********************************************************************/
	
// STRATEGY FUNCTIONS
	
	private void setStrategy(int defcon) {	
		// basic strategy overrides

		if (defcon == AiUtils.DEFCON_WAIT) {
			strategy = defcon;
		}
		
		else if (defcon == AiUtils.DEFCON_CALM) {
			// normal conditions, check self
			if (strategy == AiUtils.AI_ORDERED) {
				// previously under orders, gateway now deactivated
				if (FFScreen.DEBUG) Log.i(TAG, "strategy calm, loadAdhoc.");
				// reset
				fighter.swapOrderedTexture(false);
				objectiveReached = false;
				loadAdhocPatrol();
			}
			
			if (fighter.hitPoints <= 20) {
				AIpause = FFScreen.engine.settings.OFFENSIVE_PAUSE;
				strategy = AiUtils.AI_OFFENSIVE;
			}
			
			else if (targetInRange(enemyPosition)) {
				AIpause = FFScreen.engine.settings.OFFENSIVE_PAUSE;
				strategy = AiUtils.AI_OFFENSIVE;
			}
			
			else {
				// reset in case
				AIpause = FFScreen.engine.settings.SLOW_PAUSE;
				strategy = defcon;
			}
		}
		
		else if (defcon == AiUtils.DEFCON_ATTACK) {
			// press the attack
			// TO BE IMPLEMENTED
		}
		
		else if (defcon == AiUtils.DEFCON_WARNING) {				
			// then check if on flightpath or at objective
			if (processOrders()) {
				AIpause = FFScreen.engine.settings.WARNING_PAUSE;
				strategy = defcon;
			}
			
			else {
				if (FFScreen.DEBUG) Log.i(TAG, "processOrders false, go adhoc.");
				fighter.swapOrderedTexture(false);
				objectiveReached = false;
				loadAdhocPatrol();
			}
		}
		
		else if (defcon == AiUtils.DEFCON_ALERT) {
			// last gasp for this side
			AIpause = FFScreen.engine.settings.DEFENSIVE_PAUSE;
			strategy = defcon;
		}
	}
	
	private boolean processOrders() {
		if (objectiveReached) 
			return true; // bypass
		
		if (flightPath != null) 
			return true; // has flightPath
		
		if (flightPath == null) {
			// no flightPath, check if can get one
			if (loadFlightPath(powerupPosition)) {
				// has flightPath, set target to help clear of any obs, enemy
				nextTarget.set(powerupPosition);
				return true;
			}
		}	
		return false;
	}
	
	
	private void executeOrders() {
		nextTarget.set(powerupPosition);
		// firstly check for this
		if (objectiveReached) {	
			nextWaypoint.set(getNextPatrolWaypoint());
			nextTarget.set(switchPosition);
		}
		
		else if (objectiveInRange(powerupPosition)) {
			if (FFScreen.DEBUG) Log.i(TAG, "executeOrders, objectiveInRange(), trying objective patrol.");	
			loadObjectivePatrol(powerupPosition);
		}
		
		// then check if too slow
		else if (responseTimer >= responseTimerMax) {
			responseTimer -= responseTimerMax;

			flightPath = Navigator.refactorFlightPath(flightPath, fighter.position);
			setFlightPath();
			
			if (collider.equals(nullTarget) == false) {
				//check if collider is between fighter.position and objective
				if (Navigator.targetVectorInline(fighter.position, nextWaypoint, collider)) {
					if (FFScreen.DEBUG) Log.i(TAG, "targetVectorInline, calling unStuckUnit()...");						
					unstuckUnit();
				}				
			}
			// responseTimer reached, updated flightPath
			nextWaypoint.set(getNextFlightPathWaypoint());
		}
		else {
			// still on flightPath 
			nextWaypoint.set(getNextFlightPathWaypoint());
		}
	}
	
	private void executeResponse() {
		fighter.velocity.set(AiUtils.setVelocity(
				fighter.position, 
				nextWaypoint, 
				strategy != AiUtils.AI_ORDERED));
		
		if (nextTarget.equals(nullTarget) == false) {
			fireWeapon();
		}
	}
	
/*********************************************************************/	
	
// FLIGHT PATH AND ORDERS
	
	private void loadAdhocPatrol() {
		// start new patrol based upon current location
		if (flightPath != null) flightPath = null;
		AIpause = FFScreen.engine.settings.SLOW_PAUSE;
		strategy = AiUtils.AI_DEFENSIVE; // default strategy
		
		loadPatrolWaypoints(Navigator.getLocalScanEmpty(fighter.position));	
	}
	
	private boolean loadFlightPath(Vector2 objective) {
		flightPath = new Array<Vector2>();
		flightPath = Navigator.pathFinderScan(fighter.position, objective);
		
		if (flightPath != null) {
			AIpause = FFScreen.engine.settings.WARNING_PAUSE;			
			flightPath = Navigator.smoothPath(flightPath);
			setFlightPath();
			return true;
		}
		else 
			return false;
	}
	
/*********************************************************************/
	
// WEAPONS FUNCTION

	private void fireWeapon() {
		// add an offset to ensure not shooting self
		if (fighter.chargedUp == IdManager.POWERUP) {
			fighter.swapTexture(IdManager.NULL_ID);// reset it
			FFModel.getFFModel().weapons.add(new Cable(
					IdManager.POWER_CABLE, 
					fighter.cogOffset, 
					nextTarget));
		}		
		else if (fighter.chargedUp == IdManager.BOMBUP) {
			fighter.swapTexture(IdManager.NULL_ID);// reset it
			FFModel.getFFModel().weapons.add(new Cable(
					IdManager.MISSILE_BOMB,
					fighter.cogOffset,
					nextTarget));
		}
		else {
			FFModel.getFFModel().weapons.add(new Bullet(
					IdManager.DRONE_BULLET, 
					fighter.cogOffset, 
					nextTarget, 
					fighter.bulletDamage));
		}
	}
	
	private void targetEnemyRandom() {
		if (targetInRange(enemyPosition)) {
			nextTarget.set(enemyPosition);
		}
		else {
			nextTarget.set(getRandomVector());
		}
	}
	
/*********************************************************************/
	
// WAYPOINT FUNCTIONS
	
	private Vector2 getNextPatrolWaypoint() {
		if (nextWaypoint.equals(patrolWaypoint1)) {
			return patrolWaypoint2;
		}
		else 
			return patrolWaypoint1;
	}
	
	private Vector2 getNextFlightPathWaypoint() {
		// going too fast for this to keep up?
		if (flightPath.size > 0) {
			currentWaypoint++;
			if (currentWaypoint < maxWaypoints) {
				return flightPath.get(currentWaypoint);
			}
			else if (objectiveInRange(powerupPosition)) {
				if (FFScreen.DEBUG) Log.i(TAG, "end flightPath reached, loading objective patrol.");
				loadObjectivePatrol(powerupPosition);
				return getNextPatrolWaypoint();
			}
			else {
				// this
				if (FFScreen.DEBUG) Log.i(TAG, "end flightPath, objective not reached, updateFlightPath.");
				flightPath = Navigator.refactorFlightPath(flightPath, fighter.position);
				setFlightPath();
				return flightPath.get(currentWaypoint);
			}
		}
		else 
			return getRandomVector();
	}
	
	private void loadPatrolWaypoints(int direction) {
		if (FFScreen.DEBUG) Log.i(TAG, "load patrol waypoints");		
		patrolWaypoint1.set(fighter.position);
		
		if (direction == IdManager.UNKNOWN) {
			if (FFScreen.DEBUG) Log.i(TAG, "patrol direction unknown.");
			nextTarget = Navigator.findClosestTarget(fighter.position);
			
			if (nextTarget.equals(nullTarget)) {
				// is stuck in a box of non-shootables...
				if (FFScreen.DEBUG) Log.i(TAG, "non shootable, strategy waiting...");
				patrolWaypoint2.set(patrolWaypoint1);
				strategy = AiUtils.AI_WAITING;
			}
			else {
				// shoot before it loses target in later functions
				fireWeapon();
			}
		}
		else {
			patrolWaypoint2.set(AiUtils.getPatrolWaypoint2(patrolWaypoint1, direction));
		}
	}
	
	private void loadObjectivePatrol(Vector2 objective) {
		// patrol from drone.position -> objective.position -> buffer
		objectiveReached = true;
		fighter.swapOrderedTexture(true);
		patrolWaypoint1.set(fighter.position);
		
		patrolWaypoint2.set(
				AiUtils.extendVectorWaypoint(patrolWaypoint1, objective));
		
		nextWaypoint.set(patrolWaypoint2);
	}
	
	
/*********************************************************************/	
	
// HELPER FUNCTIONS
	
	private void setFlightPath() {
		// called from loadFlightPath() and updateFlightPath()
		flightPath.shrink();
		currentWaypoint = 0;
		maxWaypoints = flightPath.size;
		responseTimerMax = maxWaypoints;
		responseTimer = 0;
	}	
	
	
	private void checkPatrolRange() {
		//TODO
		if (patrolWaypoint1.dst(patrolWaypoint2) <= fighter.diameter) {
			if (FFScreen.DEBUG) Log.i(TAG, "got one cell patrol, try patrolScan.");
			Array<Vector2> temp = new Array<Vector2>(); // temp array for 2 waypoints
			temp = Navigator.patrolScan(fighter.position, 3, enemyPosition); // 3 cells range, not pixels
			if (temp != null) {
				patrolWaypoint1 = temp.get(0);
				patrolWaypoint2 = temp.get(1);
				temp = null;
			}
			else {
				// eh...?
			}
		}
	}
	
	
	private boolean objectiveInRange(Vector2 objective) {
		return (fighter.position.dst(objective) <= FFScreen.engine.settings.TARGET_RANGE_MIN);		
	}
	
	
	private boolean targetInRange(Vector2 target) {
		return (fighter.position.dst(target) <= FFScreen.engine.settings.TARGET_RANGE_MAX);
	}
	
	
	private Vector2 getRandomVector() {
		return AiUtils.randomMoveVector();
	}
}