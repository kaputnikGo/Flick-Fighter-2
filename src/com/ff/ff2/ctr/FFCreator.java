package com.ff.ff2.ctr;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ff.ff2.eng.FFGame;
import com.ff.ff2.gui.CellHolder;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.gui.FFSplash;
import com.ff.ff2.gui.FFStatus;
import com.ff.ff2.gui.MenuButton;
import com.ff.ff2.gui.Reports;
import com.ff.ff2.lev.Generator;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.lib.LoadManager;

public class FFCreator implements Screen, GestureListener {
	private static final String TAG = FFCreator.class.getSimpleName();
	
	// new creator as screen vars
	private FFGame ffgame;
	private boolean debug;
	
	public static final int CREATOR_LOADING = 9;
	public static final int CREATOR_RUNNING = 10;
	public static final int CREATOR_BROKEN = 20;
	
	private final float pauseTimeMax;
	private float pauseTime;
	
	private float deltaTime;
	private int screenState;
	private GestureDetector creatorGestureDetector;
	private Vector3 touchVector = new Vector3();
	
	private OrthographicCamera creatorCamera;
	private SpriteBatch creatorBatch;
	
	private FFSplash creatorSplash;

	private int creatorWidth;
	private int creatorHeight;
	private int centreX;
	private int centreY;
	private int cellSize;
	private int menubarY;
	private int fieldEndY;
	
	private FFToolbar toolbar;
	private boolean toolbarShow = false;
	private MenuButton toolbarExitButton;
	private MenuButton toolbarBuildButton;
	
	private CellHolder toolSelectIcon;
	private MenuButton toolButton;
	private MenuButton fileButton;
	private MenuButton playButton;
	private MenuButton creatorExitButton;
	
	private FFLoader ffLoader;
	private boolean loaderShow = false;
	
	private FFSelector fileActions;
	private boolean fileActionShow = false;
	private SaveFileInputListener saveListener;
	
	private FFConfirm fileConfirm;
	private boolean fileConfirmShow = false;
	private MenuButton fileConfirmButton;
	private MenuButton fileConfirmExitButton;
	
	private FFConfirm buildConfirm;
	private boolean buildConfirmShow = false;
	private MenuButton buildProcButton;
	private MenuButton buildRandButton;
	private MenuButton buildGroupsButton;
	private MenuButton buildArenaButton;
	
	private FFConfirm overwriteConfirm;
	private boolean overwriteConfirmShow = false;
	private MenuButton overwriteConfirmButton;
	private MenuButton overwriteConfirmExitButton;

    private FFStatus status;
    private int dialogSize;
    private int dialogOffset;
    private int selectorWidth;
    private int selectorOffset;
    private MenuButton statusExitButton;
    private boolean statusShow = false;
	private boolean changesMade;
	private boolean canPlay;
	private boolean fromBuild;
	
	public FFCreator(FFGame ffgame, boolean debug) {
		this.ffgame = ffgame;
		this.debug = debug;
		
		creatorWidth = FFScreen.gameWidth;
		creatorHeight = FFScreen.gameHeight;
		centreX = FFScreen.gameCentreX;
		centreY = FFScreen.gameCentreY;
		cellSize = FFScreen.gameCellSize;
		
		// these are independent as a buffer for extra size in y possible
		menubarY = FFScreen.gameHeight - cellSize; // at bottom	of screen	
		fieldEndY = (FFScreen.gameCellsHigh * cellSize) - cellSize; // at bottom of possible number of cells
		
		dialogSize = FFScreen.dialogSize;
		dialogOffset = dialogSize / 2;
		selectorWidth = FFScreen.selectorWidth;
		selectorOffset = selectorWidth / 2;
		
		toolbar = new FFToolbar(new Vector2(centreX - selectorOffset, 
				centreY - dialogOffset));
		
		toolbarExitButton = new MenuButton(new Vector2(
				(toolbar.position.x + toolbar.width) - (cellSize * 3), 
				toolbar.position.y + (toolbar.height - cellSize)), 
				IdManager.BTN_FILE_EXIT);
		
		toolbarBuildButton = new MenuButton(new Vector2(
				(centreX - cellSize), 
				toolbar.position.y + (toolbar.height - cellSize)), 
				IdManager.BTN_BUILD);
			
		pauseTimeMax = 1.5f; //not too long here...
		pauseTime = 0;
		
		creatorBatch = new SpriteBatch();
		// center camera in start screen
		creatorCamera = new OrthographicCamera(creatorWidth, creatorHeight);
		creatorCamera.setToOrtho(true, creatorWidth, creatorHeight);
		creatorCamera.position.x = centreX;
		creatorCamera.position.y = centreY;
		creatorCamera.update();	
		creatorBatch.getProjectionMatrix().set(creatorCamera.combined);
		
		creatorSplash = new FFSplash(new Vector2(0, 
				centreY - FFScreen.splashSize / 2),
				FFSplash.SPLASH_CREATOR);
		
		// full filename resolve here, in theme folder
		loadCreator(LoadManager.CREATOR_DEFAULT_FILENAME, LoadManager.GAME_STORAGE);
	}
	
