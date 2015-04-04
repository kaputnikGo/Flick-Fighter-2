package com.ff.ff2.nav;


import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ff.ff2.eng.FFModel;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.CollisionUtils;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.obs.ScanCell;

public class NavigatorUtils {
	private static final String TAG = NavigatorUtils.class.getSimpleName();
	
	public static final int cellsWide = FFScreen.gameCellsWide;
	public static final int cellsHigh = FFScreen.gameCellsHigh;
	public static final int cellSize = FFScreen.gameCellSize;
	public static final int pathCellOffset = cellSize / 2;
	
	// reuseable vars
	private static Vector2 tempVector = Navigator.nullTarget;
	private static float closestTarget = Navigator.MAX_RANGE;
	private static float distance = -1;
	
	
/*********************************************************************/	
	
	public NavigatorUtils() {
		//
	}
	
/*********************************************************************/
	
// VECTOR BASED FUNCTIONS
	
	public static int getDirection(float start, float end) {
		// based upon game co-ords system
		if (start < end) return 1; 
		else if (start > end) return -1;
		else return 0; // is aligned
	}
	
	public static Vector2 getVectorToCell(Vector2 source) {
		// round off source location to nearest cell
		source.x = Math.round(source.x / cellSize) * cellSize;
		source.y = Math.round(source.y / cellSize) * cellSize;
		return source;	
	}
	
	public static Vector2 getCellToVector(Vector2 cellLocation) {
		// get a cell co-ords as position vector
		cellLocation.x *= cellSize;
		cellLocation.y *= cellSize;
		return cellLocation;
	}
	
	public static Vector2 findClosestVector(Array<Vector2> positions, Vector2 target) {
		// reset
		distance = -1;
		closestTarget = Navigator.MAX_RANGE;
		
		for (Vector2 candidate : positions) {
			distance = target.dst(candidate);				
			if (distance <= closestTarget) {
				closestTarget = distance;
				tempVector.set(candidate);
			}
		}		
		return tempVector;
	}
	
	public static boolean findVectorinline(Array<Vector2> positions, Vector2 source, Vector2 target) {
		// search positions to see if vector is inline
		for (Vector2 candidate : positions) {
			if (targetVectorInline(source, target, candidate)) 
				return true;
		}	
		return false;
	}
	
	public static int findClosestVectorIndex(Array<Vector2> positions, Vector2 target) {
		// reset
		distance = -1;
		closestTarget = Navigator.MAX_RANGE;
		
		int number = 0; // will always return first in list if none found
		
		for (int i = 0; i < positions.size; i++) {
			distance = target.dst(positions.get(i));
			if (distance <= closestTarget) {
				closestTarget = distance;
				number = i;
			}
		}		
		return number;
	}
	
	public static Vector2 extendVectorWaypoint(Vector2 source, Vector2 target) {
		// buffer could be cellSize * 2
		// or dst(source, target) * 2...this gets a bit wild
		return new Vector2(
				target.x + ((cellSize * 2) * getDirection(source.x, target.x)), 
				target.y + ((cellSize * 2) * getDirection(source.y, target.y)));
	}
	
	public static Vector2 getNextDirectionVector(Vector2 source, Vector2 target) {
		return new Vector2(
				source.x + (cellSize * getDirection(source.x, target.x)), 
				source.y + (cellSize * getDirection(source.y, target.y)));
	}
	
	public static boolean targetVectorInline(Vector2 start, Vector2 end, Vector2 target) {
		// is this accurate?
		// may need to round off the getTheta floats	
		// check if angle is same?
		// double angle = atan2(y2 - y1, x2 - x1) * 180 / PI;
		return (CollisionUtils.getTheta(start, end) == CollisionUtils.getTheta(start, target));		
	}
	
	public static Vector2 pathCellOffsetVector(Vector2 original) {
		// shift x and y to be + 16 (cellSize / 2), centre of cell
		return original.set(original.x += pathCellOffset, original.y += pathCellOffset);
	}
	
	
/*********************************************************************/
	
// CELL/SCAN BASED FUNCTIONS
	
