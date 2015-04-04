package com.ff.ff2.lib;

/*
 * all graphics need to be in powers of two sizes...
 * 32x32, 64x64, 128x128, ...
 * 
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.ff.ff2.gui.FFScreen;

//import android.util.Log;

public class GraphicsManager {
	//private static final String TAG = GraphicsManager.class.getSimpleName();
	//public static AssetManager assetManager;
	
	private static final String mobileAssetFolder = "Mobile";
	//private static final String tabletAssetFolder = "Tablet";
	
	// set a default
	private static String assetFolder = "NOT SET";
	private static boolean loaded = false;
	
	private static String themeName;
	private static String creatorName;
	private static String uiName;
	private static String gameName;
	
	public Texture ff2Logo;
	public Texture ff2LogoHit;
	public Texture playerword;
	public Texture playerwordHit;
	public Texture turretword;
	public Texture turretwordHit;
	public Texture fenceword;
	public Texture fencewordHit;
	public Texture barrierword;
	public Texture barrierwordHit;
	public Texture gatewayword;
	public Texture gatewaywordHit;
	public Texture powerupword;
	public Texture powerupwordHit;
	public Texture switchword;
	public Texture switchwordHit;
	public Texture enemyfighterword;
	public Texture enemyfighterwordHit;
	public Texture mineword;
	public Texture minewordHit;
	public Texture pushblockword;
	public Texture pushblockwordHit;
	public Texture bombupword;
	public Texture bombupwordHit;

	public Texture soundonbutton;
	public Texture soundoffbutton;
	public Texture createbutton;
	public Texture nocreatebutton;
	public Texture menubutton;
	public Texture nomenubutton;
	public BitmapFont exoFFfont;
	
	// creator 
	public Texture exitbutton;
	public Texture filebutton;
	public Texture toolsbutton;
	public Texture nextbutton;
	public Texture prevbutton;
	public Texture deletebutton;
	public Texture yesbutton;
	public Texture nobutton;
	public Texture buildbutton;
	public Texture procbutton;
	public Texture randbutton;
	public Texture arenabutton;
	public Texture groupsbutton;
	public Texture fileExitButton;
	public Texture fileLoadButton;
	public Texture fileSaveButton;
	public Texture filePlayButton;
	public Texture fileNoPlayButton;
	public Texture fileSendButton;
	public Texture toasterBack;
	public Texture windowBack;
	public Texture emptyCellTexture;
	public Texture createSplashTexture;
	
	public Texture playerDef;
	public Texture gatewayDef ;
	public Texture powerupDef;
	public Texture bombupDef;
	public Texture switchDef;
	public Texture scaffoldDef;
	public Texture barrierDef;
	public Texture fenceDef;
	public Texture turretDef;
	public Texture enemyDef;
	public Texture mineDef;
	public Texture pushblockDef;
	public Texture hangerDef;
	
	public Texture deleteDef;
	public Texture deleteSel;
	public Texture lockUnlock;
	public Texture lockLock;
	public Texture toolSelect;
	
	// ui
	public Texture loaderTexture;
	public Texture gameOverTexture;
	public Texture nextFieldTexture;
	public Texture diffEasyButton;
	public Texture diffNormButton;
	public Texture diffHardButton;
	public Texture osdButton;
	
	// game
	public Texture playerShipTexture;
	public Texture playerShipPowerupTexture;
	public Texture playerShipBombupTexture;
	public Texture enemyShipTexture;
	public Texture enemyShipPowerupTexture;
	public Texture enemyShipBombupTexture;
	public Texture enemyShipOrderedTexture;
	public Texture playerBulletTexture;
	public Texture playerCableTexture;
	public Texture enemyBulletTexture;
	public Texture enemyCableTexture;
	public Texture bombCableTexture;
	public Texture turretTexture;
	public Texture turrethitTexture;
	public Texture fenceTexture;
	public Texture fencehitTexture;
	public Texture barrierTexture;
	public Texture barrierhitTexture;
	public Texture gatewayonTexture;
	public Texture gatewayoffTexture;
	public Texture powerupTexture;	
	public Texture bombupTexture;	
	public Texture explosion;
	public Texture death;
	public Texture powerupCharge;
	public Texture bombupCharge;
	public Texture switchonTexture;
	public Texture switchoffTexture;
	public Texture scaffoldTexture;
	public Texture mineTexture;
	public Texture minehitTexture;
	public Texture mineDebrisTexture;
	public Texture pushblockTexture;
	public Texture hangerTexture;
	public Texture hangerhitTexture;
	
	public static GraphicsManager graphicsManager;	
	
	public static GraphicsManager getGraphicsManager() {
		if (graphicsManager == null) {
			graphicsManager = new GraphicsManager();
		}
		return graphicsManager;
	}
	
	public GraphicsManager() {
		//
	}
	
	public void destroy() {
		dispose();
		graphicsManager = null;
	}
	
	public void loadGraphicAssets(int deviceType) {
		// boolean loaded to prevent GL out of memory errors
		// and null pointer errors for loading assets
		if (loaded) {
			// skip as already loaded
			//Log.d(TAG, "already loaded...");
		}
		else {
			assetFolder = mobileAssetFolder;
			//TODO
			/*
			 * rem'd for getting app to store...
			 * -testing
			 * 
			if (deviceType == FFScreen.MOBILE) {
				assetFolder = mobileAssetFolder;
			}
			else if (deviceType == FFScreen.TABLET) {
				assetFolder = tabletAssetFolder;
			}
			else {
				// deviceType == NO_DEVICE, fallback
				assetFolder = mobileAssetFolder;
			}
			*/
			setAssetFolders();
			graphicsLoader();
		}		
	}
	
	public Texture getIDTextureCell(char id, boolean icon) {
		switch (id) {
			case IdManager.EMPTY:
				//if (icon) return emptyCellTexture;
				return emptyCellTexture;
	
			case IdManager.PRIVATE_FIGHTER:
				if (icon) return playerDef; 
				return playerShipTexture;
	
			case IdManager.POWERUP:
				if (icon) return powerupDef;
				return powerupTexture;

			case IdManager.GATEWAY:
				if (icon) return gatewayDef;
				return gatewayoffTexture;
		
			case IdManager.SWITCH:
				if (icon) return switchDef;
				return switchoffTexture;
	
			case IdManager.SCAFFOLD:
				if (icon) return scaffoldDef;
				return scaffoldTexture;
	
			case IdManager.BARRIER:
				if (icon) return barrierDef;
				return barrierTexture;

			case IdManager.FENCE:
				if (icon) return fenceDef;
				return fenceTexture;
	
			case IdManager.TURRET:
				if (icon) return turretDef;
				return turretTexture;

			case IdManager.DRONE:
				if (icon) return enemyDef;
				return enemyShipTexture;
	
			case IdManager.DELETE:
				if (icon) return deleteDef;
				return deleteSel;
			
			case IdManager.LOCK:
				if (icon) return lockUnlock;
				return lockLock;

			case IdManager.MINE:
				if (icon) return mineDef;
				return mineTexture;
				
			case IdManager.PUSHBLOCK:
				if (icon) return pushblockDef;
				return pushblockTexture;

			case IdManager.BOMBUP:
				if (icon) return bombupDef;
				return bombupTexture;

			case IdManager.HANGER:
				if (icon) return hangerDef;
				return hangerTexture;
				
			default:
				if (icon) return toolSelect;
				return emptyCellTexture;
		}		
	}
	
	public Texture getIDButtonTexture(int id) {
		switch (id) {
			case IdManager.BTN_DEFAULT:
				//TODO
				// none yet...
				return null;
				
			case IdManager.BTN_SOUND:
				return soundonbutton;
				
			case IdManager.BTN_NO_SOUND:
				return soundoffbutton;
	
			case IdManager.BTN_CREATOR:
				return createbutton;
				
			case IdManager.BTN_NO_CREATOR:
				return nocreatebutton;
		
			case IdManager.BTN_EXIT:
				return exitbutton;
		
			case IdManager.BTN_FILE:
				return filebutton;
	
			case IdManager.BTN_FILE_EXIT:
				return fileExitButton;
		
			case IdManager.BTN_FILE_SAVE:
				return fileSaveButton;
		
			case IdManager.BTN_FILE_SEND:
				return fileSendButton;
		
			case IdManager.BTN_FILE_LOAD:
				return fileLoadButton;
	
			case IdManager.BTN_FILE_PLAY:
				return filePlayButton;
				
			case IdManager.BTN_FILE_NO_PLAY:
				return fileNoPlayButton;
		
			case IdManager.BTN_TOOL:
				return toolsbutton;
		
			case IdManager.BTN_NEXT:
				return nextbutton;
		
			case IdManager.BTN_PREV:
				return prevbutton;
		
			case IdManager.BTN_DELETE:
				return deletebutton;
	
			case IdManager.BTN_YES:
				return yesbutton;

			case IdManager.BTN_NO:
				return nobutton;
				
			case IdManager.BTN_BUILD:
				return buildbutton;
				
			case IdManager.BTN_PROC:
				return procbutton;
				
			case IdManager.BTN_RAND:
				return randbutton;
				
			case IdManager.BTN_GROUPS:
				return groupsbutton;
				
			case IdManager.BTN_ARENA:
				return arenabutton;
				
			case IdManager.BTN_DIFF_EASY:
				return diffEasyButton;
			
			case IdManager.BTN_DIFF_NORM:
				return diffNormButton;
			
			case IdManager.BTN_DIFF_HARD:
				return diffHardButton;
				
			case IdManager.BTN_MENU:
				return menubutton;
				
			case IdManager.BTN_NO_MENU:
				return nomenubutton;
				
			case IdManager.BTN_OSD:
				return osdButton;
				
			default:
				//TODO
				return null;
		}
	}
	
	public Texture getIDWordTexture(int id, boolean hit) {
		switch (id) {
			case IdManager.WORD_LOGO:
				if (hit) return ff2LogoHit;
				return ff2Logo;
		
			case IdManager.WORD_PLAYER:
				if (hit) return playerwordHit;
				return playerword;
		
			case IdManager.WORD_TURRET:
				if (hit) return turretwordHit;
				return turretword;
	
			case IdManager.WORD_FENCE:
				if (hit) return fencewordHit;
				return fenceword;
	
			case IdManager.WORD_BARRIER:
				if (hit) return barrierwordHit;
				return barrierword;
		
			case IdManager.WORD_GATEWAY:
				if (hit) return gatewaywordHit;
				return gatewayword;
		
			case IdManager.WORD_POWERUP:
				if (hit) return powerupwordHit;
				return powerupword;
	
			case IdManager.WORD_SWITCH:
				if (hit) return switchwordHit;
				return switchword;
		
			case IdManager.WORD_ENEMY:
				if (hit) return enemyfighterwordHit;
				return enemyfighterword;
	
			case IdManager.WORD_MINE:
				if (hit) return minewordHit;
				return mineword;
				
			case IdManager.WORD_PUSHBLOCK:
				if (hit) return pushblockwordHit;
				return pushblockword;
			
			case IdManager.WORD_BOMBUP:
				if (hit) return bombupwordHit;
				return bombupword;

			default:
				//TODO
				return null;
		}
	}
	
