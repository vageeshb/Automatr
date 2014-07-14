package def;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*; 
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
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
		Boolean isJqueryUsed = (Boolean)(((JavascriptExecutor)driver).executeScript("return (typeof(jQuery) != 'undefined')"));
		if(isJqueryUsed) {
			while (true)
		    {
    			// JavaScript test to verify jQuery is active or not
    			Boolean ajaxIsComplete = (Boolean)(((JavascriptExecutor)driver).executeScript("return jQuery.active == 0"));
	        	if (ajaxIsComplete)
	        		break;
	        	Thread.sleep(100);
    		}
		}
		else {
			// Application uses some other method for XHR calls
	    	// TODO
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
	
	public static String[] miscActions(WebDriver driver, String actionName, String actionValue, String[] miscParams) {
		
		// Assume every action is successful
		String[] result = new String[] {".", null};
		
		try {

			// Check for AJAX
			WaitForAjax(driver);
				
			// Wait for action element to be stable - Mimicking user action latency
			Thread.sleep(500);
			
			// Perform action
			switch(actionName.toLowerCase()) {
				
				// Alert handler
				case "acceptalert":
					Alert alert = driver.switchTo().alert();
					alert.accept();
			        break;
			    
			    // Action - Execute a JavaScript snippet
				case "javascript":
					// Execute JS if driver can run JS
					if (driver instanceof JavascriptExecutor) {
					    ((JavascriptExecutor)driver).executeScript(actionValue);
					}
					// Driver cant run JS, fail the step
					else {
						result = new String[]{"F", "The driver cannot handle JavaScript execution."};
					}
					break;
				
				// Evaluation
				case "evaluate":
					// Execute JS if driver can run JS
					if (driver instanceof JavascriptExecutor) {
					    Object t = ((JavascriptExecutor)driver).executeScript("return eval(\"" + miscParams[0] + "\");");
					    if( t instanceof Long) {
					    	result[1] = Long.toString((Long)t);
					    } 
					    else if( t instanceof Double ) {
					    	result[1] = Double.toString((Double)t);
					    }
					    else if ( t instanceof Boolean) {
					    	result[1] = ((Boolean)t == true) ? "True" : "False";
					    }
					    else{
					    	result[1] = (String)t;
					    }
					}
					// Driver cant run JS, fail the step
					else {
						result = new String[]{"F", "The driver cannot handle JavaScript execution."};
					}
					break;
				case "switchto":
					Set<String> windows = driver.getWindowHandles();
		            for (String w : windows) {
		                driver.switchTo().window(w);
		                //System.out.println(w + ":" + driver.getTitle());
		                int counter = 0;
		                while( counter < 20 ) {
		                	counter++;
		                	Thread.sleep(100);
			                if (driver.getTitle().contains(actionValue)) {
			                	return new String[]{".", null};
			                }
		                }
		                result = new String[]{"F", "Could not locate window name - &#39;" + actionValue + "&#39;."};
		            }
					break;
				default:
					result = new String[]{"F", "Unknown action &#39;" + actionName + "&#39;."};
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			if(actionName.equalsIgnoreCase("evaluate"))
				result = new String[]{"F", e.getMessage().split("[(]")[0]};
			else
				result = new String[]{"F", "Unexpected error for action - &#39;" + actionName + "&#39;."};
		}
		
		return result;
	}
	
	public static String[] stringActions(WebDriver driver, String actual, String actionName, String actionValue, String[] miscParams) {
						
		// Assume every action is successful
		String[] result = new String[] {".", null};		
		
		try {
			
			// Check for AJAX
			WaitForAjax(driver);
				
			// Wait for action element to be stable - Mimicking user action latency
			Thread.sleep(500);
				
			// Perform Action
			switch (actionName.toLowerCase()) {
					
				// Verification - Verify if the url matches the passed value
				case "assert":
					if(!actual.equalsIgnoreCase(actionValue)) {
						int counter = 0;
						result[0] = "F";
						
						while(counter <= 50) {
							if (driver.getCurrentUrl().contains(actionValue)) {
								result[0] = ".";
								break;
							} else {
								result[1] = "Expected - &#39;" + actionValue + "&#39;, Found - &#39;" + driver.getCurrentUrl() + "&#39;.";
								Thread.sleep(100);
								counter++;
							}
						}
					}
					break;
					
				default:
					result = new String[]{"F", "Unknown action &#39;" + actionName + "&#39;."};
				}
		}
		catch(Exception e) {
			result = new String[]{"F", "Unexpected error for action - &#39;" + actionName + "&#39;."};
		}
		
		return result;
	}
	/**
	 * This method performs a selenium action on the web element.
	 * @param driver The web driver to attach the action to
	 * @param webElement The web element on which an action is to be performed
	 * @param actionName The type of action to be performed
	 * @param actionValue The value to be used with performing an action
	 * @return status, message(Default = null)
	 */
	public static String[] elementActions(WebDriver driver, WebElement webElement, String actionName, String actionValue, String[] miscParams) {

		// Assume every step is successful
		String[] result = new String[] {".", null};
		
		WebDriverWait actionWait = new WebDriverWait(driver, 10);
		Actions action;
		Select selectBox;
		String expected;
		
		try {

			// Check for AJAX
			WaitForAjax(driver);
			
			// Wait for action element to be stable - Mimicking user action latency
			Thread.sleep(500);
			
			// Perform Action
			switch (actionName.toLowerCase()) {
			
				// 	Action - Perform Input to element
				case "input":
					webElement.sendKeys(actionValue);
					break;
					
				case "send":
					// Check if special keys were sent
					if( miscParams != null && miscParams[0] != null ) {
						switch(miscParams[0].toLowerCase()) {
							case "copy":
								webElement.sendKeys(Keys.CONTROL + "c");
								break;
							case "paste":
								webElement.sendKeys(Keys.CONTROL + "v");
								break;
							case "enter":
								webElement.sendKeys(Keys.RETURN);
								break;
							case "backspace":
								webElement.sendKeys(Keys.BACK_SPACE);
								break;
							case "down":
								webElement.sendKeys(Keys.DOWN);
								break;
							case "up":
								webElement.sendKeys(Keys.UP);
								break;
							case "left":
								webElement.sendKeys(Keys.LEFT);
								break;
							case "right":
								webElement.sendKeys(Keys.RIGHT);
								break;
						}
					}
					break;
				// Action - Perform Hover over element
				case "hover":
					action = new Actions(driver);
					action.moveToElement(webElement).perform();
					Thread.sleep(500);
					break;
					
				// Action - Perform Click on Element
				case "click":
					try {
						// Check if element was on screen
						/*if (thisElement.getSize() != null) {
							thisElement.click();
						}*/
						if(webElement.isDisplayed()) {
							webElement.click();
						}
						// Element was not displayed on screen
						else {
							((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", webElement);
							// Wait for element to be stable
							Thread.sleep(1000);
							webElement.click();
						}
					}
					catch(Exception e) {
						if(webElement.isDisplayed() == true) {
							((WebElement) webElement).click();
							result = new String[]{"W", "We waited for the element to be clickable, but its state might have changed. We clicked it anyway."};
						} else {
							result = new String[]{"F", "The element might not be present and hence could not be clicked!"};
						}
					}
					break;
				
				// Action - Perform Right Click
				case "rightclick":
					action = new Actions(driver);
					action.moveToElement(webElement);
					action.contextClick(webElement).build().perform();;
					break;
					
				// Action - Perform Drag and Drop
				case "draganddrop":
					WebElement onElement = webElement;
					WebElement toElement = driver.findElement(By.xpath(actionValue));
					action = new Actions(driver);
					action.clickAndHold(onElement);
					Thread.sleep(500);
					action.moveToElement(toElement);
					Thread.sleep(500);
					action.release();
					action.perform();
					break;
					
				// Action - Perform Clear on element
				case "clear":
					webElement.clear();
					break;
					
				// Action - Perform Select by Text on Select Box
				case "selectbytext":
					selectBox = new Select(webElement);
					selectBox.selectByVisibleText(actionValue);
					break;
				
				// Action - Perform Select by Index on Select Box
				case "selectbyindex":
					selectBox = new Select(webElement);
					selectBox.selectByIndex(Integer.parseInt(actionValue));
					break;

				// Action - Perform Select by Value on Select Box
				case "selectbyvalue":
					selectBox = new Select(webElement);
					selectBox.selectByValue(actionValue);
					break;
					
				// Action - Save the value of the element
				case "save":
					if(webElement.getText() != null && !webElement.getText().isEmpty()) { 
						result[1] = webElement.getText();
					} 
					else if(webElement.getAttribute("value") != null && !webElement.getAttribute("value").isEmpty()) {
						result[1] = webElement.getAttribute("value");
					}
					else {
						result = new String[]{"F", "The element - " + webElement.toString() + " , has no text or value attribute."};
					}
					break;
					
				// Action - Save first selected option
				case "saveselected":
					result[1] = (new Select (webElement)).getFirstSelectedOption().getText();
					break;

				// Verification - Verify if element is element
				case "isempty":
					if(!webElement.getText().isEmpty()) {
						result = new String[]{"F", "Expected the element to be empty, instead found - " + webElement.getText() + "."};
					}
					break;
				
				// Verification - Verify if checkbox was checked
				case "ischecked":
					if(!webElement.isSelected()) {
						result = new String[]{"F", "Element was not checked!"};
					}
					break;
				
				// Verification - Verify if checkbox was not checked
				case "isnotchecked":
					if(webElement.isSelected()) {
						result = new String[]{"F", "Element was checked!"};
					}
					break;
					
				// Verification - Verify if element is present or is displayed
				case "ispresent":
				case "isdisplayed":
					
					// Wait until the element is visible on screen
					try {
						actionWait.until(ExpectedConditions.visibilityOf(webElement));
					}
					// Wait timed out, element was not present, fail the test
					catch(Exception e) {
						result = new String[]{"F", "The element - " + webElement.toString() + " , is not displayed/present on the page."};
					}
					break;
					
				// Verification - Verify if element has the same value as passed
				case "match":
				case "assert":
					expected = actionValue;
					if(miscParams == null) {
						String actual = webElement.getText();
						result = localAssert(expected, actual, true);
						if(result[0] != ".") {
							actual = webElement.getAttribute("value");
							result = localAssert(expected, actual, true);
						}
					} else {
						String actual = null;
						switch(miscParams[0].toLowerCase()) {
							case "attribute":
								actual = webElement.getAttribute(miscParams[1]);
								break;
						}
						result = localAssert(expected, actual, true);
					}
					break;
					
				// ASSERT FALSE
				case "assertfalse":
					expected = actionValue;
					if(miscParams == null) {
						String actual = webElement.getText();
						result = localAssert(expected, actual, false);
						if(result[0] != ".") {
							actual = webElement.getAttribute("value");
							result = localAssert(expected, actual, false);
						}
					} else {
						String actual = null;
						switch(miscParams[0].toLowerCase()) {
							case "attribute":
								actual = webElement.getAttribute(miscParams[1]);
								break;
						}
						result = localAssert(expected, actual, false);
					}
					break;
				
				// Attribute related
				case "getattribute":
					result = new String[]{".", webElement.getAttribute(miscParams[0])};
					break;
					
				// Default
				default:
					result = new String[]{"F", "Unknown action &#39;" + actionName + "&#39;."};
					break;
			}
		}
		catch(Exception e) {
			result = new String[]{"F", "Unexpected error for action - &#39;" + actionName + "&#39;."};
		}
		
		return result;
	}
	
	private static String[] localAssert(String expected, String actual, Boolean flag) {
		if( expected == null || actual == null || expected.equals("") || actual.equals("")) {
			return new String[]{"F", "One of the assertion values is null, please re-check the step inputs."};
		} 
		else {
			if( flag == true ) {
				if ( expected.contains(actual) || actual.contains(expected) ) {
					return new String[]{".", null};
				} else {
					return new String[]{"F", "Expected - &#39;" + expected + "&#39;, Found - &#39;" + actual + "&#39;."};
				}
			} else {
				if (!( expected.contains(actual) || actual.contains(expected) )) {
					return new String[]{".", null};
				} else {
					return new String[]{"F", "Expected - &#39;" + expected + "&#39;, Found - &#39;" + actual + "&#39;."};
				}
			}
		}
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
