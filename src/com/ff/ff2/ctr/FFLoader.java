package com.ff.ff2.ctr;

import android.util.Log;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ff.ff2.gui.FFScreen;
import com.ff.ff2.gui.FFStatus;
import com.ff.ff2.gui.MenuButton;
import com.ff.ff2.gui.Reports;
import com.ff.ff2.lib.GraphicsManager;
import com.ff.ff2.lib.IdManager;
import com.ff.ff2.lib.LoadManager;

public class FFLoader {
	private static final String TAG = FFLoader.class.getSimpleName();
	private static final int listDisplaySize = 17; // max number of entries per screen page
	
    private FFCreator creatorLink;
   
    private BitmapFont loaderFont;
    public float loaderFontHeight;
    private float cellSize;
    private int dialogSize;
   
    private Array<String> headingString;
    private Array<String> filesList;
    private Array<String> displayList;
    
    private Array<Rectangle> listHitBox;
    private float numberPages;
    private int currentPage;
    
    private int displayEntryCount;
    private int displayEnd;
    private int displayStart;
    
    private boolean nextButtonShow = false;
    private boolean prevButtonShow = false;
    
    private Array<String> userFortressFiles;
   
    public Vector2 position = new Vector2();
    public Vector2 listPosition = new Vector2();
   
    private ShapeRenderer backingShape;
    private Vector2 backingPosition = new Vector2();
    private float width;
    private float height;
    private int centreX;
    
    private FFConfirm fileConfirm;
    private boolean fileConfirmShow = false;
    
    private FFStatus status;
    private MenuButton statusExitButton;
    private boolean statusShow;
    
    private MenuButton loadFileButton;
    private MenuButton exitFileButton;   
    private MenuButton deleteFileButton;
    
    private MenuButton exitButton;   
    private MenuButton nextButton;
    private MenuButton prevButton;
    
    public String userSelectedFortFile;
    public int fortressLocation;
    
    public FFLoader(Camera camera, FFCreator creatorLink) {
    	this.creatorLink = creatorLink;
    	cellSize = FFScreen.gameCellSize;
    	position.set(cellSize / 2, cellSize / 2);
    	
    	width = FFScreen.selectorWidth;
    	height = FFScreen.selectorHeight;
    	centreX = FFScreen.gameCentreX;
    	dialogSize = FFScreen.dialogSize;
    	
    	backingShape = new ShapeRenderer();
    	backingShape.setProjectionMatrix(camera.combined);
    	backingPosition.set(position);
    	backingShape.setColor(Color.LIGHT_GRAY);
    	
    	listPosition.set(position.x + cellSize, position.y + cellSize);
    	headingString = new Array<String>();
    	filesList = new Array<String>();

    	currentPage = 1;   	
    	displayEntryCount = 0;
    	displayStart = 0;
    	displayEnd = listDisplaySize;
    	listHitBox = new Array<Rectangle>(listDisplaySize);
    	displayList = new Array<String>(listDisplaySize);
    	
    	userSelectedFortFile = new String(LoadManager.DEFAULT_BLANK_FIELD);
    	userFortressFiles = new Array<String>();
      
    	loaderFont = GraphicsManager.getGraphicsManager().exoFFfont;
    	loaderFont.setColor(1.f, 1.f, 0.f, 1.f);
    	loaderFontHeight = loaderFont.getLineHeight();
    	
        prevButton = new MenuButton(new Vector2(
        		position.x + cellSize, 
	 			position.y + height - (cellSize * 2)),
	 			IdManager.BTN_PREV);
        
        nextButton = new MenuButton(new Vector2(
        		FFScreen.gameCentreX - cellSize, 
	 			position.y + height - (cellSize * 2)),
	 			IdManager.BTN_NEXT);

    	exitButton = new MenuButton(new Vector2(
    			position.x + width - (cellSize * 3), 
				position.y + height - (cellSize * 2)),
				IdManager.BTN_FILE_EXIT);
    }
    
    
/*********************************************************************/
    
    public void destroy() {
        creatorLink = null;
        if (userFortressFiles != null) userFortressFiles = null;
		if (headingString != null) headingString = null;
		if (filesList != null) filesList = null;
		if (listHitBox != null) listHitBox = null;
		if (loaderFont != null) loaderFont = null;
		if (backingShape != null) backingShape = null;
		if (fileConfirm != null) {
			fileConfirm.destroy();
			fileConfirm = null;
		}
    }
    
/*********************************************************************/
    
