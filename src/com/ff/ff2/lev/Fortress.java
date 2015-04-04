package com.ff.ff2.lev;

import android.util.Log;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ff.ff2.ent.Fighter;
import com.ff.ff2.ent.Turret;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.GraphicsManager;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.lib.LoadManager;
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
import com.ff.ff2.obs.ScanCell;
import com.ff.ff2.obs.Switch;

public class Fortress {
	private static final String TAG = Fortress.class.getSimpleName();
	// relates to a single game screen size of cellsWide x cellsHigh
	public Array<Obstacle> obstacles = new Array<Obstacle>();
	public Array<ScanCell> obsCells = new Array<ScanCell>();
	
	public Array<ScanCell> emptyCells = new Array<ScanCell>();
	public Array<Turret> turrets = new Array<Turret>();
	public Array<Obstacle> scaffolds = new Array<Obstacle>();
	public Array<Fighter> drones = new Array<Fighter>();
	public Array<Hanger> hangers = new Array<Hanger>();
	
	private char[][] fortressArray;
	private Obstacle respawner;
	
	public Vector2 privateFighterStartVector;
	public Vector2 powerupVector;
	public Vector2 switchVector;
	public Vector2 gatewayVector;
	
	private int cellSize;
	private int cellsWide;
	private int cellsHigh;
	
	private int lastScanSize;
	
	public Fortress() {
		cellsWide = FFScreen.gameCellsWide;
		cellsHigh = FFScreen.gameCellsHigh;
		cellSize = FFScreen.gameCellSize;
		lastScanSize = 0;
		// default
		privateFighterStartVector = new Vector2(
				cellSize * 3, 
				cellSize * 21);
	}
	
	public void reset() {
		turrets.clear();
		drones.clear();
		obstacles.clear();
		scaffolds.clear();
		emptyCells.clear();
		hangers.clear();
		
		powerupVector = new Vector2();
		switchVector = new Vector2();
		gatewayVector = new Vector2();
		respawner = null;
		lastScanSize = 0;
	}
	
	public void destroy() {
		if (obstacles != null) obstacles = null;
		if (turrets != null) turrets = null;
		if (scaffolds != null) scaffolds = null;
		if (drones != null) drones = null;
		if (obsCells != null) obsCells = null;
		if (emptyCells != null) emptyCells = null;
		if (hangers != null) hangers = null;
		if (fortressArray != null) fortressArray = null;
	}

	public void rescanFortressObstacles() {
		if (obstacles.size < lastScanSize) {
			scanFortressObstacles();
		}
	}
	
	private void scanFortressObstacles() {
		obsCells = new Array<ScanCell>(cellsWide * cellsHigh);
		for (Obstacle obstacle : obstacles) {
			if (obstacle.alive) {
				obsCells.add(new ScanCell(obstacle.position, IdManager.getObstacleId(obstacle)));
			}
		}		
		obsCells.shrink();
		lastScanSize = obsCells.size;
	}
	
	public void debugEmptyCells() {
		if (FFScreen.DEBUG) {
			Log.i(TAG, "debug emptyCells array:");	
			for (ScanCell cell : emptyCells) {
				if (cell.id == IdManager.PATH_DEBUG) {
					Log.i(TAG, "found debug path cell id");
				}
			}
		}
	}
	
/*********************************************************************/	
	
	public int loadFortress(String fortressName, int location) {
		return loadFortressFromFile(fortressName, location);
	}
	
	public void addObstacleToField(Obstacle obstacle) {
		obstacles.add(obstacle);
	}
	
	public void gatewayActivated(boolean gatewayActivated) {
		// change both switch and portal/gateway
		// this assumes only one switch and gateway...
		for (Obstacle obstacle : obstacles) {
			if (obstacle instanceof Gateway) {
				if (gatewayActivated) obstacle.swapTexture(GraphicsManager.getGraphicsManager().gatewayonTexture);
				else obstacle.swapTexture(GraphicsManager.getGraphicsManager().gatewayoffTexture);
			}
			if (obstacle instanceof Switch) {
				if (gatewayActivated) obstacle.swapTexture(GraphicsManager.getGraphicsManager().switchonTexture);
				else obstacle.swapTexture(GraphicsManager.getGraphicsManager().switchoffTexture);
			}
		}
	}
	
	public void createSpawner() {
		respawner = new Powerup(powerupVector);
	}
	
	public void spawnSpawner() {
		if (respawner != null) obstacles.add(respawner); 
	}
	
/*********************************************************************/
	
	private void createRandomFortress() {
		// double random chance for boolean
		// needs updating since new obs added and switchVector etc
		if (Generator.prepForRandomFortress()) {
			Generator.createSimpleRandomFortress();
			powerupVector = Generator.powerupVector;
			switchVector = Generator.switchVector;
			gatewayVector = Generator.gatewayVector;
			obstacles = Generator.obstacles;
			obstacles.shrink();
		}
		else {
			//error
		}
	}
	
