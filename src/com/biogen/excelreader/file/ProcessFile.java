package com.biogen.excelreader.file;

abstract class ProcessFile {
	String fileName;
	String fileType;
	String delimiter;
	boolean forceQuote;
	boolean skipHeaderRow;
	int inputFileIndex;
	String inputSheetName;
	String[] columnList;
	String[] columnNames;
	int maxRows = -1;
	ProcessFile processFile;
	
	static private String PROPERTY_INPUT = "INPUT";
	static private String PROPERTY_OUTPUT = "OUTPUT";
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
	
	static private String FILETYPE_TEXT = "TEXT";
	static private String FILETYPE_XLS = "XLS";
	static private String FILETYPE_XLSX = "XLSX";

	public boolean open() {
		processFile = null;
		
		if (fileType.equals(FILETYPE_XLS))
			processFile = new ProcessFileXLS(fileName);
				
		return (null!=processFile);
	}
		
	protected boolean setSheet() {
		return processFile.setSheet();
	}
		
	protected boolean createSheet() {
		return processFile.createSheet();
	}
		
	public boolean nextRow() {
		return processFile.nextRow();
	}
	
	protected String readColumn() {
		return processFile.readColumn();
	}
	
	protected boolean writeColumn() {
		return processFile.writeColumn();
	}
	
	
	public String getFileName() {
		return this.fileName;
	}

	public String getFileType() {
		return this.fileType;
	}

	public String getDelimiter() {
		return this.fileName;
	}

	boolean isForceQuote() {
		return this.forceQuote;
	}

	boolean isSkipHeaderRow() {
		return this.skipHeaderRow;
	}

	int getInputFileIndex() {
		return this.inputFileIndex;
	}

	String getInputSheetName() {
		return this.inputSheetName;
	}

	String[] getColumnList() {
		return this.columnList;
	}

	String[] getColumnNames() {
		return this.columnNames;
	}

	int getMaxRows() {
		return this.maxRows;
	}

	String[] smartSplit(String value, String delimiter) {
		String[] returnValue = value.split(delimiter);
		for (int i = returnValue.length; i-- > 0; ) {
			if (0==returnValue[i].indexOf("\"")) returnValue[i] = returnValue[i].substring(1);
			if ((returnValue[i].length() -1)==returnValue[i].lastIndexOf("\"")) returnValue[i] = returnValue[i].substring(0, returnValue[i].length() -1);
		}
		
		return returnValue;
	}

	public void setProperty(String propertyName, String value) {
		if (propertyName.toUpperCase().equals(PROPERTY_FILENAME))
			this.fileName = value;
		else if (propertyName.toUpperCase().equals(PROPERTY_FILETYPE))
			this.fileType = value;
		else if (propertyName.toUpperCase().equals(PROPERTY_DELIMITER))
			this.delimiter = value;
		else if (propertyName.toUpperCase().equals(PROPERTY_FORCE_QUOTE))
			this.forceQuote = "Y".equals(value.toUpperCase());
		else if (propertyName.toUpperCase().equals(PROPERTY_SKIP_HEADER))
			this.skipHeaderRow = "Y".equals(value.toUpperCase());
		else if (propertyName.toUpperCase().equals(PROPERTY_INPUT_FILE_INDEX))
			this.inputFileIndex = Integer.valueOf(value);
		else if (propertyName.toUpperCase().equals(PROPERTY_INPUT_SHEET))
			this.inputSheetName = value;
		else if (propertyName.toUpperCase().equals(PROPERTY_COLUMNS))
			this.columnList = smartSplit(value, ",");
		else if (propertyName.toUpperCase().equals(PROPERTY_COLUMN_NAMES))
			this.columnNames = smartSplit(value, ",");
		else if (propertyName.toUpperCase().equals(PROPERTY_MAX_ROWS))
			this.maxRows = Integer.valueOf(value);
	}
}
