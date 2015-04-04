package com.ff.ff2.nav;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.obs.ScanCell;

public class PathFinder {
	private static final String TAG = PathFinder.class.getSimpleName();
	private static final int cellsWide = FFScreen.gameCellsWide;
	private static final int cellsHigh = FFScreen.gameCellsHigh;
	private static final int MAX_DEPTH_SEARCH = 200; // was 100...
	
	private static ScanCell[][] scanCellMap;
	
	private static Array<ScanCell> openList;
	private static Array<ScanCell> closedList;	
	
	
	public PathFinder() {
		//
	}
	
	/*
	 * pathfinder find path errors:
	 * 	if target below and left of source, path goes up and left till obs makes it go down..
	 */
	
	public static Array<ScanCell> findPath(ScanCell[][] scannedMap, ScanCell sourceCell, ScanCell targetCell) {	
		scanCellMap = null;
		scanCellMap = new ScanCell[cellsWide][cellsHigh];

		scanCellMap = scannedMap;
		
		// reset lists
		openList = new Array<ScanCell>();
		closedList = new Array<ScanCell>();
		// start at sourceCell
		ScanCell currentCell = new ScanCell();
		ScanCell nextCell = new ScanCell();
		
		openList.add(scanCellMap[sourceCell.x][sourceCell.y]);
		openList.get(0).fCost = 0;
		openList.get(0).parent = null;

		boolean found = false;
		int searchDepth = 0;
		
		while(openList.size > 0 && !found) {
			// get cell to search from
			currentCell = openList.get(0);
			// remove current from open, add to closed
			openList.removeValue(currentCell, true);
			closedList.add(currentCell);
			
			if (searchDepth == MAX_DEPTH_SEARCH) {
				// has reached safety catch
				if (Navigator.DEBUG_NAV) Log.i(TAG, "max depth of search reached, break");
				break;
			}	
			else {
				for (int x = currentCell.x - 1; x <= currentCell.x + 1; x++) {
					for (int y = currentCell.y - 1; y <= currentCell.y + 1; y++) {
						
						if (x == currentCell.x && y == currentCell.y)  {
							// is the curentCell, skip
							continue;
						}
						else if (x == targetCell.x && y == targetCell.y) {
							// has reached target, can stop now
							if (Navigator.DEBUG_NAV) Log.i(TAG, "has 1st FOUND TARGET");
							found = true;
							nextCell = scanCellMap[x][y];
							nextCell.parent = currentCell;
							openList.add(nextCell);
							break;
						}
						else if (isValidEmptyCellLocation(x, y)) {
							// get it as next cell
							nextCell = scanCellMap[x][y];
							
							if (closedList.contains(nextCell, true)) {
								// ignore this one
								continue;
							}
							
							// add costs	
							if ((x == currentCell.x) || (y == currentCell.y)) {
								nextCell.gCost = 10;
							}
							else {
								nextCell.gCost = 14;
							}
							int moveCost = nextCell.gCost;
							
							if (!openList.contains(nextCell, true)) {
								// if not in openList, add it
								nextCell.parent = currentCell;
								nextCell.gCost += currentCell.gCost;
								nextCell.hCost = getHeuristicCost(x, y, targetCell.x, targetCell.y);
								nextCell.factorFCost();
								
								// add to open list in order of lowest fCost
								insertCellList(nextCell);								
							}
							else {
								// already in openList, so retrieve old one to check
								nextCell = new ScanCell();
								nextCell = openList.get(0);
								if ((currentCell.gCost + moveCost) < nextCell.gCost) {
									nextCell.gCost = currentCell.gCost + moveCost;
									// remove its old position
									openList.removeValue(nextCell, true);
									// add to open list in order of lowest fCost
									insertCellList(nextCell);
								}
							}
						}
					}
				}
			}
			searchDepth++;
		}
		if (found) {
			// build and check path from closedList
			return sortPathArray(sourceCell, targetCell);
		}
		else {
			// not found target...
			// trigger a patrol path scan
			if (Navigator.DEBUG_NAV) Log.i(TAG, "not found target, pathScan depth: " + searchDepth);
			return null;
		}
	}
	
	
	public static Array<Vector2> getRangedWaypoints(ScanCell[][] scanMap, int range, int heading) {
		//TODO
		
		// range is length of scan to waypoint2, currently 3 cells
		// heading is the preferred direction for the patrol to head in,
		// try and get closest...
		
		// centre of map will ALWAYS be pivot
		// using scanMap and pivot 'p' find first empty axis cell
		// this will be saved as waypoint1 (but may change further down
		
		/*
		 * |0|1|2|
		 * |7|p|3|
		 * |6|5|4|  
		 */
		
		/*
		 search pattern regardless of direction, OR search based upon heading...?
		 
		 |x|x|x|2|x|x|x| 
		 |x|x|x|p|x|x|x| 
		 |x|x|x|p|x|x|x|
		 |2|p|p|1|p|p|2|
	  	 |x|x|x|S|x|x|x|
	  	 
	  	 s = start, 1 = waypoint(0), 2 = possible waypoint(1)
		 */
		
		// init vars
		int direction = IdManager.UNKNOWN;
		int bestDirection = IdManager.UNKNOWN;
		int directionOp = IdManager.UNKNOWN;
		int radius = range + 1;
		int count = 0;
		int longestPath = 0;
		
		Array<Vector2> waypoints = new Array<Vector2>();
		
		// replace this bit with heading based direction search
		// search for waypoints(0)
		int left = IdManager.UNKNOWN;
		int right = IdManager.UNKNOWN;
		
		if (heading == IdManager.NORTH) {
			left = IdManager.WEST;
			right = IdManager.EAST; 
		}
		else if (heading == IdManager.EAST) {
			left = IdManager.NORTH;
			right = IdManager.SOUTH; 
		}
		else if (heading == IdManager.SOUTH) {
			left = IdManager.EAST;
			right = IdManager.WEST; 
		}
		else if (heading == IdManager.WEST) {
			left = IdManager.SOUTH;
			right = IdManager.NORTH; 
		}

	// search heading first
		if (Navigator.DEBUG_NAV) Log.i(TAG, "first search, heading..." + heading);
		for (int n = 0; n <= 2; n++) {
			if (n == 0) direction = heading;
			else if (n == 1) direction = left;
			else direction = right;
			
			count = directionSearchEmpty(scanMap, direction, radius);
			if (count > 0) {
				// save the first cell in this direction as waypoint(0)
				//TODO
				// always saves the last one found...
				// need to save heading one if it has it...
				waypoints.add(getPositionCellRangedDirection(scanMap, direction, radius, 1));
			}
			
			if (count > longestPath) {
				longestPath = count;
				bestDirection = direction;
				// save as temp waypoint(1)
				waypoints.add(getPositionCellRangedDirection(scanMap, direction, radius, longestPath));
			}			
		}
		
		if (longestPath == 0) {
			// no empty found at all
			return null;
		}
		else {
			// save this bestDirection and longestPath cell position as waypoint1
			count = 0;
			longestPath = 0;
			
			directionOp = flipDirection(bestDirection);			
			// then, search along bestDirection but not directionOp as we came from there,
			// for waypoints(1)
			if (Navigator.DEBUG_NAV) Log.i(TAG, "second search, heading..." + heading);
			for (int n = 0; n <= 2; n++) {
				if (n == 0) direction = heading;
				else if (n == 1) direction = left;
				else direction = right;
				
				if (direction != directionOp) {
					count = directionSearchEmpty(scanMap, direction, radius);
					if (count > longestPath) {
						longestPath = count;
						bestDirection = direction;
					}
				}
			}
			if (longestPath == 0) {
				// skip
			}
			else {
				waypoints.add(getPositionCellRangedDirection(scanMap, direction, radius, longestPath));
			}
		}
		
		// debug the waypoints here
		NavigatorUtils.debugRangedScanPath(scanMap, waypoints);
			
		return waypoints;
	}

/*********************************************************************/	

//PATHFINDER UTILITY FUNCTIONS
	
