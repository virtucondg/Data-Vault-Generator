package com.biogen.generator.io.output;

import java.io.FileOutputStream;
import java.util.TreeMap;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.biogen.generator.io.format.ExcelTextFormat;
import com.biogen.generator.io.format.TextFormat;
import com.biogen.generator.template.Operation;
import com.biogen.utils.Debug;

/**
 * This class creates Excel workbooks.
 * @author drothste
 *
 */
public class ExcelOutput extends Output {

	private Workbook workbook = null;
	private CreationHelper helper;
	private Sheet sheet = null;
	private Row row = null;
	private int rowNum;
	private Cell cell = null;
	private int cellNum;
	private String filename;
	private int maxColumn = 0;
	
	/**
	 * Constructor
	 */
	public ExcelOutput() {
		super();
	}
	
	@Override
	public void open(String filename) {
		Debug.println("Opening Workbook:" + filename, true);
		
		createWorkbook(filename);
	}
	
	@Override
	public void print(String data, String format, String textFormat, String hyperlink) {
		if (null!=data)
			cell.setCellValue(super.format(data, format));

		if (null!=textFormat) {
			ExcelTextFormat tf = (ExcelTextFormat)getFormat(textFormat);
			cell.setCellStyle(tf.getCellStyle());
		}
		if (null!=hyperlink) {
			Hyperlink hl = helper.createHyperlink(HyperlinkType.DOCUMENT);
			hl.setAddress(hyperlink);
			hl.setLabel(data);
			hl.setFirstColumn(cellNum - 1);
			hl.setLastColumn(cellNum - 1);
			hl.setFirstRow(rowNum - 1);
			hl.setLastRow(rowNum - 1);
			
			cell.setHyperlink(hl);
		}
	}

	@Override
	public void nextRow() {
		_moveRow(rowNum);
	}
	
	private void _moveRow(int _rowNum) {
		if (null!=row && (-1 < row.getLastCellNum())) {
			Cell c = row.getCell(row.getLastCellNum());
			
			if (null!=c)
				row.removeCell(c);
		}

		row = sheet.getRow(_rowNum);
		if (null==row)
			row = sheet.createRow(_rowNum);
		rowNum = _rowNum + 1;
		_moveColumn(0);
	}

	@Override
	public void nextColumn() {
		_moveColumn(cellNum);
	}
	
	private void _moveColumn(int _cellNum) {
		cell = row.getCell(_cellNum);
		if (null==cell)
			cell = row.createCell(_cellNum);
		cellNum = _cellNum + 1;

		if (maxColumn < cellNum)
			maxColumn = cellNum;
	}
	
	@Override
	public void moveTo(int row, int col) {
		if (0 < row)
			_moveRow(row - 1);
		
		if (0 < col)
			_moveColumn(col - 1);
	}
	
	@Override
	public void merge(int numCol) {
		CellStyle cellStyle = cell.getCellStyle();
		int startCellNum = cellNum;
		for (int i = 1; i < numCol; i++) {
			_moveColumn(cellNum);
			cell.setCellStyle(cellStyle);
		}
		
		sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, startCellNum - 1, startCellNum + numCol - 2));
	}
	
	@Override
	public void createWorkbook(String name) {
		filename = name;
		workbook = new XSSFWorkbook();
		helper = workbook.getCreationHelper();
		initializeTextFormats();
	}

	@Override
	public void createSheet(String name) {
		if (null!=workbook) {
			Debug.print("Opening Sheet:" + name + "...", true);
			sheet = workbook.getSheet(name);
			if (null==sheet)
				sheet = workbook.createSheet(name);
			rowNum = 0;
			maxColumn = 0;
			nextRow();
			Debug.println("Done", true);
		}
	}
	
	private String _getRowData(int row, String[] cols) {
		StringBuffer data = new StringBuffer();
		Row r = sheet.getRow(row);
		if (null!=r) {
			if (0==cols.length)
				for (int col = r.getFirstCellNum(); col < r.getLastCellNum(); col++) {
					data.append(r.getCell(col).getStringCellValue()).append(SEPARATOR);
				}
			else {
				int c;
				for (String col : cols) {
					if ((c = column2Int(col)) <= r.getLastCellNum())
						data.append(r.getCell(c).getStringCellValue());
						
				data.append(SEPARATOR);				
				}
			}
		} else {
			data.append("");
		}
		
		return data.toString();
	}
	
	private void _purgeRows(int startRow) {
		sheet.shiftRows(startRow, sheet.getLastRowNum(), -1);
		if (true) {
		   int nFirstDstRow = startRow - 1;
		   int nLastDstRow = sheet.getLastRowNum();
		   for (int nRow = nFirstDstRow; nRow <= nLastDstRow; ++nRow) {
			    Row row = sheet.getRow(nRow);

			    if (row != null) {
			    	String msg = "Row[rownum=" + row.getRowNum()
				       + "] contains cell(s) included in a multi-cell array formula. "
				       + "You cannot change part of an array.";

				     for (Cell c : row) {
				    	 ((XSSFCell) c).updateCellReferencesForShifting(msg);
				     }
			    }
		   }
		}
	}
	
	private void _sortSheet(String[] sortCols) {
		TreeMap<String, Row> rows = new TreeMap<String, Row>();
		
		for (int r1 = sheet.getFirstRowNum(); r1 <= sheet.getLastRowNum(); r1++) {
			rows.put(_getRowData(r1, sortCols) + String.valueOf(r1), sheet.getRow(r1));
		}
			
		int rowCount = 0;
		
		for (Row row : rows.values())
			row.setRowNum(rowCount++);
	}
	
	private final String[] NO_COLS = new String[0];
	
	private void _distinctSheet() {
		TreeMap<String, String> map = new TreeMap<String, String>();
		
		Debug.println("Removing duplicate rows from sheet", true);
		for (int row = sheet.getLastRowNum(); row >= sheet.getFirstRowNum(); row--) {
			String data = _getRowData(row, NO_COLS);
			
			if (null==map.get(data)) {
				map.put(data,  "");
			} else {
				_purgeRows(1 + row);
			}
		}
	
		_sortSheet(getDistinctColumns());
		
		map.clear();
	}

	@Override
	public void endSheet() {
		if (1==row.getLastCellNum())
			sheet.removeRow(row);
		
//		if (isDistinctRows())
//			_distinctSheet();
		
//		if (isSortedRows())
//			_sortSheet(getSortColumns());
		
		for (int i = 0; i < maxColumn; )
			sheet.autoSizeColumn(i++);

		Debug.println("Closing Sheet " + sheet.getSheetName() + ".", true);
	}
	
	@Override
	public void close() {
		sheet = null;
		row = null;
		cell = null;
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			workbook.write(fos);
			fos.close();
			workbook.close();
			workbook = null;
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}
		Debug.println("Workbook closed.", true);
	}

	private void initializeTextFormats() {
		for (TextFormat tf : getFormats().values()) {
			if (tf instanceof ExcelTextFormat) {
				((ExcelTextFormat) tf).initializeFormat(workbook);
			}
		}
	}
	
	@Override
	public void createTextFormat(Operation operation) {
		ExcelTextFormat tf = new ExcelTextFormat(operation);
		putTextFormat(tf);

		if (null!=workbook)
			tf.initializeFormat(workbook);
	}
	
}
