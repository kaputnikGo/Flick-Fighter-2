package com.ff.ff2.lib;

//import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.gui.FFScreen;


public class CollisionUtils {
	//private static final String TAG = CollisionUtils.class.getSimpleName();
	public static final int CELL_SIZE = FFScreen.gameCellSize;
	public static final float CELL_RADIUS = FFScreen.gameCellSize / 2;
	public static final float SAFE_WIDTH = FFScreen.gameWidth - CELL_SIZE;
	public static final float SAFE_HEIGHT = FFScreen.gameHeight - (CELL_SIZE * 2.5f);
	//private final float FLICK_FACTOR = 15.f;
	
	public CollisionUtils() {
		//
	}
	
	public static Vector2 withinScreen(Vector2 position) {
		// true if NOT hit edge
		if (position.x <= 0) position.x = 0;	
		if (position.x >= SAFE_WIDTH) position.x = SAFE_WIDTH;
		
		if (position.y <= 0) position.y = 0;
		// make this account for the menu area
		if (position.y >= SAFE_HEIGHT) position.y = SAFE_HEIGHT;
		
		return position;
	}

	public static int updateCollideAxis(Vector2 position, Vector2 collider) {	
		
		float theta = getTheta(position, collider);
		
		if (theta <= 45 && theta > -45) {
			// hit bottom
			return IdManager.SOUTH;
		}
		else if (theta > 45 && theta < 135) {
			// hit right
			return IdManager.EAST;
		}
		else if (theta <= -45 && theta > -135) {
			// hit left
			return IdManager.WEST;
		}
		else {
			// hit top, hopefully
			return IdManager.NORTH;
		}
	}
	
	public static float getTheta(Vector2 source, Vector2 target) {
		// center the positions to the COG
		// for hopefully accurate theta...
		
		// check if angle is same?
		// double angle = atan2(y2 - y1, x2 - x1) * 180 / PI;
		
		float deltaX = (source.x + CELL_RADIUS) - (target.x + CELL_RADIUS);
		float deltaY = (source.y - CELL_RADIUS) - (target.y - CELL_RADIUS);

		return (float)(180.0 / Math.PI * Math.atan2(deltaX, deltaY));
	}

	public static class BezierCurve {
		// based upon de Casteljau algorithm
	    private double[] x;
	    private double[] y;
	    private int n;

	    private double[][] b;

	    public BezierCurve(double[] x, double[] y, int n) {
	        //require x.length = y.length = n
	        this.x = x;
	        this.y = y;
	        this.n = n;
	        this.b = new double[n][n];
	    }

	    private void init(double[] initialValues) {
	        for (int i = 0; i < n; i++) {
	            b[0][i] = initialValues[i];
	        }
	    }

	    private double evaluate(double t, double[] initialValues) {
	        init(initialValues);
	        for (int j = 1; j < n; j++) {
	            for (int i = 0; i < n - j; i++) {
	                b[j][i] = b[j-1][i] * (1-t) + b[j-1][i+1] * t;
	            }
	        }
	        return(b[n-1][0]);
	    }

	    public double[] getXYvalues(double t) {
	        double xVal = evaluate(t, x);
	        double yVal = evaluate(t, y);
	        return new double[] {xVal, yVal};
	    }

	}         
}