    public int loadList() {
		int result = LoadManager.getLoadManager().loadFortressNamesList(LoadManager.USER_STORAGE);
		if (result == LoadManager.FILE_LOADED) {
			userFortressFiles = LoadManager.getLoadManager().getFortressNamesList();
			
			addHeadingText(" ");
			addHeadingText("Select fortress file from:");			
			String userPath = LoadManager.getLoadManager().getLocalStoragePath();
			
			if (userPath != null) {
				addHeadingText(userPath);		
				if (userFortressFiles != null) {
					// here
					String tempString;
					for (int i = 0; i < userFortressFiles.size; i++) {
						tempString = userFortressFiles.get(i).toString();
						// remove localPath from fortress name
						filesList.add(LoadManager.getLoadManager().simpleUserFortName(tempString));
					}
				}
			}
			userFortressFiles.shrink();
			headingString.shrink();
			filesList.shrink();
		
			numberPages = MathUtils.ceil((float)filesList.size / (float)listDisplaySize);
			loadPage();
		}
		return result;
    }
   
/*********************************************************************/
    
    private void reloadList() {
    	userFortressFiles.clear();
    	headingString.clear();
    	listPosition.set(position);
    	filesList.clear();
    	listHitBox.clear();
    	displayEntryCount = 0;
    	loadList();
    }
    
    private void displayStatus(int reportInt) {
	    status = new FFStatus(Reports.FILE_ACTION_STATUS, Reports.reportList.get(reportInt));										
		statusExitButton = new MenuButton(status.exitButtonPosition, IdManager.BTN_EXIT);	
		statusShow = true;
    }
    
    private void dismissStatus() {
    	statusShow = false;
    	statusExitButton = null;
	    status.destroy();
	    status = null;
	    dismissFileConfirm();
	    reloadList();
    }
  
    private void loadPage() {
    	listHitBox.clear();
    	displayList.clear();
    	displayEntryCount = 0;
    	String entry;
    	
    	for (int i = 0; i < listDisplaySize; i++) {
    		entry = getPageListEntry(i);
    		if (entry != null) {
		    	listHitBox.add(new Rectangle(listPosition.x,
		      		  listPosition.y + (i * loaderFontHeight),
		      		  dialogSize, loaderFontHeight));
		    	displayEntryCount++;
		    	displayList.add(entry);
    		}
    		else 
    			break;
    	}

    	if (filesList.size > listDisplaySize) {
	    	if (currentPage == 1) {
	    		// has a next, no prev
	    		prevButtonShow = false;
	    		nextButtonShow = true;
	    	}
	    	if (currentPage >= 2) {
	    		// has both next and prev
	    		prevButtonShow = true;
	    		nextButtonShow = true;
	    	}
    	}
    }
    
    private void loadPrevPage() {
    	if (currentPage > 0) currentPage--;
    	loadPage();
    }
    
    private void loadNextPage() {
    	if (currentPage < numberPages) currentPage++;
    	loadPage();
    }
    
    private String getPageListEntry(int entryNum) {
    	displayEnd = currentPage * listDisplaySize; // 17, 34, 51 : 1 * 17, 2 * 17, ...
    	displayStart = displayEnd - listDisplaySize; // 0, 17, 34 : 
    	
    	if (displayStart + entryNum < filesList.size)
    		return filesList.get(displayStart + entryNum);
    	else
    		return null;
    }
    
    private void addHeadingText(String string) {
	   headingString.add(string);
	   listPosition.y += loaderFontHeight;
    }
  
    private boolean checkForListHit(float touchX, float touchY) {
	   // go thru listHitBox and find the one that is tapped
    	for (int i = 0; i < displayEntryCount; i++) {
    		if (listHitBox.get(i).contains(touchX, touchY)) {
    			if (i <= filesList.size) {
    				userFileRequest(getPageListEntry(i));
    				return true;
    			}
		    }
	   }	   
	   return false;
    }
   
    private void userFileRequest(String userFortName) {
    	fileConfirm = new FFConfirm(Reports.CONFIRM_DIALOG);
    	
    	fileConfirm.addListText(userFortName);
	    loadFileButton = new MenuButton(fileConfirm.button1position, IdManager.BTN_FILE_LOAD);
    	deleteFileButton = new MenuButton(fileConfirm.button2position, IdManager.BTN_DELETE);
	    exitFileButton = new MenuButton(fileConfirm.button4position, IdManager.BTN_FILE_EXIT);

	    fileConfirmShow = true; 
    }
    
