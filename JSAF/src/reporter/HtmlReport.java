package reporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import def.Logger;

/**
 * Selenium Automation Framework
 * reporter.HtmlReport.java
 * Purpose: Contains methods to perform report generation.
 * 
 * @author VAGEESH BHASIN
 * @version 0.0.1
 */
public class HtmlReport {
	// Class Variables
	private static String reportContent = "";
	private static String filename = "";
	
	/**
	 * This method creates a directory using dirName.
	 * @param dirName The directory path
	 */
	private static void makeDir(String dirName) {
		File theDir = new File(dirName);
		if (!theDir.exists()) {
		    theDir.mkdir();  
		}
	}
	
	/**
	 * This method writes the head section of report
	 */
	private static String header() {
		return	 	"<head>\n"
				+ 	"	<title>Test Execution Report</title>\n"
				+	"	<link rel='stylesheet' href='./resources/css/bootstrap.min.css'>\n"
				+ 	"	<script src='./resources/js/jquery.min.js'></script>\n"
				+ 	"	<script src='./resources/js/bootstrap.min.js'></script>\n"
				+ 	"	<script src='./resources/js/report.min.js'></script>\n"
				+ 	"</head>\n";
	}
	
	/**
	 * This method writes the Page Header of the report
	 */
	private static String pageHeader() {
		
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		return 		"<div class='page-header'>\n"
				+	"	<h1>" + filename + " <small>Test execution report generated on " + dateFormat.format(date) + " at " + timeFormat.format(date) + "</small></h1>\n"
				+	"</div>\n";
	}
	
	/**
	 * This method writes the navigation tabs of the report.
	 */
	private static String navTabs() {
		return 		"<!-- Nav Tabs -->\n"
				+	"<ul class='nav nav-tabs nav-justified'>\n"
				+	"	<li class='active'><a href='#summary' data-toggle='tab'>Summary</a></li>\n"
				+   "	<li><a href='#modules' data-toggle='tab'>Modules</a></li>\n"
				+   "	<li><a href='#details' data-toggle='tab'>Details</a></li>\n"
				+   "</ul>\n";
	}
	
	/**
	 * This method writes the summary tab details using the input parameter.
	 * @param summary - String array with values as [Number of modules, Tests Executed, Test Steps executed, Number of TC passed, Number of TC failed, Number of TC skipped]
	 */
	private static String summaryTab(String[] summary) {
		return    	"<div class='tab-pane active' id='summary'>\n"
				+ 	"	<table class='table table-bordered table-hover'>\n"
				+ 	"			<tbody>\n"
				+ 	"				<tr>\n"
				+ 	"					<td class='info'><strong>Filename</strong></td>\n"
				+ 	"					<td>" + filename + "</td>\n"
				+ 	"				</tr>\n"
				+ 	"				<tr>\n"
				+ 	"					<td class='info'><strong>Modules</strong></td>\n"
				+ 	"					<td>" + summary[0] + "</td>\n"
				+ 	"				</tr>\n"
                + 	"				<tr>\n"
                + 	"					<td class='info'><strong>Tests Executed</strong></td>\n"
                + 	"					<td>" + summary[1] + "</td>\n"
                + 	"				</tr>\n"
                + 	"				<tr>\n"
                + 	"					<td class='info'><strong>Tests Steps Executed</strong></td>\n"
                + 	"					<td>" + summary[2] + "</td>\n"
                + 	"				</tr>\n"
                + 	"				<tr>\n"
                + 	"					<td class='success'><strong>Passed</strong></td>\n"
                + 	"					<td>" + summary[3] + "</td>\n"
                + 	"				</tr>\n"
                + 	"				<tr>\n"
                + 	"					<td class='danger'><strong>Failed</strong></td>\n"
                + 	"					<td>" + summary[4] + "</td>\n"
                + 	"				</tr>\n"
                + 	"			</tbody>\n"
                + 	"	</table>\n"
				+ 	"</div>\n";
	}
	