	public void reset() {
		CreatorUtils.reset();
		changesMade = false;
		canPlay = false;
		fromBuild = false;
	}
	
	public void destroy() {
		if (creatorBatch != null) creatorBatch = null;
		if (creatorCamera != null) creatorCamera = null;
		if (creatorGestureDetector != null) creatorGestureDetector = null;
		CreatorUtils.destroy();
	}
	
/*********************************************************************/
	
    public class SaveFileInputListener implements TextInputListener {
    	String userFortName = new String(Reports.DEFAULT_SAVE_NAME);
    	
    	@Override
    	public void input (String text) {
    	   // user has entered text as String text
    		userFortName = text;
    		processFileSave(userFortName);
    	}

    	@Override
    	public void canceled () {
    	   // user cancelled text entry, dismissed
    		userFortName = null;
    	}
    }
	
/*********************************************************************/
	
	private void loadCreator(String fortressName, int location) {
		if (debug) Log.i(TAG, "load creator called.");
		screenState = CREATOR_LOADING;
		reset();

		if (CreatorUtils.loadFortressFile(fortressName, location) != LoadManager.FILE_LOADED) {
			screenState = CREATOR_BROKEN;
			displayStatus();
		}
		else {
			if (fortressName != LoadManager.CREATOR_DEFAULT_FILENAME) {
				if (debug) Log.i(TAG, "not default fort file, can play");
				canPlay = true;
			}
			else {
				if (debug) Log.i(TAG, "is default fort file, no play.");
				canPlay = false;
			}
			displayCreator();
		}
	}	
	
/*********************************************************************/	
	
	public void loadFromSelector(String selectedFileName, int storage) {
		// checks here for stuff, not necessarily a file to load...
		if (selectedFileName.equals(LoadManager.DEFAULT_BLANK_FIELD)) {
			Log.d(TAG, "loadFromSelector, default name used.");
			loadCreator(LoadManager.CREATOR_DEFAULT_FILENAME, LoadManager.GAME_STORAGE);
		}
		
		else { 
			if (ffLoader.fortressLocation == LoadManager.GAME_STORAGE
					|| ffLoader.fortressLocation == LoadManager.USER_STORAGE) {
				loadCreator(selectedFileName, ffLoader.fortressLocation);
			}
			else {
				CreatorUtils.processResult = LoadManager.NO_STORAGE;
				displayStatus();
			}
		}
		loaderShow = false;
		ffLoader.destroy();
		ffLoader = null;
	}
	
/*********************************************************************/
	
	private void displayCreator() {
		displayFortress();
		displayMenubar();
	}
	
    private void displayStatus() {
	    status = new FFStatus(Reports.CREATOR_STATUS, 
	    		Reports.reportList.get(CreatorUtils.processResult));
	    
		statusExitButton = new MenuButton(status.exitButtonPosition, IdManager.BTN_EXIT);
		statusShow = true;
    }
    
