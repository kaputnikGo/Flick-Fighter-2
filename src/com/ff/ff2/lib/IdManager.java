package com.ff.ff2.lib;

import com.ff.ff2.obs.Barrier;
import com.ff.ff2.obs.Bombup;
import com.ff.ff2.obs.Fence;
import com.ff.ff2.obs.Gateway;
import com.ff.ff2.obs.Hanger;
import com.ff.ff2.obs.Mine;
import com.ff.ff2.obs.Obstacle;
import com.ff.ff2.obs.Powerup;
import com.ff.ff2.obs.Pushblock;
import com.ff.ff2.obs.Scaffold;
import com.ff.ff2.obs.Switch;

//import android.util.Log;

public class IdManager {
	//private static final String TAG = IdManager.class.getSimpleName();

	// BUTTON ID
	public static final int BTN_DEFAULT = 0;
	public static final int BTN_SOUND = 1;
	public static final int BTN_CREATOR = 2;
	public static final int BTN_EXIT = 3;
	public static final int BTN_FILE = 4;
	public static final int BTN_FILE_EXIT = 5;
	public static final int BTN_FILE_SAVE = 6;
	public static final int BTN_FILE_SEND = 7;
	public static final int BTN_FILE_LOAD = 8;
	public static final int BTN_FILE_PLAY = 9;
	public static final int BTN_FILE_NO_PLAY = 10;
	public static final int BTN_TOOL = 11;
	public static final int BTN_NEXT = 12;
	public static final int BTN_PREV = 13;
	public static final int BTN_DELETE = 14;
	public static final int BTN_YES = 15;
	public static final int BTN_NO = 16;
	public static final int BTN_BUILD = 17;
	public static final int BTN_PROC = 18;
	public static final int BTN_RAND = 19;
	public static final int BTN_GROUPS = 20;
	public static final int BTN_NO_CREATOR = 21;
	public static final int BTN_NO_SOUND = 22;
	public static final int BTN_ARENA = 23;
	public static final int BTN_DIFF_EASY = 24;
	public static final int BTN_DIFF_NORM = 25;
	public static final int BTN_DIFF_HARD = 26;
	public static final int BTN_MENU = 27;
	public static final int BTN_NO_MENU = 28;
	public static final int BTN_OSD = 29;
	
	
	// WORD NUM ID
	public static final int WORD_LOGO = 1;
	public static final int WORD_PLAYER = 2;
	public static final int WORD_TURRET = 3;
	public static final int WORD_FENCE = 4;
	public static final int WORD_BARRIER = 5;
	public static final int WORD_GATEWAY = 6;
	public static final int WORD_POWERUP = 7;
	public static final int WORD_SWITCH = 8;
	public static final int WORD_ENEMY = 9;
	public static final int WORD_MINE = 10;
	public static final int WORD_TURNSTILE = 11;
	public static final int WORD_PUSHBLOCK = 12;
	public static final int WORD_BOMBUP = 13;
	
	// SIDES AND WEAPONS
	public static final int PRIVATE_ID = 1;
	public static final int GENERAL_ID = 2;
	
	public static final int PRIVATE_BULLET = 3;
	public static final int DRONE_BULLET = 4;
	public static final int TURRET_BULLET = 5; 
	
	public static final int POWER_CABLE = 6;
	public static final int MISSILE_BOMB = 7;	
	public static final int MINE_DEBRIS = 8;
	
	public static final int OBSTACLE_ID = 9; // for sounds
	
	public static final int WORDBLOCK_NUM = 15;
	
	// FORTRESS/CREATOR CELLS
	public static final char EMPTY = '+'; // empty means deliberately empty
	//public static final char BLANK = '-'; // blank means checks found a blank
	public static final char DELETE = 'd';	
	public static final char LOCK = 'l'; // lower-case L
	