    private void exitWithFileConfirmed(String confirmedName) {
    	userSelectedFortFile = confirmedName;
	    fortressLocation = LoadManager.USER_STORAGE;	    
	    dismissFileConfirm();
	    if (FFScreen.DEBUG) Log.i(TAG, "exit with file name: " + confirmedName);
	    creatorLink.loadFromSelector(userSelectedFortFile, fortressLocation);
    }
    
    private void deleteFileConfirmed(String confirmedName) {
    	int result = LoadManager.getLoadManager().deleteExistingFile(
    			confirmedName, 
    			LoadManager.USER_STORAGE);
    	
    	displayStatus(result);	
    }
   
    private void dismissFileConfirm() {
    	fileConfirmShow = false;
	    fileConfirm.destroy();
	    fileConfirm = null;
	    // reset font colour
	    loaderFont.setColor(1.f, 1.f, 0.f, 1.f);
	}
    
    private float centreText(String string) {   
 	   return (centreX - (loaderFont.getBounds(string).width / 2));
 	   //fontY = (backerHeight / 2) + (toastFont.getBounds(fileString).height / 2); 
    }
   
/*********************************************************************/
    
    public boolean handleTap(float touchX, float touchY) {
	   // quick check
    	if (statusShow) {
 		   if (statusExitButton.boundBox.contains(touchX, touchY)) {
 			   dismissStatus();
 			   return true;
 		   }
 		   return false;
 	    }
 
		else if (fileConfirmShow) {
		   if (exitFileButton.boundBox.contains(touchX, touchY)) {
			   dismissFileConfirm();
			   return true;
		   }
		   else if (deleteFileButton.boundBox.contains(touchX, touchY)) {
			   deleteFileConfirmed(fileConfirm.confirmedName);
			   return true;
		   }
		   else if (loadFileButton.boundBox.contains(touchX, touchY)) {
			   exitWithFileConfirmed(fileConfirm.confirmedName);
			   return true;
		   }
		   return false;
	    }
    	// order here
		if (exitButton.boundBox.contains(touchX, touchY)) {
			creatorLink.dismissLoader();
			return true;
		}
		
	    if (prevButtonShow) {
			if (prevButton.boundBox.contains(touchX, touchY)) {
				loadPrevPage();
				return true;
			}
	    }
		if (nextButtonShow) {
			if (nextButton.boundBox.contains(touchX, touchY)) {
				loadNextPage();
				return true;
			}
		}  	
    	
		return checkForListHit(touchX, touchY);   
    }

/*********************************************************************/
    
   public void draw(SpriteBatch creatorBatch) {
	   if (statusShow) {
		   status.draw(creatorBatch);
		   statusExitButton.draw(creatorBatch);
	   }
	   else {	   
		   backingShape.begin(ShapeType.Filled);
		   
		   //if (deleteConfirmShow) backingShape.setColor(Color.RED);
		   // not working ...
		   backingShape.setColor(Color.LIGHT_GRAY);
		   
	       backingShape.rect(backingPosition.x, backingPosition.y, width, height);
	       backingShape.end();
	
		   
		   for (int i = 0; i < headingString.size; i++) {
			   loaderFont.draw(creatorBatch, headingString.get(i), centreText(headingString.get(i)), 
	    			  position.y + (i * loaderFontHeight));
	       }
  
		   for (int j = 0; j < displayEntryCount; j++) {
			   loaderFont.draw(creatorBatch, 
					   j + displayStart + ": " + displayList.get(j),
					   listPosition.x, 
	    			   listPosition.y + (j * loaderFontHeight));
	       }
		   
		   if (prevButtonShow) prevButton.draw(creatorBatch);
		   if (nextButtonShow) nextButton.draw(creatorBatch);
		   
		   exitButton.draw(creatorBatch);
		   
		   if (fileConfirmShow) {
			    fileConfirm.draw(creatorBatch);
				loadFileButton.draw(creatorBatch);
				deleteFileButton.draw(creatorBatch);
				exitFileButton.draw(creatorBatch);
		   }
	   }
   }
}