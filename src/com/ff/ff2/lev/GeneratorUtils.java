package com.ff.ff2.lev;

import java.util.Random;

import android.util.Log;

import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.IdManager;

public class GeneratorUtils {
	private static final String TAG = GeneratorUtils.class.getSimpleName();

	private static int groupSize = Generator.GROUP_SIZE;
	
	// weighted for added chance
	public static final char[] MAIN_ENEMIES = {
		IdManager.TURRET,
		IdManager.TURRET,
		IdManager.DRONE,
		IdManager.TURRET
	};
	
	public static final char[] SPECIAL_ENEMIES = {
		IdManager.MINE,
		IdManager.MINE,
		IdManager.HANGER
	};
	
	public static final char[] MAIN_OBSTACLES = {
		IdManager.FENCE,
		IdManager.BARRIER,
		IdManager.FENCE
	};
	
	public static final char[] REQUIRED_CELLS = {
		IdManager.PRIVATE_FIGHTER, 
		IdManager.GATEWAY,
		IdManager.POWERUP,
		IdManager.SWITCH
	};
	
	public static final char[] EXTRAS_CELLS = {
		IdManager.PUSHBLOCK,
		IdManager.BOMBUP,
		IdManager.PUSHBLOCK
	};

	
/*********************************************************************/	
	
	public GeneratorUtils() {
		//
	}
	
/*********************************************************************/
	
// GROUP FUNCTIONS
	
	public static Group placeRequiredCellInGroup(Group group) {
		/*
		 * interior of GROUP of 5x5 cells, y = can place vital cell(p,g,s,u) here
		 * the rest are free for placing scaffolds,barriers,fences,turrets,etc
		 * 
		 * | | | | | |
		 * | |y|y|y| |
		 * | |y|y|y| |
		 * | |y|y|y| |
		 * | | | | | | 
		 * 
		 */
		group.cells[getRandInt(1, 3)][getRandInt(1, 3)] = group.primeCellId;		
		return group;
	}
	
	public static int getGroupsSeparatingAxis(Group group1, Group group2) {		
		// find relative to group1
		if (group1.groupX < group2.groupX) {
			return Generator.EAST;
		}
		else if (group1.groupX > group2.groupX) {
			return Generator.WEST;
		}
		else if (group1.groupX == group2.groupX) {
			if (group1.groupY < group2.groupY) {
				return Generator.SOUTH;
			}
			else if (group1.groupY > group2.groupY) {
				return Generator.NORTH;
			}
		} 
		// else error - are in same group?	
		return Generator.UNKNOWN;
	}
	
	public static void scaffoldWallInGroup(Group group, int wall) {
		switch (wall) {
			case Generator.NORTH:
				// scaffold along x
				for (int x = 0; x < groupSize; x++) {
					group.cells[x][0] = IdManager.SCAFFOLD;
				}
				break;
			case Generator.SOUTH:
				// scaffold along x
				for (int x = 0; x < groupSize; x++) {
					group.cells[x][4] = IdManager.SCAFFOLD;
				}
				break;
				
			case Generator.EAST:
				// scaffold down y
				for (int y = 0; y < groupSize; y++) {
					group.cells[4][y] = IdManager.SCAFFOLD;
				}
				break;
			case Generator.WEST:
				// scaffold down y
				for (int y = 0; y < groupSize; y++) {
					group.cells[0][y] = IdManager.SCAFFOLD;
				}
				break;
			case Generator.UNKNOWN:
			default:
				// uh-oh
				scaffoldWallInGroup(group, getRandInt(0, 3));
		}				
		//return group;
	}
	
	public static Group generateRandomObstacleGroup(Group group) {
		// quantity = number of obs per row - not the best implement
		// group type now 20 to 24 (was 7,8,9,10,11)
		// want 2,3,4,5,6 - need to account for reorganising group.type numbering
		int quantity = group.type - Generator.OBSTACLES_SIZER;
	
		int count = 0;
		int rx = 0;
		
		for (int y = 0; y < groupSize; y++) {
			for (int x = 0; x < groupSize; x++) {
				if (count < quantity) {
					rx = getRandInt(0 + x, groupSize - 1);
					group.cells[rx][y] = getRandObstacle();
					count++;
				}
			}
			count = 0;
		}		
		return group;
	}
	
