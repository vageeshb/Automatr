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
	private static HashMap<String, ArrayList<String[]>> testStatuses;
	private static HashMap<String, HashMap<String, ArrayList<String[]>>> status = new HashMap<String, HashMap<String, ArrayList<String[]>>>();
	
	/**
	 * This method returns current date/time using the supplied format type.
	 * @param formatType
	 * @return Current Data/Time
	 */
	private static String now(String formatType) {
		DateFormat dateFormat = new SimpleDateFormat(formatType);
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	/**
	 * This method executes a test step either by finding an element and performing action or by assertion.
	 * @param testStep Should contain [Step Name, Locator Type, Locator Value, Action, Data Value]
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String[] executeTestStep(Object testStep) throws IOException, InterruptedException {
		
		// Convert testStep to ArrayList to String Array
		ArrayList temp = (ArrayList)testStep;
		String [] step = (String[]) temp.toArray(new String[temp.size()]);
		
		String[] actionResult = new String[2];
		String[] stepResult = new String[4];
		
		Object element;
		String stepAction;
		String stepDataValue;
		
		// Initialize Status
		stepResult[0] = step[0];
		stepResult[2] = now("dd/MM/yyyy HH:mm:ss:S");
		
		// Get action type
		if(step[3] != null) 
			stepAction = step[3];
		else
			stepAction = null;
		
		// Get Test Data value, if found in Test_Data hash
		if( (String) testData.get(step[4]) != null)
			stepDataValue = (String) testData.get(step[4]);
		else
			stepDataValue = step[4];
		
		
		switch(stepAction.toLowerCase()) {

			// Run another self contained test as a part of this test
			case "run":
				// Get contained test and its test steps
				HashMap containedTest = (HashMap)allTestsHash.get(step[1]);
				ArrayList<String[]> containedTestSteps = (ArrayList<String[]>)(containedTest).get(step[2]);
				try {
					if(stepDataValue != null && Integer.parseInt(stepDataValue) == -1)
						containedTestSteps.remove(containedTestSteps.size() -1 );
				} catch (Exception e) {}
				executeTest(currentTest, containedTestSteps, false).get(currentTest);
				
				return null;
			
			// Assertion step
			case "assert":
				// Find web element
				element = Selenium.find(driver, step[1], step[2]);
				
				// Element not found
				if(element == null){
					actionResult = Selenium.action(null, step[1], stepAction, stepDataValue);
				}
				// Element was a string
				else if (element.getClass().getSimpleName().equalsIgnoreCase("string")) {
					actionResult = Selenium.action(null, (String)element, stepAction, stepDataValue);
				}
				// Element exists
				else {
					actionResult = Selenium.action(null, (WebElement)element, stepAction, stepDataValue);
				}
				break;
				
			// JAVASCRIPT
			case "javascript":
				actionResult = Selenium.action(driver, null, stepAction, stepDataValue);
				break;
				
			// Wait
			case "wait":
				Thread.sleep(Integer.parseInt(step[4]) * 100);
				actionResult[0] = ".";
				break;
				
			// Closing Driver
			case "close":
				driver.close();
				driver = null;
				actionResult[0] = ".";
				break;
			
			// Open and get URL
			case "open/get":
				driver = Selenium.initDriver(stepDataValue, config[1]);
				actionResult[0] = ".";
				break;
				
			// Open Browser
			case "open":
				driver = Selenium.initDriver(null, config[1]);
				actionResult[0] = ".";
				break;
				
			// Get URL
			case "get":
				driver.get(stepDataValue);
				actionResult[0] = ".";
				break;
			// Web Element Related - Negative
			case "isnotdisplayed":
			case "isnotpresent":
				// Find web element
				element = Selenium.find(driver, step[1], step[2]);
				
				// Element not found, success
				if(element == null) {
					actionResult[0] = ".";
				}
				// Element found, check for displayed property
				else {
					actionResult = Selenium.action(null, (WebElement)element, stepAction, stepDataValue);
				}
				break;
			// Web Element Related - Positive
			case "isdisplayed":
			case "ispresent":
			case "click":
			case "input":
			case "hover":
				// Find web element
				element = Selenium.find(driver, step[1], step[2]);

				// Element not found
				if (element == null) {
					String[] x = {"F", "Could not locate element with { &#39;" + step[1] + "&#39; = &#39;" + step[2] + "&#39; }."};
					actionResult = x;
				}
				// Element was found
				else if(element.getClass().getSimpleName().equalsIgnoreCase("RemoteWebElement")){
					// Perform action step
					actionResult = Selenium.action(driver, (WebElement)element, stepAction, stepDataValue);
				} else {
					String[] x = {"F", "There is a problem at this step. Could not identify the element type."};
					actionResult = x;
				}
				break;
			default:
				String[] x = {"F", "There is a problem at this step. Could not identify the element type."};
				actionResult = x;
				break;
		}
		
		if (actionResult[0].equalsIgnoreCase(".")) {
			System.out.print(".");
			stepResult[1] = "PASS";
		} else {
			System.out.print("F");
			stepResult[1] = "FAIL: " + actionResult[1];
		}
		
		stepResult[3] = now("dd/MM/yyyy HH:mm:ss:SS");
		
		return stepResult;
	}
	
	/**
	 * This method takes in a hash of tests and passes each test step to method 'executeTestStep'
	 * @param moduleName The name of the current module
	 * @param test HashMap with Test Name as Key and Test Steps as Value.
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @returns HashMap with Test Name as Key and Test Step Status Array as Values.
	 */
	@SuppressWarnings("rawtypes")
	private static HashMap<String, ArrayList<String[]>> executeTest(String testName, ArrayList test, boolean openDriver) throws IOException, InterruptedException {
		
		// Set Current Test
		currentTest = testName;
		
		// Initialize driver if called from parent module
		if(openDriver == true) driver = Selenium.initDriver(config[0], config[1]);
		
		for(Object testStep: test) {
			
			String[] testStepResult = executeTestStep(testStep);
			
			// If self contained test was not run, add status
			if(testStepResult != null) {
				// Take screenshot if test step failed				
				if (testStepResult[1].contains("FAIL")) {
					String tempFileName = currentModule + "_" + testName + "_" + testStepResult[0] + "_error.png"; 
					Selenium.screenshot(driver, tempFileName);
				}
				
				if(testStatuses.get(currentTest) == null) testStatuses.put(currentTest, new ArrayList<String[]>());

				testStatuses.get(currentTest).add(testStepResult);
			}
		}
			
		// Close driver if called from parent module
		if(openDriver == true) driver.close();
		
		return testStatuses;
	}
	
	/**
	 * This method performs the whole execution of tests using the config, exection hash, test data and default steps.
	 * @param config The configuration settings.
	 * @param testsHash The execution hash with Module Name as Key and Tests as Values.
	 * @param testData The test data to be used.
	 * @param defaultSteps The default steps to be executed before each test.
	 * @return HashMap with Module Name as Key and Test Case Result HashMap as Values.
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashMap<String, HashMap<String, ArrayList<String[]>>> performExecution(String[] config, HashMap execManagerHash, HashMap testsHash, HashMap testData) throws IOException, InterruptedException {
		
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
			
			// Initialize Test Statuses for this module
			testStatuses = new HashMap<String, ArrayList<String[]>>();
			
			for (String test : tests) {
				
				System.out.print(" ");
				// Initialize before execution
				if(status.get(moduleName) == null) {
					HashMap<String, ArrayList<String[]>> tempTestHash = new HashMap<String, ArrayList<String[]>>();
					tempTestHash.put(test, new ArrayList<String[]>());
					status.put(moduleName, tempTestHash);
				} 
				
				// Get test steps of this test
				ArrayList testSteps = (ArrayList)((HashMap)testsHash.get(moduleName)).get(test);
				
				executeTest(test, testSteps, true);
				
			}
			
			status.put(moduleName, testStatuses);
			
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
