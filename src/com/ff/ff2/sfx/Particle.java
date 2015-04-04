package com.ff.ff2.sfx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Particle {

    public float life;
    public Vector2 position = new Vector2();
    public Vector2 velocity = new Vector2();
    public float scale;
    public Texture texture;
	
    public Particle() {
    	
	}

	public void setup(Texture texture, Vector2 position, Vector2 velocity, float life, float scale) {
		this.texture = texture;
		this.position.set(position);
		this.velocity.set(velocity);
		this.life = life;
		this.scale = scale;
	}
    
}