	public static ScanCell[][] loadScanMap() {
		if (Navigator.DEBUG_NAV) Log.i(TAG, "reload new scanMap from Fortress emptyCells array");
		ScanCell[][] scanMap = new ScanCell[cellsWide][cellsHigh];
		
		// set new map to null id
		for (int x = 0; x < cellsWide; x++) {
			for (int y = 0; y < cellsHigh; y++) {
				scanMap[x][y] = new ScanCell(
						getCellToVector(new Vector2(x, y)), 
						IdManager.UNKNOWN_CELL);
			}
		}
		
		for (ScanCell empty : FFModel.getFFModel().field.fortress.emptyCells) {
			scanMap[empty.x][empty.y].id = IdManager.EMPTY;
		}
		
		return scanMap;
	}
	
	public static Vector2 getAdjacentCellLocation(Vector2 pivot, int location) {		
		// scan cells topleft start, clockwise 0,1,2,3,4,5,6,7
		/*
		 * |0|1|2|
		 * |7|p|3|
		 * |6|5|4| 
		 * 
		 */
		// work out cell to scan based upon pivot and desired location
		tempVector.set(pivot);
		switch (location) {
			case IdManager.NW:
				tempVector.x -= cellSize;
				tempVector.y -= cellSize;
				break;
			case IdManager.NORTH:
				tempVector.y -= cellSize;
				break;
			case IdManager.NE:
				tempVector.x += cellSize;
				tempVector.y -= cellSize;
				break;
			case IdManager.EAST:
				tempVector.x += cellSize;
				break;
			case IdManager.SE:
				tempVector.x += cellSize;
				tempVector.y += cellSize;
				break;
			case IdManager.SOUTH:
				tempVector.y += cellSize;
				break;
			case IdManager.SW:
				tempVector.x -= cellSize;
				tempVector.y += cellSize;
				break;
			case IdManager.WEST:
				tempVector.x -= cellSize;
				break;
		}
		return tempVector;
	}
	
	public static int findEmptyAxisInScan(Array<ScanCell> scan) {
		// favour direction towards centre of game or a target?
		if (scan.size == Navigator.SCAN_RANGE + 1) {
			//full scan
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
		}
		/*
		else if (scan.size == 2) {
			//corner scan
			Log.d(TAG, "corner scan");
			if (scan.get(0).id == IdManager.EMPTY) 
				return Navigator.VERTICAL;				
			else if (scan.get(1).id == IdManager.EMPTY) 
				return Navigator.HORIZONTAL;
		}
		else if (scan.size == 3) {
			//edge row scan
			Log.d(TAG, "edge scan");
			if (scan.get(1).id == IdManager.EMPTY) 
				return Navigator.VERTICAL;				
		}
		*/
		return IdManager.UNKNOWN;				
	}
	
	public static ScanCell findTargetCellInScan(Array<ScanCell> scan) {
		if (scan.size == 0) {
			return Navigator.nullScanCell;
		}
		if (scan.size == Navigator.SCAN_RANGE + 1) {
			//full scan
			// check for empty in 1,3,5,7:
			for (int i = 1; i <= 7; i += 2) {
				if (scan.get(i).id == IdManager.BARRIER || 
						scan.get(i).id == IdManager.FENCE) {
					return scan.get(i);
				}
			}
		}
		else if (scan.size == 2) {
			// corner scan
			if (scan.get(0).id == IdManager.BARRIER || 
					scan.get(0).id == IdManager.FENCE) { 
				return scan.get(0);
			}
			
			else if (scan.get(1).id == IdManager.BARRIER || 
					scan.get(1).id == IdManager.FENCE) { 
				return scan.get(1);
			}
		}
		else if (scan.size == 3) {
			// extents row scan
			if (scan.get(1).id == IdManager.BARRIER || 
					scan.get(1).id == IdManager.FENCE) { 
				return scan.get(1);	
			}
		}
		return Navigator.nullScanCell;
	}
	
	public static Vector2 findPositionCellTypeInScan(int type, Array<ScanCell> scan) {	
		if (scan.size == Navigator.SCAN_RANGE + 1) {
			// has full scan
			if (scan.get(IdManager.NORTH).id == type) 
				return scan.get(IdManager.NORTH).position;
			
			else if (scan.get(IdManager.EAST).id == type) 
				return scan.get(IdManager.EAST).position;
			
			else if (scan.get(IdManager.SOUTH).id == type) 
				return scan.get(IdManager.SOUTH).position;
			
			else if (scan.get(IdManager.WEST).id == type) 
				return scan.get(IdManager.WEST).position;
			
		}
		else if (scan.size == 2) {
			// corner scan
			if (scan.get(0).id == type) 
				return scan.get(0).position;
			
			else if (scan.get(1).id == type) 
				return scan.get(1).position;
		}
		else if (scan.size == 3) {
			// extents row scan
			if (scan.get(1).id == type) 
				return scan.get(1).position;				
		}	
		return Navigator.nullTarget;
	}
	
	
/*********************************************************************/
	
// PATH BASED FUNCTIONS
	
