package com.biogen.generator.io.output;

/**
 * This class creates a text file for debugging.
 * @author drothste
 *
 */
public class DebugOutput extends Output {

	/**
	 * Constructor
	 */
	public DebugOutput() {
		super();
	}
	
	@Override
	public void print(String data, String format, String textFormat, String hyperlink) {
		if (null==data)
			nextColumn();
		else {
			System.out.print(super.format(data, format));
			if (null!=hyperlink) System.out.print("[" + hyperlink + "]");
		}
	}

	@Override
	public void nextRow() {
		System.out.println("");
	}

	@Override
	public void nextColumn() {
		System.out.print("\t");
	}

	@Override
	public void createSheet(String name) {
		System.out.println("");
		System.out.println("<<" + name + ">>");
	}
	
}
