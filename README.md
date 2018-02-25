# Paint App

An Android application that is a basic drawing program.  
• The application should have the user select a color and enable the user to draw with their finger on the screen.  
• The application should also have a method of clearing the drawing.  


## Competitor App Analysis - Apps from Google Play Store  
1) Sketch App - https://play.google.com/store/apps/details?id=com.sonymobile.sketch&hl=en  
  In the upper tab:  
    Brush size - slider  
    Basic colors provided in addition to a color picker option.  
    Eraser  
  In the lower tab:  
	  Undo, Redo  
	  On confirmation of the image, save, share and discard options.  
	  
2) Paint App - https://play.google.com/store/apps/details?id=nanashi.studios.paint&hl=en  
	In the upper tab:   
    save, clear, undo.  
	In the overflow menu:  
    Load, Share, Exit  
	In the lower tab:   
    color  
	In the right tab:   
    brush size.  
  
3) 	ibisPaintX App  - https://play.google.com/store/apps/details?id=jp.ne.ibis.ibispaintx.app&hl=en  
	In the home page: show saved images and provides + icon to create Image.  
  In the lower tab:   
    In the paint view, opacity and brush size are slider  
    color selection is above these two.  
  Upper tab:   
    undo, redo.  
    
## Main Usercases
1) Draw a paint and save
   a) Open app
   b) Press on add button
   c) draw with defaults brush color and size.
   d) click on checkmark to save.
   e) enter the name and click save.
   Verify: If the name appears in the main list view.
   f) click on the list item in main list view.
   Verify: The previous painting reappears and able to edit it.
    
 2) Pick color
   a) Open app
   b) Press on add button
   c) draw with defaults brush color and size.
   d) click on color picker on the right side.
   e) drag and select the color.
   f) click checkmark to select.
   Verify: See if the floating button color changes and further painting reflects the same color.
  
 3) Pick size 
   a) Open app
   b) Press on add button
   c) draw with defaults brush color and size.
   d) click on size picker on the left side.
   e) drag and select the size.
   f) click checkmark to select.
   Verify: See if the further painting reflects the picked size.
   
 4) Clear drawing
   a) Open app
   b) Press on add button
   c) draw with defaults brush color and size.
   d) click on clear (cross icon) on the upper toolbar.
   e) dialog appears to clear the drawing.
   f) click on clear.
   Verify: See if the painting clears.
   
## Paper Prototyping
<img src="./refimages/paperprototype.jpg" width="532" height="400"> 


## Initial Design
<img src="./refimages/initial_design.jpg" width="732" height="500"> 


## Known Issues
1) writing the app data into filesystem is in an asynchronous thread but reading is not yet.
2) Color picker gradient values are adjusted (not working with calculated values).
3) If painting is saved with same name, warning is not displayed currently.
