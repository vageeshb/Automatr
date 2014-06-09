package exec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import def.Logger;
import def.Selenium;
import def.Utils;

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
		
		Object element = new Object();
		String stepAction;
		String stepDataValue;
		
		// Initialize Status
		stepResult[0] = step[0];
		stepResult[2] = Utils.now("dd/MM/yyyy HH:mm:ss:S");
		
		// Initialize step variables
		String stepName = step[0];
		
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
				ArrayList<String[]> containedTestSteps = (ArrayList<String[]>)((ArrayList<String[]>)(containedTest).get(step[2])).clone();
				try {
					if(stepDataValue != null && Integer.parseInt(stepDataValue) == -1)
						containedTestSteps.remove(containedTestSteps.size() -1 );
				} catch (Exception e) {}
				executeTest(currentTest, containedTestSteps, false).get(currentTest);
				
				return null;
			
			// Assertion step
			case "assert":
				// Find web element
				element = Selenium.find(driver, step[1], step[2], null);
				
				// Element not found
				if(element == null){
					actionResult[0] = "F";
					actionResult[1] = "Element - {" + step[1] + " => " + step[2] + "} not found.";
				}
				// Element was a string
				else if (element.getClass().getSimpleName().equalsIgnoreCase("string")) {
					actionResult = Selenium.action(driver, (String)element, stepAction, stepDataValue);
				}
				// Element exists
				else {
					actionResult = Selenium.action(driver, (WebElement)element, stepAction, stepDataValue);
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
			case "count":
				// Find web elements
				List<WebElement> elementList = Selenium.findElements(driver, step[1], step[2]);
				
				// Verify Element Count 
				if(elementList != null && elementList.size() == Integer.parseInt(stepDataValue)) {
					actionResult[0] = ".";
				}
				else {
					actionResult[0] = "F";
					if(elementList != null)
						actionResult[1] = "Expected number of elements - " + stepDataValue + ", but found - " + elementList.size() + ".";
					else
						actionResult[1] = "Expected number of elements - " + stepDataValue + ", but found - 0.";
				}
				break;
			// Get URL
			case "get":
				driver.get(stepDataValue);
				actionResult[0] = ".";
				break;
			// Web Element Related - Negative
			case "isnotdisplayed":
			case "isnotpresent":
				actionResult = Selenium.action(driver, step[1], stepAction, step[2]);
				break;
			// Web Element Related - Positive
			case "isdisplayed":
			case "ispresent":
			case "isempty":
			case "click":
			case "rightclick":
			case "input":
			case "hover":
			case "clear":
			case "draganddrop":
				// Find web element
				element = Selenium.find(driver, step[1], step[2], stepDataValue);

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
				String[] x = {"F", "There is a problem at this step. Could not identify the element type or action type."};
				actionResult = x;
				break;
		}
		
		// PASS
		if (actionResult[0].equalsIgnoreCase(".")) {
			System.out.print(".");
			stepResult[1] = "PASS";
		} 
		// WARNING
		else if (actionResult[0].equalsIgnoreCase("W")) {
			System.out.print("W");
			stepResult[1] = "WARNING: " + actionResult[1];
			
			// Take screenshot			
				String tempFileName = currentModule + "_" + currentTest + "_" + stepName + "_error.png"; 
						
			// If element was present, take screenshot around it, else take complete screenshot
			if(element != null && element instanceof WebElement){
				Selenium.screenshot(driver, tempFileName, (WebElement)element);
			}
			else
				Selenium.screenshot(driver, tempFileName, null);
		}
		else{
			System.out.print("F");
			stepResult[1] = "FAIL: " + actionResult[1];
			
			// Take screenshot			
			String tempFileName = currentModule + "_" + currentTest + "_" + stepName + "_error.png"; 
			
			// If elemen was present, take screenshot around it, else take complete screenshot
			if(element != null && element instanceof WebElement)
				Selenium.screenshot(driver, tempFileName, (WebElement)element);
			else
				Selenium.screenshot(driver, tempFileName, null);
		}
		
		stepResult[3] = Utils.now("dd/MM/yyyy HH:mm:ss:SS");
		
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
				
				
				if(testStatuses.get(currentTest) == null) testStatuses.put(currentTest, new ArrayList<String[]>());

				testStatuses.get(currentTest).add(testStepResult);
			}
		}
			
		// Close driver if called from parent module
		if(openDriver == true) { 
			driver.close();
			driver.quit();
		}
		
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
				
				System.out.print("\n" + test + ": ");
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
