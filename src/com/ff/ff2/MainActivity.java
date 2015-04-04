package com.ff.ff2;

/*
 * 
 *  FlickFighter 2 - MainActivity
 *  libGDX port
 *  
 *  min SDK 10 (2.3.3)
 * 
 */

import android.os.Bundle;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.ff.ff2.eng.FFGame;

public class MainActivity extends AndroidApplication {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = false;
        initialize(new FFGame(), cfg);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    
    @Override
	// always called
	protected void onResume() {
		super.onResume();
    }
}