    private void displayFileConfirm(String heading, int type) {
 	   fileConfirm = new FFConfirm(heading);			   
 	   if (type == FFSelector.SAVE) {
 		  fileConfirmButton = new MenuButton(fileConfirm.button1position, IdManager.BTN_FILE_SAVE);
 	   }
 	   else if (type == FFSelector.SEND) {
 		  fileConfirmButton = new MenuButton(fileConfirm.button1position, IdManager.BTN_FILE_SEND);
 	   }	      
 	   fileConfirmExitButton = new MenuButton(fileConfirm.button4position, IdManager.BTN_EXIT);
 	   
 	   fileConfirmShow = true;    	
    }
    
    private void dismissBuildConfirm() {	
    	if (buildConfirmShow) {
			buildConfirmShow = false;
		 	buildProcButton = null;
		 	buildRandButton = null;  
		 	buildGroupsButton = null;
		 	buildConfirm.destroy();
		 	buildConfirm = null;
    	}
    }
    
    private void dismissFileConfirm() {
    	if (fileConfirmShow) {
	    	fileConfirmShow = false;
	    	fileConfirmButton = null;
	    	fileConfirmExitButton = null;
	    	fileConfirm.destroy();
	    	fileConfirm = null;
    	}
    }
    
    private void dismissOverwriteConfirm() {
    	if (overwriteConfirmShow) {
    		overwriteConfirmShow = false;
    		overwriteConfirmButton = null;
    		overwriteConfirmExitButton = null;
    		overwriteConfirm.destroy();
    		overwriteConfirm = null;
    	}
    }
    
    private void dismissStatus() {
    	//TODO need more handling of errors reported here
    	// and where the user can go next
    	if (statusShow) {
	    	statusShow = false;
	    	statusExitButton = null;
		    status.destroy();
		    status = null;
		    // reload?
		    if (CreatorUtils.processResult == LoadManager.FILE_LENGTH_ERROR 
		    		|| CreatorUtils.processResult == LoadManager.FILENAME_LONG) {
		    	requestFileSaver();
		    }
		    else if (CreatorUtils.processResult == LoadManager.FILE_EXISTS) {
		    	// allow user to overwrite
		    	requestOverwriteFile();
		    }
		    else {
		    	loadCreator(LoadManager.CREATOR_DEFAULT_FILENAME, LoadManager.GAME_STORAGE);
		    }
    	}
    }
    
    private void dismissFileActions() {
    	if (fileActionShow) {
	    	fileActionShow = false;
	    	fileActions.destroy();
	    	fileActions = null;
    	}
    }
	
	public void dismissLoader() {
		// checks here for stuff, not necessarily a file to load...
		if (ffLoader.userSelectedFortFile.equals(LoadManager.DEFAULT_BLANK_FIELD)) {
			loadCreator(LoadManager.CREATOR_DEFAULT_FILENAME, LoadManager.GAME_STORAGE);
		}		
		else { 
			if (ffLoader.fortressLocation == LoadManager.GAME_STORAGE
					|| ffLoader.fortressLocation == LoadManager.USER_STORAGE) {
				loadCreator(ffLoader.userSelectedFortFile, ffLoader.fortressLocation);
			}
			else {
				CreatorUtils.processResult = LoadManager.NO_STORAGE;
				displayStatus();
			}
		}
		loaderShow = false;
		ffLoader.destroy();
		ffLoader = null;
	}
	
	private void requestCreatorExit() {
		// exit with errors to report to engine?
		if (changesMade) {
			// catch user made changes and pressed exit before saving
			displayFileConfirm(Reports.CREATOR_EXIT, FFSelector.SAVE);
		}
		else {
			creatorExit();
		}
	}
	
	private void creatorExit() {
		// catch an error here?
		dismissFileConfirm();
		FFScreen.engine.creatorExiting();
		creatorExitButton = null;
		fileButton = null;
		playButton = null;
		destroy();
		ffgame.requestMode(false);
	}
	
