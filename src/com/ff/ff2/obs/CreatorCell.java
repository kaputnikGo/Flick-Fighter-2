package com.ff.ff2.obs;

//import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.GraphicsManager;

public class CreatorCell extends Sprite {	
	//private static final String TAG = CreatorCell.class.getSimpleName();
	public char id;
	public Vector2 position = new Vector2();
	public Rectangle boundBox = new Rectangle();
	public Texture textureIcon; // creator icon texture
	public Texture textureCell;
	public Texture texture;
	public boolean draggable;
	public boolean selected;

	private float height;
	private float width;
	
	public CreatorCell(char id, Vector2 position) {
		super();
		this.id = id;
		this.position.set(position);
		this.width = FFScreen.gameCellSize;
		this.height = FFScreen.gameCellSize;
		
		this.textureCell = GraphicsManager.getGraphicsManager().getIDTextureCell(id, false);
		this.textureIcon = GraphicsManager.getGraphicsManager().getIDTextureCell(id, true);

		// set default texture
		this.texture = this.textureCell;
		this.boundBox.set(position.x, position.y, width, height);
		
		draggable = false;
		selected = false;
	}
	
	public void updateBoundBox() {
		boundBox.setPosition(position.x, position.y);
	}
	
	public void selectedTexture() {
		texture = textureCell;
		selected = true;
	}
		
	public void resetIconTexture() {
		texture = textureIcon;
		selected = false;
	}
	
	public void draw(SpriteBatch creatorBatch) {
		creatorBatch.draw(texture, position.x, position.y, width, height);
	}
	
}