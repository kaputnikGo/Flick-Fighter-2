package com.ff.ff2.gui;

import com.badlogic.gdx.Gdx;

//import android.util.Log;

public class ScreenUtils {
	//private static final String TAG = ScreenUtils.class.getSimpleName();
	// width always is width, regardless of orientation
	// so if user has tablet in landscape (even though game fixed to portrait)
	// width will be the left to right dimension (y in landscape held)
	
	public static ScreenUtils screenUtils;
	public static float deviceDensity;
	public static int factoredWidth;
	public static int factoredHeight;
	public static float factoredAspectRatio;
	
	public static int factoredCellSize;
	public static int factoredCellsWide;
	public static int factoredCellsHigh;
	public static int screenType;
	
	public static final int NO_DEVICE = 0;
	public static final int MOBILE = 1;
	public static final int TABLET = 2;
	public static final int SCREEN_SMALL_ERROR = 33;
	public static final int SCREEN_ASPECT_ERROR = 34;
	public static int refactorError;

//desired screen dimensions
	// s4 mini : 540 x 960px
	// SE xperia 480 x 854px
	
	public static final int optimumWidth = 480;
	public static final int optimumHeight = 854;
	public static final int optimumHeight2 = 800;
	public static final int optimumCellSize = 32;
	public static final int optimumCellsWide = 15;
	public static final int optimumCellsHigh = 25;
	// 16:9  = 1.7
	// 16:10, 5:3 = 1.6
	public static final float optimumAspectRatio = 1.6f;
	public static final float optimumAspectRatio2 = 1.7f;
	
	private static int virtualWidth;
	private static int virtualHeight;
	
	
	public static ScreenUtils getScreenUtils() {
		if (screenUtils == null) {
			screenUtils = new ScreenUtils();
		}
		return screenUtils;
	}
	
	public ScreenUtils() {
		//
	}
	
	/*
	 * screen density (approx values):
	 * ldpi (low) = 120dpi
	 * mdpi (med) = 160dpi
	 * hdpi (hi) = 240dpi
	 * xdpi (ex.hi) = 320dpi
	 * 
	 * libgdx getDensity returns value in relation to 160 dpi
	 * hence Defy get density of 1.5 as 240 / 160 = 1.5
	 * 
	 *  use the 3:4:6:8 scaling ratio
	 *  so for a given base image of 12x12 pixels:
	 *  
	 *  36x36 for low-density
	 *	48x48 for medium-density
	 *	72x72 for high-density
	 *	96x96 for extra high-density
	 *
	 *
	 * 
	 */
	
/*****************************************************************/	
	
