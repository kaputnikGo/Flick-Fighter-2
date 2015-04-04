package com.ff.ff2.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Random;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.gui.Reports;
import com.ff.ff2.gui.ScreenUtils;

public class LoadManager {
	private static final String TAG = LoadManager.class.getSimpleName();
	
	// saved prefs file for android
	public static final String USER_PREFS_FILE = "FF2.global.prefs";
	
	// local - example file path : "/data/data/com.ff.ff2/files/" + name
	public static final int USER_STORAGE = 0;
	
	// internal, assets folder - example "/fortress/test/" + name
	public static final int GAME_STORAGE = 1;
	
	// external - SD card, make sure is NOT root but folder created for app
	// wants to be /sdcard/FF2/ 
	public static final int CARD_STORAGE = 2; // NOT USED YET
	
	private static final String TEST_FORTRESS_FOLDER = "fortress/test/";
	private static final String DEMO_FORTRESS_FOLDER = "fortress/demo/";
	//private static final String CARD_STORAGE_FOLDERNAME = "FF2";
	//private static final String STORAGE_NOT_SET = "not set";
	//private static String CARD_STORAGE_FOLDER = STORAGE_NOT_SET;
	
	public static final String FORTRESS_EXT = "fort";
	public static final String DEMO_FILENAME = DEMO_FORTRESS_FOLDER + "demo" + "." + FORTRESS_EXT;
	public static final String CREATOR_DEFAULT_FILENAME = DEMO_FORTRESS_FOLDER + "creator" + "." + FORTRESS_EXT;
	public static final String DEFAULT_BLANK_FIELD = "default_blank_field";
	public static final String RANDOM_FORT = "random";
	
	public static final String AUTOSAVE_FILENAME = "FF2_autosave" + "." + FORTRESS_EXT;
	
	private static final String ALLOWED_RANDOM_CHARS ="qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
	
	private static final int MIN_FORTRESS_FILE_LENGTH = 773;
	private static final int MAX_FORTRESS_FILE_LENGTH = 775;
	public static final int MAX_FORTRESS_FILE_NAME_LENGTH = 20;
	
	// IN ORDER AS PER REPORTS.JAVA STRING MATCHING
	// pre-save
	public static final int DEFAULT = 0;
	public static final int NO_STORAGE = 1;
	public static final int NO_FILENAME = 2;
	public static final int FILENAME_LONG = 3;
	public static final int NO_LOCAL_ACCESS = 4;
	public static final int NO_EXTERNAL_ACCESS = 5;
	public static final int CHECK_GOOD = 6;
	public static final int CHECK_ERROR = 7;
	//
	public static final int NO_PLAYER = 8;
	public static final int MULTIPLE_PLAYER = 9;
	public static final int NO_SWITCH = 10;
	public static final int NO_GATEWAY = 11;
	public static final int NO_POWERUP = 12;
	public static final int VERIFY_GOOD = 13;
	public static final int VERIFY_ERROR = 14;
	//
	public static final int FILE_EXISTS = 15;
	public static final int FILE_SAVED = 16;
	//
	public static final int LOAD_ERROR = 17;
	public static final int CELL_COUNT_ERROR = 18;
	public static final int LINE_COUNT_ERROR = 19;
	public static final int FILE_LENGTH_ERROR = 20;
	public static final int VERIFIED_FILE = 21;
	public static final int FILE_LOADED = 22;
	public static final int NO_FILE_FOUND = 23;
	public static final int FILE_FOUND = 24;
	public static final int FILE_EXT_ERROR = 25;
	public static final int NO_FILES_FOUND = 26;
	public static final int FILE_DELETED = 27;
	public static final int FILE_NOT_DELETED = 28;
	//
	public static final int NO_CREATED_CELLS = 29;
	public static final int CELL_ARRAY_CONVERTED = 30;
	public static final int CELL_CONVERT_ERROR = 31;
	//
	public static final int EXIT_BEFORE_SAVE = 32;
	public static final int CONFIRM_SEND = 33;
	
	public static final int SCREEN_SMALL_ERROR = 34;
	public static final int SCREEN_ASPECT_ERROR = 35;
	