	private void playFort() {
		FFScreen.engine.creatorRequestLoadField(
				CreatorUtils.currentFortress, 
				CreatorUtils.currentLocation);
		creatorExitButton = null;
		fileButton = null;
		playButton = null;
		destroy();
		ffgame.requestMode(false);
	}
	
/*********************************************************************/
	
	private void requestFileActions() {
	    fileActions = new FFSelector(Reports.FILE_DIALOG, canPlay);						
		fileActionShow = true;
	}
	
	private void requestFileLoader() {
		dismissFileActions();		
		ffLoader = new FFLoader(creatorCamera, this);
		
		CreatorUtils.processResult = ffLoader.loadList();
		if (CreatorUtils.processResult != LoadManager.FILE_LOADED) {
			displayStatus();
		}
		else {
			loaderShow = true;
		}	
	}
	
	private void requestPlayFile() {
		if (fromBuild) {
			// use the auto save and load from there
			processAutoSave();
			return;
		}
		
		else if (canPlay) {
			dismissFileActions();
			// catch for unsaved fortress
			if (CreatorUtils.requireUserFortName())  {
				requestFileSaver();
			}
			else if (CreatorUtils.requireStorage()) {
				requestFileSaver();
			}
			else {
				playFort();
			}
		}
	}	
	
	private void requestFileSaver() {
		dismissFileActions();
		dismissFileConfirm();
		// need a name and a location
    	saveListener = new SaveFileInputListener();
        Gdx.input.getTextInput(saveListener, Reports.SAVE_DIALOG, 
        		Reports.DEFAULT_SAVE_NAME);
	}
	
	
	private void processFileSave(String userFortName) {			
		// be more specific here
		if (CreatorUtils.saveUserFortress(userFortName) != LoadManager.FILE_SAVED) {
			displayStatus();
		}
		else {		
			changesMade = false;
			canPlay ^= true;
			playButton.swapTexture(canPlay);
			// reload creator with the just saved file, so they can either edit or play
			//Load manager saves new fortress and adds file extension
			// we need to add it here to reload creator with it.
			loadCreator(CreatorUtils.currentFortress + "." + LoadManager.FORTRESS_EXT, 
					CreatorUtils.currentLocation);
		}
	}
	
	
	private void processAutoSave() {	
		if (CreatorUtils.autoSaveFortress() != LoadManager.FILE_SAVED) {
			displayStatus();
		}
		else {		
			playFort();
		}
	}
	
	private void requestOverwriteFile() {
		overwriteConfirm = new FFConfirm(Reports.OVERWRITE_DIALOG);			   
		overwriteConfirmButton = new MenuButton(buildConfirm.button1position, IdManager.BTN_FILE_SAVE);
		overwriteConfirmExitButton = new MenuButton(buildConfirm.button4position, IdManager.BTN_EXIT); 	   
	 	overwriteConfirmShow = true;
	}
	
	private void sendFileRequest() {
		//TODO
		//displayConfirmPopup(FFSelector.SEND);
		
	}
	
	private void processOverwriteFile() {
		if (CreatorUtils.overwriteFortFile() != LoadManager.FILE_SAVED) {
			displayStatus();
		}
	}
	
/*********************************************************************/  
	
	private void displayFortress() {
		if (CreatorUtils.loadFortressCells() == LoadManager.CELL_CONVERT_ERROR) {		
			displayStatus();
		}
	}
	
	private void displayMenubar() {
		Vector2 menuButtonVector = new Vector2(cellSize, 
				creatorHeight - cellSize);
		
		toolSelectIcon = new CellHolder(menuButtonVector);
		
		menuButtonVector.x += (cellSize * 2);
		toolButton = new MenuButton(menuButtonVector, IdManager.BTN_TOOL);
		
		menuButtonVector.x += (cellSize * 3);
		fileButton = new MenuButton(menuButtonVector, IdManager.BTN_FILE);
		
		menuButtonVector.x += (cellSize * 3);
		playButton = new MenuButton(menuButtonVector, IdManager.BTN_FILE_PLAY);
		// make button greyed out
		playButton.swapTexture(canPlay);
		
		// far right of screen
		menuButtonVector.x = creatorWidth - (cellSize * 3);
		creatorExitButton = new MenuButton(menuButtonVector, IdManager.BTN_EXIT);		

		menuButtonVector = null;
	}
	
