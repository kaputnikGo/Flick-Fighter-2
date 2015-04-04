package com.ff.ff2.ctr;

import android.text.format.Time;
import android.util.Log;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.gui.Reports;
import com.ff.ff2.lev.Generator;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.lib.LoadManager;
import com.ff.ff2.obs.CreatorCell;


public class CreatorUtils {
	private static final String TAG = CreatorUtils.class.getSimpleName();
	public static int processResult;
	private static final int cellsWide = FFScreen.gameCellsWide;
	private static final int cellsHigh = FFScreen.gameCellsHigh;
	private static final int cellSize = FFScreen.gameCellSize;
	
	
	public static String currentFortress;
	public static int currentLocation;
	public static char[][] fortressArray = new char[cellsWide][cellsHigh];
	public static Array<CreatorCell> createdCells = new Array<CreatorCell>();
	public static Array<CreatorCell> cellsCache = new Array<CreatorCell>();
	
/*********************************************************************/	
	
	public CreatorUtils() {
		//
	}
	
	public static void reset() {
		currentFortress = new String();
		fortressArray = new char[cellsWide][cellsHigh];
		createdCells = new Array<CreatorCell>();
		cellsCache = new Array<CreatorCell>();
		processResult = LoadManager.DEFAULT;
	}
	
	public static void destroy() {
		if (fortressArray != null) fortressArray = null;
		if (createdCells != null) createdCells = null;
		if (cellsCache != null) cellsCache = null;
	}
	
/*********************************************************************/	
	
	public static boolean requireUserFortName() {
		// catch for unsaved fortress
		if (currentFortress.equals("") || 
				currentFortress.equals(null) ||
				currentFortress.equals(LoadManager.CREATOR_DEFAULT_FILENAME))  {
			return true;
		}
		return false;
	}
	
	public static boolean requireStorage() {
		if (currentLocation != LoadManager.USER_STORAGE 
				&& currentLocation != LoadManager.GAME_STORAGE
				&& currentLocation != LoadManager.CARD_STORAGE) {
			return true;
		}
		return false;
	}
	
	public static int loadFortressFile(String fortressName, int location) {
		// checks first and location
		if (FFScreen.DEBUG) {
			Log.i(TAG, "loading file: " + fortressName);
			Log.i(TAG, "loading file from location: " + location);
		}
		processResult = LoadManager.getLoadManager().loadFortressFile(fortressName, location);	
		if (processResult == LoadManager.FILE_LOADED) {
			fortressArray = LoadManager.getLoadManager().getFortressArray();
			currentFortress = fortressName;
			currentLocation = location;
		}
		return processResult;
	}
	
	public static int loadFortressCells() {
		// fortressArray already loaded by now...
		Vector2 initVector = new Vector2();
		if (fortressArray != null) {		
			for (int y = 0; y < cellsHigh; y++) {
				for (int x = 0; x < cellsWide; x++) {
					// first draw backing of empty cells to cache
					initVector.x = x * cellSize;
					initVector.y = y * cellSize;
					cellsCache.add(new CreatorCell(IdManager.EMPTY, initVector));
					
					if (fortressArray[x][y] != IdManager.EMPTY) {
						initVector.x = x * cellSize; // convert cell to pixel
						initVector.y = y * cellSize; // convert cell to pixel
						createdCells.add(new CreatorCell(fortressArray[x][y], initVector));
					}
				}
			}
			return LoadManager.CELL_ARRAY_CONVERTED;
		}
		return LoadManager.CELL_CONVERT_ERROR;
	}
	
	public static int saveUserFortress(String userFortName) {	
		currentFortress = userFortName;

		if (currentFortress.equals(Reports.DEFAULT_SAVE_NAME)) { 
			currentFortress = Reports.DEFAULT_SAVE_NAME;			
			Time now = new Time();
			now.setToNow();	
			// timestamp(9) to timestamp(15) is time format: 141526 (hh:mm:ss)
			currentFortress += "-" + now.toString().substring(9, 15);
		}
		
		// can ask for this one day...
		currentLocation = LoadManager.USER_STORAGE;
		
		processResult = convertUserCellsToArray();
		
		if (processResult == LoadManager.CELL_ARRAY_CONVERTED) {
			processResult = LoadManager.getLoadManager().checkSaveNewFile(
					currentFortress, currentLocation);
			
			if (processResult == LoadManager.CHECK_GOOD) { 
				processResult = LoadManager.getLoadManager().saveNewFortress(
						currentFortress, currentLocation, fortressArray);
			}		
		}
		return processResult;
	}
	
