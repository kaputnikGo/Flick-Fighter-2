package com.ff.ff2.eng;

/*
 * 
 *  FlickFighter 2 - Model
 *  libGDX port
 *  
 *  min SDK 10 (2.3.3)
 *  
 *  this is the Model of all the objects and logic
 *  
 * 
 */

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ff.ff2.ail.AiUtils;
import com.ff.ff2.ent.Bullet;
import com.ff.ff2.ent.Cable;
import com.ff.ff2.ent.Fighter;
import com.ff.ff2.ent.Turret;
import com.ff.ff2.ent.Weapon;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.gui.FFSplash;
import com.ff.ff2.lib.Collision;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.lib.SoundManager;
import com.ff.ff2.nav.Navigator;
import com.ff.ff2.obs.Barrier;
import com.ff.ff2.obs.Bombup;
import com.ff.ff2.obs.Gateway;
import com.ff.ff2.obs.Hanger;
import com.ff.ff2.obs.Switch;
import com.ff.ff2.obs.Mine;
import com.ff.ff2.obs.Obstacle;
import com.ff.ff2.obs.Powerup;
import com.ff.ff2.obs.Pushblock;
import com.ff.ff2.obs.Scaffold;
import com.ff.ff2.sfx.ParticleEmitter;

//import android.util.Log;

public class FFModel {
	//private static final String TAG = FFModel.class.getSimpleName();
	public Array<Weapon> weapons = new Array<Weapon>();
	public ParticleEmitter particleEmitter;

	public FFPrivate attackerPrivate;
	public FFGeneral fortressGeneral;
	public boolean playerControlled = true;
	public FFField field;

	private float respawnTimeMax;
	private float respawnTime;
	
	private Vector2 splashPosition;
	private FFSplash titleSplash;
	private FFSplash gatewaySplash;
	private FFSplash gameoverSplash;
	
	public static FFModel ffModel;
	
	public static FFModel getFFModel() {
		if (ffModel == null) {
			ffModel = new FFModel();
		}
		return ffModel;
	}
	
	public void readyModel() {
		// load splashes
		splashPosition = new Vector2(0, FFScreen.gameCentreY - FFScreen.splashSize / 2);
		titleSplash = new FFSplash(splashPosition, FFSplash.SPLASH_TITLE);
		gatewaySplash = new FFSplash(splashPosition, FFSplash.SPLASH_NEXT_FIELD);
		gameoverSplash = new FFSplash(splashPosition, FFSplash.SPLASH_GAME_OVER);
		// select player type here
		fortressGeneral = new FFGeneral(!playerControlled);
		fortressGeneral.reset();
		attackerPrivate = new FFPrivate(playerControlled);
		attackerPrivate.reset();
		
		field = new FFField();
		respawnTime = 0;
		respawnTimeMax = 4f;
	}
	
	public void destroy() {
		if (particleEmitter != null) particleEmitter.dispose();
		if (fortressGeneral != null) fortressGeneral = null;
		if (attackerPrivate != null) attackerPrivate = null;
		if (weapons != null) weapons = null;
		if (field != null) {
			field.destroy();
			field = null;
		}
		ffModel = null;
	}
	
/*****************************************************************/
	
	public void clearField() {
		weapons.clear();		
		fortressGeneral.reset();
		attackerPrivate.reset();
		field.reset();
		if (particleEmitter != null) particleEmitter.dispose();
	}

	public void initField() {
		// called by Engine when a fortress has completed loading and is ready to be used
		Navigator.prepareNavigator();	
		particleEmitter = new ParticleEmitter();		
		attackerPrivate.initFighter(field.fortress.privateFighterStartVector);	
		fortressGeneral.initWeapons();
		respawnTime = 0;
	}
	
	
	private void respawnPowerup() {
		field.createSpawner();	
		// restart timer
	    respawnTime -= respawnTimeMax;
	}
	
/*****************************************************************/
	
	public void droneDestruct(Vector2 position) {
		SoundManager.playCrash(IdManager.GENERAL_ID);
		particleEmitter.scaledExplosion(position, 8.f, FFScreen.fps, IdManager.DRONE_BULLET);
	}
	
	public void unitSelfDestruct(Vector2 position) {
		debrisExplode(position, IdManager.MISSILE_BOMB);
	}
	
