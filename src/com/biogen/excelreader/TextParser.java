package com.biogen.excelreader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TextParser {

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
	static private String PROPERTY_IGNORE_BLANK_ROWS = "IGNOREBLANKROWS";
	
	static private String FILETYPE_TEXT = "TEXT";
	static private String FILETYPE_XLS = "XLS";
	static private String FILETYPE_XLSX = "XLSX";
	
	public TextParser(String paramFileName) {
		readParamFile(paramFileName);
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
			
			try {
				outputFileInfo.add(index, returnValue);
			} catch(Exception ignored) {
				index = index;
			}
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
		
		for (int i = 0; i < 21; ) {
			getInputRow(i);
			getOutputRow(i++);
		}
		
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
			retIndex = retIndex * 27 + col.charAt(i) - 'A' + 1;
		
		return retIndex;
	}
	
	String getColFromIndex(int colIndex) {
		int ci = colIndex;
		String retVal = "";
		while (ci > 0) {
			retVal = (char)('A' - 1 + (ci % 27)) + retVal;
			ci /= 27;
		}
		
		return retVal;
	}
	
	public void process() throws Exception {
		BufferedWriter bow;
		BufferedReader bir;
		
        for (HashMap<String, String> output : outputFileInfo) {
        	if (!output.get(PROPERTY_FILENAME).equals("")) {
	        	HashMap<String, String> inputFile = inputFileInfo.get(Integer.valueOf(output.get(PROPERTY_INPUT_FILE_INDEX)));
	    		File myFile = new File(inputFile.get(PROPERTY_FILENAME));
	    		for (File file : myFile.listFiles()) {
	    		  if (-1 < file.getName().indexOf("txt") && (-1==file.getName().indexOf(".gz"))) {
	    			bir = new BufferedReader(new FileReader(file));
	    			bow = new BufferedWriter(new FileWriter(output.get(PROPERTY_FILENAME) + "/" + file.getName()));
	            
			        int maxRow = Integer.valueOf("0" + output.get(PROPERTY_MAX_ROWS));
			        
			        String str;
			        
		            for (int i = maxRow; (i-- > 0) && (null!=(str=bir.readLine()));) {
		            	bow.write(str);
		            	bow.newLine();
		            }
		            bir.close();
		            bow.flush();
		            bow.close();
	    		  }
	    		}
        	}
        }     
    }
	
	static public void main (String[] args) throws Exception {
//		if (0==args.length) {
//			System.out.println("ExcelParser usage:");
//			System.out.println("\tExcelParser <parameter file>:");
//		} else {
//			System.out.println("Processing " + args[0] + ":");
//		ExcelParser er = new ExcelParser("C:\\Users\\drothste\\Documents\\Parameters.txt");
		TextParser er = new TextParser("C:\\Projects\\Candle\\Specs\\Payer Analytics\\Sample Files\\Payer Analytics.txt");
			er.process();
//		}
	}
}
