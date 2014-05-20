package executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import def.Logger;
import def.Selenium;

public class Execute {
	@SuppressWarnings("rawtypes")
	private static HashMap testData;
	@SuppressWarnings("rawtypes")
	private static ArrayList defaultSteps;
	private static String[] config;
	private static WebDriver driver;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void executeTestStep(Object testStep) {
		
		// Variables
		ArrayList temp = (ArrayList)testStep;
		String [] step = (String[]) temp.toArray(new String[temp.size()]);
		String value = (String) testData.get(step[4]);
		String stepResult[] = new String[2]; 
		Object e = Selenium.find(driver, step[1], step[2]);
		
		if(e.getClass().getSimpleName().equalsIgnoreCase("string")) {
			if (value != null) {
				stepResult = Selenium.action((String)e, step[3], value);
			} else {
				stepResult = Selenium.action((String)e, step[3], step[4]);
			}
		} else {
			if (value != null) {
				stepResult = Selenium.action((WebElement)e, step[3], value);
			} else {
				stepResult = Selenium.action((WebElement)e, step[3], step[4]);
			}
		}
		
		if (stepResult[1] != null && stepResult[0].equalsIgnoreCase("f")) {
			System.out.println(stepResult[0]);
			//System.out.println(stepResult[1]);
		} else {
			System.out.print(stepResult[0]);
		}
		
		
	}
	
	@SuppressWarnings("rawtypes")
	private static void executeTests(HashMap tests) {
		Set set = tests.entrySet();
		Iterator i = set.iterator();
		while(i.hasNext()) {
			Map.Entry me = (Map.Entry)i.next();
			
			//System.out.println("Executing Test Name: " + me.getKey());
			
			driver = Selenium.initDriver(config[0], config[1]);
			
			System.out.print(" ");
			
			ArrayList test =  (ArrayList)me.getValue();
			
			if (defaultSteps.isEmpty() == false) {
				for (Object step : defaultSteps) {
					executeTestStep(step);
				}
				for (Object testStep : test) {
					executeTestStep(testStep);
				}
			} else {
				for (Object testStep : test) {
					executeTestStep(testStep);
				}
			}
			
			driver.close();
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void main(String[] config, HashMap executionHash, HashMap testData, ArrayList defaultSteps) {
		Set set = executionHash.entrySet();
		Iterator i = set.iterator();
		
		setConfig(config);
		setTestData(testData);
		setDefaultSteps(defaultSteps);
		
		while(i.hasNext()) {
			Map.Entry me = (Map.Entry)i.next();
			HashMap tests = (HashMap) me.getValue();
			
			Logger.separator();
			System.out.println("Executing Module: " + me.getKey().toString());
			Logger.separator();
			
			executeTests(tests);
		}
		
	}

	public static HashMap<?,?> getTestData() {
		return testData;
	}

	public static void setTestData(HashMap<?, ?> testDataList) {
		Execute.testData = testDataList;
	}

	public static ArrayList<?> getDefaultSteps() {
		return defaultSteps;
	}

	public static void setDefaultSteps(ArrayList<?> defaultStepsList) {
		Execute.defaultSteps = defaultStepsList;
	}

	public static String[] getConfig() {
		return config;
	}

	public static void setConfig(String[] config) {
		Execute.config = config;
	}
}
