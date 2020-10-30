package com.biogen.generator.io.output;

import java.util.HashMap;

import com.biogen.generator.io.format.TextFormat;
import com.biogen.generator.template.Operation;

/** 
 * This abstract class represents a generic output type.  Subclasses will either override methods here for functionality or
 * keep the default null behaviors.
 * 
 * @author drothste
 *
 */
public abstract class Output {
	/** formatting separator */
	static public String SEPARATOR = "\f\f";

	private HashMap<String, TextFormat> formats = new HashMap<String, TextFormat>();
	private String distinctRows = null;
	private String sortedRows = null;
	
	/**
	 * Constructor
	 */
	public Output() {
	}
	
	/**
	 * Print data to the output device
	 * @param data the data to print
	 * @param format the datatype format to use
	 * @param textFormat the {@link com.biogen.generator.io.format.TextFormat TextFormat} object to use; found in formats
	 * @param hyperlink a hyperlink value; will be used if the subclass supports use it
	 */
	abstract public void print(String data, String format, String textFormat, String hyperlink);
	
	/**
	 * Move to the next column of output
	 */
	public void nextColumn() {}
	
	/**
	 * Move to the previous column of output
	 */
	public void previousColumn() {}
	
	/**
	 * Should columns be wrapped in the output?
	 * @param wrap <code>TRUE</code> if columns should be wrapped; <code>FALSE</code> otherwise
	 */
	public void wrapColumn(boolean wrap) {}
	
	/** 
	 * Move to the next row of output
	 */
	public void nextRow() {}
	
	/**
	 * Move to a specific row and column of the output
	 * @param row the row to move to
	 * @param col the column to move to
	 */
	public void moveTo(int row, int col) {}
	
	/**
	 * Merge several columns together
	 * @param numCol the number of columns to merge with this one
	 */
	public void merge(int numCol) {}
	
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
	 * Create a workbook
	 * @param name the name of the workbook
	 */
	public void createWorkbook(String name) {}
	
	/**
	 * Create a sheet
	 * @param name the name of the sheet
	 */
	public void createSheet(String name) {}
	
	/**
	 * Sort the rows in the sheet
	 */
	public void sortSheet() {}
	
	/** 
	 * End the sheet
	 */
	public void endSheet() {}
	
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
	 * Should duplicate rows be removed?
	 * @param value semicolon (";") delimited list of columns for distinct check
	 */
	public void setDistinctRows(String value) {
		this.distinctRows = value;
	}
	
	/**
	 * Get the value for distinct rows
	 * @return the distinct rows value
	 */
	public String getDistinctRows() {
		return this.distinctRows;
	}
	
	/**
	 * Is distinct rows set?
	 * @return <code>TRUE</code> if distinct rows; <code>FALSE</code> otherwise
	 */
	public boolean isDistinctRows() {
		return (null != this.distinctRows);
	}
	
	/**
	 * If distinct rows set, the list of columns to use for distinct rows
	 * @return <code>NULL</code> for all rows; array of columns to use for distinct row checks
	 */
	public String[] getDistinctColumns() {
		if (null != this.distinctRows)
			return this.distinctRows.split(";");
		
		return new String[0];
	}
	
	/** 
	 * Set if rows should be sorted
	 * @param value semicolon (";") delimited list of columns to use for sorting
	 */
	public void setSortedRows(String value) {
		this.sortedRows = value;
	}
	
	/**
	 * Get the value for sorted rows
	 * @return the value for sorted rows
	 */
	public String getSortedRows() {
		return this.sortedRows;
	}
	
	/** 
	 * Should rows be sorted?
	 * @return <code>TRUE</code> if rows should be sorted; <code>FALSE</code> otherwise
	 */
	public boolean isSortedRows() {
		return (null != this.sortedRows);
	}
	
	/**
	 * If sorted rows set, the list of columns to use for sorted rows
	 * @return <code>NULL</code> for all rows; array of columns to use for sorting
	 */
	public String[] getSortColumns() {
		if (null != this.sortedRows)
			return this.sortedRows.split(";");
		
		return new String[0];
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
	 * @param format the data type format to use (Ex. BOOLEAN)
	 * @return the formatted data
	 */
	public String format(String data, String format) {
		String result = data;
		
		if ("BOOLEAN".equalsIgnoreCase(format)) {
			result = "TRUE".equalsIgnoreCase(data) ? "Y" : "N";
		}
		
		return result;
	}
	
	/**
	 * Create a text format from a template element
	 * @param operation the template element
	 */
	public void createTextFormat(Operation operation) {
		TextFormat tf = new TextFormat(operation);
		putTextFormat(tf);
	}
	
	/**
	 * Add a {link TextFormat} to the list of output formats
	 * @param tf the {@link com.biogen.generator.io.format.TextFormat TextFormat} to add
	 */
	public void putTextFormat(TextFormat tf) {
		formats.put(tf.getName().toUpperCase(), tf);
	}
	
	/**
	 * Get the map of {@link com.biogen.generator.io.format.TextFormat TextFormat}s
	 * @return the map
	 */
	public HashMap<String, TextFormat> getFormats() {
		return formats;
	}
	
	/**
	 * Find a {@link com.biogen.generator.io.format.TextFormat TextFormat} by name
	 * @param name the name to find
	 * @return the {@link com.biogen.generator.io.format.TextFormat TextFormat} if found; <code>NULL</code> otherwise
	 */
	public TextFormat getFormat(String name) {
		return formats.get(name.toUpperCase());
	}
	
}
