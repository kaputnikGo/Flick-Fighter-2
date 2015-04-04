package com.ff.ff2.gui;

/*
 * 
 *  FlickFighter 2 - View/Controller
 *  libGDX port
 *  
 *  min SDK 10 (2.3.3)
 *  
 *  this runs the game loop, renders to screen and listens
 *  for touches with GestureListener
 * 
 */
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ff.ff2.eng.FFEngine;
import com.ff.ff2.eng.FFGame;
import com.ff.ff2.eng.FFModel;
import com.ff.ff2.eng.FFSettings;
import com.ff.ff2.lib.GraphicsManager;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.lib.LoadManager;
import com.ff.ff2.lib.SoundManager;
import com.ff.ff2.obs.Obstacle;

public class FFScreen implements Screen, GestureListener { 
	private static final String TAG = FFScreen.class.getSimpleName();

	private FFGame ffGame;
	public OrthographicCamera camera;
	private SpriteBatch gameBatch;
	private SpriteCache gameCache;
	private int gameCacheSize;
	
	// device game dimensions
	public static int gameWidth;
	public static int gameHeight;
	public static float gameAspectRatio;
	
	public static int gameCellSize;
	public static int gameCellsWide;
	public static int gameCellsHigh;
	public static int gameCentreX;
	public static int gameCentreY;
	
	public static int dialogSize;
	public static int selectorWidth;
	public static int selectorHeight;
	public static int splashSize;
	
	public static int deviceType;
	public static final int NO_DEVICE = 0;
	public static final int MOBILE = 1;
	public static final int TABLET = 2;
	
	// used by particleEmitter
	public static final int FPS_MED = 45;
	public static final int FPS_MIN = 30;
	
	private int deviceWidth;
	private int deviceHeight;
	
    private FFStatus status;
    private MenuButton statusExitButton;
    private boolean statusShow = false;
	
	private GestureDetector gestureDetector;
	private boolean touchEnabled;
	private Vector3 touchVector = new Vector3();

	// player touchArea
	private Rectangle touchArea;
	private float touchSize;
	private float touchRadius;
	
	// gui elements
	private MenuButton soundButton;
	private MenuButton menuButton;
	private static MenuButton createButton;
	private FFUserSet userSet;
	private boolean userSetShow;
	
	private boolean createMenuButtonEnable;
	private float noShowTime;
	private static final float NO_SHOW_TIME = 10f;
	
	private boolean soundToggle;
	
	private float deltaTime;
	private int screenState;
	public static FFEngine engine;

	// used by debug and FFModel for particleEmitter
	public static int fps;
	
	// debug to log
	public static boolean DEBUG;
	private boolean buildDebug;
	private BitmapFont fpsFont;
	private float FPSx;
	private float FPSy;
	private final String buildDate;
	private String deviceSize;	
	
	public FFScreen(int deviceWidth, int deviceHeight, FFGame ffGame) {
		this.ffGame = ffGame;
		this.deviceWidth = deviceWidth;
		this.deviceHeight = deviceHeight;
		
		Reports.loadList();
		
		gameCacheSize = 0;
		
		engine = new FFEngine();
		soundToggle = true;
		touchArea = new Rectangle();
		touchEnabled = true;
		fpsFont = new BitmapFont(true); // boolean is for flip y
		fpsFont.setColor(0, 1, 0, 1); // green
		buildDate = "02.SEP.14";
	}

	
/*****************************************************************/
	
	private void startEngine() {
		engine.startEngine();
		createMenu();
	}
	
