package reporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class HtmlReport {
	private static String report = "";
	private static String filename = "";
	
	private static void makeDir(String dirName) {
		File theDir = new File(dirName);
		// if the directory does not exist, create it
		if (!theDir.exists()) {
		    theDir.mkdir();  
		}
	}
	
	private static String header() {
		return	 	"<head>\n"
				+ 	"	<title>Test Execution Report</title>\n"
				+	"	<link rel='stylesheet' href='./resources/css/bootstrap.min.css'>\n"
				+ 	"	<script src='./resources/js/jquery.min.js'></script>\n"
				+ 	"	<script src='./resources/js/bootstrap.min.js'></script>\n"
				+ 	"	<script src='./resources/js/report.min.js'></script>\n"
				+ 	"</head>\n";
	}
	
	private static String pageHeader() {
		
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		return 		"<div class='page-header'>\n"
				+	"	<h1>" + filename + " <small>Test execution report generated on " + dateFormat.format(date) + " at " + timeFormat.format(date) + "</small></h1>\n"
				+	"</div>\n";
	}
	
	private static String navTabs() {
		return 		"<!-- Nav Tabs -->\n"
				+	"<ul class='nav nav-tabs nav-justified'>\n"
				+	"	<li class='active'><a href='#summary' data-toggle='tab'>Summary</a></li>\n"
				+   "	<li><a href='#modules' data-toggle='tab'>Modules</a></li>\n"
				+   "	<li><a href='#details' data-toggle='tab'>Details</a></li>\n"
				+   "</ul>\n";
	}
	
	/**
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
                + 	"				<tr>\n"
                + 	"					<td class='warning'><strong>Skipped</strong></td>\n"
                + 	"					<td>" + summary[5] + "</td>\n"
                + 	"				</tr>\n"
                + 	"			</tbody>\n"
                + 	"	</table>\n"
				+ 	"</div>\n";
	}
	
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
	
	private static String detailTab() {
		String temp = 	"<div class='tab-pane' id='details'>"
					+ 	"	<div class='panel panel-default'>"
					+ 	"		<div class='panel-heading'><strong>Details of Test Execution</strong></div>"
					+ 	"			<div class='panel-body'>"
					+ 	"				<ul class='nav nav-tabs nav-justified'>"
		
        			//    <li class='active'><a href='#contact' data-toggle='tab'>CONTACT</a></li>
        
        			+	"				</ul>"
        			+ 	"				<br>"
        			+ 	"				<div class='tab-content'><div class='tab-pane active' id='contact'>"
        			+ 	"					<table class='table table-bordered table-hover'>"
        			+ 	"						<thead>"
        			+ 	"							<tr class='info'>"
        			+ 	"								<th>Test Case Name</th>"
        			+ 	"								<th>Test Step Name</th>"
        			+ 	"								<th>Status</th>"
        			+ 	"							</tr>"
        			+ 	"						</thead>"
        			+	"						<tbody>"
              
        			/* 	<tr class='success'>
                  			<td>1</td>
                  			<td>Enter username</td>
                  			<td>PASS</td>
                		</tr>
                	*/
                  
        			+	"						</tbody>"
        			+	"					</table>"
        			+ 	"				</div>"
        			+ 	"			</div>"
        			+ 	"		</div>"
        			+ 	"	</div>"
        			+ 	"</div>";
		return temp;
	}
	
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
	
	private static String footer() {
		return 		"<div id='footer'>\n"
				+ 	"	<div class='text-center'>\n"
				+ 	"		<hr>\n"
				+ 	"		<span>Generated using <strong>SAF - v0.0.1</strong></span>\n"
				+ 	"		<span>&copy; 2014 VB</span>\n"
				+ 	"	</div>\n"
				+ 	"</div>\n";
	}
	
	private static void body() {
		HashMap<String, String[]> temp = new HashMap<String, String[]>();
		String[] strTemp = new String[4];
		strTemp[0] = "4";
		strTemp[1] = "3";
		strTemp[2] = "4";
		strTemp[3] = "2";
		String[] summary = new String[5];
		for (int i = 0; i < 6; i++) {
			summary[i] = "5";
		}
		temp.put("Contact", strTemp);
		report += "<html>\n"
				+ header()
				+ "<body>\n"
				+ "<div class='container'>\n"
				+ pageHeader()
				+ navTabs()
				+ "<br>\n<div class='tab-content'>\n"
				+ "<!-- Summary Tab -->\n"
				+ summaryTab(summary)
				+ "\n<!-- Module Tab -->\n"
				+ moduleTab(temp)
				+ "\n<!-- Details Tab -->\n"
				+ detailTab()
				+ "</div>\n"
				+ modal()
				+ footer()
				+ "</div>\n"
				+ "</body>\n"
				+ "</html>\n";		
	}
	
	private static void createReportResources(String dirName) throws IOException {
		String currentDir = System.getProperty("user.dir");
		
		File srcDir = new File(currentDir + "/resources/report");
		File trgDir = new File(dirName + "/resources");

		FileUtils.copyDirectory(srcDir, trgDir);
	}
	
	
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
		writer.println(report);
		writer.close();
	}
	
	public static void main(String[] args) throws IOException {
		setFilename();
		body();
		createReport();
	}
	public static String getReport() {
		return report;
	}
	public static void setReport(String report) {
		HtmlReport.report = report;
	}
	public static String getFilename() {
		return filename;
	}
	public static void setFilename() {
		DateFormat fileFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date date = new Date();
		HtmlReport.filename = fileFormat.format(date).toString();
	}
}
