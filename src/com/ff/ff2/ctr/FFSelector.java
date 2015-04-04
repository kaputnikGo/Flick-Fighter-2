package com.ff.ff2.ctr;

//import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.gui.MenuButton;
import com.ff.ff2.lib.GraphicsManager;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.lib.LoadManager;

public class FFSelector {
	//private static final String TAG = FFSelector.class.getSimpleName();
	
	public static final int EXIT = 0;
	public static final int SEND = 1;
	public static final int PLAY = 2;
	public static final int SAVE = 3;
	public static final int LOAD = 4;
	
    private BitmapFont toastFont;
    private String headingText;
    public String confirmedName;

    private Texture toastback;
    private float width;
    private float height;
    private float cellSize;
    public Vector2 position = new Vector2();
   
	private MenuButton exitButton;
	private MenuButton saveFileButton;
	private MenuButton loadFileButton;
	private MenuButton playFileButton;
	private MenuButton sendFileButton;

	public FFSelector(String heading, boolean canPlay) {
		this.toastback = GraphicsManager.getGraphicsManager().toasterBack;
	    this.width = FFScreen.selectorWidth;
	    this.height = FFScreen.dialogSize;
	    this.cellSize = FFScreen.gameCellSize;
	    
	    this.position.set(
	    		FFScreen.gameCentreX - width / 2, 
	    		FFScreen.gameCentreY - height / 2); // center on screen
	      
	    
	    headingText = new String(heading);
	    confirmedName = new String(LoadManager.DEFAULT_BLANK_FIELD);
	    toastFont = GraphicsManager.getGraphicsManager().exoFFfont;
	    toastFont.setColor(0.f, 0.f, 0.f, 1.f);
      
		// add buttons for save and load if not simple confirm dialog
	    Vector2 buttonPosition = new Vector2(position.x + cellSize,
	    		position.y + (height / 2) - cellSize);
	    
		loadFileButton = new MenuButton(buttonPosition, IdManager.BTN_FILE_LOAD);
	
		buttonPosition.x += cellSize * 3;
		saveFileButton = new MenuButton(buttonPosition, IdManager.BTN_FILE_SAVE);
				
		buttonPosition.x += cellSize * 3;
		playFileButton = new MenuButton(buttonPosition, IdManager.BTN_FILE_PLAY);
		// make button greyed out
		playFileButton.swapTexture(canPlay);
		
		buttonPosition.x += cellSize * 3;
		sendFileButton = new MenuButton(buttonPosition, IdManager.BTN_FILE_SEND);
		
		buttonPosition.x += cellSize;
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
	   if (saveFileButton != null) saveFileButton = null;
	   if (loadFileButton != null) loadFileButton = null;
	   if (playFileButton != null) playFileButton = null;
	   if (sendFileButton != null) sendFileButton = null;
	   if (exitButton != null) exitButton = null;
   }
   
/*********************************************************************/
   
   public int handleTap(float touchX, float touchY) {
		   if (exitButton.boundBox.contains(touchX, touchY)) {
				return EXIT;
		   }
		   else if (sendFileButton.boundBox.contains(touchX, touchY)) {
				return SEND;
		   }
		   else if (playFileButton.boundBox.contains(touchX, touchY)) {
				return PLAY;
		   }
		   else if (saveFileButton.boundBox.contains(touchX, touchY)) {
				return SAVE;
		   }
		   else if (loadFileButton.boundBox.contains(touchX, touchY)) {
				return LOAD;
		   }
	   return EXIT;   
   }
   
/*********************************************************************/
   
   public void draw(SpriteBatch creatorBatch) {
	   creatorBatch.draw(toastback, position.x, position.y, width, height);
	   
	   toastFont.draw(creatorBatch, headingText, centreText(headingText), 
			   position.y + 20);

	   exitButton.draw(creatorBatch);
	   saveFileButton.draw(creatorBatch);
	   loadFileButton.draw(creatorBatch);
	   playFileButton.draw(creatorBatch);
	   sendFileButton.draw(creatorBatch);
   }
}