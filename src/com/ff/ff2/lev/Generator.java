package com.ff.ff2.lev;

import java.util.Random;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.obs.Barrier;
import com.ff.ff2.obs.Fence;
import com.ff.ff2.obs.Gateway;
import com.ff.ff2.obs.Obstacle;
import com.ff.ff2.obs.Powerup;
import com.ff.ff2.obs.Switch;

public class Generator {
	private static final String TAG = Generator.class.getSimpleName();
	public static final int GROUP_SIZE = 5;
	
	public static final int GROUPS_WIDE = 3;
	public static final int GROUPS_HIGH = 5;
	
	// furthest group on screen edges
	public static final int GROUP_TOP = 0;
	public static final int GROUP_BOTTOM = 5;
	public static final int GROUP_LEFT = 0;
	public static final int GROUP_RIGHT = 3;
	
	private static final int GROUP_POSITION_CELL_OFFSET = 3;
	
	public static final int NORTH = 0; 
	public static final int SOUTH = 1;
	public static final int EAST = 2;  
	public static final int WEST = 3; 
	public static final int UNKNOWN = 4;
	
	public static final int BUILD_TYPE_PROC = 0;
	public static final int BUILD_TYPE_RAND = 1;
	public static final int BUILD_TYPE_GROUPS = 2;
	public static final int BUILD_TYPE_ARRAY = 3;
	public static final int BUILD_TYPE_ARENA = 4;
	
	// serves as group ID
	public static final int GROUP_EMPTY = 0;
	// outer wall based, screen edge. not for rand gen
	public static final int BATTERY_WALL_N = 1;
	public static final int BATTERY_WALL_S = 2;
	public static final int BATTERY_WALL_E = 3;
	public static final int BATTERY_WALL_W = 4;
	
	// inner wall based
	public static final int BATTERY_AXIS_Y = 5;
	public static final int BATTERY_AXIS_X = 6;
	public static final int BATTERY_TUNNEL = 7;
	// passable
	public static final int BATTERY_CORNER = 8;
	public static final int BATTERY_ISLAND = 9;
	public static final int BATTERY_VAULT = 10;
	public static final int BATTERY_PLUS = 11;
	public static final int BATTERY_CROSS = 12;
	
	public static final int HANGER = 13;

	public static final int OBSTACLES_SIZER = 20;
	public static final int OBSTACLES_MIN = 21;
	public static final int OBSTACLES_SPARSE = 22;
	public static final int OBSTACLES_LOW = 23;
	public static final int OBSTACLES_MED = 24;
	public static final int OBSTACLES_DENSE = 25;
	public static final int OBSTACLES_FULL = 26;
	
	public static final int REQUIRED_CELL_GROUP = 30;
	
	// for use in random generation
	// gets a number based upon group types listed above
	private static final int BATTERY_ALL_MIN = BATTERY_AXIS_Y;
	private static final int BATTERY_PASS_MIN = BATTERY_CORNER;
	private static final int BATTERY_ALL_MAX = BATTERY_CROSS;
	private static final int OBSTACLES_ALL_MIN = OBSTACLES_MIN;
	private static final int OBSTACLES_ALL_MAX = OBSTACLES_FULL;
	
	
	public static Array<Obstacle> obstacles = new Array<Obstacle>();
	public static Vector2 switchVector = new Vector2();
	public static Vector2 gatewayVector = new Vector2();
	public static Vector2 powerupVector = new Vector2();
	
	private static int cellSize;
	private static int cellsWide;
	private static int cellsHigh;
	public static char[][] fortressCellArray;	
	public static Group[][] groupsArray;
	
	public Generator() {
		// not here
	}
	
	
	public static boolean prepForRandomFortress() {
		cellsWide = FFScreen.gameCellsWide;
		cellsHigh = FFScreen.gameCellsHigh;
		cellSize = FFScreen.gameCellSize;
		obstacles = new Array<Obstacle>();
		switchVector = new Vector2();
		gatewayVector = new Vector2();
		powerupVector = new Vector2();
		fortressCellArray = new char[cellsWide][cellsHigh];
		groupsArray = new Group[GROUPS_WIDE][GROUPS_HIGH];
		return true;
	}
	
	
/*********************************************************************/	
	
