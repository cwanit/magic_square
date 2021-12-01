/*
 * ==========================================================================
 * MagicSquare.java
 *  
 * Author:		Luke Wanitthananon
 * Title:		Homework 4 Part B: Magic Square
 * Course:		CSC 142 at South Seattle College, Winter 2021.
 * Date: 		March 14, 2021.
 * Instructor:  Prof. Ravi Gandham
 * ==========================================================================
 * 
 * MagicSquare.java create 10 magic squares of 4 x 4 size and populate the
 * squares with numbers that satisfy magic square rules. Magic squares are saved
 * to folder as png file.
 * 
 */


import java.awt.*;	//	for Graphics
import java.io.IOException; // for IOException from save method

public class MagicSquare {

	public static final int PAN_WIDTH = 500;		// Drawing panel width = 500 px
	public static final int PAN_HEIGHT = PAN_WIDTH;	// Drawing panel height = 500 px
	public static final int MAX_COLUMN = 4; 		// Maximum number of squares in a row
	public static final int CELL_WIDTH = PAN_WIDTH / (MAX_COLUMN+2);	// Cell width based on total columns
	public static final int CELL_HEIGHT = PAN_HEIGHT / (MAX_COLUMN+2);	// Cell height base on total rows
	
	public static void main(String[] args) throws IOException {
		// configure the drawing panel
		DrawingPanel panel = new DrawingPanel(PAN_WIDTH,PAN_WIDTH);
		
		// A list of 10 magic values to generate new squares
		int[] magicValue = {34,40,52,67,71,74,83,87,94,99}; 
		
		// for each magicValue, make new magicSquare
		for(int n:magicValue) { 
			int[][] newMagicSquare = makeMagicSquare(n);
			// populate on drawing panel and save only when square is magic
			if(isSquareMagic(newMagicSquare)) { 
				populateGrid(panel,newMagicSquare);
				panel.save("MagicSquare" + n + ".png");
			}
		}
	}
	
	/**
	 * populateGrid takes a DrawingPanel and fills that with 4 x 4 grid and 
	 * values from a 2-dimensional array
	 * @param panel is a DrawingPanel object of width x height
	 * @param squareData is a integer symmetrical 2-dimensional array containing cell values
	 */
	private static void populateGrid (DrawingPanel panel, int [][] squareData) {
		
		Graphics g = panel.getGraphics();
		g.setFont(new Font("Georgia", Font.BOLD, 25));

		panel.clear();
		// Generate template
		drawTitleString(g,Color.RED);
		drawEmptyMagicSquare(g, Color.BLACK);
		drawFilledMagicSquare(g, Color.RED, squareData);
	}

	
	/**
	 * makeMagicSquare generates values for a Magic Square based on a magicValue.
	 * <p>Limitation: Since baseline data that is used to generate new squares are 4 x 4,
	 * <br> this method only works to generate another 4 x 4 symmetrical array.
	 * @param magicValue is used to generate a new Magic Square where all rows   
	 * or columns, or two diagonals add up to that value 
	 * @return a symmetrical 2-dimensional array containing the newly generated Magic Square
	 */
	private static int[][] makeMagicSquare(int magicValue){
		if(magicValue < 34) {
			throw new IllegalArgumentException(
					"Please enter a number larger than or equal to 34"
					);
		}
		
		int[][] magicArray = new int[MAX_COLUMN][MAX_COLUMN];
		int[][] baselineData = {{8,11,14,1},{13,2,7,12},{3,16,9,6},{10,5,4,15}};
		int quotient = (magicValue-34)/4;
		int remainder = (magicValue-34)%4;
		
		//add quotient to all numbers in the array
		for(int row = 0; row < magicArray.length; row++) {
			for(int col = 0; col < magicArray.length; col++) {
				magicArray[row][col] = baselineData[row][col]+quotient;
				// special case that needs remainder to be added to
				if(baselineData[row][col] == 13 ||
						baselineData[row][col] == 14 ||
						baselineData[row][col] == 15 ||
						baselineData[row][col] == 16) {
					magicArray[row][col] += remainder;
				}
			}
		}
		
		return magicArray;
	}

	
	/**
	 * isSquareMagic verifies whether a given square is a magic square or not
	 * @param squareData is a symmetrical 2-dimensional array of integers
	 * @return true if the square is magic if all rows or columns, or two main diagonals add up to same value
	 * return false otherwise
	 */
	private static boolean isSquareMagic (int [][] squareData) {
		// initialize the magicSum by summing the first row for squareData.
		// this variable will be used to compare against all rows, cols, and diags.
		int magicValue = sumArray(squareData[0]);
		
		// testArray is a list of each test combination in row-major order for easy array access.
		int[][] testArray = buildTestArray(squareData); // create test matrix
		
		// test each case
		for(int[] testCase:testArray) { // picking each row to test
			if(magicValue != sumArray(testCase)) {
				return false;
			}
		}
		
		return true;
	}
	
	
	/**
	 * buildTestArray creates all possible test combinations by grabbing value
	 * <br>from all rows, all columns, and two major diagonals from squareData
	 * <br>and put them a 2-dimensional array.
	 * @param squareData is a symmetrical 2-dimensional array of integers
	 * @return 2-dimensional array where all test cases are stored in row-major order
	 */
	public static int[][] buildTestArray(int[][] squareData){
		
		int[][] testArray = new int[squareData.length*2+2][squareData.length];
		int masterRow = 0; // use this var as running row to store data into testArray
		
		// fill testArray with row combinations (top to btm)
		for(int row = 0; row < squareData.length; row++) {
			testArray[masterRow] = squareData[row];
			masterRow++;
		}
		
		// fill testArray with column combinations (left to right)
		for(int col = 0; col < squareData.length; col++) {
			testArray[masterRow] = getColumnFrom2D(col,squareData);
			masterRow++;
		}
		
		// fill testArray with first diagonal (top left to btm right)
		for(int i = 0; i < squareData.length; i++) {
			testArray[masterRow][i] = squareData[i][i];
		}
		masterRow++;
		
		// fill testArray with second diagonal (top right to btm left)
		int row = 0; // start from first row
		int col = squareData.length-1; //start from last column
		for(int i = 0; i < squareData.length; i++) {
			testArray[masterRow][i] = squareData[row][col];
			row++; // traverse downward
			col--; // traverse left-ward
		}
		
		return testArray;
	}
	
	
	/**
	 * getColumnFrom2D allows a 2D array to be accessed by calling column
	 * @param col is an integer specify which column in 2D array to access
	 * @param list is a 2D array of integer to get the column from
	 * @return 1-dimensional array containing values from the column of the 2D array passed
	 */
	public static int[] getColumnFrom2D(int col, int[][] list) {
		if(col < 0 || col > list[0].length) { // catch illegal index
			throw new IllegalArgumentException(
					"The column index you've specified is out of range"
					);
		}
		
		
		int[] columnArray = new int[list.length];
		// fix column index while stepping thru all rows
		for(int i = 0; i < list.length; i++) {
			columnArray[i] = list[i][col]; // build column array
		}
		return columnArray;
	}

	
	/**
	 * sumArray adds up all the integers from a 1-dimensional array passed
	 * @param numList is a 1-dimensional array of integers
	 * @return a sum of all the numbers in the 1-dimensional array passed
	 */
	public static int sumArray(int[] numList) {
		if(numList == null) {
			throw new NullPointerException();
		};
		
		int sum = 0; // running sum
		
		// traverse the array and sum all numbers
		for(int n:numList) {
			sum += n;
		}

		return sum;
	}
	

