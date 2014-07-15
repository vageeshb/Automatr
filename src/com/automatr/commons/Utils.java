package com.automatr.commons;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
/**
 * Automatr
 * def.Utils.java
 * Purpose: Contains utility methods
 * 
 * @author VAGEESH BHASIN
 * @version 0.0.10
 */
public class Utils {
		
	/**
	 * This method flattens an arraylist
	 * @param input [ArrayList] - The input arraylist to be flattened
	 * @return
	 */
	public static ArrayList<?> flatten(ArrayList<?> input) {
		ArrayList<Object> result = new ArrayList<Object>();
	
	    for (Object o: input) {
	        if (o instanceof String[]) {
	        	String[] temp = (String[])o;
	            for (int i = 0; i < temp.length; i++) {
					result.add(temp[i]);
				}
	        } else {
	            result.add(o);
	        }
	    }
	
	    return result;
	}
	
	/**
	 * This method searched for the given string pattern in the ArrayList
	 * @param list [ArrayList] - The list to find pattern in
	 * @param pattern [String] - The pattern to find
	 * @return
	 */
	public static boolean containsPattern(ArrayList<?> list, String pattern) {
		for (Object o : list) {
			if(Pattern.matches(pattern, o.toString())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method creates a directory using dirName.
	 * @param dirName [String] - The directory path
	 */
	public static void makeDir(String dirName) {
		File theDir = new File(dirName);
		if (!theDir.exists()) {
		    theDir.mkdir();  
		}
	}
	
	/**
	 * This method tells the difference in time between to date strings
	 * @param format [String] - The format of the string
	 * @param start [String] - Start Date
	 * @param end [String] - End Date
	 * @param timeunit [TimeUnit] - Time Unit
	 * @return Long
	 */
	public static long timeDifference(String format, String start, String end, TimeUnit timeunit) {
		
		Date date1, date2;
		try {
			date1 = new SimpleDateFormat(format).parse(start);
			date2 = new SimpleDateFormat(format).parse(end);
		} catch (ParseException e) {
			// Parsing error
			return 0;
		}
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeunit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
	/**
	 * This utility method concats a string (Better errors)
	 * @param input [String[]] - Array of string
	 * @param start [int] - Start index
	 * @param end [int] - End index
	 * @return
	 */
	public static String strConcat(String[] input, int start, int end) {
		String temp = "";
		for (int i = start; i <= end; i++) {
			temp += input[i];
			if (i != end) 
				 temp += ":";
		}
		temp.replaceAll("\"", "&quot;");
		temp.replaceAll("\'", "&quot;");
		return temp;
	}
	
	/**
	 * This method returns current date/time using the supplied format type.
	 * @param formatType [String] - The format type to be returned
	 * @return Current Data/Time
	 */
	public static String now(String formatType) {
		DateFormat dateFormat = new SimpleDateFormat(formatType);
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	/**
	 * This method replaces whitespaces with _ in a string and returns a lower case value
	 * @param input [String] - Input string
	 * @return
	 */
	public static String uglify(String input) {
	return input.replaceAll("\\s","_").toLowerCase();
}

}