	/**
	 * This method writes the module-wise summary details.
	 * @param moduleSum The HashMap of module summary with Module Name as Key and Summary Detail as Value.
	 */
	private static String moduleTab(HashMap<?,?> moduleSum) {
		String temp =   "<div class='tab-pane' id='modules'>\n"
					+   "	<table class='table table-bordered table-hover'>\n"
					+   "		<thead>\n"
					+   "			<tr class='info'>\n"
					+   "				<th>Module Name</th>\n"
					+   "				<th>Number of Test Cases</th>\n"
					+   "				<th>Passed Test Cases</th>\n"
					+   "				<th>Failed Test Cases</th>\n"
					+   "				<th>Skipped Test Cases</th>\n"
					+   "			</tr>\n"
					+   "		</thead>\n"
					+   "		<tbody>\n";
		
		Set<?> set = moduleSum.entrySet();
		Iterator<?> i = set.iterator();
		while(i.hasNext()) {
			Map.Entry<?, ?> me = (Map.Entry<?, ?>)i.next();
			String[] moduleStatus = (String[])me.getValue();
			temp += 	"			<tr>\n"
					+	"				<td>" + me.getKey().toString() + "</td>\n"
					+	"				<td>" + moduleStatus[0] + "</td>\n"
					+	"				<td>" + moduleStatus[1] + "</td>\n"
					+	"				<td>" + moduleStatus[2] + "</td>\n"
					+	"				<td>" + moduleStatus[3] + "</td>\n"
					+	" 			</tr>\n";
		}
		
		temp 		+=	"		</tbody>\n"
					+ 	"	</table>\n"
					+ 	"</div>";
		return temp;
	}
	
	/**
	 * This method writes the test details of each module, including test name, test step name and status.
	 * @param hashMap The HashMap contains each module test details
	 */
	@SuppressWarnings({ "unchecked" })
	private static String detailTab(HashMap<?, ?> hashMap) {
		String temp = 	"<div class='tab-pane' id='details'>"
					+ 	"	<div class='panel panel-default'>"
					+ 	"		<div class='panel-heading'><strong>Details of Test Execution</strong></div>"
					+ 	"			<div class='panel-body'>"
					+ 	"				<ul class='nav nav-tabs nav-justified'>";
		
		Set<?> set = hashMap.keySet();
		Iterator<?> i = set.iterator();
		while(i.hasNext()) {
			String moduleName = i.next().toString().toUpperCase();
			temp += 	"<li><a href='#" + moduleName + "' data-toggle='tab'>" + moduleName + "</a></li>";
		}
		
		temp 		+=	"				</ul>\n"
        			+ 	"				<br>\n"
        			+	"				<div class='tab-content'>\n";
		
		set = hashMap.entrySet();
		i = set.iterator();
		while(i.hasNext()) {
			Map.Entry<String, ArrayList<String[]>> me = (Map.Entry<String, ArrayList<String[]>>)i.next();
			String moduleName = me.getKey().toString().toUpperCase();
			ArrayList<String[]> moduleSteps = (ArrayList<String[]>)me.getValue();
			temp	+=  "				<div class='tab-pane' id='" + moduleName + "'>"
        			+ 	"					<table class='table table-bordered table-hover'>"
        			+ 	"						<thead>"
        			+ 	"							<tr class='info'>"
        			+ 	"								<th>Test Case Name</th>"
        			+ 	"								<th>Test Step Name</th>"
        			+ 	"								<th>Status</th>\n"
        			+	"								<th>Start Time</th>\n"
        			+ 	"								<th>End Time</th>\n"
        			+ 	"							</tr>"
        			+ 	"						</thead>"
        			+	"						<tbody>";
			
			for (String[] steps : moduleSteps) {
				temp += "							<tr>\n";
				for (int j = 0; j < steps.length; j++) {
				temp +=	"								<td>" + steps[j] + "</td>";
				}
				temp += "							</tr>\n";
			}
				
        	temp 	+=	"						</tbody>"
        			+	"					</table>"
        			+ 	"				</div>"
        			+ 	"			</div>"
        			+ 	"		</div>"
        			+ 	"	</div>"
        			+ 	"</div>";
		}
		return temp;
	}
	
