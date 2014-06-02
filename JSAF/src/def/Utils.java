package def;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

	public class Utils {
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
		 * @param list The list to find pattern in
		 * @param pattern The pattern to find
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
		 * @param dirName The directory path
		 */
		public static void makeDir(String dirName) {
			File theDir = new File(dirName);
			if (!theDir.exists()) {
			    theDir.mkdir();  
			}
		}
		
		/**
		 * This method tells the difference in time between to date strings
		 * @param format The format of the string
		 * @param start Start Date
		 * @param end End Date
		 * @param timeunit Time Unit
		 * @return Long
		 * @throws ParseException
		 */
		public static long timeDifference(String format, String start, String end, TimeUnit timeunit) throws ParseException {
			
			Date date1 = new SimpleDateFormat(format).parse(start);
			Date date2 = new SimpleDateFormat(format).parse(end);
			long diffInMillies = date2.getTime() - date1.getTime();
			return timeunit.convert(diffInMillies,TimeUnit.MILLISECONDS);
		}
		
		public static String strConcat(String[] input, int start, int end) {
			String temp = "";
			for (int i = start; i <= end; i++) {
				temp += input[i] + " ";
			}
			return temp;
		}
}