	public static void createSimpleRandomFortress() {
		// double random chance for boolean
		// needs updating since new obs
		Random rand = new Random();
		int count = 0;

		Vector2 initVector = new Vector2();
		boolean switcher = false;
		
		// add the gateway,switch here
		obstacles.add(new Gateway(gatewayVector.set(cellSize * 5, cellSize)));
		obstacles.add(new Switch(switchVector.set(cellSize * 9, cellSize * 18)));
		obstacles.add(new Powerup(powerupVector.set(cellSize, cellSize * 20)));
		
		// limit creation to top half of screen
		for (int y = 5; y < 14; y++) {
			for (int x = 0; x < cellsWide; x++) {
				if (rand.nextBoolean() && count < cellsHigh) {
					if (rand.nextBoolean()) {
						initVector.x = x * cellSize; // convert cell to pixel
						initVector.y = y * cellSize; // convert cell to pixel
						if (switcher) {
							obstacles.add(new Fence(initVector));
						}
						else {
							obstacles.add(new Barrier(initVector));
						}
						// flip boolean
						switcher ^= true;
						count++;
					}
				}
			}
		}
	}
	
	public static char[][] getSelectedBuildFortress(int type) {
		switch (type) {	
			case BUILD_TYPE_PROC:
				return getProceduralFortress();
			case BUILD_TYPE_RAND:
				return getRandomFortress();
			case BUILD_TYPE_GROUPS:
				return getTestGroupsFortress();
			case BUILD_TYPE_ARRAY:
				return getTestArrayFortress();	
			case BUILD_TYPE_ARENA:
				return getArenaFortress();
			default:
				return getTestArrayFortress();		
		}
	}
	
/*********************************************************************/
	
	private static char[][] getTestArrayFortress() {
		prepareGeneratorArrays();
		
		for (int y = 0; y < GROUPS_HIGH; y++) {
			for (int x = 0; x < GROUPS_WIDE; x++) {
				groupsArray[x][y] = GeneratorUtils.generateRandomBatteryGroup(
						new Group(x, y, GeneratorUtils.getRandInt(1, 6)));
			}
		}
		populateFortressWithGroupsArray();		
		// randomly overwrite any cell with any Extras
		populateRandomExtras();	
		
		// overwrite existing true
		hangerToGroup(true);
		
		// then randomly overwrite any cell with a required cells
		populateRandomRequireds();
			
		return fortressCellArray;
	}
	
