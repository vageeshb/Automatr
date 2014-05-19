package parser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jxl.*;
import jxl.read.biff.BiffException;
import def.*;;

public class Reader {
	public static String[] readConfig(Workbook workbook) {
		
		// Read and define 'Config' work sheet
		Sheet configSheet = workbook.getSheet(0);
		
		// Assign config variables and return
		String[] configs = new String[3];
		configs[0] = configSheet.getCell(0,1).getContents();
		configs[1] = configSheet.getCell(1,1).getContents();
		configs[2] = configSheet.getCell(2,1).getContents();
		return configs;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static HashMap readExecutionManager(Workbook workbook) {
		
		// Read and define 'Execution Manager' work sheet
		Sheet executionSheet = workbook.getSheet(1);
		
		// Generate a hash of modules and test cases to be executed
		HashMap executionList = new HashMap();
		
		int numberOfRows = executionSheet.getRows();
		for (int i = 1; i < numberOfRows; i++) {
			if (executionSheet.getCell(2,i).getContents().equalsIgnoreCase("y") == true) {
				String moduleName = executionSheet.getCell(0,i).getContents();
				if (executionList.containsKey(moduleName) == false ) {
					ArrayList al = new ArrayList();
					al.add(executionSheet.getCell(1,i).getContents());
					executionList.put(moduleName, al);
				} else {
					ArrayList temp = (ArrayList) executionList.get(moduleName);
					temp.add(executionSheet.getCell(1,i).getContents());
					executionList.put(moduleName, temp);
				}
			}
		}
		return executionList;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ArrayList readDefaultSteps(Workbook workbook) {
		
		// Local Variables
		ArrayList defaultSteps = new ArrayList();
		
		// Read and define 'Execution Manager' work sheet
		Sheet defaultStepsSheet = workbook.getSheet("before");
		if (defaultStepsSheet != null) {
			
			int numberOfRows = defaultStepsSheet.getRows();
			
			for (int i = 1; i < numberOfRows; i++) {
				if(defaultStepsSheet.getCell(0,i).getContents().isEmpty() == false) {
					ArrayList temp = new ArrayList();
					
					// Step Name
					temp.add(defaultStepsSheet.getCell(0,i).getContents());
					
					// Locator Type
					temp.add(defaultStepsSheet.getCell(1,i).getContents());
					
					// Locator Value
					temp.add(defaultStepsSheet.getCell(2,i).getContents());
					
					// Action
					temp.add(defaultStepsSheet.getCell(3,i).getContents());
					
					// Test Data
					if (defaultStepsSheet.getCell(4,i).getContents().isEmpty() == false) {
						temp.add(defaultStepsSheet.getCell(4,i).getContents());
					}
					
					defaultSteps.add(temp);
					
				}
			}
			
		}

		return defaultSteps; 
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList readTestData(Workbook workbook) {
		
		// Local Variables
		ArrayList testData = new ArrayList();
		
		// Read and define 'Execution Manager' work sheet
		Sheet testDataSheet = workbook.getSheet("test_data");
		
		if (testDataSheet != null) {
			int numberOfRows = testDataSheet.getRows();
				
			
			
			for (int i = 1; i < numberOfRows; i++) {
				if(testDataSheet.getCell(0,i).getContents().isEmpty() == false) {
					ArrayList temp = new ArrayList();
					
					// Test Data Name
					temp.add(testDataSheet.getCell(0,i).getContents());
					
					// Test Data Value
					temp.add(testDataSheet.getCell(1,i).getContents());
					
					testData.add(temp);
				}
			}
		}
		
		return testData;
		
	}
	
	@SuppressWarnings("rawtypes")
	public static void main(String filename) throws BiffException, IOException {
		
		Logger.separator();
		
		Workbook workbook = Workbook.getWorkbook(new File("resources/" + filename + ".xls"));
		
		System.out.println("Parsing data file         : " + filename + ".xls");
		
		Logger.separator();
		
		String[] configs = readConfig(workbook);
		
		System.out.println("URL                       : " + configs[0]);
		System.out.println("Driver Type               : " + configs[1].toUpperCase());
		System.out.println("Default Steps?            : " + configs[2].toUpperCase());
		 
		HashMap hm = readExecutionManager(workbook);
		
		System.out.println("Total modules found       : " + hm.size());
		
		ArrayList defaultSteps = readDefaultSteps(workbook);
		
		System.out.println("Number of default steps   : " + defaultSteps.size());
		
		ArrayList testData = readTestData(workbook);
		
		System.out.println("Number of test data       : " + testData.size());
		
		Logger.separator();
		
		
		 /*// Get a set of the entries
	     Set set = hm.entrySet();
	     // Get an iterator
	     Iterator i = set.iterator();
	     // Display elements
	     while(i.hasNext()) {
	        Map.Entry me = (Map.Entry)i.next();
	        System.out.print(me.getKey() + ": ");
	        System.out.println(me.getValue());
	     }*/
	}
}
