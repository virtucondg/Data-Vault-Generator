package com.biogen.excelreader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelParser {

	ArrayList<HashMap<String, String>> inputFileInfo = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> outputFileInfo = new ArrayList<HashMap<String, String>>();
	
	static HashMap<String, String> defaultInputMap = new HashMap<String, String>();
	static HashMap<String, String> defaultOutputMap = new HashMap<String, String>();
	
	static private String PROPERTY_FILENAME = "FILENAME";
	static private String PROPERTY_FILETYPE = "FILETYPE";
	static private String PROPERTY_DELIMITER = "DELIMITER";
	static private String PROPERTY_FORCE_QUOTE = "FORCEQUOTE";
	static private String PROPERTY_SKIP_HEADER = "SKIPHEADER";
	static private String PROPERTY_INPUT_FILE_INDEX = "INPUTFILEINDEX";
	static private String PROPERTY_INPUT_SHEET = "INPUTSHEET";
	static private String PROPERTY_COLUMNS = "COLUMNS";
	static private String PROPERTY_COLUMN_NAMES = "COLUMNNAMES";
	static private String PROPERTY_MAX_ROWS = "MAXROWS";
	static private String PROPERTY_SKIP_ROWS = "SKIPROWS";
	static private String PROPERTY_SKIP_COLUMNS = "SKIPCOLUMNS";
	static private String PROPERTY_IGNORE_BLANK_ROWS = "IGNOREBLANKROWS";
	static private String PROPERTY_STRIP_SMART_PUNCTUATION = "STRIPSMARTPUNCTUATION";
	static private String PROPERTY_TEST_COLUMN = "TESTCOLUMN";
	
	static private String FILETYPE_TEXT = "TEXT";
	static private String FILETYPE_XLS = "XLS";
	static private String FILETYPE_XLSX = "XLSX";
	
	public ExcelParser(String paramFileName) {
		readParamFile(paramFileName);
	}
	
	static public String stripSpecialCharacters(String data) {
		String retVal = data;
		
		// smart single quotes
		retVal = retVal.replaceAll("\u2018", "\'");
		retVal = retVal.replaceAll("\u2019", "\'");
		retVal = retVal.replaceAll("\u201A", "\'");
		retVal = retVal.replaceAll("\u201B", "\'");
	    // smart double quotes
		retVal = retVal.replaceAll("\u201C", "\"");
		retVal = retVal.replaceAll("\u201D", "\"");
		retVal = retVal.replaceAll("\u201E", "\"");
		retVal = retVal.replaceAll("\u201F", "\"");
	    // ellipsis
		retVal = retVal.replaceAll("\u2026", "...");
	    // dashes
		retVal = retVal.replaceAll("\u2013", "-");
		retVal = retVal.replaceAll("\u2014", "-");
	    // circumflex
		retVal = retVal.replaceAll("\u02C6", "^");
	    // open angle bracket
		retVal = retVal.replaceAll("\u2039", "<");
	    // close angle bracket
		retVal = retVal.replaceAll("\u203A", ">");
	    // spaces
		retVal = retVal.replaceAll("\u02DC", " ");
		retVal = retVal.replaceAll("\u00A0", " ");
		
		return retVal;
	}
	
	private HashMap<String, String> getInputRow(int index) {
		HashMap<String, String> returnValue = null;
		try {
			returnValue = inputFileInfo.get(index);
		} catch (Exception ignored) {}
		
		if (null==returnValue) {
			returnValue = new HashMap<String, String>();			
			returnValue.put(PROPERTY_FILENAME, "");
			returnValue.put(PROPERTY_FILETYPE, FILETYPE_XLSX);
			returnValue.put(PROPERTY_DELIMITER, "|");
			inputFileInfo.add(index, returnValue);
		}
		
		return returnValue;
	}

	private HashMap<String, String> getOutputRow(int index) {
		HashMap<String, String> returnValue = null;
		try {
			returnValue = outputFileInfo.get(index);
		} catch (Exception ignored) {}
		
		if (null==returnValue) {
			returnValue = new HashMap<String, String>();
			returnValue.put(PROPERTY_FILENAME, "");
			returnValue.put(PROPERTY_FILETYPE, FILETYPE_TEXT);
			returnValue.put(PROPERTY_DELIMITER, "|");
			returnValue.put(PROPERTY_FORCE_QUOTE, "N");
			returnValue.put(PROPERTY_SKIP_HEADER, "N");
			returnValue.put(PROPERTY_INPUT_FILE_INDEX, "1");
			returnValue.put(PROPERTY_INPUT_SHEET, "");
			returnValue.put(PROPERTY_SKIP_ROWS, "0");
			returnValue.put(PROPERTY_MAX_ROWS, "0");
			returnValue.put(PROPERTY_COLUMNS, "");
			returnValue.put(PROPERTY_COLUMN_NAMES, "");
			returnValue.put(PROPERTY_IGNORE_BLANK_ROWS, "N");
			returnValue.put(PROPERTY_STRIP_SMART_PUNCTUATION, "N");
			returnValue.put(PROPERTY_SKIP_COLUMNS, "0");
			returnValue.put(PROPERTY_TEST_COLUMN, "");
			
			try {
				outputFileInfo.add(index, returnValue);
			} catch(Exception ignored) {}
		}
		
		return returnValue;
	}

	private void readParamFile(String paramFileName) {
		Properties props = new Properties();
		HashMap<String, String> currentRecord;
		try {
			FileInputStream fis = new FileInputStream(paramFileName);
			props.load(fis);
			fis.close();
		} catch (Exception ignored) {}
		
		inputFileInfo = new ArrayList<HashMap<String, String>>();
		getInputRow(0);
		outputFileInfo = new ArrayList<HashMap<String, String>>();
		
		for (int i = 0; i < 21; )
			getOutputRow(i++);
		
		for (String propertyName : props.stringPropertyNames()) {
			String[] propParts = propertyName.split("\\.");
			switch(propParts[0].toUpperCase()) {
			case "INPUT" : 
				currentRecord = getInputRow(Integer.valueOf(propParts[1]));
				break;
			case "OUTPUT" :
				currentRecord = getOutputRow(Integer.valueOf(propParts[1]));
				break;
			default:
				currentRecord = null;
				break;
			}
			if (null!=currentRecord) {
				String value = props.getProperty(propertyName);
				if (0==value.indexOf("\"")) value = value.substring(1);
				if ((value.length() - 1) == value.lastIndexOf("\"")) value = value.substring(0,  value.length() - 1);
				currentRecord.put(propParts[2].toUpperCase(), value.trim());
			} else 
				currentRecord = null;
		}
	}
	
	int getColIndex(String col) {
		int retIndex = 0;
		for (int i = 0; i < col.length(); i++)
			retIndex = retIndex * 26 + col.charAt(i) - 'A' + 1;
		
		return retIndex - 1;
	}
	
	String getColFromIndex(int colIndex) {
		int ci = colIndex + 1;
		String retVal = "";
		while (ci > 0) {
			retVal = (char)('A' - 1 + (ci % 27)) + retVal;
			ci /= 27;
		}
		
		return retVal;
	}
	
	DecimalFormat numberFormatter = new DecimalFormat("############.#########");
	SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
	SimpleDateFormat timeFormatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

	public void process() throws Exception {
        for (HashMap<String, String> output : outputFileInfo) {
        	if (!output.get(PROPERTY_FILENAME).equals("")) {
	        	HashMap<String, String> inputFile = inputFileInfo.get(Integer.valueOf(output.get(PROPERTY_INPUT_FILE_INDEX)));
	    		File myFile = new File(inputFile.get(PROPERTY_FILENAME));
	            FileInputStream fis = new FileInputStream(myFile);
	            Workbook myWorkBook = null;
	            ZipSecureFile.setMinInflateRatio(0.001);
	            
	            if (inputFile.get(PROPERTY_FILETYPE).equals(FILETYPE_XLSX))
	            	myWorkBook = new XSSFWorkbook (fis);
	    		else if (inputFile.get(PROPERTY_FILETYPE).equals(FILETYPE_XLS))
	    			myWorkBook = new HSSFWorkbook (fis);
	
	            String sheet = output.get(PROPERTY_INPUT_SHEET).trim();
	            
	            if ("*".equals(sheet)) {
	            	Iterator<Sheet> it = myWorkBook.sheetIterator();
	            	while (it.hasNext()) {
	            		processSheet(myWorkBook, it.next().getSheetName(), output);
	            	}
	            } else {
	            	processSheet(myWorkBook, sheet, output);
	            }
	            
		        if (null != myWorkBook) myWorkBook.close();
        	}
        }        
    }

	private void processSheet(Workbook myWorkBook, String sheet, HashMap<String, String> output) throws Exception {
		BufferedWriter bow;
		String testValue = "test";
		
		Sheet mySheet = myWorkBook.getSheet(sheet);
    
		if (null==mySheet) {
        	System.out.println("Error getting sheet: " + sheet);
        } else {
	        String ofileName = output.get(PROPERTY_FILENAME).replace("~", sheet);

	        System.out.println(mySheet.getSheetName() + ":" + output.get(PROPERTY_COLUMNS) + " -> " + ofileName);
	        
        	bow = new BufferedWriter(new FileWriter(ofileName));
	        
	        Iterator<Row> rowIterator = mySheet.iterator();
	        Row row = null;
	        Cell cell;
            
	        if (output.get(PROPERTY_SKIP_HEADER).equals("Y")) row = rowIterator.next();
	        
	        if (!"".equals(output.get(PROPERTY_COLUMN_NAMES))) {
	        	String labels = output.get(PROPERTY_COLUMN_NAMES).replace(",", output.get(PROPERTY_DELIMITER));
	        	if (output.get(PROPERTY_FORCE_QUOTE).equals("Y"))
	        		labels = "\"" + labels.replace(output.get(PROPERTY_DELIMITER), "\"" + output.get(PROPERTY_DELIMITER) + "\"");
	        	
	        	bow.write(labels);
	        	bow.newLine();
	        }
	        
	        int countRow = 0;
	        int maxRow = Integer.valueOf("0" + output.get(PROPERTY_MAX_ROWS));
	        int skipRow = Integer.valueOf("0" + output.get(PROPERTY_SKIP_ROWS));
	        int skipColumn = Integer.valueOf("0" + output.get(PROPERTY_SKIP_COLUMNS));
	        boolean keepBlankRow = "N".equals(output.get(PROPERTY_IGNORE_BLANK_ROWS));
	        boolean stripSmartPunctuation = "Y".equals(output.get(PROPERTY_STRIP_SMART_PUNCTUATION));
	        int testColunmn = getColIndex(output.get(PROPERTY_TEST_COLUMN));
	        
	        if (0==maxRow) 
	        	maxRow = mySheet.getLastRowNum() + 1;

	        String forceQuoteChar = (output.get(PROPERTY_FORCE_QUOTE).equals("Y") ? "\"" : "");
	        String delimiter = output.get(PROPERTY_DELIMITER);
	        ArrayList<Integer> colList = new ArrayList<Integer>();
	        
	        for (String col : output.get(PROPERTY_COLUMNS).split(","))
	        	if (col.charAt(0)=='*') {
	        		if (null==row) {
	        			colList.add(-1);
	        		} else {
		                for (int i = 0; i < row.getLastCellNum(); ) 
		                	colList.add(i++);
	        		}
	        	} else if (-1 == col.indexOf("-")) {
					colList.add(getColIndex(col));
	        	} else {
					String[] colRange = col.split("-");
					int start = getColIndex(colRange[0]);
					int end = getColIndex(colRange[1]);
					for (int i = start; i <= end; )
						colList.add(i++);
				}							
	        
	        if (0==colList.size())
	        	colList.add(-1);
	        
	        int numCols = colList.size();
            int[] colNum = new int[numCols];
            for (int i = colList.size(); i-- > 0; ) {
            	colNum[i] = colList.get(i).intValue();
            }
            	
	        while (rowIterator.hasNext() && (countRow++ <= maxRow)) {
	            row = rowIterator.next();
	            StringBuffer buf = new StringBuffer();
	            String value;
	            int maxCol = row.getLastCellNum();
	            
	            if (-1==colNum[0]) { // no columns specified so create a list of all columns
	            	colNum = new int[maxCol];
	                for (int i = 0; i < maxCol; ) {
	                	colNum[i] = i++;
	                }
	                
	                numCols = colNum.length;
	            }
	            
	            if (-1 < testColunmn) {	        
	            	try {
	            		testValue = row.getCell(testColunmn).getStringCellValue();
	            	} catch (Exception ex) {
	            		testValue = "";
	            	}
	            }
	            
	            if (0 < testValue.length()) {
		            for (int col = skipColumn; col-- > 0; )
		            	buf.append(delimiter);
		            
		            for (int col = 0; col < numCols; ) {
		            	buf.append(forceQuoteChar);
	
		                if ((-1 < colNum[col])) { // && (colNum[col] < maxCol)) {
			            	cell = row.getCell(colNum[col]);
			            	
			            	if (null != cell)
				                switch (cell.getCellType()) {
				                	case FORMULA: 
					                case STRING:
					                	value = cell.getStringCellValue();
					                	if (stripSmartPunctuation) value = stripSpecialCharacters(value);
					                	
					                	if ((-2 < value.indexOf("\n") + value.indexOf(delimiter)) && forceQuoteChar.equals(""))
					                		value = "\"" + value + "\"";
	
					                	buf.append(value);
					                    break;
					                case NUMERIC:
					                	if (DateUtil.isCellDateFormatted(cell)) {
					                		BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
					                		if (bd==bd.setScale(0, BigDecimal.ROUND_DOWN))
					                			buf.append(dateFormatter.format(cell.getDateCellValue()));
					                		else
					                			buf.append(timeFormatter.format(cell.getDateCellValue()));
					                	}
					                	else
					                		buf.append(numberFormatter.format(cell.getNumericCellValue()));
					                    break;
					                case BOOLEAN:
					                	buf.append(String.valueOf(cell.getBooleanCellValue()));
					                    break;
					                default :
				                }
		                }
		                
		            	buf.append(forceQuoteChar);
	
		            	if (++col < numCols) 
		            		buf.append(delimiter);
		            }
		            
		            String dataRow = buf.toString();
		            
		            if (keepBlankRow || !"".equals(dataRow.replace(delimiter, "").replace("\"", "").trim())) {
		            	bow.write(dataRow);
		            	bow.newLine();;
		            }
		            
		            for (int i = skipRow; i-- > 0 && rowIterator.hasNext(); )
		            	row = rowIterator.next();
	            }
	        }
	        
	        bow.flush();
	        bow.close();
        }	
	}
	
	static public void main (String[] args) throws Exception {
//		System.out.println("‘This is “a”- test’ …– fff");
//		System.out.println(ExcelParser.stripSpecialCharacters("‘This is “a”- test’ …– fff"));
		ExcelParser er = new ExcelParser("C:/Projects/Data Catalog/COE_Vendor_Template.txt");
		System.out.println(er.getColIndex("AQ"));
		er.process();
		
		if (0==args.length) {
			System.out.println("ExcelParser usage:");
			System.out.println("\tExcelParser <parameter file>");
		} else {
			System.out.println("Processing " + args[0] + ":");
//		ExcelParser er = new ExcelParser(args[0]);
		
		er.process();
		}
	}
}
