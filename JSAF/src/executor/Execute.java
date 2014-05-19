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
		
		Object e = Selenium.find(driver, step[1], step[2]);
		
		switch(e.getClass().getSimpleName().toLowerCase()) {
			case "string":
				if (value != null) {
					System.out.print(Selenium.action((String)e, step[3], value));
				} else {
					System.out.print(Selenium.action((String)e, step[3], step[4]));
				}
				break;
			case "remotewebelement":
				if (value != null) {
					System.out.print(Selenium.action((WebElement)e, step[3], value));
				} else {
					System.out.print(Selenium.action((WebElement)e, step[3], step[4]));
				}
				break;
			default:
				break;
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
