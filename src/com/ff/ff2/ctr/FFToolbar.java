package com.ff.ff2.ctr;

//import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.GraphicsManager;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.obs.CreatorCell;

public class FFToolbar {
	//private static final String TAG = FFToolbar.class.getSimpleName();
	private Texture toastback;
    public float width;
    public float height;
	public Array<CreatorCell> toolbarCells = new Array<CreatorCell>();
	public Vector2 position = new Vector2();
	private Vector2 toolPosition = new Vector2();
	
	private float cellSize;
	
	public boolean fieldLocked;
	public char selectedCellId;
	public boolean hasSelectedTool;
	
	public FFToolbar (Vector2 position) {
		this.position.set(position);
		this.toolPosition.set(position);
		this.cellSize = FFScreen.gameCellSize;
		this.toastback = GraphicsManager.getGraphicsManager().toasterBack;
	    this.width = FFScreen.selectorWidth;
	    this.height = FFScreen.dialogSize / 2;
		
		fieldLocked = false;
		selectedCellId = '+';
		hasSelectedTool = false;
		
		createToolbar();
	}
	
/*********************************************************************/	
	
	private void createToolbar() {		
		toolPosition.y += cellSize;
		toolPosition.x = cellSize;
		
		for (int i = IdManager.toolbarStart; i <= IdManager.toolbarEnd; i++ ) {
			toolPosition.x += cellSize;
			// add space for lock
			if (IdManager.cellList[i] == IdManager.LOCK) {
				// move lock to bottom left of toolbar
				toolPosition.y += cellSize * 2;
				toolPosition.x -= cellSize * 10;
			}
			
			toolbarCells.add(new CreatorCell(IdManager.cellList[i], toolPosition));
		}		
		//cycle thru toolbar cells and set to icon texture
		
		for (CreatorCell cellT : toolbarCells) {
			cellT.resetIconTexture();
		}		
	}
	
	private void resetToolbarIcons() {
		//all but lock
		for (CreatorCell cellT : toolbarCells) {
			if (cellT.id != IdManager.LOCK)
				cellT.resetIconTexture();
		}
	}
	
	private void selectToolbarTool(float touchX, float touchY) {
		// constrain touchX to closest single cell
		touchX = Math.round(touchX / cellSize) * cellSize;
		
		resetToolbarIcons();
		
		for (CreatorCell cellSelect : toolbarCells) {			
			if (cellSelect.boundBox.contains(touchX, touchY)) {
				// found the selected one, not if field locked
				if (cellSelect.id == IdManager.LOCK) {
					// flip the lock
					fieldLocked ^= true;
					// check for lock state
					if (fieldLocked) 
						cellSelect.selectedTexture();
					else 
						cellSelect.resetIconTexture();
					return;
				}
				if (fieldLocked == false) {
					cellSelect.selectedTexture();
					selectedCellId = cellSelect.id;
					hasSelectedTool = true;
					return;
				}
			} 
		}
	}
	
/*********************************************************************/	
	
	public boolean handleTap(float touchX, float touchY) {
		selectToolbarTool(touchX, touchY);
		return true;
	}
	
/*********************************************************************/

	public void destroy() {
		if (toolbarCells != null) toolbarCells = null;
	}
	
/*********************************************************************/
	   
	public void draw(SpriteBatch creatorBatch) {
		creatorBatch.draw(toastback, position.x, position.y, width, height);
		// toolbar cells
		for (CreatorCell cellT : toolbarCells) {
			cellT.draw(creatorBatch);
		}
	}
	
}