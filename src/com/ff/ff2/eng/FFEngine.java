package com.ff.ff2.eng;

import android.util.Log;

//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.gui.FFStatus;
import com.ff.ff2.gui.MenuButton;
import com.ff.ff2.gui.Reports;
import com.ff.ff2.lib.DemoManager;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.lib.LoadManager;
import com.ff.ff2.lib.SoundManager;

public class FFEngine {
	private static final String TAG = FFEngine.class.getSimpleName();

	private int GAMESTATE;
	public static final int HALT = 0; // used for userSettings only
	public static final int WAITING = 1;
	public static final int LOADING = 2;
	public static final int RUNNING = 3;
	public static final int SPLASH = 4;
	
	public static final int PRIVATE_DIE = 5;
	public static final int GENERAL_DIE = 6;
	public static final int GATEWAY_TRAVEL = 7;

	public static final int CREATOR_FOCUS = 10;
	public static final int BROKEN = 20;
	
	private final float splashTimeMax;
	private float splashTime;
	private final float pauseTimeMax;
	private float pauseTime;
	
	public boolean demoActive;
	private boolean userFort;
	
	private boolean validFlickTouch;
	public Vector2 privatePosition;
	
	public String fortressName;
	private int currentFieldNum;
	
	public FFSettings settings;
	
	private int statusResult;
    private FFStatus status;
    private MenuButton statusExitButton;
    private boolean statusShow = false;
	
	public FFEngine() {
		GAMESTATE = WAITING;
		statusResult = 0;
		splashTimeMax = 3f;
		splashTime = 0;
		pauseTimeMax = 3f;
		pauseTime = 0;
		
		demoActive = false;
		validFlickTouch = false;
		userFort = false;
		fortressName = "initialising...";
		if (FFScreen.DEBUG) Log.i(TAG, "new engine.");
	}
	
	public void startEngine() {
		if (userFort) {
			// ignore
			// called by FFScreen resetting from Creator
		}	
		else if (!userFort) {
			demoActive = true;
			currentFieldNum = 0;
			privatePosition = new Vector2();
			
			IdManager.loadCellList();
			FFModel.getFFModel().readyModel();
			FFModel.getFFModel().clearField();
			GAMESTATE = SPLASH;
		}	
	}
	
	public void destroy() {
		//
	}
	
	public void loadSettings(boolean log, boolean nav) {
		settings = new FFSettings();		
		settings.loadDebug(log, nav);
	
		if (FFScreen.DEBUG) {
			Log.i(TAG, "new engine.");
			Log.i(TAG, "settings range max: " + settings.TARGET_RANGE_MAX);
			Log.i(TAG, "settings range max: " + settings.TARGET_RANGE_MAX);
			if (SoundManager.sayHi()) {
				Log.i(TAG, "SoundManager says hi.");
			}
			else {
				Log.i(TAG, "SoundManager doesn't say hi...");
			}
		}
	}
	
	public void toggleVolume(boolean toggle) {
		SoundManager.toggleVolume(toggle);	
	}
	
	public void screenReady() {
		GAMESTATE = LOADING;
		loadField();
	}
	
	public boolean requestUserMenu() {
		GAMESTATE = HALT;
		return true;
	}
	
	public void userMenuExit() {
		GAMESTATE = RUNNING;
	}
	
/*********************************************************************/
	
	private void restartEngine() {
		GAMESTATE = SPLASH;
		FFModel.getFFModel().readyModel();
		currentFieldNum = 0;
		validFlickTouch = false;
		demoActive = true;
		userFort = false;
		fortressName = LoadManager.DEMO_FILENAME;
	}
	
    private void displayStatus(int reportInt) {
	    status = new FFStatus(Reports.ENGINE_STATUS, Reports.reportList.get(reportInt));								
		statusExitButton = new MenuButton(status.exitButtonPosition, IdManager.BTN_EXIT);	
		statusShow = true;
    }
    
