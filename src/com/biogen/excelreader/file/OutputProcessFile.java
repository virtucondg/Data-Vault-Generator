package com.biogen.excelreader.file;

public class OutputProcessFile extends ProcessFile {
	public boolean isForceQuote() {
		return super.isForceQuote();
	}

	public boolean isSkipHeaderRow() {
		return super.isSkipHeaderRow();
	}

	public int getInputFileIndex() {
		return super.getInputFileIndex();
	}

	public String getInputSheetName() {
		return super.getInputSheetName();
	}

	public String[] getColumnList() {
		return super.getColumnList();
	}

	public String[] getColumnNames() {
		return super.getColumnNames();
	}

	public int getMaxRows() {
		return super.getMaxRows();
	}

	public boolean setSheet() {
		return super.setSheet();
	}
		
	public boolean createSheet() {
		return super.createSheet();
	}
		
	public boolean writeColumn() {
		return super.writeColumn();
	}

}