	private void loadToolSelectIcon() {
		// toolbar.selectedCellId
		toolSelectIcon.swapTexture(toolbar.selectedCellId);
	}
	
	private void requestBuildFortress() {
		buildConfirm = new FFConfirm(Reports.BUILD_DIALOG);			   
	 	buildProcButton = new MenuButton(buildConfirm.button1position, IdManager.BTN_PROC);
	 	buildRandButton = new MenuButton(buildConfirm.button2position, IdManager.BTN_RAND);  
	 	buildGroupsButton = new MenuButton(buildConfirm.button3position, IdManager.BTN_GROUPS);
	 	buildArenaButton = new MenuButton(buildConfirm.button4position, IdManager.BTN_ARENA);	 	   
	 	buildConfirmShow = true;
	}
	
	private void loadBuildFortress(int type) {
		if (debug) Log.i(TAG, "request Build fortress type : " + type);
		dismissBuildConfirm();

		// 0=proc,1=rand,2=groups,default=testArray
		if (CreatorUtils.generateBuildFortress(type) == LoadManager.BUILD_GOOD) {
			// go ahead
			// added to allow player to play without saving the Build forts
			changesMade = false;
			canPlay = true;
			fromBuild = true;
			playButton.swapTexture(canPlay);
			displayFortress();
			
		}
		else {
			displayStatus();
		}
	}
	
/*********************************************************************/	
	
	private boolean handleMenubarArea(float touchX, float touchY) {
		// order is important here...
		if (creatorExitButton.boundBox.contains(touchX, touchY)) {
			requestCreatorExit();
			return true;
		}
		else if (playButton.boundBox.contains(touchX, touchY)) {
			requestPlayFile();
			return true;
		}
		else if (fileButton.boundBox.contains(touchX, touchY)) {
			requestFileActions();
			return true;
		}
		else if (toolButton.boundBox.contains(touchX, touchY)) {
			toolbarShow = true;
			return true;
		}
		return false;
	}
	
	private boolean handleFortressArea(float touchX, float touchY) {
		// round off the touch to a cell based vector
		Vector2 tempVector = new Vector2();
		tempVector.x = Math.round(touchX / cellSize) * cellSize;
		tempVector.y = Math.round(touchY / cellSize) * cellSize;
		
		if (CreatorUtils.handleFortressArea(tempVector, toolbar.selectedCellId)) {
			changesMade = true;
			canPlay = false;
			tempVector = null;
			return true;
		}
		tempVector = null;
		return false;
	}
	
	private boolean handleDrag(float touchX, float touchY) {
		if (CreatorUtils.handleDrag(touchX, touchY)) {
			changesMade = true;
			canPlay = false;
			return true;
		}
		return false;
	}


/*********************************************************************/
	
	private int creatorStateUpdate(float deltaTime) {
		if (screenState == CREATOR_LOADING) {
			// allow time to load
			pauseTime += deltaTime;
			if (pauseTime >= pauseTimeMax) {
				screenState = CREATOR_RUNNING;
				pauseTime -= pauseTimeMax;
			}
		}
		else if (screenState == CREATOR_RUNNING) {
			// erm...
		}
		return screenState;
	}
		
