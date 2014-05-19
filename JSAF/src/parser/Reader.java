package parser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jxl.*;
import jxl.read.biff.BiffException;
import def.*;;

/**
 * @author VAGEESH BHASIN
 * @version 0.0.1
 */

public class Reader {
	private static String[] readConfig(Workbook workbook) {
		
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
	private static HashMap readExecutionManager(Workbook workbook) {
		
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
	private static ArrayList readDefaultSteps(Workbook workbook) {
		
		// Local Variables
		ArrayList defaultSteps = new ArrayList<String>();
		
		// Read and define 'Before' work sheet
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
					} else {
						temp.add("");
					}
					
					defaultSteps.add(temp);
					
				}
			}
			
		}

		return defaultSteps; 
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static HashMap readTestData(Workbook workbook) {
		
		// Local Variables
		HashMap testData = new HashMap();
		
		// Read and define 'Test Data' work sheet
		Sheet testDataSheet = workbook.getSheet("test_data");
		
		if (testDataSheet != null) {
			int numberOfRows = testDataSheet.getRows();
			
			for (int i = 1; i < numberOfRows; i++) {
				if(testDataSheet.getCell(0,i).getContents().isEmpty() == false) {
					
					// Test Data Name, Test Data Value
					testData.put(testDataSheet.getCell(0, i).getContents(), testDataSheet.getCell(1, i).getContents());
				}
			}
		}
		
		return testData;
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static HashMap readTests(Workbook workbook, String moduleName, ArrayList tests) {
		
		// Local Variables
		HashMap moduleTests = new HashMap();
		
		// Read and define 'Test Data' work sheet
		Sheet moduleSheet = workbook.getSheet(moduleName);
		
		if (moduleSheet != null) {

			int numberOfRows = moduleSheet.getRows();

			for (int i = 1; i < numberOfRows; i++) {
				
				if((moduleSheet.getCell(0,i).getContents().isEmpty() == false) && (tests.contains(moduleSheet.getCell(0,i).getContents()) == true)) {
					String testName = moduleSheet.getCell(0,i).getContents();
					
					if (moduleTests.containsKey(testName) == false ) {
						
						// Put empty list for test name
						moduleTests.put(testName, new ArrayList());
						
						ArrayList temp = new ArrayList();
						
						// Test Step Name, Locator Type, Locator Value, Action, Test Data
						temp.add(moduleSheet.getCell(1,i).getContents());
						temp.add(moduleSheet.getCell(2,i).getContents());
						temp.add(moduleSheet.getCell(3,i).getContents());
						temp.add(moduleSheet.getCell(4,i).getContents());
						if (moduleSheet.getCell(5,i).getContents().isEmpty() == false) {
							temp.add(moduleSheet.getCell(5,i).getContents());
						} else {
							temp.add("");
						}
						
						ArrayList testArray = (ArrayList) moduleTests.get(testName);
						
						testArray.add(temp);
						
						moduleTests.put(testName, testArray);
						
					} else {
						
						ArrayList testArray = (ArrayList) moduleTests.get(testName);
						
						ArrayList temp = new ArrayList();
						
						// Test Step Name, Locator Type, Locator Value, Action, Test Data
						temp.add(moduleSheet.getCell(1,i).getContents());
						temp.add(moduleSheet.getCell(2,i).getContents());
						temp.add(moduleSheet.getCell(3,i).getContents());
						temp.add(moduleSheet.getCell(4,i).getContents());
						if (moduleSheet.getCell(5,i).getContents().isEmpty() == false) {
							temp.add(moduleSheet.getCell(5,i).getContents());
						} else {
							temp.add("");
						}
						
						testArray.add(temp);
						
						moduleTests.put(testName, testArray);
					}
				}
			}
		}
		
		return moduleTests;
		
	}
	
	
	/**
	 * @param filename
	 * @return HashMap
	 * @throws BiffException
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashMap read(String filename) throws BiffException, IOException {
		
		// Variables 
		HashMap executionHash = new HashMap();
		HashMap results = new HashMap();
		HashMap testData = new HashMap();
		
		ArrayList defaultSteps = new ArrayList();
		
		
		Logger.separator();
		
		Workbook workbook = Workbook.getWorkbook(new File("resources/" + filename + ".xls"));
		
		System.out.println("Parsing data file         : " + filename + ".xls");
		
		Logger.separator();
		
		String[] configs = readConfig(workbook);
		
		System.out.println("URL                       : " + configs[0]);
		System.out.println("Driver Type               : " + configs[1].toUpperCase());
		System.out.println("Default Steps?            : " + configs[2].toUpperCase());
		
		results.put("config", configs);
		
		HashMap execManagerHash = readExecutionManager(workbook);
		
		System.out.println("Total modules found       : " + execManagerHash.size());
		
		if (configs[2].equalsIgnoreCase("Y") == true) {

			defaultSteps = readDefaultSteps(workbook);
		
			System.out.println("Number of default steps   : " + defaultSteps.size());
			
			results.put("default_steps", defaultSteps);
			
		}
		
		testData = readTestData(workbook);
		
		System.out.println("Number of test data       : " + testData.size());
		
		results.put("test_data", testData);
		
		Logger.separator();
		
		// Get entries of tests to be executed
		Set set = execManagerHash.entrySet();
		
		Iterator i = set.iterator();
		while(i.hasNext()) {
			Map.Entry me = (Map.Entry)i.next();
			// Read test for a module and put into Execution Hash
			executionHash.put(me.getKey().toString(), readTests(workbook, me.getKey().toString(), (ArrayList)me.getValue()));
		}
		
		// Return final Hash
		
		results.put("tests", executionHash);
		
		return results;
	}
}
