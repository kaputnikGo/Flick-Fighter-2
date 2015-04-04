package com.ff.ff2.nav;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ff.ff2.eng.FFModel;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.obs.ScanCell;

public class Navigator {
	private static final String TAG = Navigator.class.getSimpleName();
	
	private static final int cellsWide = FFScreen.gameCellsWide;
	private static final int cellsHigh = FFScreen.gameCellsHigh;
	private static final int cellSize = FFScreen.gameCellSize;
	
	public static final int SCAN_RANGE = 7; // all adjacent cells to a unit (clockwise from top left)
	public static final int FORWARD_SCAN_RANGE = 3; // one cell forward on given axis plus one either side.
	public static final int PATH_RANGE = 10; // 10 x 10 of cells
	
	public static final float MAX_RANGE = FFScreen.gameHeight;
	
	public static Array<Vector2> turretPositions;
	public static Array<Vector2> dronePositions;
	private static ScanCell[][] scanMap;
	private static int lastScanSize;
	
	// used to indicate not a valid target, must be off-screen and/or playable area
	public static final Vector2 nullTarget = new Vector2(-1, -1);
	public static ScanCell nullScanCell = new ScanCell();
	
	// set for debugs
	public static boolean DEBUG_NAV;

	
/*********************************************************************/

// MAJOR SCANNING PATHFINDING FUNCTIONS
	
	public static void prepareNavigator() {
		// called by FFModel at initField()	
		// first time run for any fortress
		FFModel.getFFModel().field.fortress.rescanFortressObstacles();
		
		lastScanSize = FFModel.getFFModel().field.fortress.emptyCells.size;
		scanMap = new ScanCell[cellsWide][cellsHigh];
		scanMap = NavigatorUtils.loadScanMap();
		
		turretPositions = new Array<Vector2>();
		dronePositions = new Array<Vector2>(); 
		nullScanCell.reset();
		DEBUG_NAV = FFScreen.engine.settings.DEBUG_NAVPATH;
	}


	public static Array<Vector2> patrolScan(Vector2 source, int patrolRange, Vector2 headingPosition) {
		// can return null here to indicate: 
		// no map created or, 
		// no patrolWaypoints found
		
		// source is unit position
		// patrolRange is the max cell number away from source for patrol patrol range
		// headingPosition is the favoured direction to aim for, ie towards gateway (patrol not necessarily reach it...)
		
		// get IdManager.direction from source and headingPosition
		int heading = IdManager.UNKNOWN;
		
		heading = NavigatorUtils.directionAxisFromSource(source, headingPosition);
		
		return PathFinder.getRangedWaypoints(getRangedScan(source, patrolRange), patrolRange + 1, heading);
	}
	
	public static int getLocalScanEmpty(Vector2 source) {
		Array<ScanCell> scan = new Array<ScanCell>(SCAN_RANGE);		
		scan = getLocalScan(NavigatorUtils.getVectorToCell(source), true);
		
		return NavigatorUtils.findEmptyAxisInScan(scan);
	}

	public static Array<Vector2> pathFinderScan(Vector2 source, Vector2 target) {
		if (source.dst(target) <= FFScreen.engine.settings.TARGET_RANGE_MIN) {
			if (FFScreen.DEBUG) Log.i(TAG, "close enough for 2 point flightPath");
			// close enough for a two vector flightpath ( < 2 * cellSize)
			Array<Vector2> flightPath = new Array<Vector2>();
			flightPath.add(source);
			flightPath.add(target);
			flightPath.shrink();
			return flightPath;
		}
		
		else {
			// scan for a path thru empty cells		
			ScanCell sourceCell = new ScanCell(source, IdManager.SCAN_SOURCE);	
			ScanCell targetCell = new ScanCell(target, IdManager.SCAN_TARGET);
	
			loadPathFinderScanMap();
			plotPathFinderScanMap(sourceCell, targetCell);
			
			Array<ScanCell> pathFinderPath = new Array<ScanCell>();
			pathFinderPath = PathFinder.findPath(scanMap, sourceCell, targetCell);
					
			if (pathFinderPath != null) {
				if (FFScreen.DEBUG) NavigatorUtils.debugFlightPathMap(scanMap, pathFinderPath);
				return convertPathToFlight(pathFinderPath);
			}
			else {
				if (FFScreen.DEBUG) Log.i(TAG, "has no target found or can't find path to target");
				return null;
			}
		}
	}
	
		
	public static Array<Vector2> smoothPath(Array<Vector2> original) {
		// this is destructive edit of flightpath...
		if (Navigator.DEBUG_NAV) Log.i(TAG, "smoothing path coarse...");
		if (original != null) {
			return NavigatorUtils.smoothPathCoarse(original);
		}
		else 
			return null;
	}
	