	public static final char WORDBLOCK = 'w';
	public static final char PRIVATE_FIGHTER = 'p';
	public static final char TURRET = 't';
	public static final char DRONE = 'e';
	public static final char SCAFFOLD = 'x';
	public static final char FENCE = 'f';
	public static final char BARRIER = 'b';
	public static final char GATEWAY = 'g';
	public static final char POWERUP = 'u';
	public static final char SWITCH = 's';
	public static final char MINE = 'm';
	public static final char PUSHBLOCK = 'o'; // lower-case O, not zero
	public static final char BOMBUP = 'c';	
	public static final char HANGER = 'h';
	
	public static final char PATH_DEBUG = 'z';
	
	public static final char NULL_ID = 'n';
	
	
	// below to be implemented
	public static final char BOMBER = 'a'; // is for avro lancaster...
	// end to be implemented
	
	// cell based directions
	/*
	 * |0|1|2|
	 * |7|p|3|
	 * |6|5|4| 
	 * 
	 */
	public static final int NW = 0;
	public static final int NORTH = 1;
	public static final int NE = 2;
	public static final int EAST = 3;
	public static final int SE = 4;
	public static final int SOUTH = 5;
	public static final int SW = 6;
	public static final int WEST = 7;
	public static final int UNKNOWN = 8; 
	
	// used by PathFinder Scan only
	//public static final char VOID_CELL = 'v';
	public static final char UNKNOWN_CELL = '?';
	public static final char SCAN_SOURCE = 'S';
	public static final char SCAN_TARGET = 'T';
	
	public static final int CELL_LIST_SIZE = 16; // based upon loadList() below
	
	// change this to libgdx Array<char>() ?
	public static char[] cellList;
	public static int toolbarStart;
	public static int toolbarEnd;
	
	
	public IdManager() {
		//
	}
	
	public void destroy() {
		dispose();
	}
	
	public static void loadCellList() {
		if (cellList == null) {
			cellList = new char[CELL_LIST_SIZE];
		}
		loadList();
	}
	
	public static boolean cellValidIdCheck(char candidate) {
		for (char c: cellList) {  
			if(candidate == c) {  
				return true;  
			}     
		}
		return false;
	}
	
	public static char getObstacleId(Obstacle obstacle) {		
		if (obstacle instanceof Barrier) return BARRIER;
		else if (obstacle instanceof Bombup) return BOMBUP;
		else if (obstacle instanceof Fence) return FENCE;
		else if (obstacle instanceof Gateway) return GATEWAY;
		else if (obstacle instanceof Mine) return MINE;
		else if (obstacle instanceof Powerup) return POWERUP;
		else if (obstacle instanceof Pushblock) return PUSHBLOCK;
		else if (obstacle instanceof Scaffold) return SCAFFOLD;
		else if (obstacle instanceof Switch) return SWITCH;
		else if (obstacle instanceof Hanger) return HANGER;
		else 
			return EMPTY;
	}
	
	public static boolean getIdShootable(char id) {
		//TODO this not working as expected
		// return if the id can be destroyed by shooting it
		if (id == PRIVATE_FIGHTER ||
				id == TURRET ||
				id == DRONE ||
				id == FENCE ||
				id == BARRIER ||
				id == HANGER ||
				id == MINE) {
			return true;
		}
		else {
			return false;
		}
	}
	
/*********************************************************************/
	
	private static void loadList() {
		// order is important as used by FFCreator
		// firstly, below required for valid fortress
		cellList[0] = PRIVATE_FIGHTER;
		cellList[1] = GATEWAY;
		cellList[2] = POWERUP;
		cellList[3] = SWITCH;
				
		// secondly, below (toolbar tools) extras from here
		toolbarStart = 4;	
		cellList[4] = DELETE;
		cellList[5] = SCAFFOLD;
		cellList[6] = BARRIER;
		cellList[7] = FENCE;
		cellList[8] = TURRET;
		cellList[9] = DRONE;
		cellList[10] = MINE;
		cellList[11] = PUSHBLOCK;
		cellList[12] = BOMBUP;
		cellList[13] = HANGER;
		cellList[14] = LOCK; // this one at end of list
		toolbarEnd = 14;
		// end toolbar tools
				
		cellList[15] = EMPTY;
	}
	
	private void dispose() {
		
	}
}