	private void factorScreenSize() {
		gameBatch = new SpriteBatch();
		ScreenUtils.getScreenUtils();
		ScreenUtils.factorScreenSize(deviceWidth, deviceHeight);	
		
		gameWidth = ScreenUtils.factoredWidth;	
		gameHeight = ScreenUtils.factoredHeight;
		gameAspectRatio = ScreenUtils.factoredAspectRatio;
		
		gameCellSize = ScreenUtils.factoredCellSize;
		gameCellsWide = ScreenUtils.factoredCellsWide; // 15
		gameCellsHigh = ScreenUtils.factoredCellsHigh; // 25
		
		// removed tablet asset loading
		deviceType = ScreenUtils.screenType; 
		GraphicsManager.getGraphicsManager().loadGraphicAssets(deviceType);
		
		gameCentreX = gameWidth / 2;
		gameCentreY = gameHeight / 2;
		dialogSize = gameCellSize * 8; // 256 px
		selectorWidth = gameCellSize * 14; // 448
		splashSize = gameCellSize * 16; // 512
		selectorHeight = gameCellSize * 24; // 768
	
		// center camera in start screen
		camera = new OrthographicCamera(gameWidth, gameHeight);
		camera.setToOrtho(true, gameWidth, gameHeight);
		camera.position.x = gameCentreX;
		camera.position.y = gameCentreY;
		camera.update();	

		gameBatch.getProjectionMatrix().set(camera.combined);
		
		// gestureDetector defaults:
		// halfTapSquareSize=20, tapCountInterval=0.4f, longPressDuration=1.1f, maxFlingDelay=0.15f, listener
		gestureDetector = new GestureDetector(gameCellSize, 0.4f, 1.1f, 0.15f, this);
		
		touchSize = gameCellSize * 2; 
		touchRadius = gameCellSize / 2;
		
		FPSx = gameCellSize + 10;
		FPSy = 10;
		deviceSize = new String("erm");
		if (deviceType == MOBILE) {
			deviceSize = "MOBILE";
		}
		else if (deviceType == TABLET) {
			deviceSize = "TABLET";
		}
		else {
			deviceSize = "NO_DEVICE";
			displayStatus(ScreenUtils.refactorError);
		}

		// loadSettings(log, nav)
		engine.loadSettings(false, false);
		DEBUG = engine.settings.DEBUG_LOG;
		// for fps/build/device on screen top
		buildDebug = engine.settings.DEBUG_OSD;
		
		if (DEBUG) Log.i(TAG , "end refactor for screen size.");
		
		// ready the sound manager
		SoundManager.sayHi();
		
	}
	
	private void renderFPS(SpriteBatch batch) {
		fps = Gdx.graphics.getFramesPerSecond();

		fpsFont.draw(batch, "fps: " + fps + " | build: " + buildDate + " | " + deviceSize 
				+ " | " + engine.settings.currentDifficulty, FPSx, FPSy);
	}
	
	private void destroy() {
		if (FFScreen.DEBUG) Log.i(TAG, "destroy called.");
		
		if (camera != null) camera = null;
		if (gameBatch != null) gameBatch = null;
		if (gameCache != null) gameCache = null;
		gestureDetector = null;
		fpsFont = null;
		LoadManager.getLoadManager().destroy();
		SoundManager.destroy();
		GraphicsManager.getGraphicsManager().destroy();
		FFModel.getFFModel().destroy();
		Gdx.input.setInputProcessor(null);
		if (engine != null) engine.destroy();
	}
	
/*****************************************************************/
	
	private void clearScreen() {	
		toggleTouchEnabled(false);			
			
		if (gameCache != null) {
			gameCache.clear();
			gameCache.dispose();
			gameCache = null;
		}	
		engine.screenReady();
		loadScreen();
	}
	
	private void loadScreen() {
		gameCache = new SpriteCache();
		gameCache.getProjectionMatrix().set(camera.combined);

		gameCache.beginCache();
		for (Obstacle obstacle : FFModel.getFFModel().field.fortress.scaffolds) {
			if (obstacle.alive) {
				gameCache.add(obstacle.texture, obstacle.position.x, obstacle.position.y);
				gameCacheSize++;
			}
		}	
		gameCacheSize = gameCache.endCache();
		
		// allow touch inputs
		toggleTouchEnabled(true);
		toggleCreateMenuButton(true);
		
		touchArea.setSize(touchSize, touchSize);
		touchArea.setPosition(engine.privatePosition.x - touchRadius, 
				engine.privatePosition.y - touchRadius);		
	}

	private void loadCreator() {
		if (createMenuButtonEnable) {
			toggleTouchEnabled(false);
			engine.requestCreator();
			if (gameCache != null) {
				gameCache.clear();
				gameCache.dispose();
				gameCache = null;
			}
			createButton = null;
			soundButton = null;
			camera = null;
			gameBatch = null;
			gestureDetector = null;
			ffGame.requestMode(true);
		}
	}
	