	/**
	 * This method writes the error modal element used to display error screenshots.
	 */
	private static String modal() {
		return 		"<!-- Error modal -->"
				+	"<div class='modal fade' id='error-modal' tabindex='-1' role='dialog' aria-labelledby='error-modal-label' aria-hidden='true' style='display: none;'>\n"
				+	"	<div class='modal-dialog'>\n"
				+ 	"		<div class='modal-content'>\n"
				+ 	"			<div class='modal-header'>\n"
				+ 	"				<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>\n"
				+ 	"				<h4 class='modal-title text-center' id='myModalLabel'><strong>Error Screen Shot</strong></h4>\n"
				+ 	"			</div>\n"
				+ 	"			<div class='modal-body'>\n"
				+ 	"				<div class='text-center'>\n"
				+ 	"					<img id='modal-image' src='' class='img-responsive'>\n"
				+	"				</div>\n"
				+ 	"			</div>\n"
				+ 	"			<div class='modal-footer'>\n"
				+ 	"				<button type='button' class='btn btn-default' data-dismiss='modal'>Close</button>\n"
				+ 	"			</div>\n"
				+ 	"		</div>\n"
				+ 	"	</div>\n"
				+ 	"</div>\n";
	}
	
	
	/**
	 * This method writes the footer content.
	 */
	private static String footer() {
		return 		"<div id='footer'>\n"
				+ 	"	<div class='text-center'>\n"
				+ 	"		<hr>\n"
				+ 	"		<span>Generated using <strong>SAF - v0.0.1</strong></span>\n"
				+ 	"		<span>&copy; 2014 VB</span>\n"
				+ 	"	</div>\n"
				+ 	"</div>\n";
	}
	
	
	/**
	 * This method uses all the other method to fill the report.
	 * @param results The results HashMap contains data to be provided to other functions.
	 */
	private static void fillReport(HashMap<?,?> results) {
		
		reportContent += "<html>\n"
				+ header()
				+ "<body>\n"
				+ "<div class='container'>\n"
				+ pageHeader()
				+ navTabs()
				+ "<br>\n<div class='tab-content'>\n"
				+ "<!-- Summary Tab -->\n"
				+ summaryTab((String[])results.get("summary"))
				+ "\n<!-- Module Tab -->\n"
				+ moduleTab((HashMap<?,?>)results.get("modules"))
				+ "\n<!-- Details Tab -->\n"
				+ detailTab((HashMap<?,?>)results.get("details"))
				+ "</div>\n"
				+ modal()
				+ footer()
				+ "</div>\n"
				+ "</body>\n"
				+ "</html>\n";		
	}
	
	/**
	 * This method creates and copies report resources for a report.
	 * @param dirName Target directory to which resources will be copied.
	 * @throws IOException
	 */
	private static void createReportResources(String dirName) throws IOException {
		String currentDir = System.getProperty("user.dir");
		
		File srcDir = new File(currentDir + "/resources/report");
		File trgDir = new File(dirName + "/resources");

		FileUtils.copyDirectory(srcDir, trgDir);
	}
	
	
	/**
	 * This method creates the report, writes the class variable 'reportContent' to the created report file.
	 * @throws IOException
	 */
	public static void createReport() throws IOException {
		
		// Get Present Working Directory
		String currentDir = System.getProperty("user.dir");
		
		// Create reports directory if it does not exist
		makeDir(currentDir + "/reports");
		
		// Create individual report directory and copy resources(css,js) to that folder
		makeDir(currentDir + "/reports/" + filename);
		String reportFolder = currentDir + "/reports/" + filename;
		createReportResources(reportFolder);
		
		// Create report.html in the folder, print contents of test execution and close file
		PrintWriter writer = new PrintWriter(currentDir + "/reports/" + filename + "/report.html", "UTF-8");
		writer.println(reportContent);
		writer.close();
	}
	
	/**
	 * This method takes in the necessary HashMap to generate the sections of report.
	 * @param testExecutionResult A HashMap with (summary, modules, details) as Key and (Summary details, Module Details, Test Details) as Values.
	 * @throws IOException
	 */
	public static void generate(HashMap<String, ?> testExecutionResult) throws IOException {
		setFilename();
		fillReport(testExecutionResult);
		createReport();
		
		Logger.separator();
		System.out.println("Report Generated : " + filename + "/report.html");
		Logger.separator();
	}
	
	// Getter and Setter functions
	public static String getReport() {
		return reportContent;
	}
	public static String getFilename() {
		return filename;
	}
	private static void setFilename() {
		DateFormat fileFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date date = new Date();
		HtmlReport.filename = fileFormat.format(date).toString();
	}
}