	private static boolean isValidEmptyCellLocation(int x, int y) {
		// check if within screen and is EMPTY
		if (x >= 0 && x < cellsWide) {
			if (y >= 0 && y < cellsHigh) {
				if (scanCellMap[x][y].id == IdManager.EMPTY) return true;
			}
		}
		return false;
	}
	
	private static boolean isValidEmptyCell(ScanCell candidate) {
		if (candidate.position.x >= 0 && candidate.position.x < FFScreen.gameWidth) {
			if (candidate.position.y >= 0 && candidate.position.y < FFScreen.gameHeight) {
				return candidate.id == IdManager.EMPTY;
			}
		}	
		return false;
	}
	
	private static int flipDirection(int direction) {
		if (direction < 4) 
			return direction + 4;
		else 
			return direction - 4;
	}
	
	private static int directionSearchEmpty(ScanCell[][] scanMap, int direction, int radius) {
		//TODO
		
		// assumes centre (radius) is start...
		// search along a single compass axis direction (n,s,e or w) for radius length
		// return number count of empty cells found
		
		if (Navigator.DEBUG_NAV) Log.i(TAG, "searchEmpty radius: " + radius);
		if (Navigator.DEBUG_NAV) Log.i(TAG, "direction: " + direction);
		if (Navigator.DEBUG_NAV) Log.i(TAG, "scanMap.length: " + scanMap.length);
		int count = 0;
		int scanX = radius;
		int scanY = radius;
		int deltaX = 0;
		int deltaY = 0;
		
		if (direction == IdManager.NORTH) {
			deltaY = -1;
		}
		else if (direction == IdManager.SOUTH) {
			deltaY = 1;
		}
		else if (direction == IdManager.WEST) {
			deltaX = -1;
		}
		else if (direction == IdManager.EAST) {
			deltaX = 1;
		}
		
		// start next cell
		scanX += deltaX;
		scanY += deltaY;
		
		while (isValidEmptyCell(scanMap[scanX][scanY])) {
			if (count <= radius) {
				count++;
				scanX += deltaX;
				scanY += deltaY;
			}
			if (scanX == scanMap.length || scanY == scanMap.length
					|| scanX == 0 || scanY == 0) {
				break;
			}
		}		
		return count;
	}
	