	public static Group generateRandomBatteryGroup(Group group) {
		switch(group.type) {
			case Generator.BATTERY_AXIS_Y:
			case Generator.BATTERY_AXIS_X:
			case Generator.BATTERY_TUNNEL:
				group = getAxisBatteryGroup(group);
				break;
				
			case Generator.BATTERY_CORNER:
			case Generator.BATTERY_ISLAND:
			case Generator.BATTERY_VAULT:
			case Generator.BATTERY_PLUS:
			case Generator.BATTERY_CROSS:
				group = getShapeBatteryGroup(group);
				break;
				
			case Generator.GROUP_EMPTY:
			default:
				break;
		}		
		return group;
	}
	
	public static Group generateBatteryWallGroup(Group group) {
		return group = getWallBatteryGroup(group);
	}
	
	public static Group generateWallHangerGroup(Group group) {
		return group = getWallHangerGroup(group);
	}
	
	public static Group generateIslandHangerGroup(Group group) {
		return group = getIslandHangerGroup(group);
	}
	
/*********************************************************************/	
		
	public static int getRandInt(int min, int max) {
	    Random rand = new Random();
	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    return rand.nextInt((max - min) + 1) + min;
	}
	
	public static char[] getRandOrderExtrasCells() {
		return shuffleArray(EXTRAS_CELLS);
	}
	
	public static char[] getRandOrderSpecialEnemies() {
		return shuffleArray(SPECIAL_ENEMIES);
	}
	
	public static char[] getRandOrderRequiredCells() {	
		return shuffleArray(REQUIRED_CELLS);
	}
	
/*********************************************************************/	
	
	public static char[][] checkMaxEnemiesFortress(char[][] fortress) {
		if (FFScreen.DEBUG) Log.i(TAG, "checking for maxCounts in fortress:");
		debugPrintFort(fortress);
		// go thru a generated fort and check that number of enemies not too high
		// replacing with barriers
		// currently checks from top-left down, this weights enemies to be at that corner...
		// could change to start furthest end away from playerShip?
		
		// either scan ahead, count enemies and then divide max num to get even space distribution,
		// or weight the actual enemies left to be in the groups near REQUIRED_CELLS
		
		int bCount = 0, dCount = 0, mCount = 0, tCount = 0;
		
		// scan and count first
		for (int y = 0; y < FFScreen.gameCellsHigh; y++) {
			for (int x = 0; x < FFScreen.gameCellsWide; x++) {
				if (fortress[x][y] == IdManager.BOMBUP) {
					bCount++;
				}
				else if (fortress[x][y] == IdManager.DRONE) {
					dCount++;
				}
				else if (fortress[x][y] == IdManager.MINE) {
					mCount++;
				}
				else if (fortress[x][y] == IdManager.TURRET) {
					tCount++;
				}
			}
		}	
		debugCheckCount("found", bCount, dCount, mCount, tCount);
		
		int bReplace = 0, dReplace = 0, mReplace = 0, tReplace = 0;
		// check for each count
		if (bCount > FFScreen.engine.settings.MAX_BOMBUPS) {
			//
			bReplace = (int) Math.ceil(bCount / FFScreen.engine.settings.MAX_BOMBUPS);
		}
		if (dCount > FFScreen.engine.settings.MAX_DRONES) {
			//
			dReplace = (int) Math.ceil(dCount / FFScreen.engine.settings.MAX_DRONES);
		}
		if (mCount > FFScreen.engine.settings.MAX_MINES) {
			//
			mReplace = (int) Math.ceil(mCount / FFScreen.engine.settings.MAX_MINES);
		}
		if (tCount > FFScreen.engine.settings.MAX_TURRETS) {
			//
			tReplace = (int) Math.ceil(tCount / FFScreen.engine.settings.MAX_TURRETS);
		}
		debugCheckCount("remove every ", bReplace, dReplace, mReplace, tReplace);
		
		// setup for even re-distribution, reset counters
		bCount = 0;
		dCount = 0;
		mCount = 0; 
		tCount = 0;
	
		// scan again and replace cells if needed
		for (int y = 0; y < FFScreen.gameCellsHigh; y++) {
			for (int x = 0; x < FFScreen.gameCellsWide; x++) {
				// checks and replaces one or other
				if (fortress[x][y] == IdManager.BOMBUP) {
					if (bReplace > 1) {
						bCount++;
						if (bCount % bReplace != 0) fortress[x][y] = IdManager.BARRIER;
					}
				}
				
				else if (fortress[x][y] == IdManager.DRONE) {
					if (dReplace > 1) {
						dCount++;
						if (FFScreen.DEBUG) Log.i(TAG, "mod drone dCount: " + dCount + ", mod = " + dCount % dReplace);
						if (dCount % dReplace != 0) fortress[x][y] = IdManager.BARRIER;
					}
				}
				
				else if (fortress[x][y] == IdManager.MINE) {
					if (mReplace > 1) {
						mCount++;
						if (mCount % mReplace != 0) fortress[x][y] = IdManager.BARRIER;
					}
				}
				
				else if (fortress[x][y] == IdManager.TURRET) {
					if (tReplace > 1) {
						tCount++;
						if (tCount % tReplace != 0) fortress[x][y] = IdManager.BARRIER;
					}
				}
			}
		}
		if (FFScreen.DEBUG) Log.i(TAG, "replace fin, fortress: ");
		debugPrintFort(fortress);
		
		return fortress;
	}
	
/*********************************************************************/
	
// FUNCTIONS TO FILL ENEMY FORTRESS GROUPS
	
