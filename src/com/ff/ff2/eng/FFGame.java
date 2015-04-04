package com.ff.ff2.eng;

/*
 * 
 *  FlickFighter 2
 *  libGDX port
 *  
 *  min SDK 10 (2.3.3)
 *  
 *  this is the main starting class,
 *  calls and sets a Screen class to 
 *  do the running and rendering
 * 
 */

import android.util.Log;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.ff.ff2.ctr.FFCreator;
import com.ff.ff2.gui.FFScreen;

public class FFGame extends Game {
	private static final String TAG = FFGame.class.getSimpleName();
	private FFScreen ffScreen;
	private FFCreator ffCreator;
	
	public void requestMode(boolean creator) {
		if (creator) {
			Log.i(TAG, "requestMode ffCreator.");
			
			ffCreator = new FFCreator(this, FFScreen.DEBUG);
			setScreen(ffCreator);
		}
		else {
			Log.i(TAG, "requestMode ffScreen.");
			if (ffCreator != null) ffCreator.destroy();
			setScreen(ffScreen);
		}
	}
	
	@Override
	public void create() {	
		Log.i(TAG, "create called");
		ffScreen = new FFScreen(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), this);
		requestMode(false); //boolean creator
	}
	
	@Override
	public void pause() {
		//TODO
		// called with home button press - app to background
		Log.i(TAG, "pause called");
	}
	
	@Override
	public void resume() {
		//TODO
		// called when coming to foreground from pause state
		Log.i(TAG, "resume called");
	}

	@Override
	public void dispose() {
		// called with back button press - app destroyed
		Log.i(TAG, "dispose called");
		ffScreen.dispose();
		if (ffCreator != null) ffCreator.destroy();
	}
}
