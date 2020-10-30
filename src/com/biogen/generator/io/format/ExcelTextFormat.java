package com.biogen.generator.io.format;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

import com.biogen.generator.template.Operation;

/**
 * This class implements formatting for Excel documents
 * @author drothste
 *
 */
public class ExcelTextFormat extends TextFormat {
	private Font font;
	private CellStyle cellStyle;

	/**
	 * Constructor
	 * @param operation the operation to get formatting from
	 */
	public ExcelTextFormat(Operation operation) {
		super(operation);
	}

	/**
	 * Add the format to an Excel workbook
	 * @param workbook the workbook to add formatting to
	 */
	public void initializeFormat(Workbook workbook) {
		font = workbook.createFont();
		if (0 < getSize()) font.setFontHeightInPoints((short)getSize());
		font.setBold(isBold());
		font.setItalic(isItalic());
		font.setUnderline(getUnderlineValue());
		font.setColor(IndexedColors.valueOf(getForeColor()).getIndex());
				
		cellStyle = workbook.createCellStyle();
	    cellStyle.setFont(font);
	    cellStyle.setAlignment(HorizontalAlignment.valueOf(getAlignment()));
	    cellStyle.setVerticalAlignment(VerticalAlignment.valueOf(getVerticalAlignment()));
    	cellStyle.setBorderTop(BorderStyle.valueOf(getBorder()));
		cellStyle.setBorderLeft(BorderStyle.valueOf(getBorder()));
		cellStyle.setBorderRight(BorderStyle.valueOf(getBorder()));
		cellStyle.setBorderBottom(BorderStyle.valueOf(getBorder()));

		if (!getBackColor().equals("WHITE")) {
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle.setFillForegroundColor(IndexedColors.valueOf(getBackColor()).getIndex());
		}
		cellStyle.setWrapText(isWrap());
	}
	
	/**
	 * Get the cell style
	 * @return the cell style
	 */
	public CellStyle getCellStyle() {
		return cellStyle;
	}
	
}


