package com.ff.ff2.ent;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.ail.Gunner;
import com.ff.ff2.eng.FFModel;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.GraphicsManager;
import com.ff.ff2.lib.IdManager;

public class Turret extends Sprite {
	public Vector2 position = new Vector2();
	public Rectangle boundBox = new Rectangle();
	public Gunner gunner;

	public Vector2 cogOffset = new Vector2();
	public float diameter;
	
	public int id;
	public boolean alive;
	public float hitPoints;
	
	private Texture texture;
	public float bulletDamage;
	
	public Turret(int id, Vector2 position) {
		super();
		this.id = id;
		this.position.set(position);
		this.texture = GraphicsManager.getGraphicsManager().turretTexture;
		this.diameter = FFScreen.gameCellSize;
		this.boundBox.set(position.x, position.y, diameter, diameter);	
		alive = true;
		hitPoints = FFScreen.engine.settings.TURRET_HP;
		bulletDamage = FFScreen.engine.settings.TURRET_DAMAGE;
		this.cogOffset.set(position.x + (diameter / 2), 
				position.y + (diameter / 2));
	}
	
/*****************************************************************/
	
	public void loadAI() {
		this.gunner = new Gunner(this);
	}
	
	public void swapTexture() {
		this.texture = GraphicsManager.getGraphicsManager().turrethitTexture;
	}
	
	
	public void damage(float amount) {
		hitPoints = Math.max(hitPoints - amount,  0);
		
		if (hitPoints <= 20) swapTexture();
		if (hitPoints <= 0) alive = false;
	}
	
/*****************************************************************/
	
	public void fireWeapon(float targetX, float targetY) {
		FFModel.getFFModel().weapons.add(new Bullet(
				IdManager.TURRET_BULLET, 
				cogOffset, 
				new Vector2(targetX, targetY), 
				bulletDamage));
	}
	
	public void fireWeapon(Vector2 autoTarget) {
		FFModel.getFFModel().weapons.add(new Bullet(
				IdManager.TURRET_BULLET, 
				cogOffset, 
				autoTarget,
				bulletDamage));
	}


/*****************************************************************/
	
	public void draw(SpriteBatch spriteBatch) {
		spriteBatch.draw(texture, position.x, position.y, diameter, diameter);
	}
}