	private int loadFortressFromFile(String fortressName, int location) {
		reset();
		if (fortressName.equals(LoadManager.RANDOM_FORT)) {
			createRandomFortress();
		}
		else {
			int result = LoadManager.getLoadManager().loadFortressFile(fortressName, location);	
			if (result == LoadManager.FILE_LOADED) {
				fortressArray = LoadManager.getLoadManager().getFortressArray();
				Vector2 initVector = new Vector2();
				
				if (fortressArray != null) {		
					for (int y = 0; y < cellsHigh; y++) {
						for (int x = 0; x < cellsWide; x++) {	
							if (fortressArray[x][y] == IdManager.EMPTY) {
								// array of empty cells for pathfinder
								initVector.x = x * cellSize; // convert cell to pixel
								initVector.y = y * cellSize; // convert cell to pixel
								emptyCells.add(new ScanCell(initVector, IdManager.EMPTY));
							}
							else if (fortressArray[x][y] == IdManager.FENCE) {
								initVector.x = x * cellSize;
								initVector.y = y * cellSize;
								obstacles.add(new Fence(initVector));
							}
							else if (fortressArray[x][y] == IdManager.BARRIER) {
								initVector.x = x * cellSize;
								initVector.y = y * cellSize;
								obstacles.add(new Barrier(initVector));
							}
							else if (fortressArray[x][y] == IdManager.MINE) {
								initVector.x = x * cellSize;
								initVector.y = y * cellSize;
								obstacles.add(new Mine(initVector));
							}
							else if (fortressArray[x][y] == IdManager.PUSHBLOCK) {
								initVector.x = x * cellSize;
								initVector.y = y * cellSize;
								// need alignX boolean
								obstacles.add(new Pushblock(initVector));
							}
							else if (fortressArray[x][y] == IdManager.BOMBUP) {
								initVector.x = x * cellSize;
								initVector.y = y * cellSize;
								// need alignX boolean
								obstacles.add(new Bombup(initVector));
							}
							else if (fortressArray[x][y] == IdManager.TURRET) {
								initVector.x = x * cellSize;
								initVector.y = y * cellSize;
								turrets.add(new Turret(IdManager.TURRET, initVector));
							}
							else if (fortressArray[x][y] == IdManager.GATEWAY) {
								gatewayVector.x = x * cellSize;
								gatewayVector.y = y * cellSize;
								obstacles.add(new Gateway(gatewayVector));
							}
							else if (fortressArray[x][y] == IdManager.POWERUP) {
								powerupVector.x = x * cellSize;
								powerupVector.y = y * cellSize;
								obstacles.add(new Powerup(powerupVector));
							}
							else if (fortressArray[x][y] == IdManager.SWITCH) {
								switchVector.x = x * cellSize;
								switchVector.y = y * cellSize;
								obstacles.add(new Switch(switchVector));
							}
							else if (fortressArray[x][y] == IdManager.HANGER) {
								initVector.x = x * cellSize;
								initVector.y = y * cellSize;
								hangers.add(new Hanger(initVector));
								obstacles.add(hangers.peek());
							}
							else if (fortressArray[x][y] == IdManager.SCAFFOLD) {
								initVector.x = x * cellSize;
								initVector.y = y * cellSize;
								scaffolds.add(new Scaffold(initVector));
								obstacles.add(scaffolds.peek());
							}	
							else if (fortressArray[x][y] == IdManager.PRIVATE_FIGHTER) {
								privateFighterStartVector.set(x * cellSize, y * cellSize);
							}
							else if (fortressArray[x][y] == IdManager.DRONE) {
								initVector.x = x * cellSize;
								initVector.y = y * cellSize;
								drones.add(new Fighter(IdManager.GENERAL_ID, initVector));
							}
						}
					}
					obstacles.shrink();
					scaffolds.shrink();
					drones.shrink();
					turrets.shrink();
					emptyCells.shrink();
					hangers.shrink();
					
					// TODO
					// create hanger group if hangers
					
					scanFortressObstacles();
					return result;
				}
			}
			else {
				// error in file reading, so:
				createRandomFortress();
				return result;
			}
		}
		return LoadManager.LOAD_ERROR;
	}
	
/*********************************************************************/	

	public void draw(SpriteBatch gameBatch, float deltaTime) {
		for (Obstacle obstacle : obstacles) {
			if (obstacle.alive) {
				obstacle.draw(gameBatch, deltaTime);
			} else {
				emptyCells.add(new ScanCell(obstacle.position, IdManager.EMPTY));
				obstacles.removeValue(obstacle, true);
				obstacles.shrink();
			}
		}
	}
}