    private void dismissStatusPopup() {
    	// not much to do here, other than restart
    	restartEngine();
    }
    
/*********************************************************************/	
	
	public int gameStateUpdate(float deltaTime) {
		switch (GAMESTATE) {		
	
			case CREATOR_FOCUS:
				// bypass, creator handles everything
				break;
				
			case WAITING:
				// wait for screen to be ready for loading
				break;
				
			case LOADING:
				// nothing yet
				break;
				
			case RUNNING:
				// update the model
				FFModel.getFFModel().gameModelUpdate(deltaTime);
				
				// check for player FIGHTER alive and position
				if (FFModel.getFFModel().attackerPrivate.fighter.alive) {
					privatePosition.set(FFModel.getFFModel().attackerPrivate.fighter.position);
				}
				// is dead
				else GAMESTATE = PRIVATE_DIE;
	
				// check for gateway
				if (FFModel.getFFModel().attackerPrivate.hitGateway == true) {
					GAMESTATE = GATEWAY_TRAVEL;
				}				
				break;
				
			case PRIVATE_DIE:
				// allow time to show
				pauseTime += deltaTime;
				if (pauseTime >= pauseTimeMax) {
					// stepback counter for replay this level
					currentFieldNum--;
					pauseTime -= pauseTimeMax;
					// clear the splash sounds
					SoundManager.sayHi();
					
					FFModel.getFFModel().clearField();
					if (demoActive) GAMESTATE = SPLASH;
					else GAMESTATE = WAITING;
				}
				break;
				
			case GENERAL_DIE:
				// nothing yet
				break;
				
			case GATEWAY_TRAVEL: 
				// allow time to show
				pauseTime += deltaTime;
				if (pauseTime >= pauseTimeMax) {
					pauseTime -= pauseTimeMax;
					// clear the splash sounds
					SoundManager.sayHi();
					
					FFModel.getFFModel().clearField();
					if (demoActive) GAMESTATE = SPLASH;
					else GAMESTATE = WAITING;
				}
				break;
				
			case SPLASH:
				// splash screen
				splashTime += deltaTime;
				if (splashTime >= splashTimeMax) {
					GAMESTATE = WAITING;
					splashTime -= splashTimeMax;
					// clear the splash sounds
					SoundManager.sayHi();
				}
				break;
				
			case HALT:
				// user prefs settings menu displayed, stop everything
			case BROKEN:
			default :
				// erm...
				// catch all, but nothing yet
				break;
			}
		return GAMESTATE;
	}
	
/*****************************************************************/
	
	
	public void requestCreator() {
		FFModel.getFFModel().clearField(); // clears
		FFModel.getFFModel().destroy(); // is now null
		GAMESTATE = CREATOR_FOCUS;
	}
	
	public void creatorExiting() {
		restartEngine();
	}
	
	public void creatorRequestLoadField(String fortressNameIn, int location) {
		// location not used yet, default to user/internal
		GAMESTATE = WAITING;
		userFort = true;
		FFModel.getFFModel().readyModel();
		currentFieldNum = 0;
		validFlickTouch = false;
		demoActive = false;
		
		fortressName = fortressNameIn;
		if (FFScreen.DEBUG) Log.i(TAG, "creator request fortressName: " + fortressNameIn);
	}
	
/*****************************************************************/

