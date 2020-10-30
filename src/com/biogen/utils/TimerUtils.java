package com.biogen.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

public class TimerUtils {
	static private SimpleDateFormat dfDateTime = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
	static private SimpleDateFormat dfDate = new SimpleDateFormat("dd-MMM-yyyy");
	static private SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm:ss");
	private Stack<Date> timers = new Stack<Date>();

	public TimerUtils() {
		// TODO Auto-generated constructor stub
	}

	/** Create an internal timer
	 * 
	 */
	public void startTimer() {
		startTimer(1);
	}
	
	/** Create multiple internal timers
	 * 
	 * @param num the number of timers to start
	 */
	public void startTimer(int num) {
		for (int i = num; i-- > 0; )
			timers.push(new Date(System.currentTimeMillis()));
	}
	
	/** End a timer and get the elapsed time
	 * 
	 * @return the formated elapsed time from the top timer
	 */
	public String endTimer() {
		Date endDate = new Date(System.currentTimeMillis());
		String retVal = "";
		
		if (!timers.isEmpty()) {
			retVal = String.valueOf((endDate.getTime() - timers.pop().getTime()) / 1440);
		}
		
		return retVal + " sec";
	}

	/** Format System time to a date/time string
	 * 
	 * @param millis the time to convert
	 * @return the formatted time
	 */
	static public String formatDateTime(long millis) {
		return dfDateTime.format(millis);
	}
	
	/** Format System time to a date string
	 * 
	 * @param millis the time to convert
	 * @return the formatted date
	 */
	static public String formatDate(long millis) {
		return dfDate.format(millis);
	}
	
	/** Format System time to a time string
	 * 
	 * @param millis the time to convert
	 * @return the formatted time
	 */
	static public String formatTime(long millis) {
		return dfTime.format(millis);
	}
	
}
