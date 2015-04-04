package com.ff.ff2.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.lib.GraphicsManager;
import com.ff.ff2.lib.IdManager;


public class MenuButton {
	public Vector2 position = new Vector2();
	public Rectangle boundBox = new Rectangle();
	
	private Texture texture;
	private Sprite sprite;
	private float width;
	private float height;
	public int id;
	
	public MenuButton(Vector2 position, int id) {
		this.position.set(position);
		this.id = id;
		
		this.width = FFScreen.gameCellSize * 2.f;
		this.height = FFScreen.gameCellSize;
		
		this.boundBox.set(position.x, position.y, 
				position.x + width, position.y + height);
		
		this.texture = GraphicsManager.getGraphicsManager().getIDButtonTexture(id);
		this.sprite = new Sprite(texture);
	}
	
/*********************************************************************/
	
	public void swapTexture(boolean toggle) {
		if (id == IdManager.BTN_SOUND) {
			if (toggle) {
				//turn it on
				this.texture = GraphicsManager.getGraphicsManager().getIDButtonTexture(IdManager.BTN_SOUND);
			}
			else {
			// turn it off
				this.texture = GraphicsManager.getGraphicsManager().getIDButtonTexture(IdManager.BTN_NO_SOUND);
			}
			this.sprite = new Sprite(this.texture);
		}
		if (id == IdManager.BTN_FILE_PLAY) {
			if (toggle) {
				//turn it on
				this.texture = GraphicsManager.getGraphicsManager().getIDButtonTexture(IdManager.BTN_FILE_PLAY);
			}
			else {
			// turn it off
				this.texture = GraphicsManager.getGraphicsManager().getIDButtonTexture(IdManager.BTN_FILE_NO_PLAY);
			}
			this.sprite = new Sprite(this.texture);
		}
		if (id == IdManager.BTN_CREATOR) {
			if (toggle) {
				//turn it on
				this.texture = GraphicsManager.getGraphicsManager().getIDButtonTexture(IdManager.BTN_CREATOR);
			}
			else {
			// turn it off
				this.texture = GraphicsManager.getGraphicsManager().getIDButtonTexture(IdManager.BTN_NO_CREATOR);
			}
			this.sprite = new Sprite(this.texture);
		}
		if (id == IdManager.BTN_MENU) {
			if (toggle) {
				//turn it on
				this.texture = GraphicsManager.getGraphicsManager().getIDButtonTexture(IdManager.BTN_MENU);
			}
			else {
			// turn it off
				this.texture = GraphicsManager.getGraphicsManager().getIDButtonTexture(IdManager.BTN_NO_MENU);
			}
			this.sprite = new Sprite(this.texture);
		}
	}
	
/*********************************************************************/
	
	public void draw(SpriteBatch spriteBatch) {
		spriteBatch.draw(sprite, position.x, position.y, width, height);
	}
}