	private static char[][] getTestGroupsFortress() {
		prepareGeneratorArrays();
				populateFortressWithGroup(
						GeneratorUtils.generateBatteryWallGroup(
								new Group(
										0, 0, BATTERY_WALL_N)));
				populateFortressWithGroup(
						GeneratorUtils.generateBatteryWallGroup(
								new Group(
										1, 0, BATTERY_WALL_N)));
				populateFortressWithGroup(
						GeneratorUtils.generateBatteryWallGroup(
								new Group(
										2, 0, BATTERY_WALL_N)));
				//
				populateFortressWithGroup(
						GeneratorUtils.generateRandomBatteryGroup(
								new Group(
										0, 1, BATTERY_ISLAND)));		
				populateFortressWithGroup(
						GeneratorUtils.generateRandomObstacleGroup(
								new Group(
										1, 1, OBSTACLES_SPARSE)));
				populateFortressWithGroup(
						GeneratorUtils.generateBatteryWallGroup(
								new Group(
										2, 1, BATTERY_WALL_E)));
				//
				populateFortressWithGroup(
						GeneratorUtils.generateRandomObstacleGroup(
								new Group(
										0, 2, OBSTACLES_DENSE)));
				populateFortressWithGroup(
						GeneratorUtils.generateRandomBatteryGroup(
								new Group(
										1, 2, BATTERY_VAULT)));
				populateFortressWithGroup(
						GeneratorUtils.generateRandomObstacleGroup(
								new Group(
										2, 2, OBSTACLES_LOW)));
				//
				populateFortressWithGroup(
						GeneratorUtils.generateRandomBatteryGroup(
								new Group(
										0, 3, BATTERY_PLUS)));
				populateFortressWithGroup(
						GeneratorUtils.generateRandomObstacleGroup(
								new Group(
										1, 3, OBSTACLES_MED)));
				populateFortressWithGroup(
						GeneratorUtils.generateRandomBatteryGroup(
								new Group(
										2, 3, BATTERY_CROSS)));
				//
				populateFortressWithGroup(
						GeneratorUtils.generateBatteryWallGroup(
								new Group(
										0, 4, BATTERY_WALL_S)));
				populateFortressWithGroup(
						GeneratorUtils.generateBatteryWallGroup(
								new Group(
										1, 4, BATTERY_WALL_S)));
				populateFortressWithGroup(
						GeneratorUtils.generateBatteryWallGroup(
								new Group(
										2, 4, BATTERY_WALL_S)));

		
		// randomly overwrite any cell with any Extras
		populateRandomExtras();		
		
		// overwrite existing true
		hangerToGroup(true);
		// convert groups to array,
		populateFortressWithGroupsArray();
		
		// then randomly overwrite any cell with a required cells
		populateRandomRequireds();
			
		return fortressCellArray;	
	}
	
