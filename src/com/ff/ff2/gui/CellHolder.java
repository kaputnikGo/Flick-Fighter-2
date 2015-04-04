package com.ff.ff2.gui;

//import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.GraphicsManager;

public class CellHolder {
	//private static final String TAG = CellHolder.class.getSimpleName();
	public Texture texture;
	public Sprite sprite;
	public Vector2 position = new Vector2();
	public float width;
	public float height;

	public CellHolder(Vector2 position) {
		this.position.set(position);
		this.texture = GraphicsManager.getGraphicsManager().toolSelect;
		this.sprite = new Sprite(texture);
		this.width = FFScreen.gameCellSize;
		this.height = FFScreen.gameCellSize;
	}

	public void swapTexture(char id) {
		this.texture = GraphicsManager.getGraphicsManager().getIDTextureCell(id, true);	
		this.sprite = new Sprite(texture);
	}
	
/*****************************************************************/	
	
	public void draw(SpriteBatch creatorBatch) {
		creatorBatch.draw(this.sprite, this.position.x, this.position.y, this.width, this.height);
		
	}
}