	private static Group getAxisBatteryGroup(Group group) {
		char[] genBattery = new char[groupSize];
		genBattery = randAxisGroupBattery(genBattery);
		int randNum = getRandInt(1, 2);
		
		if (group.type == Generator.BATTERY_TUNNEL) {
			for (int y = 0; y < groupSize; y++) {
				// places along both vertical walls
				group.cells[0][y] = genBattery[y];
				// skip 1,2,3
				group.cells[4][y] = genBattery[y];
			}
		}
		else {				
			// first fill filler with scaffolds
			for (int filler = 0; filler < randNum; filler++) {				
				if (group.type == Generator.BATTERY_AXIS_Y) {
					for (int x = 0; x < groupSize; x++) {
						if (x == 2) group.cells[x][filler] = IdManager.BARRIER;
						else group.cells[x][filler] = IdManager.SCAFFOLD;
					}
				}
				else {
					for (int y = 0; y < groupSize; y++) {
						if (y == 2) group.cells[filler][y] = IdManager.BARRIER;
						else group.cells[filler][y] = IdManager.SCAFFOLD;
					}
				}
			}
			// then add the battery
			if (group.type == Generator.BATTERY_AXIS_Y) {
				for (int x = 0; x < groupSize; x++) {
					group.cells[x][randNum] = genBattery[x];
				}
			}
			else {
				for (int y = 0; y < groupSize; y++) {
					group.cells[randNum][y] = genBattery[y];
				}
			}
		}
		genBattery = null;
		return group;
	}
	
