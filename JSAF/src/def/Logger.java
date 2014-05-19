package def;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Logger {
	public static void separator() {
		System.out.println("------------------------------------------------------------");
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
			System.out.println(me.getKey().toString() + " : " + me.getValue().toString());
		}
	}
}