	public void weaponHitFighter(Fighter fighter, Weapon weapon) {
		fighter.damage(weapon.damage);
		weaponHitObject(fighter.position, 20.f, weapon);
		
		if (fighter.id == IdManager.PRIVATE_ID) 
			attackerPrivate.fighterHit();	
		
		if (fighter.alive == false)
			weaponHitObject(fighter.position,  8.f, weapon);
	}
	
	public void weaponHitObstacle(Obstacle obstacle, Weapon weapon) {	
		if (weapon.id == IdManager.MISSILE_BOMB) {
			// skip here, bomb blows up on everything, regardless
			SoundManager.playHitObs();
		}
	
		else if (obstacle instanceof Scaffold) {
			// skip, do nothing here, just explode the weapon
			SoundManager.playHitObs();
		}
		
		else if (obstacle instanceof Hanger) {
			// only allow private bullets to damage
			if (weapon.id == IdManager.PRIVATE_BULLET) {
				SoundManager.playHitObs();
				obstacle.damage(weapon.damage);
			}
		}
		
		else {
			obstacle.damage(weapon.damage);
			if (weapon instanceof Cable) {
				if (obstacle instanceof Barrier) {
					// make it bounce
					weapon.collidedPosition(obstacle.position);
				}
				else if (obstacle instanceof Switch) {
					if (weapon.id == IdManager.POWER_CABLE) {
						fortressGeneral.gatewayActivated();
						field.gatewayActivated();
					}
				}
			}
			else if (weapon instanceof Bullet) {	
				SoundManager.playHitObs();
				if (obstacle instanceof Mine) {
					if (obstacle.alive == false)
						debrisExplode(obstacle.position, weapon.id);
				
					else 
						weaponHitObject(obstacle.position, 15.f, weapon);
				}
				
				else if (obstacle instanceof Barrier)
					weaponHitObject(obstacle.position,  2.f, weapon);
				
				else
					weaponHitObject(obstacle.position,  1.f, weapon);	
	
				particleEmitter.scaledExplosion(obstacle.position, 15.f, FFScreen.fps, weapon.id);
			}
		}
		// always end weapon with a bang
		weaponExplode(weapon);
	}
	
	public void weaponHitTurret(Turret turret, Weapon weapon) {
		turret.damage(weapon.damage);
		weaponHitObject(turret.position, 8.f, weapon);
		if (turret.alive == false) 
			SoundManager.playCrash(IdManager.GENERAL_ID); 
	}
		
	public void fighterCollideObstacle(Obstacle obstacle, Fighter fighter) {
		if (obstacle instanceof Gateway &&
				fighter.id == IdManager.PRIVATE_ID) {
			fighterCollideGateway(obstacle.position);
		}
		
		else if (obstacle instanceof Powerup 
				|| obstacle instanceof Bombup) {
			fighterCollidePowerup(obstacle, fighter);
			obstacle.alive = false;
		}
		
		else if (obstacle instanceof Mine) {
			SoundManager.playHitObs();
			fighter.damage(obstacle.damage);
			fighterCollideMine(obstacle.position, fighter.id);
			fighter.collidedPosition(obstacle.position, 1.5f);
			obstacle.alive = false;
		}
		
		else if (obstacle instanceof Pushblock) {
			SoundManager.playBounce(fighter.id);
			((Pushblock) obstacle).collidedFighter(fighter.position);
			fighter.collidedPosition(obstacle.position, 0);
		}
		
		else {
			// no damage for crash yet?	
			SoundManager.playHitObs();
			fighter.collidedPosition(obstacle.position, 0);
		}
	}
	
/*****************************************************************/
	
	private void weaponHitObject(Vector2 position, float scale, Weapon weapon) {
		SoundManager.playHitObs();
		particleEmitter.scaledExplosion(position, scale, FFScreen.fps, weapon.id);
		weaponExplode(weapon);
	}
	
	private void weaponExplode(Weapon weapon) {	
		if (weapon instanceof Bullet) {
			particleEmitter.bulletExplosion(weapon.position, FFScreen.fps, weapon.id);
		}
		else if (weapon instanceof Cable) {
			particleEmitter.scaledPowerupCharge(weapon.position, 12.f, weapon.id);
			if (weapon.id == IdManager.MISSILE_BOMB) {
				debrisExplode(weapon.position, weapon.id);
			}
		}
		
		weapon.alive = false;
		weapons.removeValue(weapon, true);
	}