	private static Vector2 getPositionCellRangedDirection(ScanCell[][] scanMap, int direction, int radius, int range) {
		// assumes centre (radius) is start...
		int scanX = radius - 1;
		int scanY = radius - 1;
		int deltaX = 0;
		int deltaY = 0;
		
		if (direction == IdManager.NORTH) {
			deltaY = -1 * range;
		}
		else if (direction == IdManager.SOUTH) {
			deltaY = 1 * range;
		}
		else if (direction == IdManager.WEST) {
			deltaX = -1 * range;
		}
		else if (direction == IdManager.EAST) {
			deltaX = 1 * range;
		}	
		scanX += deltaX;
		scanY += deltaY;
		
		return scanMap[scanX][scanY].position;
	}

	private static void insertCellList(ScanCell cell) {
		int count = openList.size;
		int i = 0;
		for (; i < count; i++) {
			if (cell.fCost <= openList.get(i).fCost){
				// has found lowest,
				break;
			}
		}
		openList.insert(i, cell);
	}
	
	private static int getHeuristicCost(int sx, int sy, int tx, int ty) {
		// Manhattan style...
		
		// extremes:
		// sx, sy = 0 and tx = 15, ty = 25
		// sqrt (15 * 15) + (25 * 25)
		// sqrt 75 + 625
		// sqrt 700
		// = 26.4575	
		int dx = tx - sx;
		int dy = ty - sy;
		return (int)Math.sqrt((dx * dx) + (dy * dy));
	}
	
	private static Array<ScanCell> sortPathArray(ScanCell sourceCell, ScanCell targetCell) {
		ScanCell cell = new ScanCell();
		Array<ScanCell> path = new Array<ScanCell>();
		// start at target
		path.insert(0, targetCell);
		cell = targetCell.parent;
		// start at target and work back via parent cells
		while (cell != sourceCell) {
			path.insert(0, cell);
			
			if (cell.parent != cell && cell.parent != null) 
				cell = cell.parent;
			else {
				if (FFScreen.DEBUG) Log.i(TAG, "cell.parent = cell...loopy.");
				// make hack fix
				break;
			}
		}
		// add start cell here
		path.insert(0, sourceCell);
		return path;
	}
}