	private void draw(SpriteBatch creatorBatch) {
		// creator cells
		CreatorUtils.draw(creatorBatch);

		// menu buttons
		toolSelectIcon.draw(creatorBatch);
		toolButton.draw(creatorBatch);
		creatorExitButton.draw(creatorBatch);
		fileButton.draw(creatorBatch);
		playButton.draw(creatorBatch);
		
		// popups and windows
		if (toolbarShow) {
			toolbar.draw(creatorBatch);
			toolbarExitButton.draw(creatorBatch);
			toolbarBuildButton.draw(creatorBatch);
		}
		if (fileActionShow) {
			fileActions.draw(creatorBatch);
		}
		if (fileConfirmShow) {
			fileConfirm.draw(creatorBatch);
			fileConfirmButton.draw(creatorBatch);
			fileConfirmExitButton.draw(creatorBatch);
		}
		if (buildConfirmShow) {
			buildConfirm.draw(creatorBatch);
			buildProcButton.draw(creatorBatch);
			buildRandButton.draw(creatorBatch);
			buildGroupsButton.draw(creatorBatch);
			buildArenaButton.draw(creatorBatch);
		}
		if (overwriteConfirmShow) {
			overwriteConfirm.draw(creatorBatch);
			overwriteConfirmButton.draw(creatorBatch);
			overwriteConfirmExitButton.draw(creatorBatch);
		}
		
		if (loaderShow) {
			ffLoader.draw(creatorBatch);
		}
		
		if (statusShow) {
			status.draw(creatorBatch);
			statusExitButton.draw(creatorBatch);
		}
	}
	
/*********************************************************************/
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1); // black/white
		// clears all previous draws for new one
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		deltaTime = Gdx.graphics.getDeltaTime();
		
		screenState = creatorStateUpdate(deltaTime);
		
		if (screenState == CREATOR_LOADING) {
			creatorBatch.begin();
			creatorSplash.draw(creatorBatch);
			creatorBatch.end();
		}
		else if (screenState == CREATOR_RUNNING) {				
			creatorBatch.begin();
			draw(creatorBatch);
			creatorBatch.end();		
		}
		else if (screenState == CREATOR_BROKEN) {
			if (statusShow) {
				creatorBatch.begin();
				status.draw(creatorBatch);
				statusExitButton.draw(creatorBatch);
				creatorBatch.end();	
			}
		}
	}	
	
/*********************************************************************/
	@Override
	public void dispose() {
		Gdx.input.setInputProcessor(null);
	}
	
	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		if (debug) Log.i(TAG, "hide called");
	}
	
	@Override
	public void pause() {
		Gdx.input.setInputProcessor(null);
	}
	
	@Override
	public void resize(int arg0, int arg1) {
		// nothing
	}
	
	@Override
	public void resume() {
		
	}
	
	@Override
	public void show() {
		// gestureDetector defaults:
		// halfTapSquareSize=20, tapCountInterval=0.4f, longPressDuration=1.1f, maxFlingDelay=0.15f, listener
		if (debug) Log.i(TAG, "show called.");
		creatorGestureDetector = new GestureDetector(cellSize, 0.4f, 1.1f, 0.15f, this);
		Gdx.input.setInputProcessor(creatorGestureDetector);		
	}