	private static char[][] getProceduralFortress() {
		prepareGeneratorArrays();
		
		// work on groupsArray first, then convert it to fortressArray at end 
		
		// place private fighter(p) and gateway(g) in rand positions but not in same GROUP
		// also need powerup(u), for switch(s) see below
		// leaving middle row and column free
		
		/*
		 * |p| | |
		 * | | |g|
		 * | | | |
		 * |u| | |
		 * |s| | |
		 * 
		 */
		int count = GeneratorUtils.REQUIRED_CELLS.length;
		char[] requireds = new char[count];
		requireds = GeneratorUtils.getRandOrderRequiredCells();
		Group group;
		count--;
		int x = 0;
		for (int y = 0; y < GROUPS_HIGH; y++) {
			if (y != 2) {
				group = new Group();
				x = GeneratorUtils.getRandInt(0, 1) * 2;
				group.setGroupXY(x, y);
				group.type = REQUIRED_CELL_GROUP;
				group.primeCellId = requireds[count];
				group = GeneratorUtils.placeRequiredCellInGroup(group);
				groupsArray[x][y] = group;
				count--;
			}
			else {
				// skip, leaving a blank row of groups mid screen
			}
		}
		
		// find player group, find gateway group
		// determine wall in between the two		
		int wall = GeneratorUtils.getGroupsSeparatingAxis(
				getMatchingIdGroup(IdManager.PRIVATE_FIGHTER),
				getMatchingIdGroup(IdManager.GATEWAY));
		
		// place wall between
		GeneratorUtils.scaffoldWallInGroup(getMatchingIdGroup(IdManager.PRIVATE_FIGHTER), wall);
		
		// then extend scaffold (x) wall to end of screen at one side	
		/*
		 * |p| | |
		 * xxxxx
		 * | |g| |
		 * | | | |
		 * |u| | |
		 * 
		 */
		
		// wall towards the centre group (1, 2)
		// based upon current scaffoldWall
		int Xin = getMatchingIdGroup(IdManager.PRIVATE_FIGHTER).groupX;
		int Yin = getMatchingIdGroup(IdManager.PRIVATE_FIGHTER).groupY;
		
		if (wall == NORTH || wall == SOUTH) {
			if (Xin == 0) {
				// is left side
				Xin += GROUP_SIZE;
				
				GeneratorUtils.scaffoldWallInGroup(
						getMatchingLocationGroup(Xin, Yin), wall);
				// perpendicular to centre
				if (Yin <= GROUP_SIZE * 2) {
					if (wall == NORTH) {
						GeneratorUtils.scaffoldWallInGroup(
								getMatchingLocationGroup(Xin, Yin), EAST);
					}
					else {
						Yin += GROUP_SIZE;
						GeneratorUtils.scaffoldWallInGroup(
								getMatchingLocationGroup(Xin, Yin), EAST);
					}				
				}
				else {
					if (wall == NORTH) {
						Yin -= GROUP_SIZE;
						GeneratorUtils.scaffoldWallInGroup(
								getMatchingLocationGroup(Xin, Yin), EAST);
					}
					else {
						GeneratorUtils.scaffoldWallInGroup(
								getMatchingLocationGroup(Xin, Yin), EAST);						
					}
				}
				group = new Group();
				group = getMatchingLocationGroup(Xin + GROUP_SIZE, Yin);
				group.type = BATTERY_AXIS_X;		
				GeneratorUtils.generateRandomBatteryGroup(group);			
			}
			else {
				// is right side
				Xin -= GROUP_SIZE;				
				GeneratorUtils.scaffoldWallInGroup(
						getMatchingLocationGroup(Xin, Yin), wall);
				// perpendicular to centre
				if (Yin <= GROUP_SIZE * 2) {
					if (wall == NORTH) {
						GeneratorUtils.scaffoldWallInGroup(
								getMatchingLocationGroup(Xin, Yin), WEST);
					}
					else {
						Yin += GROUP_SIZE;
						GeneratorUtils.scaffoldWallInGroup(
								getMatchingLocationGroup(Xin, Yin), WEST);
					}
				}
				else {
					// south wall going up?
					if (wall == NORTH) {
						Yin -= GROUP_SIZE;
						GeneratorUtils.scaffoldWallInGroup(
								getMatchingLocationGroup(Xin, Yin), WEST);
					}
					else {
						GeneratorUtils.scaffoldWallInGroup(
								getMatchingLocationGroup(Xin, Yin), WEST);
					}
				}
				group = new Group();
				group = getMatchingLocationGroup(Xin - GROUP_SIZE, Yin);
				group.type = BATTERY_AXIS_X;		
				GeneratorUtils.generateRandomBatteryGroup(group);
			}			
		}	
		// based upon wall being along y
		else if (wall == WEST || wall == EAST) {
			if (Yin <= GROUP_SIZE * 2) {
				// is top
				Yin += GROUP_SIZE;
				GeneratorUtils.scaffoldWallInGroup(
						getMatchingLocationGroup(Xin, Yin), wall);
				// perpendicular to centre
				if (Xin == 0) {
					Xin += GROUP_SIZE;
					GeneratorUtils.scaffoldWallInGroup(
							getMatchingLocationGroup(Xin, Yin), SOUTH);
				}
				else {
					Xin -= GROUP_SIZE;
					GeneratorUtils.scaffoldWallInGroup(
							getMatchingLocationGroup(Xin, Yin), SOUTH);
				}
				group = new Group();
				group = getMatchingLocationGroup(Xin, Yin += GROUP_SIZE);
				group.type = BATTERY_AXIS_Y;		
				GeneratorUtils.generateRandomBatteryGroup(group);
			}
			else {
				// is bottom
				Yin -= GROUP_SIZE;
				GeneratorUtils.scaffoldWallInGroup(
						getMatchingLocationGroup(Xin, Yin), wall);
				// perpendicular to centre
				if (Xin == 0) {
					Xin += GROUP_SIZE;
					GeneratorUtils.scaffoldWallInGroup(
							getMatchingLocationGroup(Xin, Yin), NORTH);
				}
				else {
					Xin -= GROUP_SIZE;
					GeneratorUtils.scaffoldWallInGroup(
							getMatchingLocationGroup(Xin - GROUP_SIZE, Yin), NORTH);
				}
				group = new Group();
				group = getMatchingLocationGroup(Xin, Yin -= GROUP_SIZE);
				group.type = BATTERY_AXIS_Y;		
				GeneratorUtils.generateRandomBatteryGroup(group);
			}			
		}
			
		// make defensive positions (battery) of turrets and drones
		group = new Group();
		group = getMatchingLocationGroup(GROUP_SIZE, GROUP_SIZE * 2);
		group.type = BATTERY_VAULT;		
		GeneratorUtils.generateRandomBatteryGroup(group);
		
		// hangerToGroup(overwrite existing(true) or empty only(false))
		hangerToGroup(false);
	
		// fill any remaining empty groups with batteries(true)
		populateEmptyGroups(true);
		// convert groups to array,
		populateFortressWithGroupsArray();
		
		// sprinkle with bombs, etc...
		populateRandomExtras();
		populateRandomEnemies();
	
		// filter array for too many drones...and return
		return GeneratorUtils.checkMaxEnemiesFortress(fortressCellArray);		
	}
	