	public static final int BUILD_GOOD = 36;
	public static final int BUILD_ERROR = 37;
	
	private int cellsWide = FFScreen.gameCellsWide;
	private int cellsHigh = FFScreen.gameCellsHigh;
	//private int cellSize = FFScreen.gameCellSize;

	public char[][] fortressCellArray;
	public Array<String> fortressNamesList;
	
		
	public static LoadManager loadManager;	
	
	public static LoadManager getLoadManager() {
		if (loadManager == null) {
			loadManager = new LoadManager();
		}
		return loadManager;
	}
	
	public LoadManager() {
		loader();
	}
	
	public void destroy() {
		dispose();
		loadManager = null;
	}
	
/*********************************************************************/
	
	private void loader() {
		dispose();
		cellsWide = FFScreen.gameCellsWide;
		cellsHigh = FFScreen.gameCellsHigh;
		//cellSize = FFScreen.gameCellSize;
		fortressCellArray = new char[cellsWide][cellsHigh];
	}
	
	private void dispose() {
		if (fortressCellArray != null) fortressCellArray = null;
		if (fortressNamesList != null) fortressNamesList = null;
	}
	
/*********************************************************************/
	// STORAGE ACCESS
	
	public String getLocalStoragePath() {
		if (Gdx.files.isLocalStorageAvailable()) {
			return Gdx.files.getLocalStoragePath();
		}
		return Reports.NO_STORAGE;
	}
	
	public String simpleUserFortName(String fullFortName) {
		String userPath = getLocalStoragePath();
		if (userPath.equals(Reports.NO_STORAGE)) {
			// error, no path found
			return null;
		}
		else {
			return fullFortName.substring(userPath.length());
		}
	}
	
	/*
	public String getExternalStoragePath() {
		if (CARD_STORAGE_FOLDER.equals(STORAGE_NOT_SET)) {
			if (Gdx.files.isExternalStorageAvailable()) {
				int result = createExternalStorage();
				if (result == CHECK_GOOD) 
					return Gdx.files.getExternalStoragePath();
			}
		}
		return Reports.NO_STORAGE;
	}
	*/
	
/*********************************************************************/
	// STORAGE SET UP
	/*
	private int createExternalStorage() {	
		String rootPathExt = getExternalStoragePath();
		if (rootPathExt != null) {
			FileHandle ff2ExtDir = new FileHandle(rootPathExt + CARD_STORAGE_FOLDERNAME);
			// check if already exists
			if (ff2ExtDir.isDirectory()) {
				CARD_STORAGE_FOLDER = ff2ExtDir.toString();
				return CHECK_GOOD;
			}
			else {
				ff2ExtDir.mkdirs();
				// final check
				if (ff2ExtDir.isDirectory()) {
					CARD_STORAGE_FOLDER = ff2ExtDir.toString();
					return CHECK_GOOD;
				}
			}
		}
		return NO_STORAGE;
	}
	*/
	
/*********************************************************************/
	// FILE ACCESS
	
	public int loadFortressNamesList(int location) {
		return loadFortressList(location);
	}
	
	public Array<String> getFortressNamesList() {
		return fortressNamesList;
	}
	
	public int loadFortressFile(String fortressName, int location) {
		if (FFScreen.DEBUG) Log.i(TAG, "load fortfile: " + fortressName);
		if (location != USER_STORAGE 
				&& location != GAME_STORAGE
				&& location != CARD_STORAGE) {
			return NO_STORAGE;
		}
		
		else if (fortressName.equals("") 
				|| fortressName.equals(null)) 
			return NO_FILENAME;
		
		else {
			// check file
			int result = verifyFortressFileHandle(fortressName, location);
			if (result == FILE_FOUND) {
				FileHandle fortressFileHandle = getFortressFileHandle(fortressName, location);
				if (fortressFileHandle != null) {
					// verify it is a fortress file
					result = verifyFortressFileContents(fortressFileHandle);
					if (result == VERIFIED_FILE) {
						// load the fortressfile
						if (loadVerifiedFortressFile(fortressFileHandle)) {
							return FILE_LOADED;
						}
					}
					else if (result == CELL_COUNT_ERROR 
							|| result == LINE_COUNT_ERROR
							|| result == FILE_LENGTH_ERROR) {
						return result;
					}
				}
			}
			return result;
		}
	}
	