	public static Array<Vector2> refactorFlightPath(Array<Vector2> original, Vector2 source) {
		if (original.size > 2) {
			return NavigatorUtils.refactorFlightPath(original, source);
		}
		else 
			return original;
	}
	
	public static int hangerRunwayDirection(Vector2 position) {
		// based upon position, search for empty cell adjacent to it
		// build() hanger will be surrounded by scaffolds all sides except one...
		// need to ready this for user placing hanger anywhere.
		// runway needs to head towards centre
		Array<ScanCell> scan = new Array<ScanCell>(SCAN_RANGE);
		scan = getLocalScan(position, true);
		// has a scan
		
		if (scan.size > 0) {
			//returns first found in order...
			if (scan.get(IdManager.NORTH).id == IdManager.EMPTY) {
				return IdManager.NORTH;
			}
			else if (scan.get(IdManager.EAST).id == IdManager.EMPTY) {
				return IdManager.EAST;
			}
			else if (scan.get(IdManager.SOUTH).id == IdManager.EMPTY) {
				return IdManager.SOUTH;
			}
			else if (scan.get(IdManager.WEST).id == IdManager.EMPTY) {
				return IdManager.WEST;
			}
			else {
				// need to account for the angles here, not unknown
				if (Navigator.DEBUG_NAV) Log.i(TAG, "found empty on angle, temp returning NE...");
				return IdManager.NE;
			}
		}
		else { 
			if (Navigator.DEBUG_NAV) Log.i(TAG, "blocked by shootables, return unknown.");	
			return IdManager.UNKNOWN;
		}
	}
	
