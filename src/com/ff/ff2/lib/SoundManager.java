package com.ff.ff2.lib;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.ff.ff2.gui.FFScreen;

/*
 * 
 *  using sounds generated from - 
 *  	http://www.superflashbros.net/as3sfxr/
 * 
 */

public class SoundManager {
	private static final String TAG = SoundManager.class.getSimpleName();
	private static Sound fuzzy = Gdx.audio.newSound(Gdx.files.internal("audio/splash_fuzzy.mp3")); // demoSplash
	private static Sound oscar = Gdx.audio.newSound(Gdx.files.internal("audio/splash_oscar.mp3")); // gateway
	private static Sound down = Gdx.audio.newSound(Gdx.files.internal("audio/splash_down.mp3")); // gameover
	
	private static Sound shoot = Gdx.audio.newSound(Gdx.files.internal("audio/laser2.mp3"));
	private static Sound crash = Gdx.audio.newSound(Gdx.files.internal("audio/crash4.mp3"));
	private static Sound hitShip = Gdx.audio.newSound(Gdx.files.internal("audio/crash5.mp3"));
	private static Sound bounce = Gdx.audio.newSound(Gdx.files.internal("audio/bounce3.mp3"));
	private static Sound power = Gdx.audio.newSound(Gdx.files.internal("audio/powerup1.mp3"));
	private static Sound cable = Gdx.audio.newSound(Gdx.files.internal("audio/cable2.mp3"));
	private static Sound hitObs = Gdx.audio.newSound(Gdx.files.internal("audio/crash6.mp3"));
	private static Sound fly = Gdx.audio.newSound(Gdx.files.internal("audio/fly1.mp3"));
	
	public static boolean soundActive;
	
	private static float soundVolume;
	private static boolean splashPlaying;

	
	public SoundManager() {
		//
	}
	
	public static boolean sayHi() {
		if (FFScreen.DEBUG) Log.i(TAG, "SoundManager says hi");
		splashPlaying = false;
		return true;
	}
	
	public static void destroy() {
		dispose();
	}
	
/*****************************************************************/
	
	public static void toggleVolume(boolean toggle) {
		if (toggle) {
			soundVolume = 1.f;
			soundActive = true;
		}
		else {
			soundVolume = 0.f;
			soundActive = false;
		}
	}
	
	public static void demoSplash() {
		if (soundActive) {
			stopAllSounds();
			if (!splashPlaying) {
				fuzzy.play(0.2f);
				splashPlaying = true;
			}
		}
	}
	
	public static void gatewayTravel() {
		if (soundActive) {
			stopAllSounds();
			if (!splashPlaying) {
				oscar.play(0.2f);
				splashPlaying = true;
			}
		}
	}
	
	public static void gameOver() {
		if (soundActive) {
			stopAllSounds();
			if (!splashPlaying) {
				down.play(0.2f, 0.5f, 0);
				splashPlaying = true;
			}
		}
	}
	
/*********************************************************************/
	// triggers 
	
	public static void playFly() {
		if (soundActive) {
			fly.play(soundVolume);
		}
	}
	
	public static void playBullet(int id) {
		if (soundActive) {
			if (id == IdManager.DRONE_BULLET) {
				shoot.play(soundVolume, 1.5f, 0);
			}
			else if (id == IdManager.PRIVATE_BULLET) {
				shoot.play(soundVolume, 2, 0);
			}
			else if (id == IdManager.TURRET_BULLET) {
				shoot.play(soundVolume);
			}
			else if (id == IdManager.MINE_DEBRIS) {
				shoot.play(soundVolume, 0.5f, 0);
			}
		}
	}
	
	public static void playCable(int id) {
		if (soundActive) {
			if (id == IdManager.POWER_CABLE) {
				cable.play(soundVolume, 2, 0);
			}
			else if (id == IdManager.MISSILE_BOMB) {
				cable.play(soundVolume);
			}
			else {
				// catch, no sound
			}
		}
	}
	
	// real loud and kablammo
	public static void playCrash(int id) {
		if (soundActive) {
			if (id == IdManager.PRIVATE_ID) {
				crash.play(soundVolume, 2, 0);
			}
			else if (id == IdManager.GENERAL_ID) {
				crash.play(soundVolume, 0.5f, 0);
			}
			else if (id == IdManager.OBSTACLE_ID) {
				crash.play(soundVolume);
			}
			else {
				// catch, no sound
			}
		}
	}
	
	public static void playBounce(int id) {
		if (soundActive) {
			if (id == IdManager.PRIVATE_ID) {
				bounce.play(soundVolume, 2, 0);
			}
			else if (id == IdManager.GENERAL_ID) {
				bounce.play(soundVolume, 0.5f, 0);
			}
			else {
				// catch
				bounce.play(soundVolume);
			}
		}
	}
	
	public static void playPowerup(int id) {
		if (soundActive) {
			if (id == IdManager.PRIVATE_ID) {
				power.play(soundVolume, 1.5f, 0); // no like 2.f pitch?
			}
			else if (id == IdManager.GENERAL_ID) {
				power.play(soundVolume);
			}
			else {
				// catch
				power.play(soundVolume);
			}
		}
	}
	
	public static void playHitObs() {
		if (soundActive) {
			hitObs.play(0.2f, 1.5f, 0);
		}
	}
	
	public static void playHitShip() {
		if (soundActive) {
			hitShip.play(0.2f, 1, 0);
		}
	}
	
/*********************************************************************/
	
	private static void stopAllSounds() {
		shoot.stop();	
		crash.stop();
		hitShip.stop();
		bounce.stop();
		power.stop();
		cable.stop();
		hitObs.stop();
	}
	
	private static void dispose() {
		shoot.dispose();
		crash.dispose();
		hitShip.dispose();
		bounce.dispose();
		power.dispose();
		cable.dispose();
		hitObs.dispose();
		fuzzy.dispose();
		oscar.dispose();
		down.dispose();
	}
}