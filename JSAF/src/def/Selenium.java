package def;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*; 
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Selenium Automation Framework
 * def.Selenium.java
 * Purpose: Contains Selenium related methods
 *
 * @version 0.0.1
 * @author VAGEESH BHASIN
 */

public class Selenium {
	
	
	/**
	 * This method is a listener for AJAX calls - Specifically jQuery calls, it passes after first ajax call completes
	 * @param driver
	 * @throws InterruptedException
	 */
	public static void WaitForAjax(WebDriver driver) throws InterruptedException
	{
	    while (true)
	    {
	    	// JavaScript test to verify jQuery is active or not
	        Boolean ajaxIsComplete = (Boolean)(((JavascriptExecutor)driver).executeScript("return jQuery.active == 0"));
	        if (ajaxIsComplete)
	            break;
	        Thread.sleep(100);
	    }
	}
	
	/**
	 * This method is used to return a remote web driver with capabilities according to the passed driver type.
	 * @param driverType
	 * @return WebDriver
	 */
	public static WebDriver getDriver(String driverType) {
		
		DesiredCapabilities cap;
		switch (driverType.toLowerCase()) {
			// Chrome Capabilities
			case "chrome":
				cap = DesiredCapabilities.chrome();
				break;
			// Firefox capabilities - DEFAULT
			case "firefox":
			default:
				cap = DesiredCapabilities.firefox();
				break;
		}
		return new RemoteWebDriver(cap);
	}
	
	/**
	 * This method is used to initialize driver and open the web page given by the parameter 'url'.
	 * @param url - Specifies the web page to open
	 * @param driverType - Specifies the web driver type (Firefox, Chrome, Internet Explorer)
	 * @return WebDriver
	 */
	public static WebDriver initDriver(String url, String driverType) {
		WebDriver driver = getDriver(driverType);
		
		// Add Ajax Activity listener instance to check for Ajax requests
		new EventFiringWebDriver(driver).register(new AjaxActivityIndicatorEventListener()); 
		
		// Maximaize the window
		driver.manage().window().maximize();
		
		// Open URL if it was passed
		if(url != null) {
			driver.get(url);
		}
		
		return driver;
	}
	
