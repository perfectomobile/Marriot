package test.java;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javaxt.http.Request;
import javaxt.io.Image;
import org.testng.asserts.SoftAssert;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteExecuteMethod;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionNotFoundException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import ru.yandex.qatools.allure.annotations.Attachment;
import ru.yandex.qatools.allure.annotations.Step;

public class TestBase {
		
		RemoteWebDriver driver;
		public DesiredCapabilities capabilities;
		Map<String, Object> perfectoCommand = new HashMap<>();
		String user = "username@gmail.com";
		String password = "password";
		String host = "cloud.perfectomobile.com";
		protected SoftAssert softAssert = new SoftAssert();
			
		@Parameters({ "targetEnvironment" })
		@BeforeTest
		public void beforeTest(String targetEnvironment) throws MalformedURLException{
			DesiredCapabilities capabilities = new DesiredCapabilities();
			
			switch (targetEnvironment) {	
				case "iPad Air":
					capabilities.setCapability("platformName", "iOS");
					capabilities.setCapability("description", "Patrick-Marriott");
					break;
			
			}
			
			capabilities.setCapability("user", user);
			capabilities.setCapability("password", password);
			capabilities.setCapability("bundleId", "com.marriott.iphoneprod");
			capabilities.setCapability("automationName", "appium");
			capabilities.setCapability("newCommandTimeout", "120");
			capabilities.setCapability("browserName", "");
				
			driver = new RemoteWebDriver (new URL("https://" + host + "/nexperience/perfectomobile/wd/hub"), capabilities);
			switchToContext(driver, "NATIVE_APP");
			closeApp();
			openApp();		
		}
			
		@Step("Tear Down and Download Report")
		public void testTearDown() throws Exception {
			if (driver != null) {
				closeApp();			
				driver.close();

				downloadReport("html");
	
				driver.quit();
			}
		}
		
		@Attachment
		public byte[] saveImage(byte[] imageToSave) {
	        return imageToSave;
	    }
		
		protected void saveScreenclip(String objXpath, String fileName) {
			int imageX = Integer.parseInt(driver.findElementByXPath(objXpath).getAttribute("x"));
			int imageY = Integer.parseInt(driver.findElementByXPath(objXpath).getAttribute("y"));
			int imageWidth = Integer.parseInt(driver.findElementByXPath(objXpath).getAttribute("width"));
			int imageHeight = Integer.parseInt(driver.findElementByXPath(objXpath).getAttribute("height"));
			byte[] Screenshot = downloadHighResScreenshot(user, password, host); 
			Image croppedScreenshot = new Image(Screenshot);
			
			croppedScreenshot.crop(imageX, imageY, imageWidth, imageHeight);
			
			saveImage(croppedScreenshot.getByteArray());
			croppedScreenshot.saveAs("target\\screenshots\\" + fileName);
		}
		
		public void closeApp() {
			Map<String, Object> params8 = new HashMap<>();
			params8.put("identifier", "com.marriott.iphoneprod");
			driver.executeScript("mobile:application:close", params8);	
		}
		
		public void openApp() {
			Map<String, Object> params9 = new HashMap<>();
			params9.put("identifier", "com.marriott.iphoneprod");
			driver.executeScript("mobile:application:open", params9);
		}
		
		@AfterTest
		public void closeWebDriver () throws SessionNotFoundException, IOException {
			// make sure web driver is closed
			try{
				if ( ((RemoteWebDriver) driver).getSessionId() != null) {
					driver.close();
					}
					driver.quit();
				}	
			catch (SessionNotFoundException e) {}
		}
		
		@Attachment
		private byte[] downloadReport(String type) throws IOException
		{	
			String command = "mobile:report:download";
			Map<String, String> params = new HashMap<>();
			params.put("type", type);
			String report = (String)((RemoteWebDriver) driver).executeScript(command, params);
			byte[] reportBytes = OutputType.BYTES.convertFromBase64Png(report);
			return reportBytes;
		}
		
		private static void switchToContext(RemoteWebDriver driver, String context) {
			RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
			Map<String,String> params = new HashMap<String,String>();
			params.put("name", context);
			executeMethod.execute(DriverCommand.SWITCH_TO_CONTEXT, params);
		}	
		
		protected Boolean textCheckpoint(String textToFind, Integer timeout) {
			perfectoCommand.put("content", textToFind);
			perfectoCommand.put("timeout", timeout);
			Object result = driver.executeScript("mobile:checkpoint:text", perfectoCommand);
			Boolean resultBool = Boolean.valueOf(result.toString());
			perfectoCommand.clear();
			return resultBool;
		}
		
		protected byte[] downloadHighResScreenshot(String user, String password, String host) {
			Map<String, Object> screenshotOptions = new HashMap<>();
			screenshotOptions.put("format", "png");
			screenshotOptions.put("report.resolution", "High");
			screenshotOptions.put("key", "PRIVATE:temp.png");
			driver.executeScript("mobile:screen:image", screenshotOptions);
			
			Request screenshotDownload = new Request("https://" + host + "/services/repositories/media/PRIVATE:temp.png?operation=download&user=" + user + "&password=" + password);
			return screenshotDownload.getResponse().getBytes().toByteArray();
		}
		
	    @Attachment
	    public byte[] takeScreenshot() {
	        System.out.println("Taking screenshot");
	        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
	    }
			
}
