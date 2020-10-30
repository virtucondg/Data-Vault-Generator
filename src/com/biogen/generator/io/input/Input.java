package com.biogen.generator.io.input;

/** 
 * This abstract class represents a generic input type.  Subclasses will either override methods here for functionality or
 * keep the default null behaviors.
 * 
 * @author drothste
 *
 */
public abstract class Input {
	/** formatting separator */
	static public String SEPARATOR = "\f\f";
	
	/**
	 * Constructor
	 */
	public Input() {
	}
	
	/**
	 * Print data to the output device
	 * @return the next data value
	 */
	abstract public String read();
	
	/**
	 * Move to the next column of input
	 */
	public void nextColumn() {}
	
	/**
	 * Move to the previous column of output
	 */
	public void previousColumn() {}
	
	/** 
	 * Move to the next row of output
	 */
	public void nextRow() {}
	
	/**
	 * Move to a specific row and column of the input
	 * @param row the row to move to
	 * @param col the column to move to
	 */
	public void moveTo(int row, int col) {}
	
	/**
	 * Open a file for output
	 * @param filename the file name
	 */
	public void open(String filename) {}
	
	/** 
	 * Close the file
	 */
	public void close() {}
	
	/**
	 * Open a workbook
	 * @param name the name of the workbook
	 */
	public void openWorkbook(String name) {}
	
	/**
	 * Select the current sheet
	 * @param name the name of the sheet to switch to 
	 */
	public void setCurrentSheet(String name) {}
	
	/**
	 * Get the current sheet name
	 * @return the current sheet name
	 */
	public String getCurrentSheet() {
		return "";
	}
	
	/**
	 * Convert an Excel-formatted column reference ("A") into it's numeric index (1)
	 * @param column the column name to convert
	 * @return the numeric index of the column reference
	 */
	public int column2Int(String column) {
		int col = 0;
		char[] colChars = column.toUpperCase().toCharArray();
	
		for (char ch : colChars)
			col = col * 26 + (ch - 'A') + 1;
		
		return col - 1;
	}
	
	/**
	 * Format data based on a data type
	 * @param data the data to format
	 * @param format the datatype format to use (Ex. BOOLEAN)
	 * @return the formatted data
	 */
	public String format(String data, String format) {
		String result = data;
		
		if ("BOOLEAN".equalsIgnoreCase(format)) {
			result = "TRUE".equalsIgnoreCase(data) ? "Y" : "N";
		}
		
		return result;
	}
	
}
