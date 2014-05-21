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
 * Selenium Automation Framework
 * parser.Reader.java
 * Purpose: Contains methods to read and extract data from data file.
 * 
 * @author VAGEESH BHASIN
 * @version 0.0.1
 */

public class Reader {
	/**
	 * This method reads the configuration settings from workbook.
	 * @param workbook Workbook to read the configuration settings from.
	 * @return Array(url, driverType, defaultSteps?)
	 */
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
	
	/**
	 * This method will read the data from 'execution_manager' sheet of data file and return a HashMap with Module Name as Key and ArrayList of test cases as Values.
	 * @param workbook Workbook from where the Execution Manager sheet will be read
	 * @return HashMap (Module Name, Test Cases)
	 */
	private static HashMap<String, ArrayList<String>> readExecutionManager(Workbook workbook) {
		
		// Read and define 'Execution Manager' work sheet
		Sheet executionSheet = workbook.getSheet(1);
		
		// Generate a hash of modules and test cases to be executed
		HashMap<String, ArrayList<String>> executionList = new HashMap<String, ArrayList<String>>();
		
		int numberOfRows = executionSheet.getRows();
		for (int i = 1; i < numberOfRows; i++) {
			if (executionSheet.getCell(2,i).getContents().equalsIgnoreCase("y") == true) {
				String moduleName = executionSheet.getCell(0,i).getContents();
				if (executionList.containsKey(moduleName) == false ) {
					ArrayList<String> al = new ArrayList<String>();
					al.add(executionSheet.getCell(1,i).getContents());
					executionList.put(moduleName, al);
				} else {
					ArrayList<String> temp = (ArrayList<String>) executionList.get(moduleName);
					temp.add(executionSheet.getCell(1,i).getContents());
					executionList.put(moduleName, temp);
				}
			}
		}
		return executionList;
	}
	
	/**
	 * This method will read the data from 'before' sheet of data file and return an ArrayList of all default steps to perform before each test execution.
	 * @param workbook The workbook from where the default steps will be read
	 * @return ArrayList(Test Steps)
	 */
	private static ArrayList<ArrayList<String>> readDefaultSteps(Workbook workbook) {
		
		// Local Variables
		ArrayList<ArrayList<String>> defaultSteps = new ArrayList<ArrayList<String>>();
		
		// Read and define 'Before' work sheet
		Sheet defaultStepsSheet = workbook.getSheet("before");
		
		if (defaultStepsSheet != null) {
			
			int numberOfRows = defaultStepsSheet.getRows();
			
			for (int i = 1; i < numberOfRows; i++) {
				if(defaultStepsSheet.getCell(0,i).getContents().isEmpty() == false) {
					ArrayList<String> temp = new ArrayList<String>();
					
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
	
	/**
	 * This method will read the test data from 'test_data' sheet of data file and return a HashMap with TD Name as Key and TD Value as Value.
	 * @param workbook The workbook from where the test data will be read
	 * @return HashMap (Test Data Name, Test Data Value)
	 */
	private static HashMap<String, String> readTestData(Workbook workbook) {
		
		// Local Variables
		HashMap<String, String> testData = new HashMap<String, String>();
		
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
	
	/**
	 * This method reads the tests from data file, selects only the tests as per parameter 'tests' and returns a HashMap with Module Name as Key and Tests as Value.
	 * @param workbook The workbook from where all the tests will be read.
	 * @param moduleName The module name of the work sheet to read the tests from.
	 * @param tests The list of tests that are to be executed.
	 * @return HashMap(Module Name, List of Tests (Test Steps))
	 */
	private static HashMap<String, ArrayList<ArrayList<String>>> readTests(Workbook workbook, String moduleName, ArrayList<String> tests) {
		
		// Local Variables
		HashMap<String, ArrayList<ArrayList<String>>> moduleTests = new HashMap<String, ArrayList<ArrayList<String>>>();
		
		// Read and define 'Test Data' work sheet
		Sheet moduleSheet = workbook.getSheet(moduleName);
		
		if (moduleSheet != null) {

			int numberOfRows = moduleSheet.getRows();

			for (int i = 1; i < numberOfRows; i++) {
				
				if((moduleSheet.getCell(0,i).getContents().isEmpty() == false) && (tests.contains(moduleSheet.getCell(0,i).getContents()) == true)) {
					
					String testName = moduleSheet.getCell(0,i).getContents();
					
					if (moduleTests.containsKey(testName) == false ) {
						
						// Put empty list for test name
						moduleTests.put(testName, new ArrayList<ArrayList<String>>());
						
						ArrayList<String> temp = new ArrayList<String>();
						
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
						
						ArrayList<ArrayList<String>> testArray = (ArrayList<ArrayList<String>>) moduleTests.get(testName);
						
						testArray.add(temp);
						
						moduleTests.put(testName, testArray);
						
					} else {
						
						ArrayList<ArrayList<String>> testArray = (ArrayList<ArrayList<String>>) moduleTests.get(testName);
						
						ArrayList<String> temp = new ArrayList<String>();
						
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
	 * This method reads the data files and extracts the configuration settings, the execution manager settings, the test data and the test steps to be executed.
	 * @param filename The data file name from where the settings will be extracted.
	 * @return HashMap(Setting Name, Setting Data) - Refer individual result type for details.
	 * @setting_name config, default_steps, test_data, tests 
	 * @Setting_data Config data, Default Steps Data, Test Data, Tests (Test Steps)
	 * @throws BiffException
	 * @throws IOException
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static HashMap<String, Object> read(String filename) throws BiffException, IOException {
		
		// Variables 
		HashMap<String, Object> executionHash = new HashMap<String, Object>();
		HashMap<String, Object> results = new HashMap<String, Object>();
		HashMap<String, String> testData = new HashMap<String, String>();
		
		HashMap<String, ArrayList<String>> execManagerHash = new HashMap<String, ArrayList<String>>();
		
		ArrayList<ArrayList<String>> defaultSteps = new ArrayList<ArrayList<String>>();
		
		
		Logger.separator();
		
		Workbook workbook = Workbook.getWorkbook(new File("resources/data/" + filename + ".xls"));
		
		System.out.println("Parsing data file         : " + filename + ".xls");
		
		Logger.separator();
		
		String[] configs = readConfig(workbook);
		
		System.out.println("URL                       : " + configs[0]);
		System.out.println("Driver Type               : " + configs[1].toUpperCase());
		System.out.println("Default Steps?            : " + configs[2].toUpperCase());
		
		results.put("config", configs);
		
		execManagerHash = readExecutionManager(workbook);
		
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
		Set<?> set = execManagerHash.entrySet();
		
		Iterator<?> i = set.iterator();
		while(i.hasNext()) {
			Map.Entry<String,ArrayList<String>> me = (Map.Entry<String,ArrayList<String>>)i.next();
			// Read test for a module and put into Execution Hash
			executionHash.put(me.getKey().toString(), readTests(workbook, me.getKey().toString(), (ArrayList)me.getValue()));
		}
		
		// Return final Hash
		
		results.put("tests", executionHash);
		
		return results;
	}
}
