package com.ff.ff2.lib;

//import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.ail.DemoAI;
import com.ff.ff2.eng.FFModel;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.obs.Wordblock;


public class DemoManager {
	//private static final String TAG = DemoManager.class.getSimpleName();
	private DemoAI demoAI;
	private static final float AI_PAUSE_TIME = 1.5f;
	private float AIautoTime = 0;
	
	public static DemoManager demoManager;	
	
	public static DemoManager getDemoManager() {
		if (demoManager == null) {
			demoManager = new DemoManager();
		}
		return demoManager;
	}
	
	public DemoManager() {
		demoAI = new DemoAI();
	}
	
	public void refresh() {
		loadStartWords();
		demoAI.initDemoAI(FFModel.getFFModel().attackerPrivate.fighter);
	}
	
	public void destroy() {
		demoAI.destroy();
		demoManager = null;
	}
	
/*********************************************************************/
	
	public void autoMoveFighter(float deltaTime) {
		AIautoTime += deltaTime;

		if (AIautoTime >= AI_PAUSE_TIME) {	
			demoAI.updateIntel();
			// move first
			demoAI.updatePosition(FFModel.getFFModel().field.gatewayActivated);
			demoAI.fireWeapon();

			AIautoTime -= AI_PAUSE_TIME;
		}
		
		FFModel.getFFModel().attackerPrivate.fighter.AIflight(deltaTime);
		Collision.collisionFighterCheck(FFModel.getFFModel().attackerPrivate.fighter);
	}
	
/*********************************************************************/	
	
	private void loadStartWords() {
		// create logo
		float cellSize = FFScreen.gameCellSize * 2;
		Vector2 wordVector = new Vector2(
				FFScreen.gameCellSize * 3.5f,
				FFScreen.gameCellSize * 1.5f);
		
		FFModel.getFFModel().field.addObstacleToField(new Wordblock(
				IdManager.WORD_LOGO, wordVector));

		// set for main words list
		wordVector.set(wordVector.x, wordVector.y - (FFScreen.gameCellSize / 2));
		
		wordVector.set(wordVector.x + cellSize * 2, wordVector.y + cellSize * 2);
		FFModel.getFFModel().field.addObstacleToField(new Wordblock(
				IdManager.WORD_BOMBUP, wordVector));	

		wordVector.set(wordVector.x, wordVector.y + cellSize);
		FFModel.getFFModel().field.addObstacleToField(new Wordblock(
				IdManager.WORD_TURRET, wordVector));
		
		wordVector.set(wordVector.x, wordVector.y + cellSize);
		FFModel.getFFModel().field.addObstacleToField(new Wordblock(
				IdManager.WORD_FENCE, wordVector));
		
		wordVector.set(wordVector.x, wordVector.y + cellSize);
		FFModel.getFFModel().field.addObstacleToField(new Wordblock(
				IdManager.WORD_BARRIER, wordVector));
		
		wordVector.set(wordVector.x, wordVector.y + cellSize);
		FFModel.getFFModel().field.addObstacleToField(new Wordblock(
				IdManager.WORD_MINE, wordVector));
		
		wordVector.set(wordVector.x, wordVector.y + cellSize);
		FFModel.getFFModel().field.addObstacleToField(new Wordblock(
				IdManager.WORD_GATEWAY, wordVector));
		
		wordVector.set(wordVector.x, wordVector.y + cellSize);
		FFModel.getFFModel().field.addObstacleToField(new Wordblock(
				IdManager.WORD_POWERUP, wordVector));
		
		wordVector.set(wordVector.x, wordVector.y + cellSize);
		FFModel.getFFModel().field.addObstacleToField(new Wordblock(
				IdManager.WORD_SWITCH, wordVector));

		wordVector.set(wordVector.x, wordVector.y + cellSize);
		FFModel.getFFModel().field.addObstacleToField(new Wordblock(
				IdManager.WORD_PUSHBLOCK, wordVector));
				
	}
}