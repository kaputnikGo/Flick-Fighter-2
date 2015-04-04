package com.ff.ff2.gui;

import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.eng.FFSettings;
import com.ff.ff2.lib.GraphicsManager;
import com.ff.ff2.lib.IdManager;

public class FFUserSet {
	private static final String TAG = FFUserSet.class.getSimpleName();
	
    private BitmapFont toastFont;
    private String headingText;

    private Texture toastback;
    private float width;
    private float height;
    private float cellSize;
    public Vector2 position = new Vector2();
   
	private MenuButton exitButton;
	private MenuButton diffEasyButton;
	private MenuButton diffNormButton;
	private MenuButton diffHardButton;
	private MenuButton osdButton;
	
	public FFUserSet() {
		// constr.
		this.toastback = GraphicsManager.getGraphicsManager().toasterBack;
	    this.width = FFScreen.selectorWidth;
	    this.height = FFScreen.dialogSize;
	    this.cellSize = FFScreen.gameCellSize;
	    
	    this.position.set(
	    		FFScreen.gameCentreX - width / 2, 
	    		FFScreen.gameCentreY - height / 2); // center on screen
	      
	    
	    headingText = new String("User Settings:");
	    
	    toastFont = GraphicsManager.getGraphicsManager().exoFFfont;
	    toastFont.setColor(0.f, 0.f, 0.f, 1.f);
      
		// add buttons for save and load if not simple confirm dialog
	    Vector2 buttonPosition = new Vector2(position.x + cellSize,
	    		position.y + (height / 2) - cellSize);
	    
		diffEasyButton = new MenuButton(buttonPosition, IdManager.BTN_DIFF_EASY);
		
		buttonPosition.x += cellSize * 3;
		diffNormButton = new MenuButton(buttonPosition, IdManager.BTN_DIFF_NORM);
				
		buttonPosition.x += cellSize * 3;
		diffHardButton = new MenuButton(buttonPosition, IdManager.BTN_DIFF_HARD);
		
		buttonPosition.x = width - cellSize * 2;
		osdButton = new MenuButton(buttonPosition, IdManager.BTN_OSD);
		
		//buttonPosition.x = width - cellSize * 2;
		buttonPosition.y += cellSize * 3;
		exitButton = new MenuButton(buttonPosition, IdManager.BTN_EXIT);
		
		buttonPosition = null;
	    
	}
	
	private float centreText(String string) {   
		return (width / 2) - (toastFont.getBounds(string).width / 2);
		//fontY = (backerHeight / 2) + (toastFont.getBounds(fileString).height / 2); 
	}
	/*********************************************************************/

	   public void destroy() {
		   if (toastFont != null) toastFont = null;
		   toastback.dispose();
		   if (diffEasyButton != null) diffEasyButton = null;
		   if (diffNormButton != null) diffNormButton = null;
		   if (diffHardButton != null) diffHardButton = null;
		   if (osdButton != null) osdButton = null;
		   if (exitButton != null) exitButton = null;
	   }
	   
/*********************************************************************/
	   
	   public int handleTap(float touchX, float touchY) {
		   if (exitButton.boundBox.contains(touchX, touchY)) {
			   Log.d(TAG, "exit.");
			   return FFSettings.EXIT;
		   }
		   else if (osdButton.boundBox.contains(touchX, touchY)) {
			   Log.d(TAG, "osd.");
			   return FFSettings.OSD;
		   }
		   else if (diffHardButton.boundBox.contains(touchX, touchY)) {
			   Log.d(TAG, "hard.");
			   return FFSettings.HARD;
		   }
		   else if (diffNormButton.boundBox.contains(touchX, touchY)) {
			   Log.d(TAG, "norm.");
			   return FFSettings.NORM;
		   }
		   else if (diffEasyButton.boundBox.contains(touchX, touchY)) {
			   Log.d(TAG, "easy.");
			   return FFSettings.EASY;
		   }
		   Log.d(TAG, "no contains, exit.");
		   return FFSettings.EXIT;   
	   }
	   
/*********************************************************************/
	   
	   public void draw(SpriteBatch gameBatch) {
		   gameBatch.draw(toastback, position.x, position.y, width, height);
		   
		   toastFont.draw(gameBatch, headingText, centreText(headingText), 
				   position.y + 20);

		   exitButton.draw(gameBatch);
		   diffEasyButton.draw(gameBatch);
		   diffNormButton.draw(gameBatch);
		   diffHardButton.draw(gameBatch);
		   osdButton.draw(gameBatch);
	   }
}