	public static int directionAxisFromSource(Vector2 source, Vector2 target) {	
		//work out "best" axis constrained direction of target from source
		/*
		 * |0|1|2|
		 * |7|p|3|
		 * |6|5|4| 
		 * 
		 */
		// constrain to cell positions in case we looking for a fighter...
		source = getVectorToCell(source);
		target = getVectorToCell(target);
		int direction = IdManager.UNKNOWN;
		
		// oooh:
		if (source.equals(target)) return direction;
		
		// check if aligned
		if (source.x == target.x) {
			// aligned x
			if (source.y > target.y) return IdManager.NORTH;
			else return IdManager.SOUTH;
		}
		if (source.y == target.y) {
			// aligned y
			if (source.x > target.x) return IdManager.WEST;
			else return IdManager.EAST;
		}
		//
		// work out distance along each axis, smallest wins?
		float diffX = target.x - source.x;
		float diffY = target.y - source.y;
		
		// then compare this
		// head along the longest as its furthest to go...?
		if (Math.abs(source.x + diffX) > Math.abs(source.y + diffY)) {
			// return axis along x
			if (diffX > 0) return IdManager.EAST; 
			else return IdManager.WEST;
		}
		else {
			// return axis along y
			if (diffY > 0) return IdManager.SOUTH;
			else return IdManager.NORTH;
		}
	}
	
	public static Array<Vector2> smoothPathCoarse(Array<Vector2> original) {
		// based on dividing total number of moves into flick ranged moves
		// each moves(num) is a gameCell forming a path from start to target
		
		// start, add to smooth, work along vectors until either x or y shifts more than threshold
		// add that shift to smooth, continue until end	
		
		Array<Vector2> smooth = new Array<Vector2>(original.size);
		Vector2 current = new Vector2();
		Vector2 next = new Vector2();
		int directionX;
		int directionY;
		
		if (Navigator.DEBUG_NAV) Log.i(TAG, "original.size: " + original.size);
		
		//debugPath(original);

		// set next, prev vectors, get starting direction
		current.set(original.get(0));
		next.set(original.get(1));
		
		directionX = getDirection(current.x, next.x);
		directionY = getDirection(current.y, next.y);
		
		// must save the starting vector
		smooth.add(current);
		// advance step for loop
		current.set(next);
		
		// loop
		for (int i = 2; i < original.size; i++) {
			next.set(original.get(i));
			if (getDirection(current.x, next.x) == directionX &&
					getDirection(current.y, next.y) == directionY) {
				// same direction, skip
			}
			else {
				// change direction, add vector to smooth
				directionX = getDirection(current.x, next.x);
				directionY = getDirection(current.y, next.y);
				smooth.add(next);
			}
			// advance step
			current.set(next);
		}
		// double fudge to try ensure drone gets to target vector
		// this last one gets extended below
		smooth.add(original.get(original.size - 1));
		
		// extend last one based upon direct so that fighter flies thru it
		smooth.shrink();
		smooth.get(smooth.size - 1).set(
				next.x + (cellSize * directionX), 
				next.y + (cellSize * directionY));
		
		if (Navigator.DEBUG_NAV) Log.i(TAG, "smooth.size: " + smooth.size);
		//debugPath(smooth);
		
		return smooth;
	}
	
	public static Array<Vector2> smoothPathFine(Array<Vector2> original) {
		//TODO
		//TO BE IMPLEMENTED
		
		// used for paths that are bendy around obs, and Coarse causes collision
		// called after coarse attempts to get path?
		Array<Vector2> smooth = new Array<Vector2>(original.size);
		
		return smooth;
	}
	
	public static Array<Vector2> refactorFlightPath(Array<Vector2> original, Vector2 source) {
		// find closest waypoint in original to position
		Array<Vector2> resized = new Array<Vector2>(original.size);
		int closest = findClosestVectorIndex(original, source);	
		int end = original.size;
		
		resized.addAll(original, closest, end - closest);		
		resized.shrink();

		return resized;
	}
	
	
	
