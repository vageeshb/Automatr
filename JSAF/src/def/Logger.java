package def;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Selenium Automation Framework
 * def.Logger.java
 * Purpose: Contains methods to write to console.
 * 
 * @author VAGEESH BHASIN
 * @version 0.0.1
 */
public class Logger {
	/**
	 * This method prints a line separator on the console.
	 */
	public static void separator() {
		System.out.println("\n------------------------------------------------------------");
	}
	
	@SuppressWarnings("rawtypes")
	public static void readHashMap(HashMap hm) {
		// Get a set of the entries
		Set set = hm.entrySet();
		// Get an iterator
		Iterator i = set.iterator();
		// Display elements
		while(i.hasNext()) {
			Map.Entry me = (Map.Entry)i.next();
			System.out.println(me.getKey() + " : " );
			if(me.getValue() instanceof String[]) {
				String[] temp = (String[])me.getValue();
				for (int j = 0; j < + temp.length; j++) {
					System.out.print(temp[j]);
				}
			} 
			else if(me.getValue() instanceof HashMap) {
				readHashMap((HashMap)me.getValue());
			} 
			else {
				System.out.print(me.getValue().toString());
			}
			
		}
	}
	
}