	// random fort generation
	private static char[][] getRandomFortress() {
		prepareGeneratorArrays();			
		for (int y = 0; y < GROUPS_HIGH; y++) {
			for (int x = 0; x < GROUPS_WIDE; x++) {
				populateFortressWithGroup(populateRandomGroup(new Group(x, y)));
			}
		}
		// randomly overwrite any cell with a specials
		populateRandomExtras();	
		
		// overwrite existing true
		hangerToGroup(true);
		// convert groups to array,
		populateFortressWithGroupsArray();
		
		// then randomly overwrite any cell with a required cells
		populateRandomRequireds();
	
		return fortressCellArray;
	}
	
	private static char[][] getArenaFortress() {
		prepareGeneratorArrays();
		
		for (int x = 0; x < 3; x++) {
			populateFortressWithGroup(
					GeneratorUtils.generateBatteryWallGroup(
							new Group(
									x, 0, BATTERY_WALL_N)));
	
			populateFortressWithGroup(
					GeneratorUtils.generateBatteryWallGroup(
							new Group(
									x, 4, BATTERY_WALL_S)));
		}
		
		// island/vault in the center
		populateFortressWithGroup(
				GeneratorUtils.generateRandomBatteryGroup(
						new Group(
								1, 2, BATTERY_VAULT)));
		
		for (int y = 1; y < 4; y++) {
			populateFortressWithGroup(
					GeneratorUtils.generateBatteryWallGroup(
							new Group(
									0, y, BATTERY_WALL_W)));
			
			populateFortressWithGroup(
					GeneratorUtils.generateBatteryWallGroup(
							new Group(
									2, y, BATTERY_WALL_E)));
		}
		
		// fill in gaps at screen edges with scaffolds
		GeneratorUtils.scaffoldWallInGroup(getMatchingLocationGroup(0, 0), WEST);
		GeneratorUtils.scaffoldWallInGroup(getMatchingLocationGroup(0, 4 * GROUP_SIZE), WEST);
		GeneratorUtils.scaffoldWallInGroup(getMatchingLocationGroup(2 * GROUP_SIZE, 0), EAST);
		GeneratorUtils.scaffoldWallInGroup(getMatchingLocationGroup(2 * GROUP_SIZE, 4 * GROUP_SIZE), EAST);
		
		// overwrite existing true
		hangerToGroup(true);
		
		populateFortressWithGroupsArray();
		
		populateRandomRequireds();

		return fortressCellArray;
	}
	
	
/*********************************************************************/
	// FUNCTIONS TO HELP BUILD SPECIFIC TYPES OF RANDOM FORTRESSES
	
