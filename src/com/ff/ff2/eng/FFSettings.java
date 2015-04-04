package com.ff.ff2.eng;

import android.util.Log;

import com.ff.ff2.gui.FFScreen;

/*
 * 
 *  FlickFighter 2 - Settings
 *  libGDX port
 *  
 *  min SDK 10 (2.3.3)
 *  
 *  this is the settings class for difficulty settings etc,
 *  save to prefs file, user editable
 *   -- groups should be in ratios so that a single var can change them all
 *   	but ratio remains same
 *  
 * 
 */

public class FFSettings {
	private static final String TAG = FFSettings.class.getSimpleName();
	
	public boolean DEBUG_LOG;
	public boolean DEBUG_NAVPATH;
	public boolean DEBUG_OSD;
	
	public int currentDifficulty;
	public static final int EXIT = 0;
	public static final int EASY = 1;
	public static final int NORM = 2;
	public static final int HARD = 3;
	public static final int OSD = 4;
	
// range / path vars
	public float rangeUnit = FFScreen.gameCellSize; // 32px, 1 cell
	
	public float TARGET_RANGE_MAX; //320 not so long a range...
	public float TARGET_RANGE_MIN; // 64	
	public float PATROL_RANGE_MAX; // 128
	public float PATROL_RANGE_MIN; // 64
	
// enemy vars
	public int droneUnit;
	public int MAX_DRONES;
	public int MIN_DRONES;
	public int MAX_HANGERS;
	public int MAX_BOMBUPS;
	public int MAX_MINES;
	public int MAX_TURRETS;
	
	public float pauseUnit;
	
	public float DEPLOY_PAUSE;
	public float SLOW_PAUSE;
	public float DEFENSIVE_PAUSE;
	public float WARNING_PAUSE;
	public float OFFENSIVE_PAUSE;
	public float BESERK_PAUSE;
	
// velocity vars
	public float velocityUnit;
	
	public float VELOCITY_SLOW;
	public float VELOCITY_NORM;
	public float VELOCITY_FAST;
	
// hitpoints / damage vars
	public int hpDamUnit;
	
	public int PRIVATE_HP;
	public int PRIVATE_DAMAGE;
	public int DRONE_HP;
	public int DRONE_DAMAGE;
	
	public int TURRET_HP;
	public int TURRET_DAMAGE;
	
	public int MINE_DAMAGE;
	
// obstacle hitpoints
	public int FENCE_HP;
	public int WORD_HP;
	public int MINE_HP;
	public int HANGER_HP;
	public int BARRIER_HP;
	
	
	
	public FFSettings() {
		// constr.
		rangeUnit = FFScreen.gameCellSize; // 32px, 1 cell	
		// catch for init FFSettings
		DEBUG_LOG = true;
		DEBUG_OSD = false
				;
		setDifficulty(NORM);
		loadSettings();
	}
	
	public void loadDebug(boolean log, boolean nav) {
		DEBUG_LOG = log;
		DEBUG_NAVPATH = nav;
	}
	
	public void loadSettings() {
		// set to defaults
		if (DEBUG_LOG) Log.i(TAG, "load settings.");
		TARGET_RANGE_MAX = rangeUnit * 10; //320 not so long a range...
		TARGET_RANGE_MIN = rangeUnit * 2; // 64	
		PATROL_RANGE_MAX = rangeUnit * 4; // 128
		PATROL_RANGE_MIN = rangeUnit * 2; // 64
		
	// enemy vars
		MAX_HANGERS = droneUnit;
		MAX_BOMBUPS = droneUnit * 1;
		MAX_MINES = droneUnit * 2;
		MIN_DRONES = droneUnit * 2; // trigger respawn at half num of drones
		MAX_DRONES = droneUnit * 4;
		MAX_TURRETS = droneUnit * 6;
				
		DEPLOY_PAUSE = pauseUnit * 10;
		SLOW_PAUSE = pauseUnit * 5;
		DEFENSIVE_PAUSE = pauseUnit * 4;
		WARNING_PAUSE = pauseUnit * 3;
		OFFENSIVE_PAUSE = pauseUnit * 2;
		BESERK_PAUSE = pauseUnit;
		
	// velocity vars	
		VELOCITY_SLOW = velocityUnit;
		VELOCITY_NORM = velocityUnit * 2;
		VELOCITY_FAST = velocityUnit * 4;
		
	// hitpoints / damage vars	
		PRIVATE_HP = hpDamUnit * 4;
		PRIVATE_DAMAGE = hpDamUnit * 2;
		DRONE_HP = hpDamUnit * 3;
		DRONE_DAMAGE = hpDamUnit;
		
		TURRET_HP = hpDamUnit * 4;
		TURRET_DAMAGE = hpDamUnit;
		
		MINE_DAMAGE = hpDamUnit * 2;
		
	// obstacle hitpoints
		FENCE_HP = hpDamUnit * 2;
		WORD_HP = hpDamUnit * 3;
		MINE_HP = hpDamUnit * 4;
		HANGER_HP = hpDamUnit * 8;
		BARRIER_HP = hpDamUnit * 10;
		
	}
	
	public void reset() {
		// reset to last prefs, fall back to defaults if none
		setDifficulty(NORM);
		loadSettings();
	}
	
	public void setOSD() {
		DEBUG_OSD ^= true;
		if (DEBUG_LOG) Log.i(TAG, "setOSD: " + DEBUG_OSD);
	}
	
	public void setDifficulty(int choice) {
		if (DEBUG_LOG) Log.i(TAG, "setDifficulty: " + choice);
		
		if (choice == EASY) {
			droneUnit = 1; // 4 drones
			pauseUnit = 1.f; // defensive = 4 secs
			velocityUnit = 150.f; // norm speed = 300
			hpDamUnit = 15; // fighter hp = 60
			currentDifficulty = choice;
		}
		else if (choice == NORM) {
		// default
			//rangeUnit = FFScreen.gameCellSize; // 32px, 1 cell
			droneUnit = 2; // 8 drones
			pauseUnit = .5f; // defensive = 2secs
			velocityUnit = 200.f; // normal speed = 400
			hpDamUnit = 10;	// fighter hp = 40
			currentDifficulty = choice;
		}
		else if (choice == HARD) {
			//rangeUnit = FFScreen.gameCellSize; // 32px, 1 cell
			droneUnit = 3; // 12 drones
			pauseUnit = .4f; // defensive (* 4) = 1.6 sec
			velocityUnit = 250.f; // normal speed = 500
			hpDamUnit = 5;	// fighter hp = 20
			currentDifficulty = choice;
		}
		loadSettings();
	}
	
}