/*********************************************************************/
	
	private void setAssetFolders() {
		themeName = assetFolder + "/ff2-9";
		creatorName = themeName + "/creator";
		uiName = themeName + "/ui";
		gameName = themeName +"/game";
	}
	
	private void graphicsLoader() {
		//options.inPreferredConfig = Bitmap.Config.RGB_565;
		// start menu stuff
		ff2Logo = new Texture(Gdx.files.internal(uiName + "/ff2logo.png"));
		ff2LogoHit = new Texture(Gdx.files.internal(uiName + "/ff2logohit.png"));
		playerword = new Texture(Gdx.files.internal(uiName + "/playerword.png"));
		playerwordHit = new Texture(Gdx.files.internal(uiName + "/playerwordhit.png"));
		turretword = new Texture(Gdx.files.internal(uiName + "/turretword.png"));
		turretwordHit = new Texture(Gdx.files.internal(uiName + "/turretwordhit.png"));
		fenceword = new Texture(Gdx.files.internal(uiName + "/fenceword.png"));
		fencewordHit = new Texture(Gdx.files.internal(uiName + "/fencewordhit.png"));
		barrierword = new Texture(Gdx.files.internal(uiName + "/barrierword.png"));
		barrierwordHit = new Texture(Gdx.files.internal(uiName + "/barrierwordhit.png"));
		gatewayword = new Texture(Gdx.files.internal(uiName + "/gatewayword.png"));
		gatewaywordHit = new Texture(Gdx.files.internal(uiName + "/gatewaywordhit.png"));
		powerupword = new Texture(Gdx.files.internal(uiName + "/powerupword.png"));
		powerupwordHit = new Texture(Gdx.files.internal(uiName + "/powerupwordhit.png"));
		switchword = new Texture(Gdx.files.internal(uiName + "/switchword.png"));
		switchwordHit = new Texture(Gdx.files.internal(uiName + "/switchwordhit.png"));
		enemyfighterword = new Texture(Gdx.files.internal(uiName + "/enemyfighterword.png"));
		enemyfighterwordHit = new Texture(Gdx.files.internal(uiName + "/enemyfighterwordhit.png"));
		mineword = new Texture(Gdx.files.internal(uiName + "/mineword.png"));
		minewordHit = new Texture(Gdx.files.internal(uiName + "/minewordhit.png"));
		pushblockword = new Texture(Gdx.files.internal(uiName + "/pushblockword.png"));
		pushblockwordHit = new Texture(Gdx.files.internal(uiName + "/pushblockwordhit.png"));
		bombupword = new Texture(Gdx.files.internal(uiName + "/bombupword.png"));
		bombupwordHit = new Texture(Gdx.files.internal(uiName + "/bombupwordhit.png"));
		
		// ui
		soundonbutton = new Texture(Gdx.files.internal(uiName + "/soundon.png"));
		soundoffbutton = new Texture(Gdx.files.internal(uiName + "/soundoff.png"));
		createbutton = new Texture(Gdx.files.internal(uiName + "/createbutton.png"));
		nocreatebutton = new Texture(Gdx.files.internal(uiName + "/nocreatebutton.png"));
		loaderTexture = new Texture(Gdx.files.internal(uiName + "/ff2loader.png"));
		gameOverTexture = new Texture(Gdx.files.internal(uiName + "/gameover.png"));
		nextFieldTexture = new Texture(Gdx.files.internal(uiName + "/nextfield.png"));
		
		menubutton = new Texture(Gdx.files.internal(uiName + "/menubutton.png"));
		nomenubutton = new Texture(Gdx.files.internal(uiName + "/nomenubutton.png"));
		diffEasyButton = new Texture(Gdx.files.internal(uiName + "/diffeasybutton.png"));
		diffNormButton = new Texture(Gdx.files.internal(uiName + "/diffnormbutton.png"));
		diffHardButton = new Texture(Gdx.files.internal(uiName + "/diffhardbutton.png"));
		osdButton = new Texture(Gdx.files.internal(uiName + "/osdbutton.png"));
		
		exoFFfont = new BitmapFont(Gdx.files.internal(uiName + "/font/exo-ff.fnt"), true);
		
		// creator
		exitbutton = new Texture(Gdx.files.internal(creatorName + "/exitbutton.png"));
		filebutton = new Texture(Gdx.files.internal(creatorName + "/filebutton.png"));
		toolsbutton = new Texture(Gdx.files.internal(creatorName + "/toolsbutton.png"));
		nextbutton = new Texture(Gdx.files.internal(creatorName + "/nextbutton.png"));
		prevbutton = new Texture(Gdx.files.internal(creatorName + "/prevbutton.png"));
		deletebutton = new Texture(Gdx.files.internal(creatorName + "/deletebutton.png"));
		yesbutton = new Texture(Gdx.files.internal(creatorName + "/yesbutton.png"));
		nobutton = new Texture(Gdx.files.internal(creatorName + "/nobutton.png"));
		buildbutton = new Texture(Gdx.files.internal(creatorName + "/buildbutton.png"));
		procbutton = new Texture(Gdx.files.internal(creatorName + "/procbutton.png"));
		randbutton = new Texture(Gdx.files.internal(creatorName + "/randbutton.png"));
		groupsbutton = new Texture(Gdx.files.internal(creatorName + "/groupsbutton.png"));
		arenabutton = new Texture(Gdx.files.internal(creatorName + "/arenabutton.png"));
		fileExitButton = new Texture(Gdx.files.internal(creatorName + "/fileexitbutton.png"));
		fileLoadButton = new Texture(Gdx.files.internal(creatorName + "/loadbutton.png"));
		fileSaveButton = new Texture(Gdx.files.internal(creatorName + "/savebutton.png"));
		filePlayButton = new Texture(Gdx.files.internal(creatorName + "/playbutton.png"));
		fileNoPlayButton = new Texture(Gdx.files.internal(creatorName + "/noplaybutton.png"));
		fileSendButton = new Texture(Gdx.files.internal(creatorName + "/sendbutton.png"));
		toasterBack = new Texture(Gdx.files.internal(creatorName + "/toasterback.png"));
		windowBack = new Texture(Gdx.files.internal(creatorName + "/windowback.png"));
		emptyCellTexture = new Texture(Gdx.files.internal(creatorName + "/emptycell.png"));
		createSplashTexture = new Texture(Gdx.files.internal(creatorName + "/createsplash.png"));
		
		playerDef = new Texture(Gdx.files.internal(creatorName + "/player_def.png"));
		gatewayDef = new Texture(Gdx.files.internal(creatorName + "/gateway_def.png"));
		powerupDef = new Texture(Gdx.files.internal(creatorName + "/powerup_def.png"));
		bombupDef = new Texture(Gdx.files.internal(creatorName + "/bombup_def.png"));
		switchDef = new Texture(Gdx.files.internal(creatorName + "/switch_def.png"));
		scaffoldDef = new Texture(Gdx.files.internal(creatorName + "/scaffold_def.png"));
		barrierDef = new Texture(Gdx.files.internal(creatorName + "/barrier_def.png"));
		fenceDef = new Texture(Gdx.files.internal(creatorName + "/fence_def.png"));
		turretDef = new Texture(Gdx.files.internal(creatorName + "/turret_def.png"));
		enemyDef = new Texture(Gdx.files.internal(creatorName + "/enemy_def.png"));
		mineDef = new Texture(Gdx.files.internal(creatorName + "/mine_def.png"));
		pushblockDef = new Texture(Gdx.files.internal(creatorName + "/pushblock_def.png"));
		hangerDef = new Texture(Gdx.files.internal(creatorName + "/hanger_def.png"));
		
		deleteDef = new Texture(Gdx.files.internal(creatorName + "/delete_def.png"));
		deleteSel = new Texture(Gdx.files.internal(creatorName + "/delete_sel.png"));
		lockUnlock = new Texture(Gdx.files.internal(creatorName + "/lock_unlock.png"));
		lockLock = new Texture(Gdx.files.internal(creatorName + "/lock_lock.png"));
		toolSelect = new Texture(Gdx.files.internal(creatorName + "/toolselect.png"));
		
		// game stuff
		playerShipTexture = new Texture(Gdx.files.internal(gameName + "/player2ship.png"));
		playerShipPowerupTexture = new Texture(Gdx.files.internal(gameName + "/player2shippowerup.png"));
		playerShipBombupTexture = new Texture(Gdx.files.internal(gameName + "/player2shipbombup.png"));
		enemyShipTexture = new Texture(Gdx.files.internal(gameName + "/enemy2ship.png"));
		enemyShipPowerupTexture = new Texture(Gdx.files.internal(gameName + "/enemy2shippowerup.png"));
		enemyShipBombupTexture = new Texture(Gdx.files.internal(gameName + "/enemy2shipbombup.png"));
		enemyShipOrderedTexture = new Texture(Gdx.files.internal(gameName + "/enemy2shipordered.png"));
		playerBulletTexture = new Texture(Gdx.files.internal(gameName + "/playerlaser.png"));
		playerCableTexture = new Texture(Gdx.files.internal(gameName + "/playercable.png"));
		enemyBulletTexture = new Texture(Gdx.files.internal(gameName + "/enemylaser.png"));
		enemyCableTexture = new Texture(Gdx.files.internal(gameName + "/enemycable.png"));
		bombCableTexture = new Texture(Gdx.files.internal(gameName + "/bombcable.png"));
		turretTexture = new Texture(Gdx.files.internal(gameName + "/turret.png"));
		turrethitTexture = new Texture(Gdx.files.internal(gameName + "/turrethit.png"));	
		fenceTexture = new Texture(Gdx.files.internal(gameName + "/fence.png"));
		fencehitTexture = new Texture(Gdx.files.internal(gameName + "/fencehit.png"));
		barrierTexture = new Texture(Gdx.files.internal(gameName + "/barrier.png"));
		barrierhitTexture = new Texture(Gdx.files.internal(gameName + "/barrierhit.png"));
		gatewayonTexture = new Texture(Gdx.files.internal(gameName + "/gatewayon.png"));
		gatewayoffTexture = new Texture(Gdx.files.internal(gameName + "/gatewayoff.png"));
		powerupTexture = new Texture(Gdx.files.internal(gameName + "/powerup.png"));
		bombupTexture = new Texture(Gdx.files.internal(gameName + "/bombup.png"));
		bombupCharge = new Texture(Gdx.files.internal(gameName + "/bombupcharge.png"));
		explosion = new Texture(Gdx.files.internal(gameName + "/explosion.png"));
		death = new Texture(Gdx.files.internal(gameName + "/death.png"));
		powerupCharge = new Texture(Gdx.files.internal(gameName + "/powerupcharge.png"));
		switchonTexture = new Texture(Gdx.files.internal(gameName + "/switchon.png"));
		switchoffTexture = new Texture(Gdx.files.internal(gameName + "/switchoff.png"));
		scaffoldTexture = new Texture(Gdx.files.internal(gameName + "/scaffold.png"));
		mineTexture = new Texture(Gdx.files.internal(gameName + "/mine.png"));
		minehitTexture = new Texture(Gdx.files.internal(gameName + "/minehit.png"));
		mineDebrisTexture = new Texture(Gdx.files.internal(gameName + "/minedebris.png"));
		pushblockTexture = new Texture(Gdx.files.internal(gameName + "/pushblock.png"));
		hangerTexture  = new Texture(Gdx.files.internal(gameName + "/hanger.png"));
		hangerhitTexture  = new Texture(Gdx.files.internal(gameName + "/hangerhit.png"));
		// have a catch here for the loaded=true?
		loaded = true;
	}
	