	private void loadField() {
		validFlickTouch = false;
		if (demoActive) {
			// full filename resolve here, in theme folder
			fortressName = LoadManager.DEMO_FILENAME;
			FFModel.getFFModel().attackerPrivate.demoControl = true;
			if (FFScreen.DEBUG) Log.i(TAG, "load demofort name: " + fortressName);
			statusResult = FFModel.getFFModel().field.loadFortress(fortressName, LoadManager.GAME_STORAGE);
			if (statusResult == LoadManager.FILE_LOADED) { 
				FFModel.getFFModel().initField();
				DemoManager.getDemoManager().refresh();
				GAMESTATE = RUNNING;
			}
			else {
				GAMESTATE = BROKEN;
				displayStatus(statusResult);
			}
		}
		
		else if (userFort) {
			if (FFScreen.DEBUG) Log.i(TAG, "load userFort with name, " + fortressName);		
			String userPath = LoadManager.getLoadManager().getLocalStoragePath();
			// check
			if (userPath != null) {
				if (currentFieldNum < 0 ) currentFieldNum = 0;
				
				if (currentFieldNum != 0) {
					if (currentFieldNum >= FFModel.getFFModel().field.getUserFortListSize()) {
						currentFieldNum = 0;
					}
					fortressName = FFModel.getFFModel().field.getNextFortressName(currentFieldNum, LoadManager.USER_STORAGE);
					fortressName = LoadManager.getLoadManager().simpleUserFortName(fortressName);		
				}
				if (FFScreen.DEBUG) {
					Log.i(TAG, "currentFieldNum: " + currentFieldNum);
					Log.i(TAG, "simple fortName: " + fortressName);
				}

				statusResult = FFModel.getFFModel().field.loadFortress(fortressName, LoadManager.USER_STORAGE);
				
				if (statusResult == LoadManager.FILE_LOADED) {
					FFModel.getFFModel().initField();
					GAMESTATE = RUNNING;
					currentFieldNum++;
				}
				else {
					GAMESTATE = BROKEN;
					displayStatus(statusResult);
				}				
			}			
		}
		
		else {
			// load from game fort list
			if (currentFieldNum < FFModel.getFFModel().field.getGameFortListSize()) 
				currentFieldNum++;
			else 
				currentFieldNum = 0;
			
			fortressName = FFModel.getFFModel().field.getNextFortressName(currentFieldNum, LoadManager.GAME_STORAGE);
			
			statusResult = FFModel.getFFModel().field.loadFortress(fortressName, LoadManager.GAME_STORAGE);
			if (statusResult == LoadManager.FILE_LOADED) {
				FFModel.getFFModel().initField();
				GAMESTATE = RUNNING;
			}
			else {
				GAMESTATE = BROKEN;
				displayStatus(statusResult);
			}			
		}
	}
	
/*****************************************************************/

	public void drawGameLoop(SpriteBatch gameBatch, float deltaTime) {		
		if (demoActive && GAMESTATE == SPLASH) {
				FFModel.getFFModel().drawTitleSplash(gameBatch);
	 	}
		
		// and on top
		if (GAMESTATE == PRIVATE_DIE) {
			FFModel.getFFModel().drawDeathLoop(gameBatch, deltaTime);
		}
		else if (GAMESTATE == GATEWAY_TRAVEL) {
			FFModel.getFFModel().drawGatewayTravel(gameBatch, deltaTime);
		}
		else if (GAMESTATE == RUNNING) {
			FFModel.getFFModel().drawGameLoop(gameBatch, deltaTime);
		}
		else if (GAMESTATE == BROKEN) {
			if (statusShow) {
				status.draw(gameBatch);
				statusExitButton.draw(gameBatch);
			}
		}
	}
	
/*********************************************************************/	
	
	public void handleGameValidTouch() {
		validFlickTouch = true;
		demoActive = false;
		FFModel.getFFModel().attackerPrivate.demoControl = false;
	}
	
	public boolean handleGameFling(float velocityX, float velocityY) {
		if (validFlickTouch) {
			FFModel.getFFModel().attackerPrivate.flicking(velocityX, velocityY);	
			validFlickTouch = false;
			return true;
		}
		return false;
	}
	
	public boolean handleGameTap(float x, float y) {
		FFModel.getFFModel().attackerPrivate.fireWeapon(x, y);
		FFModel.getFFModel().fortressGeneral.turretsActive = true;
		return true;
	}
	
	public boolean handleStatusTap(float x, float y) {
		if (statusShow) {
			if (statusExitButton.boundBox.contains(x, y)) {
				dismissStatusPopup();
				return true;
			}
			return false;
		}
		return false;
	}
}
