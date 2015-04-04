package com.ff.ff2.gui;

//import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ff.ff2.lib.GraphicsManager;

// this class has a simple title, message and exit button

public class FFStatus {
	//private static final String TAG = FFStatus.class.getSimpleName();
    private BitmapFont toastFont;
    private String headingText;
    private String fileString;
    private float toastFontHeight;

    private Texture toastback;
    public float width;
    public float height;
    private float cellSize;
    public Vector2 position = new Vector2();
    public Vector2 exitButtonPosition = new Vector2();

    public FFStatus(String heading, String message) {
    	this.toastback = GraphicsManager.getGraphicsManager().toasterBack;
        this.width = FFScreen.dialogSize * 2;
        this.height = FFScreen.dialogSize;
        this.cellSize = FFScreen.gameCellSize;
        this.position.set(
	    		FFScreen.gameCentreX - width / 2,
	    		FFScreen.gameCentreY - height / 2); // center on the screen
      
        this.exitButtonPosition.set(
				position.x + width - (cellSize * 3), 
				position.y + height - (cellSize * 2));
      
        headingText = new String(heading);
        fileString = new String(message);
        toastFont = GraphicsManager.getGraphicsManager().exoFFfont;
        toastFont.setColor(0.f, 0.f, 0.f, 1.f);
        toastFontHeight = toastFont.getLineHeight();
   }

   public void destroy() {
      toastback.dispose();
   }
      
   private float centreText(String string) {   
	   return (position.x + (width / 2)) - (toastFont.getBounds(fileString).width / 2);
	   //fontY = (backerHeight / 2) + (toastFont.getBounds(fileString).height / 2); 
   }  

   public void draw(SpriteBatch spriteBatch) {
	   spriteBatch.draw(toastback, position.x, position.y, width, height);
	   
	   toastFont.draw(spriteBatch, headingText, centreText(headingText), 
			   position.y + 20);
	   
	   toastFont.draw(spriteBatch, fileString, centreText(fileString), 
    			   position.y + (cellSize * 2) + toastFontHeight);
   }
}