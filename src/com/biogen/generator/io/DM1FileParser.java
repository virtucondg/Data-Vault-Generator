package com.biogen.generator.io;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.biogen.generator.ertools.erstudio.Diagram;
import com.biogen.generator.ertools.erstudio.helpers.HelperDictionary;
import com.biogen.generator.ertools.erstudio.raw.RawDataObject;
import com.biogen.generator.io.output.ExcelOutput;
import com.biogen.utils.Debug;

/** 
 * This class processes an ER/Studio .DM1 file.
 * @author drothste
 *
 */
public class DM1FileParser {
	private String filename = null;
	private static char NEWLINE = '\n';
	private static char SPACE = ' ';
	private enum TokenizerState {NORMAL, QUOTE_MODE};
	private final int quoteChar = (int)'"';
	private int delimeterChar = (int)',';
	private boolean surroundingSpacesNeedQuotes = false;

	private StringBuilder currentColumn = new StringBuilder();
	private StringBuilder currentRow = new StringBuilder();

	/**
	 * Constructor
	 */
	public DM1FileParser() {
	}

	/**
	 * Constructor
	 * @param filename the file to parse
	 */
	public DM1FileParser(String filename) {
		this.filename = filename;
	}
	
	/**
	 * Parse the .DM1 file
	 */
	public void parseERS() {
		if (null!=this.filename)
			try {
				parse(this.filename);
			} catch (IOException ex) {
				Debug.printStackTrace(ex);
			}
	}
	
	/**
	 * Dump .DM1 file to Excel
	 * @param filename the file to parse
	 * @throws IOException if the file cannot be processed 
	 */
	public void dump(String filename) throws IOException {
		LineNumberReader lnr = new LineNumberReader(new FileReader(filename));
		ArrayList<String> columns = new ArrayList<String>();
		ExcelOutput eo = new ExcelOutput();
//		int columnCount = 0;
		boolean readObj;
		eo.createWorkbook(filename.replace(".dm1",  ".xlsx"));

		try {
			while (readColumns(lnr, columns)) {
				eo.createSheet(columns.get(0));
        		readObj = true;
        		while (readObj) {
        			readColumns(lnr, columns);
        			if (0==columns.size()) 
        				readObj = false;
        			else {
//        				if (columnCount++ < 10) {
	        				for (String value : columns) {
	        					eo.print(value, "", null, null);
	        					eo.nextColumn();
	        				}
	        				eo.nextRow();
//        				}
        			}
        		}
        		
        		eo.endSheet();
			}
	    } catch (Exception ex) {
	    	Debug.printStackTrace(ex);
	    }
	    finally {
	    	if(null != lnr) 
	    		lnr.close();
	    	
	    	eo.close();
	    }
	}
	
	/**
	 * Parse the .DM1 file into a {@link Diagram} structure
	 * @param filename the file to parse
	 * @return a {@link Diagram} with all child objects
	 * @throws IOException if the file cannot be processed
	 */
	public Diagram parse(String filename) throws IOException {
		RawDataObject obj = null;
		LineNumberReader lnr = new LineNumberReader(new FileReader(filename));
		ArrayList<String> columns = new ArrayList<String>();
		HashMap<String, RawDataObject> objects = new HashMap<String, RawDataObject>();

		Debug.println("Direct loading file: " + filename, true);
		
		try {
			while (readColumns(lnr, columns)) {
    			obj = new RawDataObject(columns.get(0));

	        	Debug.println("\t" + columns.get(0), true);
        		objects.put(columns.get(0), obj);
	        	
        		readColumns(lnr, columns);
        		if (null != obj) {
        			columns.add("RawType");
        			obj.setHeader(columns);
        		}
        		boolean readObj = true;
        		while (readObj) {
        			readColumns(lnr, columns);
        			if (0==columns.size()) 
        				readObj = false;
        			else if (null != obj) {
        				columns.add("DM1");
        				obj.setProperties(columns);
        			}
        		}

        		obj = null;
			}
	    } catch (Exception ex) {
	    	Debug.printStackTrace(ex);
	    }
	    finally {
	    	if(null != lnr) 
	    		lnr.close();
	    }
		
		for (RawDataObject os : objects.values())
			Debug.println("\t\t" + os.getName() + "=" + String.valueOf(os.getPropertiesCount()));
		
		HelperDictionary.addObjects(objects);
		ArrayList<Diagram> diagrams = Diagram.create(null, objects, null);
		
		if (0==diagrams.size())
			return null;
		
		return diagrams.get(0);
	}
	
