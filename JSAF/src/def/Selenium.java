package def;

import org.openqa.selenium.*; 
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Selenium {
	public static WebDriver getDriver(String driverType) {
		switch (driverType.toLowerCase()) {
		case "firefox":
			return new FirefoxDriver(); 

		default:
			return new FirefoxDriver();
		}
	}
	
	/**
	 * {@code initDriver(url, driverType)} This method is used to initialize driver and open the web page given by 'url'
	 * @param url - Specifies the web page to open
	 * @param driverType - Specifies the web driver type (Firefox, Chrome, Internet Explorer)
	 * @return WebDriver
	 */
	public static WebDriver initDriver(String url, String driverType) {
		WebDriver driver = getDriver(driverType);
		driver.manage().window().maximize();
		driver.get(url);
		return driver;
	}
	
	/**
	 * @param driver - Takes in a webdriver to locate element
	 * @param locatorType - Specifies the method to be used to locate a web element
	 * @param locatorValue - Specifies the value for the location method to use
	 * @return WebElement - Returns a web element if found
	 */
	public static Object find(WebDriver driver, String locatorType, String locatorValue) {
		
		WebDriverWait wait = new WebDriverWait(driver,10);
		
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
			case "url":
				return driver.getCurrentUrl();
			default:
				return wait.until(ExpectedConditions.presenceOfElementLocated(By.id(locatorValue)));
		}
	}
	
	/**
	 * @param element
	 * @param actionType
	 * @param actionValue
	 * @return
	 */
	public static String action(Object element, String actionType, String actionValue) {
		String stepStatus;
		switch (actionType.toLowerCase()) {
			case "input":
				((WebElement) element).sendKeys(actionValue);
				stepStatus = ".";
				break;
			case "click":
				((WebElement) element).click();
				stepStatus = ".";
				break;
			case "clear":
				((WebElement) element).clear();
				stepStatus = ".";
				break;
			case "verify":
				if(element.getClass().toString().toLowerCase().equals("string") == true) {
					if(element.toString().equals(actionValue)) {
						stepStatus = ".";
					} else {
						stepStatus = "F";
					}
				} else {
					if(((WebElement) element).getText().equals(actionValue)) {
						stepStatus = ".";
					} else {
						stepStatus = "F";
					}
				}
				break;
			default:
				stepStatus = "F";
				break;
		}
		return stepStatus;
	}
}