	private static Group getShapeBatteryGroup(Group group) {
		char[] genBattery = new char[groupSize];		
		
		if (group.type == Generator.BATTERY_CORNER) {
			// currently orients top-left corner
			// and is size cells 
			int size = 2; // 
			genBattery = randAxisGroupBattery(genBattery);
			int ry = 0;
			for (int x = size; x >= 0; x--) {
				if (ry <= size) {
					group.cells[x][ry] = genBattery[ry];
				}
				ry++;
			}
		}
		else if (group.type == Generator.BATTERY_ISLAND) {
			// 9 cells (3x3) in centre of group
			// middle cell is scaffold
			boolean flip = true;
			for (int y = 1; y < groupSize - 1; y++) {
				for (int x = 1; x < groupSize - 1; x++) {
					if (x == 2 && y == 2) group.cells[x][y] = IdManager.SCAFFOLD;
					else {
						if (flip) group.cells[x][y] = getRandEnemy();
						else group.cells[x][y] = getRandObstacle();
						// flip boolean
						flip ^= true;
					}
				}
			}
		}
		else if (group.type == Generator.BATTERY_VAULT) {
			// diamond shape with special inside
			group.cells[2][0] = getRandObstacle();
			
			group.cells[1][1] = getRandEnemy();
			group.cells[3][1] = getRandEnemy();

			group.cells[0][2] = getRandObstacle();
			group.cells[2][2] = getRandExtras();
			group.cells[4][2] = getRandObstacle();
			
			group.cells[1][3] = getRandEnemy();
			group.cells[3][3] = getRandEnemy();
			
			group.cells[2][4] = getRandObstacle();
		}
		else if (group.type == Generator.BATTERY_PLUS) {
			// 0 0 t 0 0 
			// 0 0 b 0 0
			// t b s b t
			// 0 0 b 0 0 
			// 0 0 t 0 0
			group.cells[2][0] = getRandEnemy();
			group.cells[2][1] = getRandObstacle();
			
			group.cells[0][2] = getRandEnemy();
			group.cells[1][2] = getRandObstacle();
			group.cells[2][2] = getRandExtras();
			group.cells[3][2] = getRandObstacle();
			group.cells[4][2] = getRandEnemy();
			
			group.cells[2][3] = getRandObstacle();
			group.cells[2][4] = getRandEnemy();
		}
		else if (group.type == Generator.BATTERY_CROSS) {
			// t 0 0 0 t 
			// 0 b 0 b 0
			// 0 0 s 0 0
			// 0 b 0 b 0 
			// t 0 0 0 t
			group.cells[0][0] = getRandEnemy();
			group.cells[4][0] = getRandEnemy();

			group.cells[1][1] = getRandObstacle();
			group.cells[3][1] = getRandObstacle();

			group.cells[2][2] = getRandExtras();
			
			group.cells[1][3] = getRandObstacle();
			group.cells[3][3] = getRandObstacle();
			
			group.cells[0][4] = getRandEnemy();
			group.cells[4][4] = getRandEnemy();
		}
		genBattery = null;
		return group;
	}
	
	private static Group getWallBatteryGroup(Group group) {
		//
		// wall either N,S or E,W
		// attempt a landscape of sorts, eg NORTH:
		// x x x x x  <- must be scaffolds
		// 0 b b b b
		// 0 0 b b 0
		// 0 0 t 0 0  <- rand enemy
		// 0 0 0 0 0  <- must be empty
		
		switch (group.type) {
			case Generator.BATTERY_WALL_N:
				for (int x = 0; x < groupSize; x++) {
					group.cells[x][0] = IdManager.SCAFFOLD;
				}
				// rand 2 obs
				int rx = getRandInt(1, 2);
				int ry = getRandInt(1, 2);
				// rand 4 obs
				group.cells[rx - 1][1] = IdManager.BARRIER;
				group.cells[rx][1] = IdManager.BARRIER;
				group.cells[rx + 1][1] = IdManager.BARRIER;
				group.cells[rx + 2][1] = IdManager.BARRIER;
				// rand 2 obs
				group.cells[rx][ry] = IdManager.FENCE;
				group.cells[rx + 1][ry] = IdManager.FENCE;
				
				group.cells[2][ry + 1] = getRandEnemy();
				break;
			case Generator.BATTERY_WALL_S:
				for (int x = 0; x < groupSize; x++) {
					group.cells[x][4] = IdManager.SCAFFOLD;
				}
				// rand 2 obs
				rx = getRandInt(1, 2);
				ry = getRandInt(2, 3);
				// rand 4 obs
				group.cells[rx - 1][3] = IdManager.BARRIER;
				group.cells[rx][3] = IdManager.BARRIER;
				group.cells[rx + 1][3] = IdManager.BARRIER;
				group.cells[rx + 2][3] = IdManager.BARRIER;	
				// rand 2 obs
				group.cells[rx][ry] = IdManager.FENCE;
				group.cells[rx + 1][ry] = IdManager.FENCE;
				
				group.cells[2][ry - 1] = getRandEnemy();
				break;
			case Generator.BATTERY_WALL_W:
				for (int y = 0; y < groupSize; y++) {
					group.cells[0][y] = IdManager.SCAFFOLD;
				}
				// rand 2 obs
				rx = getRandInt(1, 2);
				ry = getRandInt(1, 2);
				// rand 4 obs
				group.cells[1][ry - 1] = IdManager.BARRIER;
				group.cells[1][ry] = IdManager.BARRIER;
				group.cells[1][ry + 1] = IdManager.BARRIER;
				group.cells[1][ry + 2] = IdManager.BARRIER;	
				// rand 2 obs
				group.cells[rx][ry] = IdManager.FENCE;
				group.cells[rx][ry + 1] = IdManager.FENCE;
				
				group.cells[rx + 1][2] = getRandEnemy();
				break;
			case Generator.BATTERY_WALL_E:
				for (int y = 0; y < groupSize; y++) {
					group.cells[4][y] = IdManager.SCAFFOLD;
				}
				rx = getRandInt(2, 3);
				ry = getRandInt(1, 2);
				// rand 4 obs
				group.cells[3][ry - 1] = IdManager.BARRIER;
				group.cells[3][ry] = IdManager.BARRIER;
				group.cells[3][ry + 1] = IdManager.BARRIER;
				group.cells[3][ry + 2] = IdManager.BARRIER;
				// rand 2 obs
				group.cells[rx][ry] = IdManager.FENCE;
				group.cells[rx][ry + 1] = IdManager.FENCE;
				
				group.cells[rx - 1][2] = getRandEnemy();
				break;
		}
		return group;
	}
	
