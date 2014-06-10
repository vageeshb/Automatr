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
	public static void WaitForAjax(WebDriver driver) throws InterruptedException
	{
	    while (true) // Handle timeout somewhere
	    {
	        Boolean ajaxIsComplete = (Boolean)(((JavascriptExecutor)driver).executeScript("return jQuery.active == 0"));
	        if (ajaxIsComplete)
	            break;
	        Thread.sleep(100);
	    }
	}
	/*public static void startGrid() {
		try {
			Runtime rt = Runtime.getRuntime();
			String path = System.getProperty("user.dir") + "/lib";
			Process proc = rt.exec("java -jar " + path +"/selenium-server-standalone-2.39.0.jar -role hub");
			
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

		    BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		    // read the output from the command
		    System.out.println("Here is the standard output of the command:\n");
		    String s = null;
		    while ((s = stdInput.readLine()) != null) {
		        System.out.println(s);
		    }

		    // read any errors from the attempted command
		    System.out.println("Here is the standard error of the command (if any):\n");
		    while ((s = stdError.readLine()) != null) {
		        System.out.println(s);
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	/**
	 * This method is used to return a web driver depending on the parameter 'driverType'.
	 * @param driverType
	 * @return WebDriver
	 */
	public static WebDriver getDriver(String driverType) {
		DesiredCapabilities cap;
		switch (driverType.toLowerCase()) {
			case "chrome":
				cap = DesiredCapabilities.chrome();
				break;
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
		//driver.manage().timeouts().implicitlyWait(10000, TimeUnit.MILLISECONDS);
		new EventFiringWebDriver(driver).register(new AjaxActivityIndicatorEventListener()); 
		driver.manage().window().maximize();
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
				default:
					return null;
			}
		} catch(Exception e) {
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
				default:
					return null;
			}
		} catch(Exception e) {
			return null;
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
	public static String[] action(WebDriver driver,Object element, String actionType, String actionValue) {
		String[] stepStatus = new String[2];
		WebDriverWait actionWait = new WebDriverWait(driver, 10);
		Actions action;
		try {
			// Check for AJAX
			WaitForAjax(driver);
			// Wait for action element to be stable - Mimicking user action latency
			Thread.sleep(500);
			switch (actionType.toLowerCase()) {
				case "input":
					if(actionValue.equalsIgnoreCase("ENTER")) {
						((WebElement) element).sendKeys(Keys.ENTER);
					} else if(actionValue.equalsIgnoreCase("SPACE")) {
						((WebElement) element).sendKeys(Keys.SPACE);
					} else if(actionValue.equalsIgnoreCase("RETURN")) {
						((WebElement) element).sendKeys(Keys.RETURN);
					} else {
						((WebElement) element).clear();
						((WebElement) element).sendKeys(actionValue);
					}
					stepStatus[0] = ".";
					break;
				case "hover":
					action = new Actions(driver);
					action.moveToElement((WebElement)element).build().perform();
					stepStatus[0] = ".";
					break;
				case "click":
					try {
						((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", (WebElement) element);
						((WebElement) element).click();
						stepStatus[0] = ".";
						break;
					}
					catch(Exception e) {
						if(((WebElement) element).isDisplayed() == true) {
							((WebElement) element).click();
							stepStatus[0] = "W";
							stepStatus[1] = "We waited for the element to be clickable, but its state might have changed. We clicked it anyway.";
						} else {
							stepStatus[0] = "F";
							stepStatus[1] = "The element might not be present and hence could not be clicked!";
						}
						break;
					}
				case "rightclick":
					action = new Actions(driver);
					action.moveToElement((WebElement) element);
					action.contextClick((WebElement) element).build().perform();;
					stepStatus[0] = ".";
					break;
				case "draganddrop":
					WebElement onElement = (WebElement) element;
					WebElement toElement = driver.findElement(By.xpath(actionValue));
					action = new Actions(driver);
					action.clickAndHold(onElement);
					action.pause(500);
					action.moveToElement(toElement);
					action.pause(500);
					action.release();
					action.perform();
					stepStatus[0] = ".";
					break;
				case "clear":
					((WebElement) element).clear();
					stepStatus[0] = ".";
					break;
				case "javascript":
					if (driver instanceof JavascriptExecutor) {
					    ((JavascriptExecutor)driver).executeScript(actionValue);
					}
					stepStatus[0] = ".";
					break;
				case "isempty":
					if(((WebElement) element).getText().isEmpty()) {
						stepStatus[0] = ".";
					}
					else {
						stepStatus[0] = "F";
						stepStatus[1] = "Expected the element to be empty, instead found - " + ((WebElement) element).getText() + ".";
					}
					break;
				case "isnotpresent":
				case "isnotdisplayed":
					try{
						String temp = ((String)element).toLowerCase();
						switch(temp) {
							case "id":
								actionWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(actionValue)));
								stepStatus[0] = ".";
								break;
							case "xpath":
								actionWait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(actionValue)));
								stepStatus[0] = ".";
								break;
							case "css":
								actionWait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(actionValue)));
								stepStatus[0] = ".";
								break;
							case "classname":
								actionWait.until(ExpectedConditions.invisibilityOfElementLocated(By.className(actionValue)));
								stepStatus[0] = ".";
								break;
							case "linktext":
								actionWait.until(ExpectedConditions.invisibilityOfElementLocated(By.linkText(actionValue)));
								stepStatus[0] = ".";
								break;
							case "name":
								actionWait.until(ExpectedConditions.invisibilityOfElementLocated(By.name(actionValue)));
								stepStatus[0] = ".";
								break;
							case "partiallinktext":
								actionWait.until(ExpectedConditions.invisibilityOfElementLocated(By.partialLinkText(actionValue)));
								stepStatus[0] = ".";
								break;
							case "tagname":
								actionWait.until(ExpectedConditions.invisibilityOfElementLocated(By.tagName(actionValue)));
								stepStatus[0] = ".";
								break;
							default:
								stepStatus[0] = "F";
								stepStatus[1] = "Unknown element locator";
								break;
						}
						break;
					} catch(Exception e) {
						stepStatus[0] = "F";
						stepStatus[1] = "Element with - " + (String)element + " = " + actionValue + ", was present on the screen.";
						break;
					}
				case "ispresent":
				case "isdisplayed":
					try {
						actionWait.until(ExpectedConditions.visibilityOf((WebElement) element));
						stepStatus[0] = ".";
						break;
					}
					catch(Exception e) {
						stepStatus[0] = "F";
						stepStatus[1] = "The element - " + element.toString() + " , is not displayed/present on the page.";
						break;
					}
				case "assert":
					if(element instanceof String) {
						if(((String) element).equalsIgnoreCase(actionValue)) {
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
									stepStatus[1] = "Expected: " + actionValue + ", Found: " + element.toString() + ".";
									Thread.sleep(100);
									counter++;
								}
							}
						}
					} else {
						WebElement temp = ((WebElement) element);
						if(temp.getText() != null && temp.getText().contains(actionValue)) { 
							stepStatus[0] = ".";
						} 
						else if(temp.getAttribute("value") != null && temp.getAttribute("value").contains(actionValue)) {
							stepStatus[0] = ".";
						}
						else {
							stepStatus[0] = "F";
							if (temp.getText() == null && temp.getAttribute("value") != null)
								stepStatus[1] = "Expected: |" + actionValue + "|, Found: |" + temp.getAttribute("value") + "|.";
							else if(temp.getText() != null && temp.getAttribute("value") == null)
								stepStatus[1] = "Expected: |" + actionValue + "|, Found: |" + temp.getText() + "|.";
							else if (temp.getText() != null && temp.getAttribute("value") != null)
								stepStatus[1] = "Expected: |" + actionValue + "|, Found: Text - |" + ((WebElement) element).getText() + "|, Value - |" + ((WebElement) element).getAttribute("value") + "|.";
							else
								stepStatus[1] = "No text or attribute - value, was found for this element";
						}
					}
					break;
				default:
					stepStatus[0] = "F";
					stepStatus[1] = "No action provided!";
					if(actionType != null) stepStatus[1] = "Could not perform --> " + actionType;
					break;
			}
		} catch(Exception e) {
			stepStatus[0] = "F";
			if(e.getMessage() != null)
				stepStatus[1] = e.getMessage().split(":")[0].toString();
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
	public static void screenshot(WebDriver driver, String filename, WebElement element, String type) throws IOException, InterruptedException {
		
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
	
}