	private void createMenu() {
		Vector2 soundPosition = new Vector2(gameWidth - (gameCellSize * 2), gameHeight - gameCellSize);
		soundButton = new MenuButton(soundPosition, IdManager.BTN_SOUND);
		// check with SoundManager if state changed
		soundToggle = SoundManager.soundActive;
		soundButton.swapTexture(soundToggle);

		Vector2 createPosition = new Vector2(soundPosition.x - (gameCellSize * 2), soundPosition.y);
		createButton = new MenuButton(createPosition, IdManager.BTN_CREATOR);
		
		Vector2 menuPosition = new Vector2(0, gameHeight - gameCellSize);
		menuButton = new MenuButton(menuPosition, IdManager.BTN_MENU);
		
		toggleCreateMenuButton(true);
		
		soundPosition = null;
		createPosition = null;
		menuPosition = null;
		userSetShow = false;
	}
	
	private void toggleCreateMenuButton(boolean toggle) {
		if (FFScreen.DEBUG) Log.i(TAG, "createButton toggle: " + toggle);
		createMenuButtonEnable = toggle;
		noShowTime = 0;
		createButton.swapTexture(toggle);
		menuButton.swapTexture(toggle);
	}
	
	private void toggleSound() {
		engine.toggleVolume(soundToggle);
		soundButton.swapTexture(soundToggle);
		soundToggle ^= true;
	}
	
	private void toggleTouchEnabled(boolean toggle) {
		touchEnabled = toggle;
		if (touchEnabled) Gdx.input.setInputProcessor(gestureDetector);
		else Gdx.input.setInputProcessor(null);
	}
	
	private void updatePlayerTouchArea() {
		touchArea.setPosition(engine.privatePosition.x - touchRadius, 
				engine.privatePosition.y - touchRadius);
	}
	
/*****************************************************************/
	
	private void displayStatus(int reportInt) {
		toggleTouchEnabled(true);
		
	    status = new FFStatus(Reports.SCREEN_STATUS, Reports.reportList.get(reportInt));								
		statusExitButton = new MenuButton(status.exitButtonPosition, IdManager.BTN_EXIT);	
		statusShow = true;
    }
	
    private void dismissStatusPopup() {
    	// calls pause() and dispose()
    	Gdx.app.exit();
    }
	
/*****************************************************************/
    
	private void loadUserMenu() {
		if (createMenuButtonEnable) {
			// engine to HALT state
			if (engine.requestUserMenu()) {
				// has halt state
				userSet = new FFUserSet();
				userSetShow = true;
			}
		}
	}
	
	private void handleUserSet(int choice) {
		if (choice == FFSettings.OSD) {
			engine.settings.setOSD();
			buildDebug = engine.settings.DEBUG_OSD;
		}
		
		else if (choice != FFSettings.EXIT) {
			engine.settings.setDifficulty(choice);
		}
		// restart app
		
		engine.userMenuExit();
		userSetShow = false;
		if (userSet != null) userSet = null;
	}
	
/*****************************************************************/
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		// clears all previous draws for new one
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		deltaTime = Gdx.graphics.getDeltaTime();
		
		screenState = engine.gameStateUpdate(deltaTime);
	
		if (screenState == FFEngine.WAITING) {
			clearScreen();
		}
		else if (screenState == FFEngine.CREATOR_FOCUS) {
			// do nothing, transitioning to FFCreator
			return;
		}
		
		else if (screenState == FFEngine.HALT && userSetShow) {
			gameBatch.begin();
			userSet.draw(gameBatch);
			gameBatch.end();
		}
		
		else if (statusShow) {
			gameBatch.begin();
			status.draw(gameBatch);
			statusExitButton.draw(gameBatch);
			gameBatch.end();
		}
		
		else {
			// must be game running for cache draw
			if (screenState == FFEngine.RUNNING) {
				// allow transparencies for spritecache
				Gdx.gl.glEnable(GL10.GL_BLEND);
				Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
				gameCache.begin();
				for (int i = 0; i <= gameCacheSize; i++) {
					gameCache.draw(i);
				}
				gameCache.end();
				if (createMenuButtonEnable) {
					noShowTime += deltaTime;
					if (noShowTime >= NO_SHOW_TIME) {
						toggleCreateMenuButton(false);
					}
				}
			}
				
			gameBatch.begin();		
			engine.drawGameLoop(gameBatch, deltaTime);
	
			soundButton.draw(gameBatch);
			createButton.draw(gameBatch);
			menuButton.draw(gameBatch);
				
			if (buildDebug) {
				renderFPS(gameBatch);
			}
			gameBatch.end();
			updatePlayerTouchArea();
		}
	}
	
