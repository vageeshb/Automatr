package exec;

import java.io.IOException;
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
public class Executor {
	private static HashMap<String, String> testData;
	private static String[] config;
	@SuppressWarnings("rawtypes")
	private static HashMap allTestsHash;
	private static WebDriver driver;
	private static String currentModule;
	private static String currentTest;
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
	 * @throws IOException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String[] executeTestStep(Object testStep) throws IOException {
		
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
		
		// Run another self contained test as a part of this test
		if(step[3].equalsIgnoreCase("run")) {
			ArrayList<String[]> test = (ArrayList<String[]>)((HashMap)allTestsHash.get(step[1])).get(step[2]);
			status.put(currentModule, executeTest(currentTest, test, true));
			stepStatus[1] = "PASS";
			stepStatus[3] = now("dd/MM/yyyy HH:mm:ss");
			return stepStatus;
		}
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
			System.out.print(".");
			stepStatus[1] = "PASS";
		} else {
			System.out.print("F");
			stepStatus[1] = "FAIL: " + actionResult[1];
		}
		
		stepStatus[3] = now("dd/MM/yyyy HH:mm:ss");
		
		return stepStatus;
	}
	
	/**
	 * This method takes in a hash of tests and passes each test step to method 'executeTestStep'
	 * @param moduleName The name of the current module
	 * @param test HashMap with Test Name as Key and Test Steps as Value.
	 * @throws IOException 
	 * @returns HashMap with Test Name as Key and Test Step Status Array as Values.
	 */
	@SuppressWarnings("rawtypes")
	private static HashMap<String, ArrayList<String[]>> executeTest(String testName, ArrayList test, boolean flag) throws IOException {
		
		// Variables
		HashMap<String, ArrayList<String[]>> testStatus = new HashMap<String, ArrayList<String[]>>();
		
		currentTest = testName;
		
		// Initialize driver if called from parent module
		if(flag == false) driver = Selenium.initDriver(config[0], config[1]);
		
		ArrayList<String[]> temp = new ArrayList<String[]>();
		
		for(Object testStep: test) {
			
			String[] testStepResults = executeTestStep(testStep);
				
			// Take screenshot if test step failed
			if (testStepResults[1].contains("FAIL")) {
				String tempFileName = currentModule + "_" + testName + "_" + testStepResults[0] + "_error.png"; 
				Selenium.screenshot(driver, tempFileName);
			}
			temp.add(testStepResults);
		}
		
		testStatus.put(testName, temp);
			
		// Close driver if called from parent module
		if(flag == false) driver.close();
		
		
		return testStatus;
	}
	
	/**
	 * This method performs the whole execution of tests using the config, exection hash, test data and default steps.
	 * @param config The configuration settings.
	 * @param testsHash The execution hash with Module Name as Key and Tests as Values.
	 * @param testData The test data to be used.
	 * @param defaultSteps The default steps to be executed before each test.
	 * @return HashMap with Module Name as Key and Test Case Result HashMap as Values.
	 * @throws IOException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashMap<String, HashMap<String, ArrayList<String[]>>> performExecution(String[] config, HashMap execManagerHash, HashMap testsHash, HashMap testData) throws IOException {
		
		// Run only the tests as defined in execution manager hash
		Set set = execManagerHash.entrySet();
		Iterator i = set.iterator();
		
		// Set class variables
		setConfig(config);
		setTestData(testData);
		setallTestsHash(testsHash);
		
		// Perform Execution Module-wise
		while(i.hasNext()) {
			

			Map.Entry me = (Map.Entry)i.next();
			
			Logger.separator();

			// Extract module name and tests to be run
			String moduleName = me.getKey().toString();
			ArrayList<String> tests = (ArrayList<String>)me.getValue();
			
			System.out.println("Executing Module: " + moduleName);
			currentModule = moduleName;
			
			for (String test : tests) {
				
				// Get test steps of this test
				ArrayList testSteps = (ArrayList)((HashMap)testsHash.get(moduleName)).get(test);
				
				status.put(moduleName, executeTest(test, testSteps, false));
			}
			
			Logger.separator();
			
		}
		
		return status;
	}
	
	// Getter and Setters
	public static HashMap<?,?> getTestData() {
		return testData;
	}
	private static void setTestData(HashMap<String, String> testDataList) {
		Executor.testData = testDataList;
	}

	public static String[] getConfig() {
		return config;
	}

	private static void setConfig(String[] config) {
		Executor.config = config;
	}

	@SuppressWarnings("rawtypes")
	public static HashMap getallTestsHash() {
		return allTestsHash;
	}

	@SuppressWarnings("rawtypes")
	private static void setallTestsHash(HashMap allTestsHash) {
		Executor.allTestsHash = allTestsHash;
	}
}