	private static Group getIslandHangerGroup(Group group) {
		// can use this for user placed hanger in creator?
		// WARNING check for REQUIREDS...
		//TODO
		/*
		| | |-| | |
		| |x|x|x| |
		|-|x|h|x|-|
		| |x|-|x| |
		| |t|-|t| |
		
		rotate axis(n,s,e,w) for runway, pivot in centre, same hanger design as wallhanger
		if a required cell is in the centre, can shift it to outer rows/cols?
		
		*/
		
		if (group.type == Generator.HANGER) {
			
		}
		else {
			// problem with group type, maybe trying to overwrite required one?
			if (FFScreen.DEBUG) Log.i(TAG, "Group type not HANGER, abort.");
		}
		return group;
	}
	
	private static Group getWallHangerGroup(Group group) {
		/*
		hanger within a group, one cell is actual hanger, the rest are mini fort and clear runway
		
		this can be placed and rotated within a group so it faces either powerup or gateway or switch
		or just to ensure it facing inwards, or has room for a runway.
		
		?? extendRunway() to be sure that drones can leave hanger, 
			
		h = hanger, t = turret, x = scaffold, - = must be blank
		 
		| | | | | |
		| | | | | |
		|x|x|x|t|-|-| | |
		|x|h|-|-|-|-|-|-|		some SPECIAL_CELL over this way, here...
		|x|x|x|t|-|-| | |
			 
			 	   ^-- runway extension from here, overwriting into adjacent group ?
		*/
		
		if (group.type == Generator.HANGER) {
			// go
			if (FFScreen.DEBUG) Log.i(TAG, "blank all cells...");
			// blank each one to be sure
			for (int y = 0; y < groupSize; y++) {
				for (int x = 0; x < groupSize; x++) {
					group.cells[x][y] = IdManager.EMPTY;
				}
			}
			
			// 0 0 0 0 0 
			// x x x t 0
			// x h 0 0 0
			// x x x t 0 
			// 0 0 0 0 0
			
			// get orientation
			if (FFScreen.DEBUG) Log.i(TAG, "get orientation...");
			if (group.groupX == 0) {
				//left facing
				if (FFScreen.DEBUG) Log.i(TAG, "x=0, left facing...");
				// main scaffolds
				for (int y = 1; y < groupSize - 1; y += 2) {
					for (int x = 0; x < groupSize - 2; x++) {
						group.cells[x][y] = IdManager.SCAFFOLD;
					}
				}
				// add hanger, back wall
				group.cells[0][2] = IdManager.SCAFFOLD;
				group.cells[1][2] = IdManager.HANGER;
				// add turrets
				group.cells[3][1] = IdManager.TURRET;
				group.cells[3][3] = IdManager.TURRET;
				
			}
			
			else if (group.groupX == 2) {
				// right facing
				if (FFScreen.DEBUG) Log.i(TAG, "x=2, right facing...");
				// main scaffolds
				for (int y = 1; y < groupSize - 1; y += 2) {
					for (int x = 2; x < groupSize; x++) {
						group.cells[x][y] = IdManager.SCAFFOLD;
					}
				}
				// add hanger, back wall
				group.cells[4][2] = IdManager.SCAFFOLD;
				group.cells[3][2] = IdManager.HANGER;
				// add turrets
				group.cells[1][1] = IdManager.TURRET;
				group.cells[1][3] = IdManager.TURRET;
			}
			
			else {
				// vertical facing
				// 0 x x x 0 
				// 0 x h x 0
				// 0 x 0 x 0
				// 0 t 0 t 0 
				// 0 0 0 0 0
				
				if (FFScreen.DEBUG) Log.i(TAG, "group requires vertical facing fortress.");
				if (group.groupY == 0) {
					// is at top facing down
					if (FFScreen.DEBUG) Log.i(TAG, "top, facing down.");
					for (int y = 0; y < groupSize - 2; y++) {
						for (int x = 1; x < groupSize - 1; x += 2) {
							group.cells[x][y] = IdManager.SCAFFOLD;
						}
					}
					// add hanger, back wall
					group.cells[2][0] = IdManager.SCAFFOLD;
					group.cells[2][1] = IdManager.HANGER;
					// add turrets
					group.cells[1][3] = IdManager.TURRET;
					group.cells[3][3] = IdManager.TURRET;
				}
				
				else if (group.groupY == 4) {
					// is at bottom facing up
					if (FFScreen.DEBUG) Log.i(TAG, "bottom, facing up");
					for (int y = 2; y < groupSize; y++) {
						for (int x = 1; x < groupSize - 1; x += 2) {
							group.cells[x][y] = IdManager.SCAFFOLD;
						}
					}
					// add hanger, back wall
					group.cells[2][4] = IdManager.SCAFFOLD;
					group.cells[2][3] = IdManager.HANGER;
					// add turrets
					group.cells[1][1] = IdManager.TURRET;
					group.cells[3][1] = IdManager.TURRET;		
				}		
			}
			
			// extendRunway() ?
			
		}
		else {
			if (FFScreen.DEBUG) Log.i(TAG, "Group type not HANGER, abort.");
		}
		return group;
	}
	
/*********************************************************************/	
//  FUNCTIONS FOR RANDOM GENERATION
	