/*****************************************************************/	

	@Override
	public void resize(int deviceWidth, int deviceHeight) {
		if (FFScreen.DEBUG) Log.i(TAG, "resize called...");
	}

	@Override
	public void show() {
		// called when current screen for FFGame	
		// sets whole screen as gesture detector
		
		// this may catch when creator exits...
		if (FFScreen.DEBUG) Log.i(TAG, "show called");
		factorScreenSize();
		if (deviceType == NO_DEVICE) {
			// has error, will display status, no game			
		}
		else {
			// good device screen, carry on
			startEngine();
		}
	}

	@Override
	public void hide() {
		// called when no longer current screen for FFGame
		Gdx.input.setInputProcessor(null);
		if (FFScreen.DEBUG) Log.i(TAG, "hide called");
		
	}

	@Override
	public void pause() {
		// called when home button pressed, app is paused
		Gdx.input.setInputProcessor(null);
		if (FFScreen.DEBUG) Log.i(TAG, "pause called");		
	}

	@Override
	public void resume() {
		// called when activity has focus again, resume from a pause() state
		if (FFScreen.DEBUG) Log.i(TAG, "resume called.");
	}

	@Override
	public void dispose() {
		// called on back button, app is destroyed
		if (FFScreen.DEBUG) Log.i(TAG, "dispose called");
		destroy();
	}
	
	
/*****************************************************************/

	@Override
	public boolean touchDown(float touchX, float touchY, int pointer, int button) {
		camera.unproject(touchVector.set(touchX, touchY, 0)); 
		if (touchEnabled) {
			if (screenState == FFEngine.RUNNING) {
				// problems with Rectangle.contains == 0, adjust to be within a cell
				if (touchVector.x == 0) touchVector.x = gameCellSize / 2;
				if (touchVector.y == 0) touchVector.y = gameCellSize / 2;
				
				if (touchArea.contains(touchVector.x, touchVector.y)) {
					engine.handleGameValidTouch();
					return false;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		camera.unproject(touchVector.set(velocityX, velocityY, 0));
		
		if (touchEnabled) {
			//return engine.handleGameFling(velocityX, velocityY);
			return engine.handleGameFling(touchVector.x, touchVector.y);
		}
		return false;

	}

	@Override
	public boolean tap(float tapX, float tapY, int count, int button) {
		camera.unproject(touchVector.set(tapX, tapY, 0));
		if (touchEnabled) {	
			// listen for button press
			if (statusShow) {
				if (statusExitButton.boundBox.contains(touchVector.x, touchVector.y)) {
					dismissStatusPopup();
					return true;
				}
				return false;
			}
			
			else if (userSetShow) {
				handleUserSet(userSet.handleTap(touchVector.x, touchVector.y));
				return true;
			}
			
			else if (soundButton.boundBox.contains(touchVector.x, touchVector.y)) {
				toggleSound();
				return true;
			}
			
			else if (screenState == FFEngine.RUNNING) {
				if (createButton.boundBox.contains(touchVector.x, touchVector.y)) {					
					loadCreator();
					return true;
				}
				else if (menuButton.boundBox.contains(touchVector.x, touchVector.y)) {
					loadUserMenu();
					return true;
				}
				else if (touchArea.contains(touchVector.x, touchVector.y)) {
					return false;
				}
				else {
					return engine.handleGameTap(touchVector.x, touchVector.y);
				}
			}
			
			else if (screenState == FFEngine.BROKEN) {
				return engine.handleStatusTap(touchVector.x, touchVector.y);
			}
		}
		return false;
	}
	
// UNUSED TOUCH INPUTS
	@Override
	public boolean pan(float screenX, float screenY, float deltaX, float deltaY) {
		return false;
	}
	
	@Override
	public boolean panStop(float screenX, float screenY, int pointer, int button) {
		return false;
	}
	
	@Override
	public boolean longPress(float x, float y) {
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		return false;
	}	
}