/*********************************************************************/
	// primary touch events
	@Override
	public boolean touchDown(float touchX, float touchY, int pointer, int button) {
		creatorCamera.unproject(touchVector.set(touchX, touchY, 0));
		if (toolbar.fieldLocked) {
			// bypass
			return false;
		}		
		else if (touchVector.y >= menubarY) {
			// ignore, is in toolbar area
			return false;
		}		
		else if (toolbar.selectedCellId != IdManager.DELETE) {
			return CreatorUtils.handleTouchDown(touchVector.x, touchVector.y);
		}
		return false;
	}	
	
	@Override
	public boolean tap(float touchX, float touchY, int count, int button) {
		creatorCamera.unproject(touchVector.set(touchX, touchY, 0));
		//menubuttons		
		if (toolbarShow) {
			if (toolbarExitButton.boundBox.contains(touchVector.x, touchVector.y)) {
				loadToolSelectIcon();
				toolbarShow = false;
				return true;
			}
			else if (toolbarBuildButton.boundBox.contains(touchVector.x, touchVector.y)) {
				requestBuildFortress();
				toolbarShow = false;
				return true;
			}
			else 
				return toolbar.handleTap(touchVector.x, touchVector.y);
		}
		
		else if (statusShow) {
			if (statusExitButton.boundBox.contains(touchVector.x, touchVector.y)) {
				dismissStatus();
				return true;
			}
			return false;
		}
		
		else if (fileConfirmShow) {
			// look out for send button here
			if (fileConfirmButton.boundBox.contains(touchVector.x, touchVector.y)) {
				requestFileSaver();
				return true;
			}
			else if (fileConfirmExitButton.boundBox.contains(touchVector.x, touchVector.y)){
				creatorExit();
				return true;
			}
			return false;
		}
		
		else if (buildConfirmShow) {
			if (buildArenaButton.boundBox.contains(touchVector.x, touchVector.y)) {
				loadBuildFortress(Generator.BUILD_TYPE_ARENA);
				return true;
			}
			else if (buildGroupsButton.boundBox.contains(touchVector.x, touchVector.y)) {
				loadBuildFortress(Generator.BUILD_TYPE_GROUPS);
				return true;
			}
			else if (buildRandButton.boundBox.contains(touchVector.x, touchVector.y)) {
				loadBuildFortress(Generator.BUILD_TYPE_RAND);
				return true;
			}
			else if (buildProcButton.boundBox.contains(touchVector.x, touchVector.y)) {
				loadBuildFortress(Generator.BUILD_TYPE_PROC);
				return true;
			}
			return false;
		}
		
		else if (overwriteConfirmShow) {
			if (overwriteConfirmButton.boundBox.contains(touchVector.x, touchVector.y)) {
				processOverwriteFile();
				return true;
			}
			else if (overwriteConfirmExitButton.boundBox.contains(touchVector.x, touchVector.y)){
				dismissOverwriteConfirm();
				return true;
			}
			return false;
		}
		
		else if (fileActionShow) {
			int result = fileActions.handleTap(touchVector.x, touchVector.y);
			switch (result) {
				case FFSelector.EXIT:
					dismissFileActions();
					break;
				case FFSelector.SEND:
					sendFileRequest();
					break;
				case FFSelector.PLAY:
					requestPlayFile();
					break;
				case FFSelector.SAVE:
					requestFileSaver();
					break;
				case FFSelector.LOAD:
					requestFileLoader();
					break;				
			}
			return true;
		}
		
		else if (loaderShow) {
			return ffLoader.handleTap(touchVector.x, touchVector.y);
		}
		
		else if (touchVector.y >= menubarY) {
			return handleMenubarArea(touchVector.x, touchVector.y);
		}
		//fortress area
		else if (touchVector.y <= fieldEndY) {
			if (toolbar.hasSelectedTool) {
				return handleFortressArea(touchVector.x, touchVector.y);
			}
		}
		return false;
	}
	
	@Override
	public boolean pan(float screenX, float screenY, float deltaX, float deltaY) {
		creatorCamera.unproject(touchVector.set(screenX, screenY, 0));
		if (toolbar.fieldLocked) {
			// bypass
			return false;
		}
		else if (touchVector.y < fieldEndY && toolbar.selectedCellId != IdManager.DELETE) {
			return handleDrag(touchVector.x, touchVector.y);
		}
		return false;
	}
	
	@Override
	public boolean panStop(float screenX, float screenY, int pointer, int button) {
		creatorCamera.unproject(touchVector.set(screenX, screenY, 0));
		return CreatorUtils.handleDragStop(touchVector.x, touchVector.y);
	}

/*********************************************************************/	
	// unused touch events
	
	@Override
	public boolean fling(float arg0, float arg1, int arg2) {
		return false;
	}
	
	@Override
	public boolean longPress(float arg0, float arg1) {
		return false;
	}
	
	@Override
	public boolean pinch(Vector2 arg0, Vector2 arg1, Vector2 arg2, Vector2 arg3) {
		return false;
	}
	
	@Override
	public boolean zoom(float arg0, float arg1) {
		return false;
	}

/*********************************************************************/	

}