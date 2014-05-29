JAVA Selenium Automation Framework
=================================

An automation framework for performing automation testing of Web Applications using Selenium.

## How to Use ##
* Create data file that hosts configuration, execution list and test cases.
	* Refer data file in resources folder for reference
* Provide data file path to main function
* Run main
* Result report is generated in reports folder

## Instructions ##
* Reads excel file from /resources/ folder
* Can call standalone test case within another test case
	* DRY Principle - Allows to reuse existing test case within another test case 

## Selenium Capabilities ##
* Can locate web elements using all 7 locating methods (Name, TagName, CSS, XPATH, ID, LinkText, PartialLinkText)
* Takes screenshot in case of step failure
* Can perform below selenium functions:
	* Input
	* Click / Check
	* Clear
	* Assert
		* Element Text Assert
		* Current Url Assert

## Dependencies ##
* jxl.jar - Reading and writing Excel files
* selenium-server-standalone - v2.39.0

## Road Map ##
* ~~Basic selenium functions~~
* ~~Reporting~~
* ~~Using results from execution module in reporting module~~
* ~~Take screenshot in case of step failure~~
* Allow providing data file path from command line / run config - In-Progress
* Adding assertion checks - In-Progress
* Adding advanced selenium functions
* Error focus in screenshot
* Migrate to Maven/Ant project

## Change Log ##
* Reworked Reading and Executing for allowing user to reuse existing test case within another test case.