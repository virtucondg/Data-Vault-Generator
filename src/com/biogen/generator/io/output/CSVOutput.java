package com.biogen.generator.io.output;

import java.io.File;
import java.io.FileWriter;
import java.util.TreeMap;

/**
 * This class will create CSV files
 * @author drothste
 *
 */
public class CSVOutput extends Output {

	private File file;
	private FileWriter writer;
	private int col;
	private String fileName;
	private TreeMap<String, String> rows = new TreeMap<String, String>();
	private StringBuilder sb = new StringBuilder();
	
	/**
	 * Constructor
	 */
	public CSVOutput() {
		super();
	}
	
	@Override
	public void open(String filename) {
		fileName = filename;
		
		System.out.println("Opening file:" + fileName);
		try {
			file = new File(fileName);
			writer = new FileWriter(file);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		col = 0;
	}
	
	@Override
	public void print(String data, String format, String textFormat, String hyperlink) {
		_moveColumn(col + 1);
		
		if (null != data)
			try {
				if (data.contains(" ") || data.contains("\n") || data.contains("\t") || data.contains("\"") || data.contains(","))
					sb.append("\"" + data.trim().replaceAll("\"", "\"\"") + "\"");
				else
					sb.append(data.trim());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
	}

	private void _write(String data) {
		try {
			writer.write(data + "\n");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	private void _flush() {
		try {
			writer.flush();
		} catch (Exception ignored) {}
	}
	
	@Override
	public void nextRow() {
		if (isDistinctRows() || isSortedRows()) {
			String[] sortCols = new String[0];
			String[] dataCols = sb.toString().split(SEPARATOR);
			StringBuilder key = new StringBuilder();
			int col;
			
			if (isDistinctRows()) 
				sortCols = getDistinctColumns();
			else
				sortCols = getSortColumns();	
			
			for (String sortCol : sortCols) {
				if ((col = column2Int(sortCol)) <= dataCols.length)
					key.append(dataCols[col]);
				key.append(SEPARATOR);
			}
			
			if (isDistinctRows())
				rows.put(key.toString() + sb.toString(), sb.toString().replaceAll(SEPARATOR, ","));
			else
				rows.put(key.toString() + String.valueOf(rows.size()), sb.toString().replaceAll(SEPARATOR, ","));
		} else {
			_write(sb.toString().replaceAll(SEPARATOR, ","));
			_flush();
		}
		sb.setLength(0);
		
		col = 0;
	}
	
	@Override
	public void nextColumn() {}

	private void _moveColumn(int _cellNum) {
		for (; col < _cellNum;)
			if (col++ > 0)
				
				try {
					sb.append(SEPARATOR);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
	}
	
	@Override
	public void moveTo(int row, int col) {
		_moveColumn(col);
	}
	
	@Override
	public void close() {
		try {
			if (isDistinctRows() || isSortedRows()) {
				for (String row : rows.values())
					_write(row);
			}

			_flush();
			writer.close();
			rows.clear();
			
			System.out.println("Closing file");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
		
}