	/**
	 * drawTitleString method generate the title shown on the panel
	 * @param g Graphics object
	 * @param fontColor color to show the font in
	 */
	public static void drawTitleString(Graphics g, Color fontColor) {
		String title = "CSC 142 Magic Square";
		int xPos = calculateLeftPadding(PAN_WIDTH,title,g);
		g.setColor(fontColor);
		g.drawString(title, xPos, 50);
	}
	
	
	/**
	 * drawEmptyMagicSquare method draws the cell borders
	 * @param g Graphics object
	 * @param penColor color of the borders
	 */
	public static void drawEmptyMagicSquare(Graphics g, Color penColor) {
		g.setColor(penColor);
		int xPos = CELL_WIDTH;		// initialize the first x-position
		int yPos = CELL_HEIGHT;		// initialize the first y-position
		int maxRow = MAX_COLUMN;	// max row = max column for square
		
		// draw the cells row by row
		for(int row = 1; row <= maxRow; row++) {
			
			// draw each column from left to right
			for(int col = 1; col <= MAX_COLUMN; col++) {
				g.drawRect(xPos, yPos, CELL_WIDTH, CELL_HEIGHT);
				xPos += CELL_WIDTH;
			}
			xPos = CELL_WIDTH;		// reset x-position
			yPos += CELL_HEIGHT;	// next row position increment
		}

	}
	
	
	/**
	 * drawFilledMagicSquare method fill the empty cells with given text from a 2D array
	 * @param g Graphics object
	 * @param fontColor color to show text in
	 * @param squareData 2D array filled with data to fill the cells with
	 */
	public static void drawFilledMagicSquare(Graphics g, Color fontColor, int[][] squareData) {

		for(int row = 0; row < squareData.length; row++) {
			for(int col = 0; col < squareData[row].length; col++) {
				
				int xCellLeft = CELL_WIDTH * (col+1);	// x-position of left side of the cell
				int yCellTop = CELL_HEIGHT * (row+1);	// y-position of top side of the cell
				
				String txt = ""+squareData[row][col];
				// get text position in the center of the cell
				int xTxt = calculateLeftPadding(CELL_WIDTH,txt,g) + xCellLeft;
				int yTxt = calculateTopPadding(CELL_HEIGHT,g) + yCellTop;

				g.setColor(fontColor);
				g.drawString(txt, xTxt, yTxt);
				
			}
		}
	}
	
	
	/**
	 * calculateLeftPadding will calculate padding space required to make the given
	 * <br>text center horizontally in a given horizontal space.
	 * 
	 * @param totalSpace how much space there is in horizontal direction
	 * @param txt String to fit in the space
	 * @param g Graphic object
	 * @return an integer value of the left padding require to center text
	 */
	public static int calculateLeftPadding(int totalSpace, String txt, Graphics g) {
		FontMetrics fm = g.getFontMetrics();
		int txtWidth = fm.stringWidth(txt);
		return (totalSpace - txtWidth)/2;
	}
	
	
	/**
	 * calculateTopPadding will calculate padding space required to make the given
	 * <br>text center vertically in a given vertical space. This is font dependent
	 * <br>not text specific.
	 * 
	 * @param totalSpace how much space there is in vertical direction
	 * @param g Graphic object
	 * @return an integer value of the top padding require to center text
	 */
	public static int calculateTopPadding(int totalSpace, Graphics g) {
		FontMetrics fm = g.getFontMetrics();
		// using getDescent looks more centering than getHeight()
		int txtDescent = fm.getDescent();
		return totalSpace/2 + txtDescent;
	}
	
}