	public char[][] getFortressArray() {
		return fortressCellArray;
	}
	
	public int checkSaveNewFile(String name, int location) {
		// check location
		if (location != USER_STORAGE 
				&& location != GAME_STORAGE
				&& location != CARD_STORAGE) {
			return NO_STORAGE;
		}
		// check name
		if (name.equals(null) || name.equals("")) {
			return NO_FILENAME;
		}
		if (name.length() > MAX_FORTRESS_FILE_NAME_LENGTH) {
			return FILENAME_LONG;
		}
		
		// check storage
		if (location == USER_STORAGE) {
			if (Gdx.files.isLocalStorageAvailable()) {
				return CHECK_GOOD;
			}
			else 
				return NO_LOCAL_ACCESS;
		}
		else if (location == CARD_STORAGE) {
			if (Gdx.files.isExternalStorageAvailable()) {
				return CHECK_GOOD;
			}
			else 
				return NO_EXTERNAL_ACCESS;
		}		
		return CHECK_ERROR;
	}
	
	public String getRandomFortressName() {
		return getRandomString(8); // number is size of string
	}
	
	public int deleteExistingFile(String fileName, int location) {
		int result = verifyFortressFileHandle(fileName, location);
		
		if (result == FILE_FOUND) {
			FileHandle deleteFile = getFortressFileHandle(fileName, location);
			result = deleteExistingFile(deleteFile);
		}
		return result;
	}
	
	public int saveNewFortress(String newName, int location, char[][] userFortressArray) {
		// assume has been thru pre-save checks above...
		// but needs file extension added
		newName += "." + FORTRESS_EXT;
		if (FFScreen.DEBUG)Log.i(TAG, "save new name: " + newName);
		
		int result = verifyFortressFileHandle(newName, location);
		
		if (result == NO_FILE_FOUND) {
			if (FFScreen.DEBUG)Log.i(TAG, "is a new file.");
			// is a new file, so can write new one
			result = verifyUserCreatedFortress(userFortressArray);
			if (result == VERIFY_GOOD) {
				if (FFScreen.DEBUG)Log.i(TAG, "user fortress verified.");
				result = writeToUserFile(newName, location, userFortressArray);
			}
		}
		if (result == FILE_FOUND) {
			// option to overwrite existing
			if (FFScreen.DEBUG)Log.i(TAG, "verifyFileHandle says it exists.");
			// change result to be FILE_EXISTS (overwrite?)
			result = FILE_EXISTS;
		}
		return result;
	}
	
	public int autoSaveFortFile(char[][] userFortressArray) {
		// if file exists, make sure is deleted first...
		deleteExistingFile(AUTOSAVE_FILENAME, USER_STORAGE);
		return writeToUserFile(AUTOSAVE_FILENAME, USER_STORAGE, userFortressArray);
	}
	
	public int overwriteExistingFile(String name, int location, char[][] userFortressArray) {
		deleteExistingFile(name, location);
		return writeToUserFile(AUTOSAVE_FILENAME, USER_STORAGE, userFortressArray);
	}
	
/*********************************************************************/
	// LOADING FILES
	
	private int loadFortressList(int location) {
		// add sort method
		
		// check location
		if (location != USER_STORAGE 
				&& location != GAME_STORAGE
				&& location != CARD_STORAGE) {
			return NO_STORAGE;
		}	
		if (location == USER_STORAGE) {
			if (Gdx.files.isLocalStorageAvailable()) {
				fortressNamesList = new Array<String>();
				FileHandle[] files = Gdx.files.local("/").list();
				if (files.length > 0) {
					for (FileHandle file: files) {
						if (verifyFortressFileExtension(file))
							fortressNamesList.add(file.toString());	
					}
					return FILE_LOADED;
				}
				return NO_FILES_FOUND;
			}
			return NO_LOCAL_ACCESS;
		}
		else if (location == GAME_STORAGE) {
			fortressNamesList = new Array<String>();
			FileHandle[] files = Gdx.files.internal(TEST_FORTRESS_FOLDER).list();
			if (files.length > 0) {
				for (FileHandle file: files) {
					if (verifyFortressFileExtension(file))
						fortressNamesList.add(file.toString());	
				}
				return FILE_LOADED;
			}
			return NO_FILES_FOUND;
		} 
		else if (location == CARD_STORAGE) {
			if (Gdx.files.isExternalStorageAvailable()) {
				fortressNamesList = new Array<String>();
				FileHandle[] files = Gdx.files.external("/").list();
				if (files.length > 0) {
					for (FileHandle file: files) {
						if (verifyFortressFileExtension(file))
							fortressNamesList.add(file.toString());	
					}
					return FILE_LOADED;
				}
				return NO_FILES_FOUND;
			}
			return NO_EXTERNAL_ACCESS;
		}
		return LOAD_ERROR;
	}
	
