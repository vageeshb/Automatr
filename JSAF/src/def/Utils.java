package def;

import java.util.ArrayList;
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
}