	private static void prepareGeneratorArrays() {
		// fill cellArray with empty cells
		for (int y = 0; y < cellsHigh; y++) {
			for (int x = 0; x < cellsWide; x++) {
				fortressCellArray[x][y] = IdManager.EMPTY;
			}
		}
		
		// fill groupsArray with empty groups
		for (int y = 0; y < GROUPS_HIGH; y++) {
			for (int x = 0; x < GROUPS_WIDE; x++) {
				groupsArray[x][y]= new Group(x, y);
			}
		}
	}
	
	private static Group getMatchingIdGroup(char targetId) {
		if (groupsArray != null) {
			for (int y = 0; y < GROUPS_HIGH; y++) {
				for (int x = 0; x < GROUPS_WIDE; x++) {
					if (groupsArray[x][y].primeCellId == targetId) {
						return groupsArray[x][y];
					}
				}
			}
		}
		// this?
		return null;
	}
	
	
	private static Group getMatchingLocationGroup(int groupXin, int groupYin) {
		if (groupsArray != null) {
			for (int y = 0; y < GROUPS_HIGH; y++) {
				for (int x = 0; x < GROUPS_WIDE; x++) {
					if (groupsArray[x][y].groupX == groupXin
							&& groupsArray[x][y].groupY == groupYin) {
						return groupsArray[x][y];
					}
					
				}
			}
		}
		// this?
		return null;
	}
	
	
/*********************************************************************/	
	// FUNCITONS TO HELP IN ANY BUILD FORTRESSES
	
	private static void populateFortressWithGroupsArray() {
		for (int y = 0; y < GROUPS_HIGH; y++) {
			for (int x = 0; x < GROUPS_WIDE; x++) {
				populateFortressWithGroup(groupsArray[x][y]);
			}
		}
	}
	
	
	private static void populateFortressWithGroup(Group group) {
		// generic method to place group within proper location of fortressCellArray
		// groupX,groupY are multiples of 5	
		
		// make sure is first added to the groupsArray for getGroup functions
		int gx = group.groupX / GROUP_SIZE;
		int gy = group.groupY / GROUP_SIZE;
		groupsArray[gx][gy] = group; 
		
		int cx = 0;
		int cy = 0;

		for (int y = group.groupY; y < group.groupY + GROUP_SIZE; y++) {
			for (int x = group.groupX; x < group.groupX + GROUP_SIZE; x++) {
				fortressCellArray[x][y] = group.cells[cx][cy];
				cx++;
			}
			cy++;
			cx = 0;
		}
	}
	
	private static Group populateRandomGroup(Group group) {
		// battery or obstacle?
		Random rand = new Random();
		if (rand.nextBoolean()) {
			// battery, not a wall as can be placed at edge, player can't get through
			group.type = GeneratorUtils.getRandInt(BATTERY_PASS_MIN, BATTERY_ALL_MAX);
			return GeneratorUtils.generateRandomBatteryGroup(group);
		}
		else {
			group.type = GeneratorUtils.getRandInt(OBSTACLES_ALL_MIN, OBSTACLES_ALL_MAX);
			return GeneratorUtils.generateRandomObstacleGroup(group);
		}
	}
	
	private static void populateRandomEnemies() {
		int count = GeneratorUtils.MAIN_ENEMIES.length - 1;
		char[] specials = new char[count];
		specials = GeneratorUtils.MAIN_ENEMIES;
		
		int minX = GROUP_POSITION_CELL_OFFSET;
		int maxX = cellsWide - minX;
		
		int minY = 0;
		int maxY = GROUP_SIZE;
		
		while (count >= 0) {
			fortressCellArray[GeneratorUtils.getRandInt(minX, maxX)]
					[GeneratorUtils.getRandInt(minY, maxY)] = specials[count];
			
			minY += GROUP_SIZE;
			maxY += GROUP_SIZE;
			count--;
		}
		specials = null;
	}
	
