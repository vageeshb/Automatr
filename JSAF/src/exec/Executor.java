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
import org.openqa.selenium.remote.RemoteWebElement;

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
	private static HashMap<String, String> runTimeHash = new HashMap<String, String>();
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
		
		// Assume that by default, every action is a success
		String[] actionResult = new String[] {".", null};
		String[] stepResult = new String[4];
		
		// Declare common variables
		Object element = new Object();
		String stepAction = null;
		String stepDataValue = null;
		String locatorType = step[1];
		String locatorValue = step[2];
		int stepReduction = 0;
		
		// Initialize Status
		stepResult[0] = step[0];
		stepResult[2] = Utils.now("dd/MM/yyyy HH:mm:ss:S");
		
		// Initialize step variables
		String stepName = step[0];
		
		// Check to see if step was to be skipped
		if(stepName.startsWith("//")) {
			stepResult[1] = "SKIP";
			stepResult[3] = Utils.now("dd/MM/yyyy HH:mm:ss:S");
			System.out.print("S");
			return stepResult;
		}
		
		// Get action type
		if(step[3] != null) 
			stepAction = step[3];
		else
			stepAction = null;
		
		// Get Test Data value, if found in Test_Data hash
		if( step[4] != null ) {
			if ((String) testData.get(step[4]) != null)
				stepDataValue = (String) testData.get(step[4]);
			else if (runTimeHash.get(step[4]) != null)
				stepDataValue = (String) runTimeHash.get(step[4]);
			else
				stepDataValue = step[4];
		}
		
		switch(stepAction.toLowerCase()) {

			// Run another self contained test as a part of this test
			case "run":
				// Get contained test and its test steps
				HashMap containedTest = (HashMap)allTestsHash.get(locatorType);
				ArrayList<String[]> containedTestSteps = (ArrayList<String[]>)((ArrayList<String[]>)(containedTest).get(locatorValue)).clone();
				
				// Catcher for Parse Exception
				try {
					
					// Check if step reduction over-ride provided
					if(stepDataValue != null) {
						
						stepReduction = (Integer.parseInt(stepDataValue));
						
						// Check if steps to remove is greater than steps in the test
						if(Math.abs(stepReduction) > containedTestSteps.size()) {
							actionResult = new String[] {"F", "Number of steps to remove is greater than the steps in the test."};
						}
						// Removal index within range
						else {
							while (stepReduction != 0) {
								
								// Remove steps from the front
								if (stepReduction > 0) {
									containedTestSteps.remove(stepReduction - 1); 
									stepReduction--;
								}
								// Remove steps from the back
								else {
									containedTestSteps.remove(containedTestSteps.size() + stepReduction); 
									stepReduction++;
								}
							}
						}
					}
					
					executeTest(currentTest, containedTestSteps, false).get(currentTest);
					
					return null;
					
				} catch (Exception e) {
					actionResult = new String[] {"F", "Test Step Data Value is not a whole number."};
				}
				break;
				
			// Assertion step
			case "assert":
				// Find web element
				element = Selenium.find(driver, locatorType, locatorValue, null);
				
				// Element not found
				if(element == null){
					actionResult = new String[] {"F", "Element - {" + locatorType + " => " + locatorValue + "} not found."};
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
				Thread.sleep(Integer.parseInt(stepDataValue) * 100);
				break;
				
			// Closing Driver
			case "close":
				driver.close();
				driver.quit();
				driver = null;
				break;
			
			// Open and get URL
			case "open/get":
				driver = Selenium.initDriver(stepDataValue, config[1]);
				break;
				
			// Open Browser
			case "open":
				driver = Selenium.initDriver(null, config[1]);
				break;
				
			case "count":
				
				int localCounter = 0;
				
				while(true) {
					
					// Find web elements
					List<WebElement> elementList = Selenium.findElements(driver, locatorType, locatorValue);
					
					// Verify Element Count
					if(elementList != null && elementList.size() == Integer.parseInt(stepDataValue)) {
						break;
					}
					
					Thread.sleep(200);
					localCounter++;
					
					if(localCounter == 50) {
						if(elementList == null)
							actionResult = new String[] {"F", "Expected number of elements - " + stepDataValue + ", but found - 0."};
						else if (elementList.size() != Integer.parseInt(stepDataValue))
							actionResult = new String[] {"F","Expected number of elements - " + stepDataValue + ", but found - " + elementList.size() + "."};
						break;
					}
				}
				
				break;
				
			// Get URL
			case "get":
				driver.get(stepDataValue);
				break;

			// Web Element Related - Negative
			case "isnotdisplayed":
			case "isnotpresent":
				actionResult = Selenium.isNotDisplayed(driver, locatorType, locatorValue);
				break;
				
			// Web Element Related - Run time data match (Save and Match)
			case "save":
			case "saveselected":
				// Find web element
				element = Selenium.find(driver, locatorType, locatorValue, stepDataValue);

				// Element not found
				if (element == null) {
					actionResult = new String[] {"F", "Could not locate element with { &#39;" + locatorType + "&#39; = &#39;" + locatorValue + "&#39; }."};
				}
				// Element was found
				else {
					// Perform action step
					actionResult = Selenium.action(driver, (WebElement)element, stepAction, stepDataValue);
					
					// If action was succesfull, store the value to a hash
					if(actionResult[0].equals(".")) {
						runTimeHash.put(stepDataValue, actionResult[1]);
					}
				} 
				break;
			case "match":
				// Find web element
				element = Selenium.find(driver, locatorType, locatorValue, stepDataValue);

				// Element not found
				if (element == null) {
					actionResult = new String[] {"F", "Could not locate element with { &#39;" + locatorType + "&#39; = &#39;" + locatorValue + "&#39; }."};;
				}
				// Element was found
				else {
					if(stepDataValue != null) {
						// Perform action step
						actionResult = Selenium.action(driver, (WebElement)element, stepAction, stepDataValue);
					}
					// Invalid run time variable name was given
					else {
						actionResult = new String[] {"F","Could not look up the value of run-time variable [" + stepDataValue + "]."};
					}
				} 
				break;
			// Alert handle
			case "acceptalert":
				actionResult = Selenium.action(driver, null, stepAction, null);
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
			case "selectbyvalue":
			case "selectbyindex":
			case "selectbytext":
				// Find web element
				element = Selenium.find(driver, locatorType, locatorValue, stepDataValue);

				// Element not found
				if (element == null) {
					actionResult = new String[] {"F", "Could not locate element with { &#39;" + locatorType + "&#39; = &#39;" + locatorValue + "&#39; }."};;
				}
				// Element was found
				else if(element instanceof RemoteWebElement || element instanceof WebElement){
					// Perform action step
					actionResult = Selenium.action(driver, (WebElement)element, stepAction, stepDataValue);
				} else {
					actionResult = new String[] {"F", "There is a problem at this step. Could not identify the element type."};
				}
				break;
			default:
				actionResult = new String[] {"F", "There is a problem at this step. Could not identify the element type or action type."};
				break;
		}
		
		
		// STEP RESULT
		// PASS
		if (actionResult[0].equalsIgnoreCase(".")) {
			
			// Write to Console
			System.out.print(".");
			stepResult[1] = "PASS";
		} 
		// FAIL / WARNING
		else {
			
			// Write to console
			System.out.print(actionResult[0].toUpperCase());
			
			// Assign step status
			if(actionResult[0].equalsIgnoreCase("f"))
				stepResult[1] = "FAIL: " + actionResult[1];
			else
				stepResult[1] = "WARNING: " + actionResult[1];
			
			// Take screenshot			
			String tempFileName = Utils.uglify(currentModule + "_" + currentTest + "_" + stepName + "_error.png"); 
			
			// If element was present, highlight the element, else take complete screenshot
			if(element != null && element instanceof WebElement)
				Selenium.screenshot(driver, tempFileName, (WebElement)element, actionResult[0].toUpperCase());
			else
				Selenium.screenshot(driver, tempFileName, null, null);
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
