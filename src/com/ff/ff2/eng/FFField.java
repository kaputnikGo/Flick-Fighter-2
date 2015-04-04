package com.ff.ff2.eng;

import android.util.Log;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ff.ff2.lev.Fortress;
import com.ff.ff2.lib.LoadManager;
import com.ff.ff2.obs.Obstacle;

public class FFField {
	private static final String TAG = FFField.class.getSimpleName();
	public boolean gatewayActivated;
		
	private Array<String> gameFortList;
	private Array<String> userFortList;
	
	public Fortress fortress;
	
	public FFField() {	
		gatewayActivated = false;	
		fortress = new Fortress();
		loadFortLists();
	}
	
	public void reset() {
		fortress.reset();	
		gatewayActivated = false;
	}
	
	public void destroy() {
		fortress.destroy();
		if (gameFortList != null) gameFortList = null;
		if (userFortList != null) userFortList = null;
	}
	
/*********************************************************************/
	
	public int loadFortress(String fortressName, int location) {
		return fortress.loadFortress(fortressName, location);
	}
	
	public String getNextFortressName(int next, int location) {
		// this returns full path name...
		if (location == LoadManager.GAME_STORAGE) {
			return gameFortList.get(next);	
		}
		else {
			if (userFortList != null) 
				return userFortList.get(next);
			else {
				// in case
				return gameFortList.get(next);
			}
		}
	}
	
	public int getUserFortListSize() {
		if (userFortList != null) 
			return userFortList.size;
		else 
			return 0;
	}
	
	public int getGameFortListSize() {
		if (gameFortList != null) 
			return gameFortList.size;
		else
			return 0;
	}
	
/*********************************************************************/	
	
	public void addObstacleToField(Obstacle obstacle) {
		fortress.addObstacleToField(obstacle);
	}
	
	public Vector2 getSwitchPosition() {
		return fortress.switchVector;
	}
	
	public Vector2 getPowerupPosition() {
		return fortress.powerupVector;
	}
	
	public Vector2 getGatewayPosition() {
		return fortress.gatewayVector;
	}
	
	public void gatewayActivated() {
		gatewayActivated ^= true;
		fortress.gatewayActivated(gatewayActivated);
	}
	
	public void createSpawner() {
		fortress.createSpawner();
	}
	
	public void spawnSpawner() {
		fortress.spawnSpawner(); 
	}
	
	
/*********************************************************************/
	
	private void loadFortLists() {	
		// TODO send to status on error?
		int result = LoadManager.getLoadManager().loadFortressNamesList(LoadManager.GAME_STORAGE);
		if (result == LoadManager.FILE_LOADED) {
			gameFortList = new Array<String>();
			gameFortList = LoadManager.getLoadManager().getFortressNamesList();
		}
		else {
			Log.d(TAG, "load game fort list result: " + result);
		}
		
		result = LoadManager.getLoadManager().loadFortressNamesList(LoadManager.USER_STORAGE);
		if (result == LoadManager.FILE_LOADED) {
			userFortList = new Array<String>();
			userFortList = LoadManager.getLoadManager().getFortressNamesList();
		}
		else {
			Log.d(TAG, "load user fort list result: " + result);
		}
	}
	
/*********************************************************************/	

	public void draw(SpriteBatch gameBatch, float deltaTime) {
		fortress.draw(gameBatch, deltaTime);
	}
}