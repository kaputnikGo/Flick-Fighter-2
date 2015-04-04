package com.ff.ff2.ent;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.lib.GraphicsManager;
import com.ff.ff2.lib.IdManager;

public abstract class Weapon {
	//private static final String TAG = Weapon.class.getSimpleName();
	public Vector2 position = new Vector2();
	public Vector2 velocity = new Vector2();
	public Vector2 target = new Vector2();
	public Vector2 direction = new Vector2();
	
	public Rectangle boundBox = new Rectangle();

	public float diameter;
	public Texture texture;
	public Sprite sprite;
	
	public float speed;
	public float damage;
	public int id;
	public boolean alive;
	public String hitAxis;
	public float buffer;
	
	public Weapon (int id, Vector2 position, Vector2 target, float damage) {
		this.id = id;
		this.position.set(position);
		this.target.set(target);
		this.alive = true;
		this.speed = 400.f;
		this.damage = damage;
		
		switch (id) {
			case IdManager.PRIVATE_BULLET:
				this.texture = GraphicsManager.getGraphicsManager().playerBulletTexture;
				break;
			case IdManager.DRONE_BULLET:
				this.texture = GraphicsManager.getGraphicsManager().enemyBulletTexture;
				break;
			case IdManager.TURRET_BULLET:
				this.texture = GraphicsManager.getGraphicsManager().enemyBulletTexture;
				break;
			case IdManager.POWER_CABLE:
				this.texture = GraphicsManager.getGraphicsManager().playerCableTexture;
				break;
			case IdManager.MISSILE_BOMB:
				this.texture = GraphicsManager.getGraphicsManager().bombCableTexture;
				break;
			case IdManager.MINE_DEBRIS:
				this.texture = GraphicsManager.getGraphicsManager().mineDebrisTexture;
				break;
		}
		this.sprite = new Sprite(this.texture);

	}
	
	public abstract void updateBoundBox();
	public abstract void collidedPosition(Vector2 collider);
	public abstract void flight(float deltaTime);
	
	public abstract void draw(SpriteBatch spriteBatch, float deltaTime);
}