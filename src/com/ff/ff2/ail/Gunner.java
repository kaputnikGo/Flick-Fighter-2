package com.ff.ff2.ail;

import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.eng.FFModel;
import com.ff.ff2.ent.Bullet;
import com.ff.ff2.ent.Turret;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.CollisionUtils;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.nav.Navigator;

//import android.util.Log;

public class Gunner {
	//private static final String TAG = Gunner.class.getSimpleName();	
	private float AIpause;
	private float AIautoTime;
	
	private Vector2 enemyPosition;
	private Vector2 nextTarget;
	private Vector2 nullTarget;
	private Vector2 primaryTarget;
	
	private Turret turret;
	private int strategy;
	
	public Gunner(Turret turret) {
		this.turret = turret;
		strategy = AiUtils.AI_WAITING;
		
		// new mem friendly targeting system 
		nullTarget = new Vector2();
		nullTarget.set(Navigator.nullTarget);
		
		nextTarget = new Vector2();
		nextTarget.set(nullTarget);
		
		enemyPosition = new Vector2();
		enemyPosition.set(nullTarget);
		
		primaryTarget = new Vector2();
		primaryTarget.set(nullTarget);	
	}
	
	public void destroy() {
		//
	}
	
	
/*********************************************************************/	
	
	public void initTactics(int defcon, Vector2 primaryTarget) {
		AIpause = FFScreen.engine.settings.DEFENSIVE_PAUSE;
		AIautoTime = 0;
		enemyPosition.set(nullTarget);
		
		updateStrategy(defcon);
		this.primaryTarget.set(primaryTarget);
	}
	
	public void updateAI(Vector2 enemyPosition, float deltaTime) {	
		AIautoTime += deltaTime;
		this.enemyPosition.set(enemyPosition);
	}
	
	public void updateStrategy(int defcon) {
		if (AIautoTime >= AIpause) {
			// Reset timer (not set to 0)
			AIautoTime -= AIpause;
			
			setStrategy(defcon);
			
			switch (strategy) {
				case AiUtils.AI_WAITING:
				case AiUtils.AI_DEFENSIVE:
					shootEnemyOnly();
					break;
					
				case AiUtils.AI_OFFENSIVE:
					shootEnemyRandom();
					break;
					
				case AiUtils.AI_ORDERED:
					nextTarget.set(primaryTarget);
					coveringFire();
					break;
					
				case AiUtils.AI_BESERK:
					aimTarget(enemyPosition);
					break;
	
				default:
					nextTarget.set(nullTarget);
					break;
			}		
			executeResponse();
		}
	}
	
/*********************************************************************/
	
// STRATEGY FUNCTIONS
	
	private void setStrategy(int defcon) {
		// basic strategy override: in order of importance
		if (defcon == AiUtils.DEFCON_WAIT) {
			strategy = defcon;
		}
		
		else if (defcon == AiUtils.DEFCON_CALM) {
			// normal conditions, check self
			if (strategy == AiUtils.AI_ORDERED) {
				// previously under orders, gateway now deactivated
				// do anything special here?
				nextTarget.set(nullTarget);
				AIpause = FFScreen.engine.settings.DEFENSIVE_PAUSE;
				strategy = AiUtils.AI_DEFENSIVE;
			}
			
			if (turret.hitPoints <= 20) {
				AIpause = FFScreen.engine.settings.BESERK_PAUSE;
				strategy = AiUtils.AI_BESERK;
			}
			else if (targetInRange(enemyPosition)) {
				AIpause = FFScreen.engine.settings.OFFENSIVE_PAUSE;
				strategy = AiUtils.AI_OFFENSIVE;
			}
			else {
				// nothing to change
				AIpause = FFScreen.engine.settings.DEFENSIVE_PAUSE;
				strategy = defcon;
			}
		}
		
		else if (defcon == AiUtils.DEFCON_ATTACK) {
			// press the attack
			// TO BE IMPLEMENTED
		}
		
		else if (defcon == AiUtils.DEFCON_WARNING) {
			// gatewayActivated
			AIpause = FFScreen.engine.settings.WARNING_PAUSE;
			strategy = defcon;
		}
		
		else if (defcon == AiUtils.DEFCON_ALERT) {
			// last gasp for this side
			AIpause = FFScreen.engine.settings.DEFENSIVE_PAUSE;
			strategy = defcon;
		}
	}
		
	private void executeResponse() {		
		if (nextTarget.equals(nullTarget)) {
			// do nothing
		}
		else {
			fireWeapon();
		}		
	}
	
/*********************************************************************/	
	
// TARGETING & WEAPONS
	
	private void shootEnemyRandom() {
		if (targetInRange(enemyPosition)) {
			nextTarget.set(enemyPosition);
		}
		else {
			nextTarget.set(getRandomTargetVector());
		}
	}
	
	private void shootEnemyOnly() {
		if (targetInRange(enemyPosition)) {
			nextTarget.set(enemyPosition);
		}
		else 
			nextTarget.set(nullTarget);
	}
	
	private void coveringFire() {
		// check for enemy first
		if (targetInRange(enemyPosition)) {
			nextTarget.set(enemyPosition);
		}
		else {
			// set nextTarget to be randomed offset of target
			aimTarget(primaryTarget);
		}
	}
	
	private void aimTarget(Vector2 target) {
		if (targetInRange(target)) {
			// shoot either side of target vector, DO NOT hit vector, could be a drone
			if (Navigator.droneVectorInline(turret.position, target)) {
				// offset it a bit?
				// don't set here ( <- what?)
				// constantly flip this so it alternates the side of target it shoots cover fire
				nextTarget.set(target.x - CollisionUtils.CELL_RADIUS,
						target.y - CollisionUtils.CELL_RADIUS);
			}
			else {
				nextTarget.set(target);
			}
		}
		else {
			nextTarget.set(getRandomTargetVector());
		}
	}

	private void fireWeapon() {
		FFModel.getFFModel().weapons.add(new Bullet(
				IdManager.TURRET_BULLET, 
				turret.cogOffset, 
				nextTarget, 
				turret.bulletDamage));
	}
	
/*********************************************************************/
	
// HELPERS
	
	private boolean targetInRange(Vector2 target) {
		return (turret.position.dst(target) <= FFScreen.engine.settings.TARGET_RANGE_MAX);
	}
	
	private Vector2 getRandomTargetVector() {
		return AiUtils.randomTargetVector(turret.position);
	}
}