	private int verifyFortressFileHandle(String fortressName, int location) {
		// check file extension here as well
		FileHandle checkFortress;
		if (location == USER_STORAGE) {
			checkFortress = Gdx.files.local(fortressName);
		}
		else if (location == GAME_STORAGE) {
			checkFortress = Gdx.files.internal(fortressName);		
		}
		else if (location == CARD_STORAGE) {
			checkFortress = Gdx.files.external(fortressName);			
		}
		else 
			return NO_STORAGE;

	
		if (checkFortress.exists()) {
			return FILE_FOUND;
		}
		else 
			return NO_FILE_FOUND;
	}
	
	
	private boolean verifyFortressFileExtension(FileHandle checkFortress) {	
		return checkFortress.extension().toString().equals(FORTRESS_EXT);
	}

	private FileHandle getFortressFileHandle(String fortressName, int location) {
		// should have been verified in the above
		FileHandle checkFortress;
		if (location == USER_STORAGE) {
			checkFortress = Gdx.files.local(fortressName);
		}
		else if (location == GAME_STORAGE) {
			checkFortress = Gdx.files.internal(fortressName);
		}
		else if (location == CARD_STORAGE) {
			checkFortress = Gdx.files.external(fortressName);
		}
		else 
			checkFortress = null;
				
		if (verifyFortressFileExtension(checkFortress)) {
			return checkFortress;
		}
		else {
			return null;
		}
	}
	
	private int verifyFortressFileContents(FileHandle fortressFileHandle) {		
		// file should exist by now...
		// last chance to fail...
		if (fortressFileHandle.exists()) {
			// first check
			double fileLength = fortressFileHandle.length();
			// IS THIS RELIABLE?
			if (fileLength >= MIN_FORTRESS_FILE_LENGTH 
					&& fileLength <= MAX_FORTRESS_FILE_LENGTH) {
				// second check
				String line;
				int lineCount = 0;
				int charCount = 0;
				
				try {
					Reader fileReader = fortressFileHandle.reader();
					BufferedReader reader = new BufferedReader(fileReader);
					
					while ((line = reader.readLine()) != null) {
						lineCount++;
						for (String s : line.split(" ")) {
							if (IdManager.cellValidIdCheck(s.charAt(0))) {
								charCount++;
							}
						}
					}
					reader.close();
					fileReader.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
					return LOAD_ERROR;
				}
				// if loading a smaller created user fort it will have one less line than optimum
				// this accounts for the original test forts being at that size
				// all creator files will be one less
				if (lineCount <= ScreenUtils.optimumCellsHigh) {
					// third check
					// should have 15 cells and 14 spaces between
					if ((charCount / lineCount) == ScreenUtils.optimumCellsWide) {
						// fourth and last check
						//need to check charset?
						return VERIFIED_FILE;
					}
					else {
						return CELL_COUNT_ERROR;
					}
				}
				else {
					return LINE_COUNT_ERROR;
				}
			}
			else {
				return FILE_LENGTH_ERROR;
			}
		}		
		return LOAD_ERROR;
	}
		
