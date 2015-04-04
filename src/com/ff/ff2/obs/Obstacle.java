package com.ff.ff2.obs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.gui.FFScreen;


public abstract class Obstacle {
	//public char id;
	public Vector2 position = new Vector2();
	public Texture texture;
	public Sprite sprite;

	public float height;
	public float width;
	
	public Vector2 cog = new Vector2();
	public Rectangle boundBox = new Rectangle();
	
	public boolean alive;
	public float hitPoints;
	public float damage;
	
	public Obstacle (Vector2 position, Texture texture) {
		this.position.set(position);
		this.texture = texture;
		this.sprite = new Sprite(texture);
		
		this.width = FFScreen.gameCellSize;
		this.height = FFScreen.gameCellSize;
		
		this.cog.set(position.x + width / 2, position.y + height / 2);	
		this.boundBox.set(position.x, position.y, width, height);
		
		this.alive = true;
		this.damage = 0;
		
		if (this instanceof Fence) {
			this.hitPoints = FFScreen.engine.settings.FENCE_HP;
		}
		else if (this instanceof Wordblock) {
			this.hitPoints = FFScreen.engine.settings.WORD_HP;
		}
		else if (this instanceof Mine) {
			this.hitPoints = FFScreen.engine.settings.MINE_HP;
			this.damage = FFScreen.engine.settings.MINE_DAMAGE;
		}
		else if (this instanceof Hanger) {
			this.hitPoints = FFScreen.engine.settings.HANGER_HP;
		}
		else {
			this.hitPoints = FFScreen.engine.settings.BARRIER_HP;
		}
	}
	
	public abstract void swapTexture();
	public abstract void swapTexture(Texture texture);
	public abstract void damage(float amount);
	//public abstract String toString();

	public void draw(SpriteBatch spriteBatch, float deltaTime) {
		spriteBatch.draw(this.sprite, this.position.x, this.position.y, this.width, this.height);
	}
}