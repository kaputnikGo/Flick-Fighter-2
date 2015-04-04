package com.ff.ff2.sfx;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.ff.ff2.eng.FFModel;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.GraphicsManager;
import com.ff.ff2.lib.IdManager;

public class ParticleEmitter extends Sprite {

	// device fps variables
	private static int maxParticle = 800;
	private int nowFPS = 0;
	
	private static final float cogOffset = FFScreen.gameCellSize / 2;
	private Vector2 particleVelocity = new Vector2();
	protected Vector2 random = new Vector2();
	private float life = 1.2f;
	private static float damping = 1f;
	private float deltaScale;
	private float delta;
	
	Array<Particle> particles = new Array<Particle>(false, maxParticle);
	private Pool<Particle> freeParticles = new Pool<Particle>(maxParticle, maxParticle) {
		@Override
		protected Particle newObject() {
			return new Particle();
		}
	};
	
	public ParticleEmitter() {
		//
		
	}
	
	private void startParticle(Vector2 position, float scale, int type) {
		addParticle(position, new Vector2(0, 0), life, scale, type);
	}
	
	public boolean particlesAlive() {
		return particles.size > 0;
	}
	
	public void dispose() {
		particles.clear();
		freeParticles.clear();
	}
	
/*********************************************************************/
	
	public void scaledExplosion(Vector2 position, float scale, int fps, int type) {
		nowFPS = fps;
		// deltaScale is 1.f to 10.f
		deltaScale = scale / 10.f;
		Vector2 cog = new Vector2(position);
		cog.x += cogOffset;
		cog.y += cogOffset;
		startParticle(cog, deltaScale, type);
		
		Vector2 random = new Vector2(MathUtils.cos((float) ((MathUtils.random() * MathUtils.PI * 2f) * Math.sqrt(MathUtils.random()))),
				(float) (MathUtils.sin(MathUtils.random() * MathUtils.PI * 2f) * Math.sqrt(MathUtils.random())));

		for (int i = 1; i <= scale; ++i) {
			Vector2 velp = new Vector2().set(random).scl(i / 20.f * .5f);
			FFModel.getFFModel().particleEmitter.particleExplosion(cog, velp, type);
		}
		
		// for large, add this
		if (scale >= 10.f) {
			for (int i = 1; i <= scale; ++i) {
				Vector2 vel = new Vector2().set(random).add(random);
				Vector2 velp = new Vector2().set(vel).scl(i / 20.f * 2.f);
				FFModel.getFFModel().particleEmitter.particleExplosion(cog, velp, type);
			}
		}
	}
	
	public void bulletExplosion(Vector2 position, int fps, int type) {
		nowFPS = fps;
		// deltaScale is 1.f to 10.f
		deltaScale = .2f;
		startParticle(position, deltaScale, type);
		
		Vector2 random = new Vector2(MathUtils.cos((float) ((MathUtils.random() * MathUtils.PI * 2f) * Math.sqrt(MathUtils.random()))),
				(float) (MathUtils.sin(MathUtils.random() * MathUtils.PI * 2f) * Math.sqrt(MathUtils.random())));

		for (int i = 1; i <= 2; ++i) {
			Vector2 velp = new Vector2().set(random).scl(i / 20.f * .5f);
			FFModel.getFFModel().particleEmitter.particleExplosion(position, velp, type);
		}		
	}
	
	public void scaledPowerupCharge(Vector2 position, float scale, int type) {
		// deltaScale between .4f to 1.2f
		deltaScale = scale / 10.f;
		Vector2 cog = new Vector2(position);
		cog.x += cogOffset;
		cog.y += cogOffset;
		startParticle(cog, deltaScale, type);
		
		Vector2 random = new Vector2(MathUtils.cos((float) ((MathUtils.random() * MathUtils.PI * 2f) * Math.sqrt(MathUtils.random()))),
				(float) (MathUtils.sin(MathUtils.random() * MathUtils.PI * 2f) * Math.sqrt(MathUtils.random())));
 
		for (int i = 1; i <= scale; ++i) {
			Vector2 vel = new Vector2(MathUtils.random() * 2 - 1, MathUtils.random() * 2 - 1).scl(4);
			Vector2 offset = new Vector2().set(random).scl(3);			
			FFModel.getFFModel().particleEmitter.particleExplosion(new Vector2(cog.x + offset.x, cog.y + offset.y), vel, type);
		}
		// for large, add this
		if (scale >= 10.f) {
			for (int i = 1; i <= scale; ++i) {
				Vector2 vel = new Vector2().set(random).add(random);
				Vector2 velp = new Vector2().set(vel).scl(i / 20.f * 2.f);
				FFModel.getFFModel().particleEmitter.particleExplosion(cog, velp, type);
			}
		}
	}
	
