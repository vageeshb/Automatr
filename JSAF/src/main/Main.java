package main;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import def.Logger;
import def.Utils;
import parser.Reader;
import report.HTMLReporter;
import exec.Executor;
import jxl.read.biff.BiffException;

public class Main {
	
	private static String[] config;
	/**
	 * This method converts the Test Execution Results to the format used for report generation.
	 * @param testExecutionResult A hashmap generated from running Execute.performExecution method.
	 * @return A hashmap to be used by Reporter.HtmlReport method.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static HashMap<String, Object> convertResultToReportFormat(HashMap<String, HashMap<String, ArrayList<String[]>>> testExecutionResult) {
		
		HashMap<String, Object> results = new HashMap();
		
		int totalModuleCount = 0;
		int totalTCCount = 0;
		int totalTSCount = 0;
		int totalTCFailed = 0;
		int totalTCPassed = 0;
		
		HashMap<String, String[]> moduleDetails = new HashMap<String, String[]>();
		
		HashMap<String,ArrayList<String[]>> testCaseDetails = new HashMap<String,ArrayList<String[]>>();
		
		totalModuleCount = testExecutionResult.keySet().size();
		
		Set<Map.Entry<String, HashMap<String, ArrayList<String[]>>>> testExecutionSet = testExecutionResult.entrySet();
		
		Iterator<Map.Entry<String, HashMap<String, ArrayList<String[]>>>> testExecutionSetIterator = testExecutionSet.iterator();
		
		while(testExecutionSetIterator.hasNext()) {
			
			int moduleTestCount = 0;
			int moduleTestPassed = 0;
			int moduleTestFailed = 0;
			ArrayList<String[]> testStepDetails =  new ArrayList<String[]>();
			
			// Module Name as Key and Test Case Execution Result as Value
			Map.Entry<String, HashMap<String, ArrayList<String[]>>> testExecutionMapEntry = testExecutionSetIterator.next();
			
			String moduleName = testExecutionMapEntry.getKey();
			
			HashMap<String, ArrayList<String[]>> testCase = testExecutionMapEntry.getValue();
			
			totalTCCount += testCase.keySet().size();
					
			Set<Map.Entry<String, ArrayList<String[]>>> testCaseSet = testCase.entrySet();
			
			Iterator<Map.Entry<String, ArrayList<String[]>>> testCaseSetIterator = testCaseSet.iterator();
			
			while(testCaseSetIterator.hasNext()) {
				
				// Increment Module Test Cases Count
				moduleTestCount++;
				
				// Test Case Name as Key and Test Step Statuses as Value
				Map.Entry<String, ArrayList<String[]>> testCaseMapEntry = testCaseSetIterator.next();
				
				ArrayList<String[]> testSteps = testCaseMapEntry.getValue();
				
				totalTSCount += testSteps.size();
				
				ArrayList flattenedTestSteps = Utils.flatten(testSteps);
				
				if(Utils.containsPattern(flattenedTestSteps, "FAIL.*")) {
					moduleTestFailed++;
					totalTCFailed++;
				} else {
					moduleTestPassed++;
					totalTCPassed++;
				}
				
				for (String[] testStep : testSteps) {
					String[] temp = {testCaseMapEntry.getKey().toString(), testStep[0], testStep[1], testStep[2], testStep[3]};
					testStepDetails.add(temp);
				}
				
			}
			
			String[] temp = {Integer.toString(moduleTestCount), Integer.toString(moduleTestPassed), Integer.toString(moduleTestFailed), Integer.toString(0)};
			moduleDetails.put(moduleName, temp);
			testCaseDetails.put(moduleName, testStepDetails);
		}
		
		// Add Summary Entry into Hash
		String[] summary = {config[0], config[1], Integer.toString(totalModuleCount), Integer.toString(totalTCCount), Integer.toString(totalTSCount),Integer.toString(totalTCPassed), Integer.toString(totalTCFailed)};
		
		results.put("summary", summary);
		results.put("modules", moduleDetails);
		results.put("details", testCaseDetails);
		
		return results;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) throws BiffException, IOException, ParseException, InterruptedException {
		
		HashMap<String, Object> readResult = Reader.read("SR");
		
		
		HashMap<String, HashMap<String, ArrayList<String[]>>> testExecutionResult = Executor.performExecution((String[])readResult.get("config"), (HashMap)readResult.get("exec_manager"), (HashMap)readResult.get("tests"), (HashMap)readResult.get("test_data"));
		
		config = (String[])readResult.get("config");
		
		HashMap reportFormattedData = convertResultToReportFormat(testExecutionResult);
		
		Logger.separator();
		
		System.out.println("Execution Completed.\n\nGenerating test result report.");
		
		HTMLReporter.generate(reportFormattedData);
	}

}