	public static boolean hangerRunwayExitScan(Vector2 start, Vector2 end) {
		// if runway is clear(EMPTY) from start to end then return true
		// if has obs, return false	
		// go along runway boolean shootableTarget(vector2)
		// runway length = 3 cells (96px)
		
		// h|s|-|-|e|  h = hanger, s = start, e = end
		
		Vector2 tempVector = new Vector2();
		tempVector.set(start);
		// 1.
		if (findEmptyCellInLocation(tempVector)) {
			tempVector.set(NavigatorUtils.getNextDirectionVector(tempVector, end));
			// 2.
			if (findEmptyCellInLocation(tempVector)) {
				tempVector.set(NavigatorUtils.getNextDirectionVector(tempVector, end));
				// 3.
				if (findEmptyCellInLocation(tempVector)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean hangerRunwayExitShootable(Vector2 start, Vector2 end) {
		// can find turrets as shootable too...
		// as above, if shootable == true, then hanger tells sqnldr to shoot along it
		// shooting self!
		// get next cell first
		Vector2 tempVector = new Vector2();
		tempVector.set(start);
		// 1.
		if (findTargetCellInLocation(tempVector)) {
			return true;
		} 
		tempVector.set(NavigatorUtils.getNextDirectionVector(tempVector, end));
		// 2.
		if (findTargetCellInLocation(tempVector)) {
			return true;
		}
		tempVector.set(NavigatorUtils.getNextDirectionVector(tempVector, end));
		// 3.
		if (findTargetCellInLocation(tempVector)) {
			return true;
		}	
		return false;
	}
	
/*********************************************************************/	
	
// VECTOR BASED FUNCTIONS
	
	public static Vector2 findClosestTarget(Vector2 source) {
		Array<ScanCell> scan = new Array<ScanCell>();
		scan = updateLocalScanFull(source);
		
		ScanCell targetCell = new ScanCell();
		targetCell = NavigatorUtils.findTargetCellInScan(scan);
	
		if (targetCell.position.equals(nullTarget))
			return nullTarget;
		
		else
			return targetCell.position;
	}

	public static Vector2 findClosestDronePosition(Vector2 source) {		
		return NavigatorUtils.findClosestVector(dronePositions, source);
	}
	
	public static Vector2 findClosestTurretPosition(Vector2 source) {
		return NavigatorUtils.findClosestVector(turretPositions, source);
	}

	public static boolean targetVectorInline(Vector2 start, Vector2 end, Vector2 target) {
		return NavigatorUtils.targetVectorInline(start, end, target);
	}
	
	public static boolean shootableTarget(Vector2 location) {
		return findTargetCellInLocation(location);
	}
	
	public static boolean droneVectorInline(Vector2 source, Vector2 target) {
		// check if a drone is in between the source and the target
		return NavigatorUtils.findVectorinline(dronePositions, source, target);
	}
	
	public static Vector2 getVectorToCellVector(Vector2 position) {
		return NavigatorUtils.getVectorToCell(position);
	}
	
	
/*********************************************************************/	

// SCANNING MAP FUNCTIONS
	
	private static Array<ScanCell> updateLocalScanFull(Vector2 source) {
		FFModel.getFFModel().field.fortress.rescanFortressObstacles();		
		return  getLocalScan(NavigatorUtils.getVectorToCell(source), false);	
	}
	
	private static void loadPathFinderScanMap() {
		if (FFModel.getFFModel().field.fortress.emptyCells.size != lastScanSize) {
			if (Navigator.DEBUG_NAV) Log.i(TAG, "reloaded scanMap");
			scanMap = null;
			scanMap = new ScanCell[cellsWide][cellsHigh];
			scanMap = NavigatorUtils.loadScanMap();
			
			lastScanSize = FFModel.getFFModel().field.fortress.emptyCells.size;
			if (Navigator.DEBUG_NAV) Log.i(TAG, "lastScanSize now: " + lastScanSize);
		}
	}
	
	private static void plotPathFinderScanMap(ScanCell source, ScanCell target) {
		scanMap[source.x][source.y] = source;
		scanMap[target.x][target.y] = target;
	}
	
	private static Array<Vector2> convertPathToFlight(Array<ScanCell> scanPath) {
		Array<Vector2> flightPath = new Array<Vector2>();
		
		for (int a = 0; a < scanPath.size; a++) {
			flightPath.add(NavigatorUtils.pathCellOffsetVector(scanPath.get(a).position));
		}
		
		flightPath.shrink();
		return flightPath;
	}
	
	private static Array<ScanCell> getLocalScan(Vector2 pivot, boolean basic) {	
		// full axis scan regardless, beyond screen edge should be picked up as UNKNOWN
		Array<ScanCell> scan = new Array<ScanCell>(SCAN_RANGE);
		
		for (int i = 0; i <= SCAN_RANGE; i++) {
			scan.add(addCellToLocalScan(pivot, i, basic));
		}		
		scan.shrink();	
		return scan;
		//NavigatorUtils.debugAxisScan(scan);
	}
	
	private static ScanCell[][] getRangedScan(Vector2 pivot, int range) {
		// pilot is currently sending range = 3
		// 9 x 9 cells (approx 1/6th of fortress area)
		/* 
		  		+ b + + + + + b +
				+ b + + + + + b +
				+ b + b b + + b +
				+ b x + x x x b +
				+ b x 1 p x + b +
				+ b + + + x x b +
				+ + + + b b b b +
				+ + + 2 + + + + +
				+ + + + + + + + +
		 */
		
		// from pivot 'p' scan with radius 'range' (this case 3)
		// normal localscan would result in patrol of one cell to left of 'p'
		// rangedScan should find that one cell left as waypoint1 '1'
		// then look for at least 3 cells free in row with end to form waypoint2 '2'
		
		int diameter = range * range;
		int radius = range + 1;
		
		ScanCell[][] scanMap = new ScanCell[diameter][diameter];
		
		Vector2 startCell = new Vector2(NavigatorUtils.getVectorToCell(pivot));
		Vector2 startVector = new Vector2();
		
		// set the start point to top-left corner of scan
		startCell.x -= radius;
		startCell.y -= radius;
		float startingX = pivot.x - (radius * cellSize);
		
		startVector.x = startingX;
		startVector.y = pivot.y - (radius * cellSize);

		// look for emptyCells
		for (int y = 0; y < diameter; y++) {
			for (int x = 0; x < diameter; x++) {
				scanMap[x][y] = getScannedCellTypeEmpty(startVector);
				startVector.x += cellSize;
			}
			// reset the x here
			startVector.x = startingX;
			startVector.y += cellSize;
		}
		// put pivot in as sourceCell
		scanMap[radius][radius] = new ScanCell(pivot, IdManager.SCAN_SOURCE);
		
		NavigatorUtils.debugRangeScan(scanMap, diameter);	
		return scanMap;
	}	
	
/*********************************************************************/
	
// SCANNING CELL FUNCTIONS
	/*
	private static int directionEmptyFromPivot(ScanCell pivot, int originDirection) {	
		return IdManager.UNKNOWN;
	}
	*/
	
	private static ScanCell addCellToLocalScan(Vector2 pivot, int direction, boolean basic) {
		// helper method for scan
		if (basic) 
			return getScannedCellTypeEmpty(NavigatorUtils.getAdjacentCellLocation(pivot, direction));
		else 
			return getScannedCellTypeTarget(NavigatorUtils.getAdjacentCellLocation(pivot, direction));
	}

	private static boolean findEmptyCellInLocation(Vector2 location) {
		if (FFModel.getFFModel().field.fortress.emptyCells.size > 0) {
			for (ScanCell empty : FFModel.getFFModel().field.fortress.emptyCells) {
				if (empty.position.equals(location)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean findTargetCellInLocation(Vector2 location) {
		if (FFModel.getFFModel().field.fortress.obsCells.size > 0) {
			for (ScanCell scanCell : FFModel.getFFModel().field.fortress.obsCells) {
				if (scanCell.position.equals(location)) {
					if (scanCell.shootable)
						return true;
				}
			}
		}
		if (turretPositions.size > 0) {
			for (Vector2 turretPosition : turretPositions) {
				if (turretPosition.equals(location)) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	private static ScanCell getScannedCellTypeEmpty(Vector2 location) {
		// look for EMPTY only
		if (FFModel.getFFModel().field.fortress.emptyCells.size > 0) {
			for (ScanCell empty : FFModel.getFFModel().field.fortress.emptyCells) {
				if (empty.position.equals(location)) {
					return empty;
				}
			}
		}
		return new ScanCell(location, IdManager.UNKNOWN_CELL);
	}
	
	
	private static ScanCell getScannedCellTypeTarget(Vector2 location) {
		// look for everything but EMPTY
		// change to ID type?...
		if (FFModel.getFFModel().field.fortress.obsCells.size > 0) {
			for (ScanCell scanCell : FFModel.getFFModel().field.fortress.obsCells) {
				if (scanCell.position.equals(location)) {
					return scanCell;
				}
			}
		}
		if (turretPositions.size > 0) {
			for (Vector2 turretPosition : turretPositions) {
				if (turretPosition.equals(location)) {
					return new ScanCell(turretPosition, IdManager.TURRET);
				}
			}
		}
		if (dronePositions.size > 0) {
			for (Vector2 dronePosition : dronePositions) {
				// round off drone location to within a cell
				// in case called after game started
				Vector2 tempVector = new Vector2();
				tempVector.x = Math.round(dronePosition.x / cellSize) * cellSize;
				tempVector.y = Math.round(dronePosition.y / cellSize) * cellSize;
				if (tempVector.equals(location)) {
					return new ScanCell(tempVector, IdManager.DRONE);
				}
			}
		}
		return new ScanCell(location, IdManager.UNKNOWN_CELL);
	}	
}