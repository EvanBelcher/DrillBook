#Usage

##Installation
????????????????????

##Terminology
- Page
	- Individual sheet of drill paper. Also known as a set. A page has [a set of information](../master/Usage.md#editing-the-page) associated with it.
- Show
	- Set of pages. An entire show is stored in one JSON file.
- Dot
	- Individual person represented on a page. A dot has a [a set of information](../master/Usage.md#editing-dots) associated with it.
- DrillSweet directory
	- **Documents/DrillSweet 2/** by default

##Placing and moving dots
- Click in the bounds of the field to place a dot. It will be automatically selected.
- Click on an existing dot to select it.
- Click and drag a dot to move it.
	- When clicking and dragging, right-click to cancel the move.
- Right-click a dot to delete it.
- Right click any empty space to deselect the currently selected dot.

##Editing dots
The "Dot Data" pane on the right side of the screen contains controls for the selected dot.
Controls:
- Instrument
	- Select a letter from A-Z. This will be displayed above the dot on the page.
- Number
	- Select any number 1 - infinity. This will be displayed above the dot on the page.
- X Position
	- Changed by clicking and dragging point as explained [above](../master/Usage.md#placing-and-moving-dots). Also editable with the controls.
- Y Position
	- Changed by clicking and dragging point as explained [above](../master/Usage.md#placing-and-moving-dots). Also editable with the controls.
- Position Text
	- Not editable. Displays the position of the dot in English.
	
Notes:
- The instrument and number combined make the dot "name". Duplicate names are allowed technically, although it doesn't make much sense from a marching standpoint. Telling someone to be in two places at once is a bit evil, even for a band director.

##Editing the page
The "Page Data" pane on the right side of the screen contains controls for the entire page.
- Navigation
	- Pages are shown in the format:
	
		> \<Page Number> | \<Song Title>, m.\<Starting Measure>-\<Ending Measure>
	- Select a page to display that page. You don't need to save the page you're working on before you do this, but it never hurts.
- Page Number
	- Just shows the page number... You didn't really have a question about this one, right?
- Song Title
	- Edit the song title here and watch as it magically updates the navigation box and text box.
- Starting Measure
	- ^
- Ending Measure
	- ^
- Counts
	- ^
- Notes
	- Write whatever you want. It will be shown in the text box.
- Text X Position
	- Move the text box horizontally.
- Text Y Position
	- Move the text box vertically.
- Clear Page
	- Remove all dots from the page. **Does not save automatically, so if you somehow managed to do this accidentally, just close without saving and re-open.**
- Delete Page
	- Remove this page and change the number of other pages to remove the hole. Band directors hate holes. **Does not save automatically, so if you somehow managed to do this accidentally, just close without saving and re-open.**

##Display settings
- Toggle Gridlines
	- Click it once and the grid disappears. Click it again and it's back. Abracadabra!
- Toggle Dot Names
	- Clap on. Dot names disappear. Clap off. Dot names re-appear.

##File menu
- New
	- Create a new show in the [DrillSweet directory](../master/Usage.md#terminology) by default or wherever you choose. It will ask you for a filename for the new page. You don't need to give it the ".ds2" extension.
- Open
	- Open an existing show in the DrillSweet directory by default or wherever you choose.
- Save
	- Save the current show under the current filename. The current filename is displayed in the title bar and taskbar next to "DrillSweet 2".
- Save As
	- Save the current show under a new filename.
- Print Current Page
	- Saves the current show as a PDF file in the DrillSweet directory. Takes current "toggle gridlines" and "toggle dot names" states into account.
- Print Show
	- Saves every page as a PDF file. The name of the PDF file will be the same as the name of the file of the show, plus "full show".
- Print Dot Sheets
	- Saves dot sheets for every player as PDF files named by their instrument letter. These are all put in a folder called the name of your show "Dot Sheets". Hope you got all that.
- Quit
	- Now why would you want to do that?