	public void scaledMassiveExplosion(Vector2 position, float scale, int type) {
		// deltaScale between .4f to 1.2f
		deltaScale = scale / 10.f;
		Vector2 cog = new Vector2(position);
		cog.x += cogOffset;
		cog.y += cogOffset;
		startParticle(cog, deltaScale, type);
		
		Vector2 random = new Vector2(MathUtils.cos((float) ((MathUtils.random() * MathUtils.PI * 2f) * Math.sqrt(MathUtils.random()))),
				(float) (MathUtils.sin(MathUtils.random() * MathUtils.PI * 2f) * Math.sqrt(MathUtils.random())));

		for (int i = 1; i <= scale; ++i) {
			Vector2 vel = new Vector2(MathUtils.random() * 2 - 1, MathUtils.random() * 2 - 1).scl(4);
			Vector2 offset = new Vector2().set(random).scl(3);
			FFModel.getFFModel().particleEmitter.particleExplosion(new Vector2(cog.x + offset.x, cog.y + offset.y), vel, type);
		}
		// for large, add this
		if (scale >= 10.f) {
			for (int i = 1; i <= scale; ++i) {
				Vector2 vel = new Vector2().set(random).add(random);
				Vector2 velp = new Vector2().set(vel).scl(i / 20.f * 2.f);
				FFModel.getFFModel().particleEmitter.particleExplosion(cog, velp, type);
			}
		}
	}
	
	
/*********************************************************************/
	
	private void particleExplosion(Vector2 position, Vector2 velocity, int type) {
		for (int i = 1; i <= 10; ++i) {
			random.set(MathUtils.cos((float) ((MathUtils.random() * MathUtils.PI * 2f) * Math.sqrt(MathUtils.random()))),
					(float) (MathUtils.sin(MathUtils.random() * MathUtils.PI * 2f) * Math.sqrt(MathUtils.random())));

			particleVelocity.set(-velocity.x + random.x, -velocity.y + random.y);
			addParticle(position, particleVelocity, 1f, 1f, type);
		}
	}
	
	private void updateParticle(Particle particle, float deltaTime) {
		delta = Math.min(0.06f, deltaTime);
		// speed things up a bit
		delta *= 2.0f;
		
		if (particle.life > 0) {
			particle.life -= delta;
			particle.position.add(particle.velocity.x * delta * 10, particle.velocity.y * delta * 10);
			particle.velocity.scl((float) Math.pow(damping, delta));
			particle.scale += this.deltaScale * delta / 5f;
		}
	}
	
	private void addParticle(Vector2 position, Vector2 velocity, float life, float scale, int type) {
		// catch for enough particles onscreen already
	     if(particles.size > maxParticle) return;
	     
	     // catch for fps slow devices
	     if(nowFPS < FFScreen.FPS_MIN && !(this instanceof ParticleEmitter)) return;
	     
		 Particle particle = freeParticles.obtain();
		 // add texture types here
		 if (type == IdManager.POWER_CABLE) {
			 particle.setup(GraphicsManager.getGraphicsManager().powerupCharge,
					 position, velocity, life, scale);
		 }
		 else if (type == IdManager.MISSILE_BOMB) {
			 particle.setup(GraphicsManager.getGraphicsManager().bombupCharge,
					 position, velocity, life, scale); 
		 }
		 else {
			 particle.setup(GraphicsManager.getGraphicsManager().explosion,
					 position, velocity, life, scale); 
		 }
	     particles.add(particle);
	}
	
/*********************************************************************/
	
	public void draw(SpriteBatch spriteBatch, float deltaTime) {
		delta = Math.min(0.06f, deltaTime); 
		
		this.setOrigin(0,0);
		for (int i = particles.size - 1; i >= 0; i--) {
			Particle particle = particles.get(i);
			if (particle.life > 0) {
				updateParticle(particle, deltaTime);
				float dx = this.getWidth() / 2 * particle.scale;
				float dy = this.getHeight() / 2 * particle.scale;
				this.setColor(1, 1, 1, Math.max(particle.life / this.life, 0));	
				this.setScale(particle.scale);
				this.setPosition(particle.position.x - dx, particle.position.y - dy);
				if(!(particle.position.y - dy >= -10 && particle.position.y - dy <= 10) &&
						!(particle.position.x - dx >= -10 && particle.position.x - dx <= 10)) {
					
					spriteBatch.draw(particle.texture, particle.position.x, particle.position.y, particle.scale, particle.scale);
					
				} else {
					particle.life = 0;
				}
			} else {
				particles.removeIndex(i);
				freeParticles.free(particle);
			}
		}
		
	}
}
