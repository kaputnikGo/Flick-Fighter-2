package com.ff.ff2.obs;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.ail.SqnLdr;
import com.ff.ff2.eng.FFModel;
import com.ff.ff2.lib.GraphicsManager;

public class Hanger extends Obstacle {
	public SqnLdr sqnldr;
	//public int runway;
	
	public Hanger(Vector2 position) {
		super(position, GraphicsManager.getGraphicsManager().hangerTexture);
		// has hitPoints 80	
	}
	
	public void initHanger() {
		sqnldr = new SqnLdr(position);
		
		if (sqnldr.initRunway() == false) {
			// uh -oh
			selfDestruct();
		}
	}
	
/*********************************************************************/
	
	@Override
	public void swapTexture(Texture texture) {
		this.texture = texture;	
	}
	
	public void swapTexture() {
		this.texture = GraphicsManager.getGraphicsManager().hangerhitTexture;
		this.sprite = new Sprite(this.texture);
	}
	
	public void damage(float amount) {
		hitPoints = Math.max(hitPoints - amount,  0);
		
		if (hitPoints <= 60) {
			sqnldr.underAttack();
		}
		
		if (hitPoints <= 40) {
			swapTexture();
		}
		
		if (hitPoints <= 0) {
			sqnldr.destroy();
			sqnldr = null;
			alive = false;
		}
	}
	
/*********************************************************************/
	
	private void selfDestruct() {
		// self ordered mega destruct like a missile bomb
		damage(hitPoints);
		FFModel.getFFModel().unitSelfDestruct(position);
	}
	
/*********************************************************************/
	
	public void draw(SpriteBatch spriteBatch, float deltaTime) {
		super.draw(spriteBatch, deltaTime);
	}
}