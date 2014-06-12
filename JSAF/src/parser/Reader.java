package parser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jxl.*;
import jxl.read.biff.BiffException;
import def.*;;

/**
 * Selenium Automation Framework
 * @filename parser.Reader.java
 * @purpose Contains methods to read and extract data from data file.
 * @author VAGEESH BHASIN
 * @version 0.0.1
 */

public class Reader {
	
	private static int testCount = 0;
	/**
	 * This method reads the configuration settings from workbook.
	 * @param workbook Workbook to read the configuration settings from.
	 * @return Array(url, driverType, defaultSteps?)
	 */
	private static String[] readConfigSheet(Workbook workbook) {
		
		// Read and define 'Config' work sheet
		Sheet configSheet = workbook.getSheet(0);
		
		// Assign config variables and return
		String[] configs = new String[2];
		configs[0] = configSheet.getCell(0,1).getContents();
		configs[1] = configSheet.getCell(1,1).getContents();
		return configs;
	}
	
	/**
	 * This method will read the data from 'execution_manager' sheet of data file and return a HashMap with Module Name as Key and ArrayList of test cases as Values.
	 * @param workbook Workbook from where the Execution Manager sheet will be read
	 * @return HashMap (Module Name, Test Cases)
	 */
	private static HashMap<String, ArrayList<String>> readExecutionManagerSheet(Workbook workbook) {
		
		// Read and define 'Execution Manager' work sheet
		Sheet execManagerSheet = workbook.getSheet("execution_manager");
		
		// Generate a hash of modules and test cases to be executed
		HashMap<String, ArrayList<String>> execHash = new HashMap<String, ArrayList<String>>();
		
		int numberOfRows = execManagerSheet.getRows();
		for (int i = 1; i < numberOfRows; i++) {
			String moduleName = execManagerSheet.getCell(0,i).getContents();
			if (execManagerSheet.getCell(2,i).getContents() != null && execManagerSheet.getCell(2,i).getContents().equalsIgnoreCase("y")) {
				testCount ++;
				if (execHash.containsKey(moduleName) == false ) {
					execHash.put(moduleName, new ArrayList<String>());
				}
				ArrayList<String> temp = (ArrayList<String>) execHash.get(moduleName);
				temp.add(execManagerSheet.getCell(1,i).getContents());
				execHash.put(moduleName, temp);
			}
		}
		return execHash;
	}
	
	/**
	 * This method will read the test data from 'test_data' sheet of data file and return a HashMap with TD Name as Key and TD Value as Value.
	 * @param workbook The workbook from where the test data will be read
	 * @return HashMap (Test Data Name, Test Data Value)
	 */
	private static HashMap<String, String> readTestDataSheet(Workbook workbook) {
		
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
	 * This method reads all the tests from data file and returns a HashMap with Module Name as Key and Tests as Value.
	 * @param workbook [WorkBook] The workbook from where all the tests will be read.
	 * @param sheetNumber [int] The index of the sheet in the workbook
	 * @return HashMap(Module Name, List of Tests (Test Steps))
	 */
	private static HashMap<String, ArrayList<ArrayList<String>>> readSheet(Workbook workbook, int sheetNumber) {
		
		// Local Variables
		HashMap<String, ArrayList<ArrayList<String>>> moduleContents = new HashMap<String, ArrayList<ArrayList<String>>>();
		
		// Read and define 'Test Data' work sheet
		Sheet moduleSheet = workbook.getSheet(sheetNumber);
		
		if (moduleSheet != null) {

			int numberOfRows = moduleSheet.getRows();

			for (int i = 1; i < numberOfRows; i++) {
				
				if(moduleSheet.getCell(0,i).getContents().isEmpty() == false)  {
					
					String testName = moduleSheet.getCell(0,i).getContents();
					
					if (moduleContents.containsKey(testName) == false ) {
						
						// Put empty list for test name
						moduleContents.put(testName, new ArrayList<ArrayList<String>>());
						
					} 
						
					ArrayList<ArrayList<String>> testArray = (ArrayList<ArrayList<String>>) moduleContents.get(testName);
						
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
						
					moduleContents.put(testName, testArray);
					
				}
			}
		}
		
		return moduleContents;
		
	}
	
	
	/**
	 * This method reads the data files and extracts the configuration settings, the execution manager settings, the test cases and the test data.
	 * @param filename The data file name from where the settings will be extracted.
	 * @return HashMap(Setting Name, Setting Data) - Refer individual result type for details.
	 * @setting_name config, default_steps, test_data, tests 
	 * @setting_data Config data, Default Steps Data, Test Data, Tests (Test Steps)
	 * @throws BiffException
	 * @throws IOException
	 */
	public static HashMap<String, Object> read(File excelFile) throws BiffException, IOException {
		
		// Variables 
		HashMap<String, Object> executionHash = new HashMap<String, Object>();
		HashMap<String, Object> results = new HashMap<String, Object>();
		HashMap<String, String> testData = new HashMap<String, String>();
		
		HashMap<String, ArrayList<String>> execManagerHash = new HashMap<String, ArrayList<String>>();
		
		Logger.separator();
		
		Workbook workbook = Workbook.getWorkbook(excelFile);
		
		System.out.println("Parsing data file             : " + excelFile.getName());
		
		Logger.separator();
		
		String[] configs = readConfigSheet(workbook);
		
		System.out.println("URL                           : " + configs[0]);
		System.out.println("Driver Type                   : " + configs[1].toUpperCase());
		
		results.put("config", configs);
		
		execManagerHash = readExecutionManagerSheet(workbook);
		
		results.put("exec_manager", execManagerHash);
		
		System.out.println("Total modules found           : " + execManagerHash.size());
		
		System.out.println("Total test cases to execute   : " + testCount);
		
		testData = readTestDataSheet(workbook);
		
		System.out.println("Number of test data           : " + testData.size());
		
		results.put("test_data", testData);
		
		Logger.separator();
		
		// Get entries of tests to be executed
		
		for (int j = 3; j < workbook.getNumberOfSheets(); j++) {
			// Read test for a module and put into Execution Hash
			executionHash.put(workbook.getSheet(j).getName(), readSheet(workbook, j));
		}
		
		// Return final Hash
		results.put("tests", executionHash);
		
		return results;
	}
}