	/**
	 * Read a line from the file
	 * @param lnr the Reader to read the line from
	 * @param columns the data that is read from the line
	 * @return <code>TRUE</code> if a line was read successfully; <code>FALSE</code> otherwise
	 * @throws IOException if there is a problem with the source file
	 */
	public boolean readColumns(LineNumberReader lnr, ArrayList<String> columns) throws IOException {
		columns.clear();
		currentColumn.setLength(0);
		currentRow.setLength(0);
		
		String line = lnr.readLine();
		if(null==line)
			return false; 
		
		currentRow.append(line);
		
		TokenizerState state = TokenizerState.NORMAL;
		int quoteScopeStartingLine = -1;
		int potentialSpaces = 0; 
		int charIndex = 0;
		while (true) {
			boolean endOfLineReached = (charIndex == line.length());
			
			if (endOfLineReached)
			{
				if (TokenizerState.NORMAL.equals(state)) {
					if (!surroundingSpacesNeedQuotes) {
						appendSpaces(currentColumn, potentialSpaces);
					}
					if ((0 < columns.size()) || (currentColumn.length() > 0))
						columns.add(currentColumn.length() > 0 ? currentColumn.toString() : null);

					return true;
				} else {
					currentColumn.append(NEWLINE);
					currentRow.append(NEWLINE); // specific line terminator lost, \n will have to suffice
					
					charIndex = 0;

					if (null == (line = lnr.readLine())) {
						throw new IOException(
							String
								.format(
									"Unexpected end of file while reading quoted column beginning on line %d and ending on line %d",
									quoteScopeStartingLine, lnr.getLineNumber()));
					}
					
					currentRow.append(line); 
					
				    if (line.length() == 0) {
                        continue;
				    }
				}
			}
			
			char c = line.charAt(charIndex);
			
			if (TokenizerState.NORMAL.equals(state)) {
				if (c == delimeterChar) {
					if (!surroundingSpacesNeedQuotes) {
						appendSpaces(currentColumn, potentialSpaces);
					}
					columns.add(currentColumn.length() > 0 ? currentColumn.toString() : null); 
					potentialSpaces = 0;
					currentColumn.setLength(0);
					
				} else if (c == SPACE) {
					potentialSpaces++;
				}
				else if (c == quoteChar) {
					state = TokenizerState.QUOTE_MODE;
					quoteScopeStartingLine = lnr.getLineNumber();
					
					if (!surroundingSpacesNeedQuotes || currentColumn.length() > 0) {
						appendSpaces(currentColumn, potentialSpaces);
					}
					potentialSpaces = 0;
					
				} else {
					if (!surroundingSpacesNeedQuotes || currentColumn.length() > 0) {
						appendSpaces(currentColumn, potentialSpaces);
					}
					
					potentialSpaces = 0;
					currentColumn.append(c);
				}
				
			} else {
				if (c == quoteChar) {
					int nextCharIndex = charIndex + 1;
					boolean availableCharacters = nextCharIndex < line.length();
					boolean nextCharIsQuote = availableCharacters && line.charAt(nextCharIndex) == quoteChar;
					if (nextCharIsQuote) {
						currentColumn.append(c);
						charIndex++;		
					} else {
						state = TokenizerState.NORMAL;
						quoteScopeStartingLine = -1; // reset ready for next multi-line cell
					}
				} else {
					currentColumn.append(c);
				}
			}
			
			charIndex++; // read next char of the line
		}
	}
	
	/**
	 * Add spaces to a string
	 * @param sb the {@link StringBuilder} to add spaces to
	 * @param spaces the number of spaces to add
	 */
	private static void appendSpaces(StringBuilder sb, int spaces) {
		for (int i = spaces; i-- > 0;) {
			sb.append(SPACE);
		}
	}
}