	// Implementing Fisher–Yates shuffle
	private static char[] shuffleArray(char[] ar) {
	    Random rnd = new Random();
	    for (int i = ar.length - 1; i > 0; i--) {
	    	int index = rnd.nextInt(i + 1);
	    	// Simple swap
	    	char a = ar[index];
	    	ar[index] = ar[i];
	    	ar[i] = a;
	    }
	    return ar;
	}
	
	private static char getRandObstacle() {
		return MAIN_OBSTACLES[getRandInt(0, MAIN_OBSTACLES.length - 1)];
	}
	
	private static char getRandEnemy() {
		return MAIN_ENEMIES[getRandInt(0, MAIN_ENEMIES.length - 1)];
	}
	
	private static char getRandExtras() {
		return EXTRAS_CELLS[getRandInt(0, EXTRAS_CELLS.length - 1)];
	}
	
	private static char[] randAxisGroupBattery(char[] battery) {
		// returns an array of 5 cells, both ends have enemy unit
		Random rand = new Random();
		int x = 0;
		// crap design here...
		while (x <= 1) {
			if (rand.nextBoolean()) {
				battery[x] = getRandEnemy();
				x++;
				battery[x] = getRandObstacle();
			}
			else {
				battery[x] = getRandObstacle();
				x++;
				battery[x] = getRandEnemy();
			}
			x++;
		}
		// 2
		battery[2] = getRandObstacle();
		x++; // 3
		while (x <= 4) {
			if (rand.nextBoolean()) {
				battery[x] = getRandEnemy();
				x++;
				battery[x] = getRandObstacle();
			}
			else {
				battery[x] = getRandObstacle();
				x++;
				battery[x] = getRandEnemy();
			}
			x++;
		}
		return battery;
	}
	
/*********************************************************************/
	
	private static void debugCheckCount(String header, int b, int d, int m, int t) {
		if (FFScreen.DEBUG) {
			Log.i(TAG, "checkFort: " + header + ", " + b + " bombups, " + d + " drones, " + m + " mines, "+ t + " turrets.");
		}
	}
	
	
	private static void debugPrintFort(char[][] fortress) {
		if (FFScreen.DEBUG) {
			String line = "";
			for (int y = 0; y < FFScreen.gameCellsHigh; y++) {
				for (int x = 0; x < FFScreen.gameCellsWide; x++) {
					line += fortress[x][y];
				}
				Log.i(TAG, "scan: " + line);
				line = "";
			}
		}
	}
}