	public static int autoSaveFortress() {
		processResult = convertUserCellsToArray();
		
		if (processResult == LoadManager.CELL_ARRAY_CONVERTED) {
			processResult = LoadManager.getLoadManager().autoSaveFortFile(fortressArray);
		}
		if (processResult == LoadManager.FILE_SAVED) {
			// set these
			currentFortress = LoadManager.AUTOSAVE_FILENAME; 
			currentLocation = LoadManager.USER_STORAGE;
		}
		
		return processResult;
	}
	
	public static int overwriteFortFile() {
		processResult = LoadManager.getLoadManager().deleteExistingFile(currentFortress, currentLocation);
		if (processResult != LoadManager.FILE_DELETED) {
			return processResult;
		}
		else {
			saveUserFortress(currentFortress);
			return LoadManager.FILE_SAVED;
		}
	}
	
	public static int convertUserCellsToArray() {		
		// any other errors here?	
		if (createdCells == null) {
			return LoadManager.NO_CREATED_CELLS;
		}		
		else {
			// now do the array to file stuff
			fortressArray = new char[cellsWide][cellsHigh];			
			for (int y = 0; y < cellsHigh; y++) {
				for (int x = 0; x < cellsWide; x++) {
					// fill with blank cells
					fortressArray[x][y] = IdManager.EMPTY;
				}
			}
			// then overwrite with any userCells
			int cellX = 0;
			int cellY = 0;
			for (CreatorCell cell : createdCells) {
				cellX = (int)cell.position.x / cellSize;
				cellY = (int)cell.position.y / cellSize;
				if (cellX < cellsWide && cellY < cellsHigh) {
					fortressArray[cellX][cellY] = cell.id;
				}
			}	
			return LoadManager.CELL_ARRAY_CONVERTED;
		}
	}
	
	public static int generateBuildFortress(int type) {
		// 0=proc,1=rand,2=groups,default=testArray
		reset();
		if (Generator.prepForRandomFortress()) {
			// clear any exiting cells and display in creator
			fortressArray = new char[cellsWide][cellsHigh];
			fortressArray = Generator.getSelectedBuildFortress(type);
		}
		else {
			// use fallback
			Generator.createSimpleRandomFortress();
		}
		return LoadManager.BUILD_GOOD;
	}
	
/*********************************************************************/		
	
	public static boolean deleteCandidateCell(Vector2 candidate) {
		for (CreatorCell cell : createdCells) {
			if (cell.boundBox.contains(candidate.x, candidate.y)) {
				// cannot delete these cells
				if (cell.id == IdManager.PRIVATE_FIGHTER
						|| cell.id == IdManager.GATEWAY
						|| cell.id == IdManager.SWITCH
						|| cell.id == IdManager.POWERUP) {
					return false;	
				}
				else {
					createdCells.removeValue(cell, true);
					createdCells.shrink();
				}
			}
		}
		return true;
	}
	
	public static boolean handleTouchDown(float touchX, float touchY) {
		for (CreatorCell cell : createdCells) {		
			if (cell.boundBox.contains(touchX, touchY)) {
				cell.draggable = true;
				return true;
			}
		}
		return false;
	}
	
	public static boolean handleFortressArea(Vector2 tempVector, char cellId) {		
		if (cellId == IdManager.DELETE) {
			deleteCandidateCell(tempVector);
			return true;
		}
		else if (cellId != IdManager.LOCK) {
			// delete any already existing, except the key cells!
			if (deleteCandidateCell(tempVector)) {
				createdCells.add(new CreatorCell(cellId, tempVector));
				return true;
			}
		}
		return false;
	}
	
	
	public static boolean handleDrag(float touchX, float touchY) {
		// is within field area, above the toolbar, not in delete mode
		// find a draggable
		for (CreatorCell cell : createdCells) {
			if (cell.draggable) {
				// snap to grid	
				cell.position.x = Math.round(touchX / cellSize) * cellSize;
				cell.position.y = Math.round(touchY / cellSize) * cellSize;
				cell.updateBoundBox();
				return true;
			}
		}
		return false;
	}
	
	public static boolean handleDragStop(float touchX, float touchY) {
		for (CreatorCell cell : createdCells) {
			if (cell.draggable) {
				cell.draggable = false;
				return true;
			}
		}
		return false;
	}
	
/*********************************************************************/
	
	public static void draw(SpriteBatch creatorBatch) {
		// creator cache
		for (CreatorCell cellC : cellsCache) {
			cellC.draw(creatorBatch);
		}
		
		// draggable created cells
		for (CreatorCell cell : createdCells) {
			cell.draw(creatorBatch);
		}
	}
}