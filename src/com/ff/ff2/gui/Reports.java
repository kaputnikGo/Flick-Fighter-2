package com.ff.ff2.gui;

import com.badlogic.gdx.utils.Array;

public class Reports {
	// accessed directly via static
	public static final String CREATOR_STATUS = "Creator status:";
	public static final String CONFIRM_DIALOG = "load, delete or exit:";
	public static final String CREATOR_EXIT = "save or exit";
	public static final String DELETE_DIALOG = "then select file to delete";
	public static final String FILE_DIALOG = "File Actions Menu";
	public static final String SAVE_DIALOG = "Save Fortress";
	public static final String ENGINE_STATUS = "Game status:";
	public static final String SCREEN_STATUS = "Screen status:";
	public static final String FILE_ACTION_STATUS = "File status:";
	public static final String DEFAULT_SAVE_NAME = "FF2fort";
	public static final String BUILD_DIALOG = "Build random fortress type:";
	public static final String OVERWRITE_DIALOG = "Overwrite file or exit?";
	
	// THESE ARE ACCESSED VIA THE reportList array
	// THESE MUST BE IN NUMERICAL ORDER FOR THE CREATOR TO USE VIA LOADMANAGER 
	public static final String DEFAULT = "Default message of nothing much.";
	// creator: pre-save checks
	public static final String NO_STORAGE = "No storage location found"; // 1
	public static final String NO_FILENAME = "Fortress filename is blank.";
	public static final String FILENAME_LONG = "Fortress filename is too long.";
	public static final String NO_LOCAL_ACCESS = "Local save location unavailable.";
	public static final String NO_EXTERNAL_ACCESS = "External save location unavailable.";
	public static final String CHECK_GOOD = "Pre-save checks good.";
	public static final String CHECK_ERROR = "Pre-save checks error.";		
	// creator: verify new save file
	public static final String NO_PLAYER = "No player cell found."; // 8
	public static final String MULTIPLE_PLAYER = "Multiple player cells found, use one only."; 
	public static final String NO_SWITCH = "No switch cell found."; 
	public static final String NO_GATEWAY =	"No gateway cell found."; 
	public static final String NO_POWERUP = "No powerup cell found.";
	public static final String VERIFY_GOOD = "Created fortress verified.";
	public static final String VERIFY_ERROR = "Error occured in verification process.";
	// creator: write new save file
	public static final String FILE_EXISTS = "File already exists, delete?"; // 15
	public static final String FILE_SAVED = "File saved";
	// creator : load file
	public static final String LOAD_ERROR = "File load error"; // 17
	public static final String CELL_COUNT_ERROR = "File has incorrect cell count.";
	public static final String LINE_COUNT_ERROR = "File has incorrect line count.";
	public static final String FILE_LENGTH_ERROR = "File length error, too much data";
	public static final String VERIFIED_FILE = "File is verified useable fortress";
	public static final String FILE_LOADED = "File successfully loaded.";
	public static final String NO_FILE_FOUND = "No file found";
	public static final String FILE_FOUND = "File exists.";
	public static final String FILE_EXT_ERROR = "Wrong file extension.";
	public static final String NO_FILES_FOUND = "No files found.";
	public static final String FILE_DELETED = "File deleted.";
	public static final String FILE_NOT_DELETED = "File not deleted.";
	// creator : convert array
	public static final String NO_CREATED_CELLS = "No user created cells found."; // 29
	public static final String CELL_ARRAY_CONVERTED = "User cells converted.";
	public static final String CELL_CONVERT_ERROR = "User cell conversion error.";
	//
	public static final String EXIT_BEFORE_SAVE = "Save the file?"; //32
	public static final String CONFIRM_SEND = "Send the file?";
	
	public static final String SCREEN_SMALL_ERROR = "Screen size too small"; //34
	public static final String SCREEN_ASPECT_ERROR = "Screen aspect not supported";
	
	public static final String BUILD_GOOD = "Generated build good."; //36
	public static final String BUILD_ERROR = "Generated build error.";
	
	public static Array<String> reportList;	
	
	public Reports() {
		loadList();
	}
	
	public void destroy() {
		dispose();
	}
	
	public static void loadList() {
		reportList = new Array<String>();
		reportList.add(DEFAULT);
		//
		reportList.add(NO_STORAGE);
		reportList.add(NO_FILENAME);
		reportList.add(FILENAME_LONG);
		reportList.add(NO_LOCAL_ACCESS);
		reportList.add(NO_EXTERNAL_ACCESS);
		reportList.add(CHECK_GOOD);
		reportList.add(CHECK_ERROR);
		//
		reportList.add(NO_PLAYER);
		reportList.add(MULTIPLE_PLAYER);
		reportList.add(NO_SWITCH);
		reportList.add(NO_GATEWAY);
		reportList.add(NO_POWERUP);
		reportList.add(VERIFY_GOOD);
		reportList.add(VERIFY_ERROR);
		//
		reportList.add(FILE_EXISTS);
		reportList.add(FILE_SAVED);
		//
		reportList.add(LOAD_ERROR);
		reportList.add(CELL_COUNT_ERROR);
		reportList.add(LINE_COUNT_ERROR);
		reportList.add(FILE_LENGTH_ERROR);
		reportList.add(VERIFIED_FILE);
		reportList.add(FILE_LOADED);
		reportList.add(NO_FILE_FOUND);
		reportList.add(FILE_FOUND);
		reportList.add(FILE_EXT_ERROR);
		reportList.add(NO_FILES_FOUND);
		reportList.add(FILE_DELETED);
		reportList.add(FILE_NOT_DELETED);
		//
		reportList.add(NO_CREATED_CELLS);
		reportList.add(CELL_ARRAY_CONVERTED);
		reportList.add(CELL_CONVERT_ERROR);
		//
		reportList.add(EXIT_BEFORE_SAVE);
		reportList.add(CONFIRM_SEND);
		//
		reportList.add(SCREEN_SMALL_ERROR);
		reportList.add(SCREEN_ASPECT_ERROR);
	}
	
	private void dispose() {
		reportList.clear();
		reportList = null;
	}
}