	private boolean loadVerifiedFortressFile(FileHandle fortressFileHandle) {	
		// file should exist by now...	
		if (fortressFileHandle.exists()) {		
			fortressCellArray = new char[cellsWide][cellsHigh];
			String line;				
			int x = 0;
			int y = 0;
						
			try {
				Reader fileReader = fortressFileHandle.reader();
				BufferedReader reader = new BufferedReader(fileReader);
							
				while ((line = reader.readLine()) != null) {
					for (String s : line.split(" ")) {
						if (y < cellsHigh) {		
							if (x < cellsWide) {
								if (IdManager.cellValidIdCheck(s.charAt(0))) {
									//only allow valid chars, else skip this
									fortressCellArray[x][y] = s.charAt(0);
								}								
								x++;
							}	
						}
					}
					x = 0;
					y++;
				}
				reader.close();
				fileReader.close();
				return true;
			}
			catch (IOException e) {
				// uh oh, error reading in fortress file
				return false;
			}
		}
		return false;
	}
	
/*********************************************************************/	
	// SAVING FILES

	private int verifyUserCreatedFortress(char[][] userFortressArray) {
		// need to have at least:
		boolean hasPlayer = false;
		int playerCount = 0;
		boolean hasSwitch = false;
		boolean hasGateway = false;
		boolean hasPowerup = false;
		
		int x = 0;
		for (int y = 0; y < cellsHigh; ) {		
			for (; x < cellsWide; ) {
				// checks each row for valid cell chars and required cells
				if (IdManager.cellValidIdCheck(userFortressArray[x][y])) {
					// is good
					if (userFortressArray[x][y] == IdManager.PRIVATE_FIGHTER) {
						hasPlayer = true;
						playerCount++;
					}
					if (userFortressArray[x][y] == IdManager.SWITCH) hasSwitch = true;
					if (userFortressArray[x][y] == IdManager.GATEWAY) hasGateway = true;
					if (userFortressArray[x][y] == IdManager.POWERUP) hasPowerup = true;
				}
				else {
					// bad character found, replace with blank
					//Log.d(TAG, "found bad char at XY: " + x + ", " + y + " : " + userFortressArray[x][y] + " , replacing with empty cell.");
					userFortressArray[x][y] = IdManager.EMPTY;
				}
				x++;
			}
			// end of line
			y++;
			x = 0;
		}
		
		if (!hasPlayer) return NO_PLAYER;
		if (playerCount > 1) return MULTIPLE_PLAYER;
		if (!hasSwitch) return NO_SWITCH;
		if (!hasGateway) return NO_GATEWAY;
		if (!hasPowerup) return NO_POWERUP;
		
		return VERIFY_GOOD;
	}
	
	private int writeToUserFile(String userFortressName, int location, char[][] userFortressArray) {
		// this assumes it is a new file for writing,
		// any previous versions have been deleted.

		FileHandle userFortressFile = getFortressFileHandle(userFortressName, location);

		int x = 0;
		StringBuilder line = new StringBuilder(cellsWide);
		for (int y = 0; y < cellsHigh; ) {		
			for (; x < cellsWide; ) {
				line.append(userFortressArray[x][y]);
				line.append(" "); // add a space
				x++;
			}
			// new line char
			line.append("\n");
			userFortressFile.writeString(line.toString(), true); // boolean append		
						
			// reset all the vars
			y++;
			x = 0;
			line = new StringBuilder(cellsWide);
		}
		return FILE_SAVED;
	}
	
	private int deleteExistingFile(FileHandle fileHandle) {
		if (fileHandle.exists()) {
			fileHandle.delete();
			return FILE_DELETED;
		}
		else 
			return FILE_NOT_DELETED;
	}

	
/*********************************************************************/	
// HELPER FUNCTIONS
	
	private String getRandomString(final int sizeOfRandomString) {
		final Random random = new Random();
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sizeOfRandomString; ++i)
			sb.append(ALLOWED_RANDOM_CHARS.charAt(random.nextInt(ALLOWED_RANDOM_CHARS.length())));
		
		return sb.toString();
	}
	
	/*
	private Array<String> sortList(Array<String> unsorted) {
		//TODO
		// no lastModified in this build of libGDX...
		//Array<String> sorted = new Array<String>();
		//sorted = Array.sort(unsorted);
		unsorted.sort();
		return unsorted;
	}
	*/
}