	/**
	 * This method is used to locate a web element on the web page.
	 * @param driver Selenium web driver to locate element
	 * @param locatorType Specifies the method to be used to locate a web element
	 * @param locatorValue Specifies the value for the location method to use
	 * @return WebElement
	 */
	public static Object find(WebDriver driver, String locatorType, String locatorValue, String opts) {
		
		// Define webdriver wait, waits for max 10 seconds to find an element
		WebDriverWait wait = new WebDriverWait(driver,10);
		
		try {
			switch (locatorType.toLowerCase()) {
				case "url":
					return driver.getCurrentUrl();
				case "id":
					return wait.until(ExpectedConditions.presenceOfElementLocated(By.id(locatorValue)));
				case "xpath":
					return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(locatorValue)));
				case "css":
					return wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(locatorValue)));
				case "classname":
					return wait.until(ExpectedConditions.presenceOfElementLocated(By.className(locatorValue)));
				case "linktext":
					return wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText(locatorValue)));
				case "name":
					return wait.until(ExpectedConditions.presenceOfElementLocated(By.name(locatorValue)));
				case "partiallinktext":
					return wait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(locatorValue)));
				case "tagname":
					return wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName(locatorValue)));
				// Invalid locator type was passed, return null
				default:
					return null;
			}
		} 
		// Wait timed out before the element could be located
		catch(Exception e) {
			return null;
		}
	}
	
	/**
	 * This method is used to locate a web elements on the web page.
	 * @param driver Selenium web driver to locate element
	 * @param locatorType Specifies the method to be used to locate a web element
	 * @param locatorValue Specifies the value for the location method to use
	 * @return WebElement
	 */
	public static List<WebElement> findElements(WebDriver driver, String locatorType, String locatorValue) {
		
		// Define webdriver wait, waits for max 10 seconds to find an element
		WebDriverWait wait = new WebDriverWait(driver,10);
		
		try {
			switch (locatorType.toLowerCase()) {
				case "id":
					return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(locatorValue)));
				case "xpath":
					return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(locatorValue)));
				case "css":
					return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(locatorValue)));
				case "classname":
					return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(locatorValue)));
				case "linktext":
					return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.linkText(locatorValue)));
				case "name":
					return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name(locatorValue)));
				case "partiallinktext":
					return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.partialLinkText(locatorValue)));
				case "tagname":
					return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName(locatorValue)));
				// Invalid locator type specified, return null
				default:
					return null;
			}
		} 
		// Wait timed out before the element could be located
		catch(Exception e) {
			return null;
		}
	}
	
	/**
	 * This method waits for the element to be invisible
	 * @param driver
	 * @param locatorType
	 * @param locatorValue
	 * @return
	 */
	public static String[] isNotDisplayed(WebDriver driver, String locatorType, String locatorValue) {
		
		// Define webdriver wait, waits for max 10 seconds to find an element
		WebDriverWait wait = new WebDriverWait(driver,10);
		
		try {
			String[] result = {".", null}; 
			switch (locatorType.toLowerCase()) {
				case "id":
					wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(locatorValue)));
					break;
				case "xpath":
					wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(locatorValue)));
					break;
				case "css":
					wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(locatorValue)));
					break;
				case "classname":
					wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className(locatorValue)));
					break;
				case "linktext":
					wait.until(ExpectedConditions.invisibilityOfElementLocated(By.linkText(locatorValue)));
					break;
				case "name":
					wait.until(ExpectedConditions.invisibilityOfElementLocated(By.name(locatorValue)));
					break;
				case "partiallinktext":
					wait.until(ExpectedConditions.invisibilityOfElementLocated(By.partialLinkText(locatorValue)));
					break;
				case "tagname":
					wait.until(ExpectedConditions.invisibilityOfElementLocated(By.tagName(locatorValue)));
					break;
				// Invalid locator type was passed, return null
				default:
					return null;
			}
			return result;
		} 
		// Wait timed out before the element could be located
		catch(Exception e) {
			return new String[]{"F", "Element with {" + locatorType + " => " + locatorValue + "}, was present on the screen."};
		}
	}
		
	/**
	 * This method performs a selenium action on the web element.
	 * @param driver The web driver to attach the action to
	 * @param element The web element on which an action is to be performed
	 * @param actionType The type of action to be performed
	 * @param actionValue The value to be used with performing an action
	 * @return status, message(Default = null)
	 */
	public static String[] action(WebDriver driver, Object element, String actionType, String actionValue) {

		String[] stepStatus = new String[2];
		
		try {
			// Action on String Element - i.e., the element was not a web element
			if(element instanceof String) {
				
				String strElement = (String) element;
				
				// Check for AJAX
				WaitForAjax(driver);
					
				// Wait for action element to be stable - Mimicking user action latency
				Thread.sleep(500);
					
				// Perform Action
				switch (actionType.toLowerCase()) {
						
					// Verification - Verify if the url matches the passed value
					case "assert":
						if(strElement.equalsIgnoreCase(actionValue)) {
							stepStatus[0] = ".";
						} 
						else {
							int counter = 0;
							stepStatus[0] = "F";
							
							while(counter <= 50) {
								if (driver.getCurrentUrl().equals(actionValue)) {
									stepStatus[0] = ".";
									break;
								} else {
									stepStatus[1] = "Expected: " + actionValue + ", Found: " + strElement.toString() + ".";
									Thread.sleep(100);
									counter++;
								}
							}
						}
						break;
					default:
						stepStatus[0] = "F";
						stepStatus[1] = "Unknown action provided!";
						if(actionType != null) stepStatus[1] = "Could not perform --> " + actionType;
						break;
						
					}
			}
			
			// Action on a WebElement
			else if (element instanceof WebElement) {
				
				WebElement thisElement = (WebElement) element;
				WebDriverWait actionWait = new WebDriverWait(driver, 10);
				Actions action;
			
				// Check for AJAX
				WaitForAjax(driver);
				
				// Wait for action element to be stable - Mimicking user action latency
				Thread.sleep(500);
				
				// Perform Action
				switch (actionType.toLowerCase()) {
				
					// 	Action - Perform Input to element
					case "input":
						
						// Check if special keys were sent
						if(actionValue.equalsIgnoreCase("ENTER")) {
							thisElement.sendKeys(Keys.ENTER);
						} else if(actionValue.equalsIgnoreCase("SPACE")) {
							thisElement.sendKeys(Keys.SPACE);
						} else if(actionValue.equalsIgnoreCase("RETURN")) {
							thisElement.sendKeys(Keys.RETURN);
						} 
						// Send other key inputs
						else {
							// Clear the element before sending input
							thisElement.clear();
							thisElement.sendKeys(actionValue);
						}
						stepStatus[0] = ".";
						break;
						
					// Action - Perform Hover over element
					case "hover":
						action = new Actions(driver);
						action.moveToElement(thisElement).build().perform();
						Thread.sleep(500);
						stepStatus[0] = ".";
						break;
						
					// Action - Perform Click on Element
					case "click":
						try {
							// Check if element was on screen
							if (thisElement.getSize() != null) {
								thisElement.click();
							}
							// Element did not have any size, scroll into view the element
							else {
								((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", thisElement);
								thisElement.click();
							}
							stepStatus[0] = ".";
						}
						catch(Exception e) {
							if(thisElement.isDisplayed() == true) {
								((WebElement) element).click();
								stepStatus[0] = "W";
								stepStatus[1] = "We waited for the element to be clickable, but its state might have changed. We clicked it anyway.";
							} else {
								stepStatus[0] = "F";
								stepStatus[1] = "The element might not be present and hence could not be clicked!";
							}
						}
						break;
					
					// Action - Perform Right Click
					case "rightclick":
						action = new Actions(driver);
						action.moveToElement(thisElement);
						action.contextClick(thisElement).build().perform();;
						stepStatus[0] = ".";
						break;
						
					// Action - Perform Drag and Drop
					case "draganddrop":
						WebElement onElement = thisElement;
						WebElement toElement = driver.findElement(By.xpath(actionValue));
						action = new Actions(driver);
						action.clickAndHold(onElement);
						Thread.sleep(500);
						action.moveToElement(toElement);
						Thread.sleep(500);
						action.release();
						action.perform();
						stepStatus[0] = ".";
						break;
						
					// Action - Perform Clear on element
					case "clear":
						thisElement.clear();
						stepStatus[0] = ".";
						break;
						
					// Action - Execute a JavaScript snippet
					case "javascript":
						// Execute JS if driver can run JS
						if (driver instanceof JavascriptExecutor) {
						    ((JavascriptExecutor)driver).executeScript(actionValue);
						    stepStatus[0] = ".";
						}
						// Driver cant run JS, fail the step
						else {
							stepStatus[0] = "F";
							stepStatus[1] = "The driver cannot handle JavaScript execution.";
						}
						break;
						
					// Action - Save the value of the element
					case "save":
						if(thisElement.getText() != null && !thisElement.getText().isEmpty()) { 
							stepStatus[0] = ".";
							stepStatus[1] = thisElement.getText();
						} 
						else if(thisElement.getAttribute("value") != null && !thisElement.getAttribute("value").isEmpty()) {
							stepStatus[0] = ".";
							stepStatus[1] = thisElement.getAttribute("value");
						}
						else {
							stepStatus[0] = "F";
							stepStatus[1] = "The element - " + thisElement.toString() + " , has no text or value attribute.";
						}
						break;
						
					// Verification - Verify if element is element
					case "isempty":
						if(thisElement.getText().isEmpty()) {
							stepStatus[0] = ".";
						}
						else {
							stepStatus[0] = "F";
							stepStatus[1] = "Expected the element to be empty, instead found - " + thisElement.getText() + ".";
						}
						break;

					// Verification - Verify if element is present or is displayed
					case "ispresent":
					case "isdisplayed":
						
						// Wait until the element is visible on screen
						try {
							actionWait.until(ExpectedConditions.visibilityOf(thisElement));
							stepStatus[0] = ".";
						}
						// Wait timed out, element was not present, fail the test
						catch(Exception e) {
							stepStatus[0] = "F";
							stepStatus[1] = "The element - " + element.toString() + " , is not displayed/present on the page.";
						}
						break;
						
					// Verification - Verify if element has the same value as passed
					case "match":
					case "assert":
						if(thisElement.getText() != null && thisElement.getText().contains(actionValue)) { 
							stepStatus[0] = ".";
						} 
						else if(thisElement.getAttribute("value") != null && thisElement.getAttribute("value").contains(actionValue)) {
							stepStatus[0] = ".";
						}
						else {
							stepStatus[0] = "F";
							if (thisElement.getText() == null && thisElement.getAttribute("value") != null)
								stepStatus[1] = "Expected: |" + actionValue + "|, Found: |" + thisElement.getAttribute("value") + "|.";
							else if(thisElement.getText() != null && thisElement.getAttribute("value") == null)
								stepStatus[1] = "Expected: |" + actionValue + "|, Found: |" + thisElement.getText() + "|.";
							else if (thisElement.getText() != null && thisElement.getAttribute("value") != null)
								stepStatus[1] = "Expected: |" + actionValue + "|, Found: Text - |" + thisElement.getText() + "|, Value - |" + thisElement.getAttribute("value") + "|.";
							else
								stepStatus[1] = "No text or attribute - value, was found for this element";
						}
						break;
					default:
						stepStatus[0] = "F";
						stepStatus[1] = "No action provided!";
						if(actionType != null) stepStatus[1] = "Could not perform --> " + actionType;
						break;
				}
			}
			
			// Unknown element type - Fail it
			else {
				stepStatus[0] = "F";
				stepStatus[1] = "Unknown element type - ";
				if(actionType != null) stepStatus[1] += "Could not perform --> [" + actionType + "] for element [" + element.toString() + "].";
			}
		}
		catch(Exception e) {
			stepStatus[0] = "F";
			if(e.getMessage() != null)
				stepStatus[1] = e.getMessage(); //.split(":")[0].toString();
			else
				stepStatus[1] = "Unexpected Error";
		}
		
		return stepStatus;
	}
	
	/**
	 * This method take a screenshot and saves it in a temporary folder (/temp)
	 * @param driver WebDriver to take screenshot with
	 * @param filename Name of the file to be saved as
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static void screenshot(WebDriver driver, String filename, WebElement element, String type) {
		
		try {
			driver = new Augmenter().augment( driver );
		
			// Create border around element
			if ( element!=null ) {
				if(type.equalsIgnoreCase("F")) {
					((JavascriptExecutor)driver).executeScript("arguments[0].style.boxShadow='0px 0px 40px red'", element);
					((JavascriptExecutor)driver).executeScript("arguments[0].style.border='3px solid red'", element);
					Thread.sleep(500);
				}
				else if(type.equalsIgnoreCase("W")) {
					((JavascriptExecutor)driver).executeScript("arguments[0].style.boxShadow='0px 0px 40px orange'", element);
					((JavascriptExecutor)driver).executeScript("arguments[0].style.border='3px solid orange'", element);
					Thread.sleep(500);
				}
			}
			
			File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
	
			// Get Present Working Directory
			String currentDir = System.getProperty("user.dir");
	
			Utils.makeDir(currentDir + "/temp");
			
			FileUtils.copyFile(scrFile, new File(currentDir + "/temp/" + filename.toLowerCase()));
		}
		catch(Exception e) {
			
		}
	}
	
}