	private static ScanCell[][] cloneScanMap(ScanCell[][] src) {
		ScanCell[][] target = new ScanCell[cellsWide][cellsHigh];
		for (int y = 0; y < cellsHigh; y++) {
			for (int x = 0; x < cellsWide; x++) {
				target[x][y] = new ScanCell(src[x][y].position, src[x][y].id);
			}
		}
	    return target;
	}
	
/*********************************************************************/	
	
// DEBUG FUNCTIONS
	
	public static void debugPath(Array<Vector2> path) {
		if (Navigator.DEBUG_NAV) {
			for (int a = 0; a < path.size; a++) {
				Log.i(TAG, "Path step num " + a + " has XY : " + path.get(a).x + ", " + path.get(a).y);
			}
		}
	}
	
	public static void debugFullScanMap(ScanCell[][] debugScanMap) {
		if (Navigator.DEBUG_NAV) {
			String line = "";
			for (int y = 0; y < cellsHigh; y++) {
				for (int x = 0; x < cellsWide; x++) {
					line += debugScanMap[x][y].id;
				}
				Log.i(TAG, "scan: " + line);
				line = "";
			}
			debugScanMap = null;
		}
	}
	
	public static void debugFlightPathMap(ScanCell[][] scannedMap, Array<ScanCell> path) {
		if (Navigator.DEBUG_NAV) {
			ScanCell[][] debugMap = new ScanCell[cellsWide][cellsHigh];
			debugMap = cloneScanMap(scannedMap);
				
			for (int a = 0; a < path.size; a++) {
				debugMap[path.get(a).x][path.get(a).y].id = IdManager.PATH_DEBUG;
			}
			
			debugFullScanMap(debugMap);
			debugMap = null;
		}
	}
	
	public static void debugAxisScan(Array<ScanCell> debugScan) {
		if (Navigator.DEBUG_NAV) {	
			/*
			 * |0|1|2|
			 * |7|p|3|
			 * |6|5|4| 
			 * 
			 */
			Log.i(TAG, "debug Axis Scan:");
			String line = "";
			line += debugScan.get(0).id;
			line += " ";
			line += debugScan.get(1).id;
			line += " ";
			line += debugScan.get(2).id;
			Log.i(TAG, "axisScan: " + line);
			line = "";
			line += debugScan.get(7).id;
			line += " ";
			line += "p";
			line += " ";
			line += debugScan.get(3).id;
			line += " ";
			Log.i(TAG, "axisScan: " + line);
			line ="";
			line += debugScan.get(6).id;
			line += " ";
			line += debugScan.get(5).id;
			line += " ";
			line += debugScan.get(4).id;
			Log.i(TAG, "axisScan: " + line);
		}
	}
	
	public static void debugRangedScanPath(ScanCell[][] scanMap, Array<Vector2> waypoints) {
		if (Navigator.DEBUG_NAV) {
			String line = "";
			int length = scanMap.length;
			Log.i(TAG, "debug range waypoints scan:" );
			for (int y = 0; y < length; y++) {
				for (int x = 0; x < length; x++) {
					if (scanMap[x][y].position == waypoints.get(0)) {
						line += "1 ";
					}
					else if (scanMap[x][y].position == waypoints.get(1)) {
						line += "2 ";
					}
					else {
						line += scanMap[x][y].id;
						line += " ";
					}
				}
				Log.i(TAG, "rangeScan: " + line);
				line ="";
			}
		}
	}
	
	public static void debugRangeScan(ScanCell[][] scanMap, int range) {
		if (Navigator.DEBUG_NAV) {
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
			String line = "";
			Log.i(TAG, "debug range scan:" );
			Log.i(TAG, "0,0 position: " + scanMap[0][0].position.x + ", " + scanMap[0][0].position.y);
			Log.i(TAG, "range - 1 position: " + scanMap[range - 1][range - 1].position.x + ", " + scanMap[range - 1][range - 1].position.y);
			
			for (int y = 0; y < range; y++) {
				for (int x = 0; x < range; x++) {
					line += scanMap[x][y].id;
					line += " ";
				}
				Log.i(TAG, "axisScan: " + line);
				line ="";
			}
		}
	}
}