/*********************************************************************/
	
	public void dispose() {	
		ff2Logo.dispose();
		playerword.dispose();
		fenceword.dispose();
		turretword.dispose();
		barrierword.dispose();
		gatewayword.dispose();
		powerupword.dispose();
		switchword.dispose();
		
		ff2LogoHit.dispose();
		playerwordHit.dispose();
		fencewordHit.dispose();
		turretwordHit.dispose();
		barrierwordHit.dispose();
		gatewaywordHit.dispose();
		powerupwordHit.dispose();
		switchwordHit.dispose();
		enemyfighterword.dispose();
		enemyfighterwordHit.dispose();
		mineword.dispose();
		minewordHit.dispose();
		pushblockword.dispose();
		pushblockwordHit.dispose();
		bombupword.dispose();
		bombupwordHit.dispose();
		
		soundonbutton.dispose();
		soundoffbutton.dispose();
		createbutton.dispose();
		nocreatebutton.dispose();
		menubutton.dispose();
		nomenubutton.dispose();
		
		exitbutton.dispose();
		filebutton.dispose();
		toolsbutton.dispose();
		nextbutton.dispose();
		prevbutton.dispose();
		deletebutton.dispose();
		yesbutton.dispose();
		nobutton.dispose();
		buildbutton.dispose();
		procbutton.dispose();
		randbutton.dispose();
		groupsbutton.dispose();
		arenabutton.dispose();
		fileExitButton.dispose();
		fileLoadButton.dispose();
		fileSaveButton.dispose();
		filePlayButton.dispose();
		fileNoPlayButton.dispose();
		fileSendButton.dispose();
		toasterBack.dispose();
		windowBack.dispose();
		createSplashTexture.dispose();
		playerDef.dispose();
		gatewayDef.dispose();
		powerupDef.dispose();
		bombupDef.dispose();
		switchDef.dispose();
		scaffoldDef.dispose();
		barrierDef.dispose();
		fenceDef.dispose();
		turretDef.dispose();
		enemyDef.dispose();
		mineDef.dispose();
		pushblockDef.dispose();
		hangerDef.dispose();
		
		deleteSel.dispose();
		deleteDef.dispose();
		lockLock.dispose();
		lockUnlock.dispose();
		toolSelect.dispose();
		diffEasyButton.dispose();
		diffNormButton.dispose();
		diffHardButton.dispose();
		osdButton.dispose();
		exoFFfont.dispose();
		
		emptyCellTexture.dispose();
		playerShipTexture.dispose();
		playerShipPowerupTexture.dispose();
		playerShipBombupTexture.dispose();
		enemyShipTexture.dispose();
		enemyShipPowerupTexture.dispose();
		enemyShipBombupTexture.dispose();
		enemyShipOrderedTexture.dispose();
		playerBulletTexture.dispose();
		playerCableTexture.dispose();
		enemyBulletTexture.dispose();
		enemyCableTexture.dispose();
		bombCableTexture.dispose();
		turretTexture.dispose();
		turrethitTexture.dispose();	
		fenceTexture.dispose();
		fencehitTexture.dispose();
		barrierTexture.dispose();
		barrierhitTexture.dispose();
		gatewayonTexture.dispose();
		gatewayoffTexture.dispose();
		powerupTexture.dispose();
		bombupTexture.dispose();
		explosion.dispose();
		death.dispose();
		powerupCharge.dispose();
		bombupCharge.dispose();
		switchonTexture.dispose();
		switchoffTexture.dispose();
		scaffoldTexture.dispose();
		mineTexture.dispose();
		minehitTexture.dispose();
		mineDebrisTexture.dispose();
		pushblockTexture.dispose();
		hangerTexture.dispose();
		hangerhitTexture.dispose();
		
		loaderTexture.dispose();
		gameOverTexture.dispose();
		nextFieldTexture.dispose();
		loaded = false;
	}
}