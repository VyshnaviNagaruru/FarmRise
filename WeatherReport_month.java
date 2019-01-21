package farmrise;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.*;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import pageobjects.BaseClass;

public class WeatherReport_month {
	AndroidDriver<MobileElement> driver;
	DesiredCapabilities dc = new DesiredCapabilities();
	BaseClass base = new BaseClass();
	String desiredlang="मराठी";
	static List<MobileElement> hourlytemperature= new ArrayList<>();
	static List<MobileElement> hourlyValue= new ArrayList<>();
	static List<MobileElement> hourlytempIcon= new ArrayList<>();
	static Set<String> hourlydetails;
	Boolean found=false;
	
	@BeforeTest
	public void launchapp() throws MalformedURLException
	{
		dc.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
		dc.setCapability(MobileCapabilityType.PLATFORM_VERSION, "9");
		dc.setCapability(MobileCapabilityType.DEVICE_NAME, "DRGID18092400741");
		dc.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
		dc.setCapability("appPackage", "com.climate.farmrise");
		dc.setCapability("appActivity", "com.climate.farmrise.SplashScreen");
		driver = new AndroidDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"), dc);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	}
	
	@Test
	public void fetch_weatherData() throws InterruptedException
	{
		
		waitforElement(base.farmreise_spashscreen);
		
		waitforElement(base.preferrdlang_title);
		
		//Verify preferred language screen
		Assert.assertEquals(setDriver(base.preferrdlang_title).getText(), "Choose your preferred language","Title is not matched, actual title displayed on screen"+setDriver(base.preferrdlang_title).getText());
		
		//select language
		List<MobileElement> langnames = driver.findElements(base.langnames);
		
		for(MobileElement e: langnames)
		{
			if(e.getText().equals(desiredlang))
			{
				found=true;
				e.click();
			}
		}
		if(!found)
		{
			System.out.println("Language is not mentioned properly");
			new SkipException("Select appropriate language inorder to proceed further");
		}
		
		setDriver(base.proceedbtn).click();
	
		setDriver(base.agreebtn).click();
		
		waitforElement(base.okbtn);
		
		setDriver(base.okbtn).click(); // Home button
		
		waitforElement(base.okbtn);
		
		setDriver(base.okbtn).click(); // Mandi button
		
		waitforElement(base.okbtn);
		
		setDriver(base.okbtn).click(); // Agronomy button
		
		waitforElement(base.okbtn);
		
		setDriver(base.okbtn).click(); // Chat button
		
		waitforElement(base.okbtn);
		
		setDriver(base.okbtn).click(); // More button
		
		waitforElement(base.checkweathedetails);
		
		setDriver(base.checkweathedetails).click();
		
		waitforElement(base.weatherTitle);
		
		hourlydetails = new HashSet<String>();
		
		do
		{
			hourlytemperature=driver.findElements(base.hourlytemp);
			hourlyValue = driver.findElements(base.hourlylist);
			hourlytempIcon=driver.findElements(base.hourlytempIcon);
			for(int i=0;i<hourlytempIcon.size();i++)
			{
				hourlydetails.add(hourlyValue.get(i).getText()+"/"+hourlytemperature.get(i).getText());
			}
			String oldName = hourlyValue.get(hourlyValue.size()-1).getText();
			
			scrollHorizontal(setDriver(base.hourlyscrollview));
			
			hourlyValue = driver.findElements(base.hourlylist);
			
			String newName = hourlyValue.get(hourlyValue.size()-1).getText();
			
			if(oldName.equals(newName))
				break;
			
		}while(setDriver(base.hourlyscrollview).getAttribute("scrollable").equals("true"));
		
		System.out.println("Complete List of temperature for 24 hrs "+ hourlydetails.size());
		
		for(String text:hourlydetails)
		{
			System.out.println(text);
		}
		
	}
	
	@AfterTest
	public void closeapp()
	{
		driver.quit();
	}
	
	public MobileElement setDriver(By by)
	{
		return driver.findElement(by);
	}

	public void waitforElement(By by)
	{
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.presenceOfElementLocated(by));
	}
	
	public void scrollHorizontal(WebElement e)
	{
	    Dimension size = driver.manage().window().getSize();
	    int x = e.getLocation().x;
	    int y = e.getLocation().y;
	    int startX =e.getLocation().x+(int)(e.getSize().width *0.9);
	    int startY = y+ (int)(e.getSize().height)/2;
	    int endX = e.getLocation().x+(int)(e.getSize().width * 0.1) ;
		TouchAction touch= new TouchAction(driver);
		touch.longPress(PointOption.point(startX, startY)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000))).moveTo(PointOption.point(endX, startY)).release().perform();
		
	}
}
