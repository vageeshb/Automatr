JAVA Selenium Automation Framework
=================================

An automation framework for performing automation testing of Web Applications using Selenium.
__Version__ : 0.0.9

## How to Use ##
* Create data file that hosts configuration, execution list and test cases.
	* Refer wiki on how to create test case file, which has the required format for execution
* Provide data file path as the first argument
* Execute main function or run the jar file
* Result report is generated in reports folder

## Instructions ##
* Reads excel file containing test cases
* Requires Selenium Grid to be set up (Looks for default port, i.e., 4444)
* Executes the test cases specified in the 'Execution Manager' sheet
* Can call standalone test case within another test case
	* DRY Principle - Allows to reuse existing test case within another test case 
* Allows overiding the steps in standalone tests called within a test

## Selenium Capabilities ##
* Can locate web elements using all 7 locating methods (Name, TagName, CSS, XPATH, ID, LinkText, PartialLinkText)
* Takes screenshot in case of step failure
* Can run testing on Firefox and Chrome
* Has default listener for AJAX calls (Listens to 'jQuery.active == 0' condition)
* Can perform Element find using:
	* Find (Waits upto 5 seconds for the element to be present)
	* Find Elements (Finds all elements matching the criteria)
* Can perform below selenium actions:
	* General Functions:
		* Wait
		* Javascript
		* Run (Run another test case)
		* SwitchTo - Switches to another window on the basis of window title
		* SwitchToParent - Jumps back to parent window
		* Evaluate - Evaluates a mathematical expression (Usese Browsers JS Runtime)
		
	* Web Element Functions:
		* Input
		* Click / Check
		* Right Click
		* Clear / Empty
		* Send - Send special key combinations (Ctrl+C, Enter, etc)
		* Element Present/Element Displayed
		* Element Not Present/Element Not Displayed
		* Element is Checked / is Not checked
		* Hover
		* Drag And Drop
		* Select Box Specific Functions
			* SelectByText
			* SelectByValue
			* SelectByIndex
		* GetAttribute - Saves the specified attribute of an element to a variable
		
	* Run-Time utility functions
		* Save - Save Text or Value attribute of an element to run-time variable
		* Match - Match the Text or Value of an element to stored run-time variable
		* Equal - To check equality of 2 variables
		* NotEqual - To check inequality of 2 variables
		* Print - Prints out text, value or string to console
	
	* Assertion Functions (Assert)
		* Element Text / Value Assert - Default
		* Element Attribute Assert - MiscParams over-ride
		* Url Assert

## Dependencies ##
* jxl.jar - Reading and writing Excel files
* selenium-server-standalone - v2.42.2

## Road Map ##
* ~~Basic selenium functions~~
* ~~Reporting~~
* ~~Using results from execution module in reporting module~~
* ~~Take screenshot in case of step failure~~
* ~~Adding assertion checks~~
* ~~Allow providing data file path from command line / run config~~
* Adding advanced selenium functions - In-Progress
* Better Errors
* ~~Better Reports~~
* ~~Error focus in screenshot~~
* ~~Migrate to Maven/Ant project~~ - Replaced with JAR executable
* Email configuration for report delivery
* ~~Add 'Evaluate' functionality~~

## Change Log ##
* __14/07/2014__ : 
	* [New Feature] : Evaluate functionality
	* [New Feature] : Print functionality
	* [New Feature] : Window handling
	* [Enhancement] : Attribute assertions
	* [Enhancement] : Code refactored
	* [Bug Fix] : Catch error of empty execution and report hashes
	* [Bug Fix] : Catch error of AJAX listener for sites not using jQuery for XHR
* __27/06/2014__ : Refactored selenium file code, added 'Equal','isChecked','isNotChecked', fixed javascript action
* __25/06/2014__ : Added select box option saving, assert can also use run-time variable data, bug fix in run-time lookup
* __18/06/2014__ : Error handling for missing parameter, option to skip a test step
* __13/06/2014__ : Added selenium functions for Select box, reworked error filename, reword error message in html report, added functionality to run command to allow step removal from front & back
* __12/06/2014__ : Added total test cases description while reading, fixed multiple module detail tab report bug, sorting test cases in report, fixed click - Scroll into view only if element out of viewport
* __11/06/2014__ : Refactored code, added Save/Match functionality, added error focus on web element, uses Selenium-2.42.2
* __10/06/2014__ : The result report now organize the test cases in accordians with status as the header color, reworked URL assert
* __06/06/2014__ : Created JAR executable, added Selenium Grid by default, AJAX listeners, command line argument for Data file
* __04/06/2014__ : Added more selenium funtions, restructured error handler for Action - Assert
* __02/06/2014__ : Restructured executor to remove if/elsifs and use switch for action types. 
* Reworked Reading and Executing for allowing user to reuse existing test case within another test case.