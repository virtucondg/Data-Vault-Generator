package com.biogen.excelreader.file;

class ProcessFileXLS extends ProcessFile {
	public ProcessFileXLS(String fileName) {
		this.fileName = fileName;
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
	
	

}
