package com.ff.ff2.obs;

import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.nav.Navigator;

public class ScanCell {
	// simple container for scanning obstacles and entities
	// has a Vector2 position and a char id at minimum
	// considered a Node for pathfinding functions	
	public Vector2 position = new Vector2();
	public char id;
	public int x;
	public int y;
	public boolean shootable;
	
	// pathfinder vars below
	public ScanCell parent;
	public int fCost; // gCost + hCost
	public int gCost; // 10 for hori or vert, 14 for diag, cost to move from current cell
	public int hCost; // heuristic cost to move to target cell
	
	public ScanCell() {
		//
	}
	
	public ScanCell(Vector2 position, char id) {
		this.position.set(position);
		this.id = id;
		this.x = (int)position.x / FFScreen.gameCellSize;
		this.y = (int)position.y / FFScreen.gameCellSize;
		this.shootable = IdManager.getIdShootable(id);
	}
	
	public void factorFCost() {
		fCost = gCost + hCost;
	}
	
	public void setCellXY(Vector2 position) {
		this.x = (int)position.x / FFScreen.gameCellSize;
		this.y = (int)position.y / FFScreen.gameCellSize;
	}
	
	public boolean equals(ScanCell other) {
		// compare x, y
		return (this.x == other.x && this.y == other.y);
	}
	
	public void reset() {
		position.set(Navigator.nullTarget);
		x = 0;
		y = 0;
		id = IdManager.UNKNOWN_CELL;
		fCost = 0;
		gCost = 0;
		hCost = 0;
		shootable = false;
	}
}