	private static void populateRandomExtras() {
		// randomly overwrite any cell with a pushblock or bombup
		int count = GeneratorUtils.EXTRAS_CELLS.length - 1;
		char[] specials = new char[count];
		specials = GeneratorUtils.getRandOrderExtrasCells();
		
		int minX = GROUP_POSITION_CELL_OFFSET;
		int maxX = cellsWide - minX;
		
		int minY = 0;
		int maxY = GROUP_SIZE;
		
		while (count >= 0) {
			fortressCellArray[GeneratorUtils.getRandInt(minX, maxX)]
					[GeneratorUtils.getRandInt(minY, maxY)] = specials[count];
			
			minY += GROUP_SIZE;
			maxY += GROUP_SIZE;
			count--;
		}
		specials = null;
	}
	
	private static void populateRandomRequireds() {
		// randomly overwrite any cell with the required cells
		int count = GeneratorUtils.REQUIRED_CELLS.length - 1;
		char[] specials = new char[count];
		specials = new char[count];
		specials = GeneratorUtils.getRandOrderRequiredCells();
		
		int minX = GROUP_POSITION_CELL_OFFSET;
		int maxX = cellsWide - minX;
		
		int minY = 0;
		int maxY = GROUP_SIZE;
		
		while (count >= 0) {
			fortressCellArray[GeneratorUtils.getRandInt(minX, maxX)]
					[GeneratorUtils.getRandInt(minY, maxY)] = specials[count];
			
			minY += GROUP_SIZE;
			maxY += GROUP_SIZE;
			count--;
		}
		specials = null;		
	}
	
	private static void populateEmptyGroups(boolean battery) {
		for (int y = 0; y < GROUPS_HIGH; y++) {
			for (int x = 0; x < GROUPS_WIDE; x++) {
				if (groupsArray[x][y].type == GROUP_EMPTY) {
					// fill with stuff
					if (battery) {
						groupsArray[x][y].type = GeneratorUtils.getRandInt(BATTERY_ALL_MIN, BATTERY_ALL_MAX);
						GeneratorUtils.generateRandomBatteryGroup(groupsArray[x][y]);
					}
					else {
						groupsArray[x][y].type = GeneratorUtils.getRandInt(OBSTACLES_ALL_MIN, OBSTACLES_ALL_MAX);
						GeneratorUtils.generateRandomObstacleGroup(groupsArray[x][y]);
					}
				}
			}
		}
	}
	
	private static void hangerToGroup(boolean overwrite) {
		// overrides a given group to replace it with a hanger (not a REQUIRED_CELL_GROUP)
		// or if false only looks for an empty group
		//TODO
		// change to look for all suitable groups, then choose random ones to fill with hanger
		int count = 0;
		for (int y = 0; y < GROUPS_HIGH; y++) {
			for (int x = 0; x < GROUPS_WIDE; x++) {
				if (x == 0 || x == 2 || y == 0 || y == 4) {
					if (overwrite == false) {
						if (groupsArray[x][y].type == GROUP_EMPTY) {
							if (FFScreen.DEBUG) Log.i(TAG, "edge groupXY " + x + ", " + y + " is empty.");
							// make sure it on edge of screen
							// is at edge, fill with hanger group
							count++;
							if (count <= FFScreen.engine.settings.MAX_HANGERS) {
								groupsArray[x][y].type = HANGER;	
								GeneratorUtils.generateWallHangerGroup(groupsArray[x][y]);
								if (FFScreen.DEBUG) Log.i(TAG, "empty Group filled with hanger.");
							}
						}
					}
					else if (overwrite) {
						// can overwrite a non REQUIRED GROUP
						if (groupsArray[x][y].type != REQUIRED_CELL_GROUP) {
							count++;
							if (count <= FFScreen.engine.settings.MAX_HANGERS) {
								groupsArray[x][y].reset();
								groupsArray[x][y].type = HANGER;	
								GeneratorUtils.generateWallHangerGroup(groupsArray[x][y]);
								if (FFScreen.DEBUG) Log.i(TAG, "Overwrite Group with hanger.");
							}
						}
					}
				}
			}
		}
	}
}