	private void debrisExplode(Vector2 position, int id) {
		SoundManager.playBullet(IdManager.MINE_DEBRIS);
		particleEmitter.scaledMassiveExplosion(position, 20.f, id);
		for (int i = 0; i <= 6; i++) {
			weapons.add(new Bullet(IdManager.MINE_DEBRIS,  
				position, 
				AiUtils.randomTargetVector(position), 
				20)); // damage
		}
	}
	
	
/*****************************************************************/	
	
	private void fighterCollideGateway(Vector2 position) {
		if (field.gatewayActivated) {
			attackerPrivate.flicking = false;
			attackerPrivate.hitGateway = true;
			particleEmitter.scaledPowerupCharge(position, 20.f, IdManager.POWER_CABLE);
		}
	}
	
	private void fighterCollidePowerup(Obstacle obstacle, Fighter fighter) {
		SoundManager.playPowerup(fighter.id);
		if (obstacle instanceof Powerup) {
			if (fighter.id == IdManager.PRIVATE_ID) {
				attackerPrivate.powerUp(IdManager.POWERUP);
			}
			else if (fighter.id == IdManager.GENERAL_ID) {
				fortressGeneral.powerUp(IdManager.POWERUP, fighter);
			}
			particleEmitter.scaledPowerupCharge(obstacle.position,  12.f, IdManager.POWER_CABLE);
			respawnPowerup();
		}
		else if (obstacle instanceof Bombup) {
			if (fighter.id == IdManager.PRIVATE_ID) {
				attackerPrivate.powerUp(IdManager.BOMBUP);
			}
			else if (fighter.id == IdManager.GENERAL_ID) {
				fortressGeneral.powerUp(IdManager.BOMBUP, fighter);
			}
			particleEmitter.scaledPowerupCharge(obstacle.position,  12.f, IdManager.MISSILE_BOMB);
		}
	}
	
	
	private void fighterCollideMine(Vector2 position, int fighterID) {
		if (fighterID == IdManager.PRIVATE_ID) {
			attackerPrivate.fighterHit();	
		}
		debrisExplode(position, IdManager.MISSILE_BOMB);
	}
	
/*****************************************************************/
	
	public void gameModelUpdate(float deltaTime) {
		if (fortressGeneral.alive && attackerPrivate.alive) {
			fortressGeneral.update(deltaTime, attackerPrivate.fighter.position);
		}
		if (attackerPrivate.alive) {
			attackerPrivate.update(deltaTime);
		}
		// make this a boolean
		respawnTime += deltaTime;
		if (respawnTime >= respawnTimeMax) {
			field.spawnSpawner();
			// Reset timer (not set to 0)
		    respawnTime -= respawnTimeMax;
		}
	}
	
/*****************************************************************/
	
	public void drawGameLoop(SpriteBatch gameBatch, float deltaTime) {		
		field.draw(gameBatch, deltaTime);

		for (Weapon weapon : weapons) {
			if (weapon.alive) {	
				Collision.collisionWeaponCheck(weapon);
				weapon.draw(gameBatch, deltaTime);
			} 
			else {
				weaponExplode(weapon);
			}
		}

		if (attackerPrivate.alive) {
			attackerPrivate.draw(gameBatch);
		}
			
		if (fortressGeneral.alive) {	
			fortressGeneral.draw(gameBatch, deltaTime);
		}
		particleEmitter.draw(gameBatch, deltaTime);
	}

	// TODO no audio calls in draw loops...!
	public void drawTitleSplash(SpriteBatch gameBatch) {
		SoundManager.demoSplash();
		titleSplash.draw(gameBatch);
	}
	
	public void drawGatewayTravel(SpriteBatch gameBatch, float deltaTime) {
		SoundManager.gatewayTravel();
	    gatewaySplash.draw(gameBatch);
		particleEmitter.draw(gameBatch, deltaTime);
	}
	
	public void drawDeathLoop(SpriteBatch gameBatch, float deltaTime) {
		SoundManager.gameOver();	
		field.draw(gameBatch, deltaTime);
	    gameoverSplash.draw(gameBatch);
		particleEmitter.draw(gameBatch, deltaTime);
	}	
}