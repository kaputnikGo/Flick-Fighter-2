package com.ff.ff2.ctr;

//import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.lib.GraphicsManager;
import com.ff.ff2.lib.LoadManager;

public class FFConfirm {
	//private static final String TAG = FFConfirm.class.getSimpleName();
    private BitmapFont toastFont;
    private String headingText;
    private String selectString;
    public String confirmedName;
    private float toastFontHeight;

    private Texture toastback;
    public float width;
    public float height;
    private float cellSize;
    public Vector2 position = new Vector2();
    public Vector2 button1position = new Vector2();
    public Vector2 button2position = new Vector2();
    public Vector2 button3position = new Vector2();
    public Vector2 button4position = new Vector2();

	public FFConfirm(String heading) {
		this.toastback = GraphicsManager.getGraphicsManager().toasterBack;
	    this.width = FFScreen.gameWidth;
	    this.height = FFScreen.dialogSize;
	    this.cellSize = FFScreen.gameCellSize;
        this.position.set(
	    		FFScreen.gameCentreX - width / 2,
	    		FFScreen.gameCentreY - height / 2); // center on the screen
	    
        this.button1position.set(
        		position.x + cellSize, 
	 			position.y + height - (cellSize * 2));
        
        this.button2position.set(
        		button1position.x + (cellSize * 3), 
	 			position.y + height - (cellSize * 2));              
        
        this.button3position.set(
        		button2position.x + (cellSize * 3), 
				position.y + height - (cellSize * 2));
        
        this.button4position.set(
        		button3position.x + (cellSize * 3), 
				position.y + height - (cellSize * 2));
	      
	    headingText = new String(heading);
	    
	    selectString = new String();
	    confirmedName = new String(LoadManager.DEFAULT_BLANK_FIELD);
	    toastFont = GraphicsManager.getGraphicsManager().exoFFfont;
	    toastFont.setColor(0.f, 0.f, 0.f, 1.f);
	    toastFontHeight = toastFont.getLineHeight();
   }
	
   public void addListText(String string) {
	   selectString = string;
	   confirmedName = selectString;
   }
   
   private float centreText(String string) {   
	   return (width / 2) - (toastFont.getBounds(string).width / 2);
	   //fontY = (backerHeight / 2) + (toastFont.getBounds(fileString).height / 2); 
   }
   
/*********************************************************************/

   public void destroy() {
	   if (toastFont != null) toastFont = null;
	   toastback.dispose();
   }
   
/*********************************************************************/
   
   public int handleTap(float touchX, float touchY) {
	   return 0;   
   }
   
/*********************************************************************/
   
   public void draw(SpriteBatch creatorBatch) {
	   creatorBatch.draw(toastback, position.x, position.y, width, height);
	   
	   toastFont.draw(creatorBatch, headingText, centreText(headingText), 
			   position.y + 20);
	   
	   toastFont.draw(creatorBatch, selectString, centreText(selectString), 
    			   position.y + (cellSize * 2) + toastFontHeight);
   }
}