	public static void factorScreenSize(int deviceWidthIn, int deviceHeightIn) {
		// set to defaults first
		deviceDensity = Gdx.graphics.getDensity();
		// ldpi = 0.75
		// mdpi = 1.0
		// hdpi = 1.5
		// xhdpi = 2.0
		
		setDefaults(deviceWidthIn, deviceHeightIn);	
		
		if (screenType == NO_DEVICE) {
			// screenType says: no device
			// or too small
			// will be caught by FFScreen
		}
		
		else if (screenType == MOBILE) {
			// OPTIMUM PHONE
			if (virtualWidth == optimumWidth && virtualHeight == optimumHeight) {
				// 33.7% - FWVGA - 16:9
				factoredWidth = optimumWidth;
				factoredHeight = optimumHeight;
			}
			// 
			else if (virtualWidth == optimumWidth && virtualHeight == optimumHeight2) {
				// 33.7% - WVGA - 5:3
				factoredWidth = optimumWidth;
				factoredHeight = optimumHeight2;
				factoredCellsHigh = optimumCellsHigh - 1;
			}					
			// ABOVE OPTIMUM PHONES	
			else if (virtualWidth > optimumWidth && virtualHeight > optimumHeight) {
				// 19.9% - DVGA - 3:2
				// 4.3% - WSVGA - 16:9
				// 4.3% - WSVGA - 15:9 to 16:9
				factoredWidth = optimumWidth;
				factoredHeight = optimumHeight;	
			}
			factorAspectRatio(deviceWidthIn, deviceHeightIn);
		}
				
		else if (screenType == TABLET) {	
			//OPTIMUM TABLET
			if (virtualWidth == 768 && virtualHeight == 1024) {
				// 4.3% - XGA - 4:3
				factoredWidth = optimumWidth;
				factoredHeight = optimumHeight;
			}
			
			else if (virtualWidth <= 800 && virtualHeight <= 1280) {
				// galaxy tab test device :- reports 800x1232(dd: 1.0) and factors to 768x1366(ar: 1.7)
				// due to Android 3.x reserving bottom 48 px for menubar...
				// Galaxy Tab 10.1, Nexus 7
				// 4.3% - WXGA - 5:3
				//TODO
				// aim to scale the 480x800 optimum
				
				factoredWidth = (int)(optimumWidth * optimumAspectRatio); // 768
				factoredHeight = (int)(optimumHeight2 * optimumAspectRatio); // 1280
				factoredCellSize = (int)(optimumCellSize * optimumAspectRatio);
				factoredCellsHigh = optimumCellsHigh - 1;			
				// 4.3% - WXGA - 16:10
				// WXGA does not need -1 cellSize...but gets it anyway
				// aim to scale the 480x800 optimum
				
				
			}
			// ABOVE OPTIMUM TABLET
			else if (virtualWidth > 800 && virtualHeight > 1280) {
				//TODO
				// ?.?% - WUXGA - 16:10
				// ?.? - QXGA - 4:3
				// ?.?% - WQXGA - 16: 10
				factoredWidth = (int)(optimumWidth * optimumAspectRatio);
				factoredHeight = (int)(optimumHeight * optimumAspectRatio);
				factoredCellSize = (int)(optimumCellSize * optimumAspectRatio);
			}
			factorAspectRatio(deviceWidthIn, deviceHeightIn);
		}
	}
	
/*****************************************************************/
	
	private static void setDefaults(int deviceWidth, int deviceHeight) {
		// set to defaults first
		factoredWidth = optimumWidth;
		factoredHeight = optimumHeight;
		factoredCellsHigh = optimumCellsHigh;
		factoredCellSize = optimumCellSize;
		factoredCellsWide = optimumCellsWide;
		
		// then ensure device reports width as the shortest value
		// independent of user device orientation
		if (deviceWidth < deviceHeight) {
			// held in portrait?
			virtualWidth = deviceWidth;
			virtualHeight = deviceHeight;
		}
		else {
			// held in landscape?
			virtualWidth = deviceHeight;
			virtualHeight = deviceWidth;
		}
		
		// catch for small device screens 
		if (virtualWidth < optimumWidth 
				|| virtualHeight < optimumHeight2) {
			// 13.6% - HVGA	- 3:2
			// ?.?% - VGA - 4:3
			// bad device size, too small
			screenType = NO_DEVICE;
			refactorError = SCREEN_SMALL_ERROR;
		}
		// final catch
		if (virtualWidth < 768 && virtualHeight <= 1024) {
			// catch for 600 x 1024 phones...
			screenType = MOBILE;
		}
		else 
			screenType = TABLET;
	}
	
	private static void factorAspectRatio(int deviceWidth, int deviceHeight) {
		// for pixel aspect ratio:
		// (aspect ratio width / apsect ratio height) / (scren width / screen height)
		// ie:  (16 / 9) / (854 / 480) 
		//        1.78f / 1.78f
		//			 1:1 pixel size	
		factoredAspectRatio = (float)factoredHeight / (float)factoredWidth;	
		// round down to one decimal place
		factoredAspectRatio = (float)Math.floor(factoredAspectRatio * 10) / 10;
		
		if (factoredAspectRatio != optimumAspectRatio
				&& factoredAspectRatio != optimumAspectRatio2) {
			screenType = NO_DEVICE;
			refactorError = SCREEN_ASPECT_ERROR;
		}
	}
}