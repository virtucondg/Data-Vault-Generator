package com.biogen.utils;

import java.io.PrintStream;

/**
 * This class performs debugging operations.  It can be turned on and off via method control.
 * @author drothste
 *
 */
public class Debug {
	static private PrintStream pw = System.out;
	static private boolean inFile = false;
	static private boolean toScreen = false;
	
	/** the current state of debugging */
	static boolean enabled = false;

	/**
	 * Enable the debugger
	 */
	static public void enable() {
		enabled = true;
	}
	
	/**
	 * Disable the debugger
	 */
	static public void disable() {
		enabled = false;
	}

	/**
	 * Should text be printed to screen?
	 * @param printToScreen <code>true</code> if the debug output should always go to screen
	 */
	static public void printToScreen(boolean printToScreen) {
		toScreen = printToScreen;
	}
	/** 
	 * Set the file name for the debug file
	 * @param fileName the file name
	 */
	static public void debugFile(String fileName) {
		try {
			pw = new PrintStream(fileName);
			inFile = true;
		} catch (Exception ex) {
			pw = System.out;
			inFile = false;
		}
	}
	
	/** 
	 * Output text to the debugger if enabled
	 * @param text the text to output
	 */
	static public void print(String text) {
		print(text, false);
	}
	
	/**
	 * Output text to the debugger, forcing even if disabled
	 * @param text the text to output
	 */
	static public void printError(String text) {
		println(text, true);
	}
	
	/**
	 * Output text to the debugger
	 * @param text the text to output
	 * @param force if <code>TRUE</code>, output even if disabled
	 */
	static public void print(String text, boolean force) {
		if (enabled || force) {
			pw.print(text);
			pw.flush();
			if (toScreen && inFile)
				System.out.print(text);
		}
	}

	/**
	 * Output text with newline to the debugger
	 * @param text the text to output
	 */
	static public void println(String text) {
		println(text, false);
	}
	
	/**
	 * Output text with newline to the debugger
	 * @param text the text to output
	 * @param force if <code>TRUE</code>, output even if disabled
	 */
	static public void println(String text, boolean force) {
		if (enabled || force) {
			pw.println(text);
			pw.flush();
			if (toScreen && inFile)
				System.out.println(text);
		}
	}
	
	/**
	 * Output a stack trace
	 * @param ex the Exception to output the stack trace for
	 */
	static public void printStackTrace(Throwable ex) {
		ex.printStackTrace(pw);
		pw.flush();
		if (toScreen && inFile)
			ex.printStackTrace();;
	}
	
	protected void finalize() {
		pw.flush();
		if (inFile) pw.close();
	}

}
