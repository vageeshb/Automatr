package executor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import def.Logger;
import def.Selenium;

/**
 * Selenium Automation Framework
 * executor.Execute.java
 * Purpose: Contains methods to perform test execution.
 * 
 * @author VAGEESH BHASIN
 * @version 0.0.1
 */
public class Execute {
	private static HashMap<String, String> testData;
	private static ArrayList<ArrayList<String>> defaultSteps;
	private static String[] config;
	private static WebDriver driver;
	private static HashMap<String, HashMap<String, ArrayList<String[]>>> status = new HashMap<String, HashMap<String, ArrayList<String[]>>>();
	
	/**
	 * This method returns current date/time using the supplied format type.
	 * @param formatType
	 * @return Current Data/Time
	 */
	private static String now(String formatType) {
		DateFormat dateFormat = new SimpleDateFormat(formatType);
		Date date = new Date();
		return dateFormat.format(date) + ":" + System.currentTimeMillis() % 1000;
	}
	
	/**
	 * This method executes a test step either by finding an element and performing action or by assertion.
	 * @param testStep Should contain [Step Name, Locator Type, Locator Value, Action, Data Value]
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String[] executeTestStep(Object testStep) {
		
		// Convert testStep to ArrayList to String Array
		ArrayList temp = (ArrayList)testStep;
		String [] step = (String[]) temp.toArray(new String[temp.size()]);
		
		String[] actionResult = new String[2];
		String[] stepStatus = new String[4];
		
		// Initialize Status
		stepStatus[0] = step[0];
		stepStatus[2] = now("dd/MM/yyyy HH:mm:ss");
		
		// Check to see if test data name is used
		String value = (String) testData.get(step[4]);
		
		// Find web element
		Object e = Selenium.find(driver, step[1], step[2]);
		
		// Check to see if element was found, if found return was a WebElement, else it was assertion step
		if(e.getClass().getSimpleName().equalsIgnoreCase("string")) {
			
			// Perform assertion step
			if (value != null) {
				actionResult = Selenium.action((String)e, step[3], value);
			} else {
				actionResult = Selenium.action((String)e, step[3], step[4]);
			}
		} else {
			// Perform action step
			if (value != null) {
				actionResult = Selenium.action((WebElement)e, step[3], value);
			} else {
				actionResult = Selenium.action((WebElement)e, step[3], step[4]);
			}
		}
		
		if (actionResult[0].equalsIgnoreCase(".")) {
			stepStatus[1] = "PASS";
		} else {
			stepStatus[1] = "FAIL: " + actionResult[1];
		}
		
		stepStatus[3] = now("dd/MM/yyyy HH:mm:ss");
		
		return stepStatus;
	}
	
	/**
	 * This method takes in a hash of tests and passes each test step to method 'executeTestStep'
	 * @param moduleName The name of the current module
	 * @param tests HashMap with Test Name as Key and Test Steps as Value.
	 * @returns HashMap with Test Name as Key and Test Step Status Array as Values.
	 */
	@SuppressWarnings("rawtypes")
	private static HashMap<String, ArrayList<String[]>> executeTests(HashMap tests) {
		
		// Variables
		HashMap<String, ArrayList<String[]>> testStatus = new HashMap<String, ArrayList<String[]>>();
		
		// Extract test name and test steps
		Set set = tests.entrySet();
		Iterator i = set.iterator();
		while(i.hasNext()) {
			
			Map.Entry me = (Map.Entry)i.next();
			
			// Initialize driver
			driver = Selenium.initDriver(config[0], config[1]);
			
			System.out.print(" ");
			
			ArrayList test = (ArrayList)me.getValue();
			
			ArrayList<String[]> temp = new ArrayList<String[]>();
			
			// Execute Default steps first, if present
			if (defaultSteps.isEmpty() == false) {
				for (Object step : defaultSteps) {
					temp.add(executeTestStep(step));
				}
				for (Object testStep : test) {
					temp.add(executeTestStep(testStep));
				}
			} 
			// Default steps not present, directly run test steps
			else {
				for (Object testStep : test) {
					temp.add(executeTestStep(testStep));
				}
			}
			
			testStatus.put((String)me.getKey(), temp);
			
			// Close driver
			driver.close();
		}
		
		return testStatus;
	}
	
	/**
	 * This method performs the whole execution of tests using the config, exection hash, test data and default steps.
	 * @param config The configuration settings.
	 * @param executionHash The execution hash with Module Name as Key and Tests as Values.
	 * @param testData The test data to be used.
	 * @param defaultSteps The default steps to be executed before each test.
	 * @return HashMap with Module Name as Key and Test Case Result HashMap as Values.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashMap<String, HashMap<String, ArrayList<String[]>>> performExecution(String[] config, HashMap executionHash, HashMap testData, ArrayList defaultSteps) {
		Set set = executionHash.entrySet();
		Iterator i = set.iterator();
		
		// Set class variables
		setConfig(config);
		setTestData(testData);
		setDefaultSteps(defaultSteps);
		
		// Perform Execution Module-wise
		while(i.hasNext()) {
			
			// Extract tests from this module
			Map.Entry me = (Map.Entry)i.next();
			HashMap tests = (HashMap) me.getValue();
			
			Logger.separator();
			
			System.out.println("Executing Module: " + me.getKey().toString());
			
			Logger.separator();
			
			// Execute tests of this module
			status.put(me.getKey().toString(), executeTests(tests));
		}
		
		return status;
	}
	
	// Getter and Setters
	public static HashMap<?,?> getTestData() {
		return testData;
	}
	private static void setTestData(HashMap<String, String> testDataList) {
		Execute.testData = testDataList;
	}
	public static ArrayList<?> getDefaultSteps() {
		return defaultSteps;
	}
	private static void setDefaultSteps(ArrayList<ArrayList<String>> defaultStepsList) {
		Execute.defaultSteps = defaultStepsList;
	}

	public static String[] getConfig() {
		return config;
	}

	private static void setConfig(String[] config) {
		Execute.config = config;
	}
}
