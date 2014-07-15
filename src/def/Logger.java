package def;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Automatr
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
			System.out.print("\n" + me.getKey() + " : {" );
			if(me.getValue() instanceof String[]) {
				String[] temp = (String[])me.getValue();
				for (int j = 0; j < temp.length; j++) {
					System.out.print(temp[j]);
					if ( j != (temp.length-1) ) System.out.print(", ");
				}
			} 
			else if(me.getValue() instanceof HashMap) {
				readHashMap((HashMap)me.getValue());
			} 
			else if(me.getValue() instanceof ArrayList) {
				for (Object object : (ArrayList)me.getValue()) {
					if(object instanceof String[]) {
						String[] temp =(String[])object;
						for (int j = 0; j < temp.length; j++) {
							System.out.print(temp[j] + " ");
						}
					} 
					else {
						System.out.print(object);
					}
					System.out.println();
				}
			}
			else {
				System.out.print(me.getValue().toString());
			}
			System.out.print("\t}" );
		}
	}
}
