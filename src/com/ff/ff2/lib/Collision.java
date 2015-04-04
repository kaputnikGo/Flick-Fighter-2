package com.ff.ff2.lib;

import com.badlogic.gdx.math.Rectangle;
import com.ff.ff2.eng.FFModel;
import com.ff.ff2.ent.Fighter;
import com.ff.ff2.ent.Turret;
import com.ff.ff2.ent.Weapon;
import com.ff.ff2.obs.Obstacle;
import com.ff.ff2.obs.Wordblock;

//import android.util.Log;

public class Collision {
	//private static final String TAG = Collision.class.getSimpleName();
	private static Turret turret;
	private static Fighter drone;
	private static Obstacle obstacle;	
	
	public static void collisionWeaponCheck(Weapon weapon) {
		// check drones, not self
		if (FFModel.getFFModel().fortressGeneral.squadronAlive 
				&& weapon.id != IdManager.DRONE_BULLET) {
			for (int n = 0; n < FFModel.getFFModel().fortressGeneral.squadron.size; n++) {
				drone = FFModel.getFFModel().fortressGeneral.squadron.get(n);
				if (weaponHitFighter(drone, weapon)) {
					return;
				}
			}
		}
		// check turrets, not self
		if (FFModel.getFFModel().fortressGeneral.turretsAlive 
				&& weapon.id != IdManager.TURRET_BULLET) {
			for (int n = 0; n < FFModel.getFFModel().fortressGeneral.turrets.size; n++) {
				turret = FFModel.getFFModel().fortressGeneral.turrets.get(n);
				if (weaponHitTurret(turret, weapon)) {
					return;
				}
			}
		}
		// check player, not self
		if (FFModel.getFFModel().attackerPrivate.alive 
				&& weapon.id != IdManager.PRIVATE_BULLET) {
			if (weaponHitFighter(FFModel.getFFModel().attackerPrivate.fighter, weapon)) {
				return;
			}
		}
		// check obstacles
		for (int n = 0; n < FFModel.getFFModel().field.fortress.obstacles.size; n++) {
			obstacle = FFModel.getFFModel().field.fortress.obstacles.get(n);
			if (obstacle.alive) {
				if (weaponHitObstacle(obstacle, weapon)) {
					return;
				}
			}
		}
	}
	
	public static boolean collisionFighterCheck(Fighter fighter) {		
		if (fighter.alive) {			
			for (int n = 0; n < FFModel.getFFModel().field.fortress.obstacles.size; n++) {
				obstacle = FFModel.getFFModel().field.fortress.obstacles.get(n);
				if (fighterHitObstacle(obstacle, fighter)) {
					return true;
				}
			}
			if (FFModel.getFFModel().fortressGeneral.turretsAlive) {
				for (int n = 0; n < FFModel.getFFModel().fortressGeneral.turrets.size; n++) {
					turret = FFModel.getFFModel().fortressGeneral.turrets.get(n);
					if (fighterHitTurret(turret, fighter)) {
						return true;
					}
				}
			}
			if (FFModel.getFFModel().fortressGeneral.squadronAlive) {
				for (int n = 0; n < FFModel.getFFModel().fortressGeneral.squadron.size; n++) {
					drone = FFModel.getFFModel().fortressGeneral.squadron.get(n);
					// not self
					if (drone != fighter) {
						if (fighterHitFighter(fighter, drone)) {
							return true;
						}
					}
				}
			}
			if (fighter.id == IdManager.GENERAL_ID) {
				if (fighterHitFighter(fighter, FFModel.getFFModel().attackerPrivate.fighter)) {
					return true;
				}
			}
		}
		return false;
	}

	
	public static boolean collisionObstacleCheck(Obstacle collider) {
		if (FFModel.getFFModel().fortressGeneral.turretsAlive) {
			for (int n = 0; n < FFModel.getFFModel().fortressGeneral.turrets.size; n++) {
				turret = FFModel.getFFModel().fortressGeneral.turrets.get(n);
				if (obstacleHitBoundBox(collider, turret.boundBox)) {
					return true;
				}
			}
		}
		if (FFModel.getFFModel().fortressGeneral.squadronAlive) {
			for (int n = 0; n < FFModel.getFFModel().fortressGeneral.squadron.size; n++) {
				drone = FFModel.getFFModel().fortressGeneral.squadron.get(n);
				if (obstacleHitBoundBox(collider, drone.boundBox)) {
					return true;
				}
			}
		}
		for (int n = 0; n < FFModel.getFFModel().field.fortress.obstacles.size; n++) {
			obstacle = FFModel.getFFModel().field.fortress.obstacles.get(n);
			// not self
			if (obstacle != collider) {
				if (obstacleHitBoundBox(obstacle, collider.boundBox)) {
					return true;
				}
			}
		}
		return false;
	}

	
/*****************************************************************/
	
	private static boolean weaponHitTurret(Turret turret, Weapon weapon) {
		// pass this to the general?
		if (turret.boundBox.overlaps(weapon.boundBox)) {
			FFModel.getFFModel().weaponHitTurret(turret, weapon);
			return true;
		}
		return false;
	}
	
	private static boolean weaponHitFighter(Fighter fighter, Weapon weapon) {	
		if (fighter.boundBox.overlaps(weapon.boundBox)) {
			FFModel.getFFModel().weaponHitFighter(fighter, weapon);
			return true;
		}
		return false;
	}
	
	private static boolean weaponHitObstacle(Obstacle obstacle, Weapon weapon) {
		if (obstacle.boundBox.overlaps(weapon.boundBox)) {
			FFModel.getFFModel().weaponHitObstacle(obstacle, weapon);
			return true;
		}
		return false;
	}
	
	private static boolean obstacleHitBoundBox(Obstacle obstacle, Rectangle collider) {
		if (obstacle.boundBox.overlaps(collider)) {
			return true;
		}
		return false;
	}

/*****************************************************************/
	
	private static boolean fighterHitFighter(Fighter first, Fighter second) {
		if (first.alive && second.alive) {
			if (first.boundBox.overlaps(second.boundBox)) {
				first.collidedPosition(second.position, 0);
				return true;
			}
		}
		return false;
	}
	
	private static boolean fighterHitTurret(Turret turret, Fighter fighter) {
		// check first
		if (turret.alive) {	
			if (turret.boundBox.overlaps(fighter.boundBox)) {
				// no damage for crash yet?
				//turret.damage(bullet.damage);
				fighter.collidedPosition(turret.position, 0);
				return true;
			}
		}
		return false;
	}
	
	private static boolean fighterHitObstacle(Obstacle obstacle, Fighter fighter) {
		// check first
		if (obstacle.alive) {	
			if (obstacle.boundBox.overlaps(fighter.boundBox)) {
				if (obstacle instanceof Wordblock) {
					return false;
				}
				else 
					FFModel.getFFModel().fighterCollideObstacle(obstacle, fighter);
					return true;				
			}
		}
		return false;
	}
}