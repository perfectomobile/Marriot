package test.java;

import java.awt.Color;

import javaxt.io.Image;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import ru.yandex.qatools.allure.annotations.Step;
public class AssertBrandColors extends TestBase {
			
		private double threshold = 0.98;
		private double thresholdDelta = 1.0 - threshold;
		
		private String imagePath1;
		private String imagePath2;
			
//		@BeforeClass
//		public void loadLibrary() {
//			System.load("C:\\opencv\\build\\java\\x64\\opencv_java249.dll");
//		}
//		
		@Parameters({ "imageMatch" })
		@Test
		public void VerifyBrandColors(boolean imageMatch) throws Exception {
			//try{
				if(imageMatch) {
					imagePath1 = "src\\resources\\courtyard_map_popover.bmp";
					imagePath2 = "src\\resources\\courtyard_hotel_details.png";
				} else {
					imagePath1 = "src\\resources\\residence_inn.png";
					imagePath2 = "src\\resources\\courtyard_hotel_details_dark.png"; 
				}
				
				bookingTabAssert();
				enterLocation();
				selectBrand();
				findHotel();
				viewHotelDetails();
				viewHotelOnMap();
				//checkAvailability();
				testTearDown();
			//} catch (Exception e){
			//	System.out.println(e.getStackTrace());
		//	} finally {
				softAssert.assertAll();
		//	}
		}

		@Step("Verify User is on Booking Tab")
		public void bookingTabAssert() {
			softAssert.assertTrue(textCheckpoint("Book Now", 30), "Book Now not found");
		}
		
		@Step("Enter Location")
		public void enterLocation() {
			
			driver.findElementByXPath("//UIAButton[@name='Without dates']").click();
//			driver.findElementByXPath("//UIAStaticText[@name='Current location']").click();
//			softAssert.assertTrue(textCheckpoint("Select destination", 30), "Select destination not found");
			driver.findElementByXPath("//UIAStaticText[@name='City, country, or airport code']").click();
			driver.findElementByXPath("//UIATextField[@value='City, country, or airport code']").sendKeys("Bethesda");
			driver.findElementByXPath("//UIAStaticText[@name='Bethesda, MD, USA']").click();
			softAssert.assertTrue(textCheckpoint("Bethesda, MD, USA", 30), "Bethesda, MD, USA not found");
		}
		
		@Step("Select Brand")
		public void selectBrand() {
			driver.findElementByXPath("//UIAStaticText[@name='All brands']").click();
			softAssert.assertTrue(textCheckpoint("Select Brand", 30), "Select Brand not found");
			driver.findElementByXPath("//UIATableCell[@name='Courtyard']").click();
			driver.findElementByXPath("//UIAButton[@name='Done']").click();
			driver.findElementByXPath("//UIAStaticText[@name='Courtyard']");
		}
		
		@Step("Find Hotel")
		public void findHotel() {
			driver.findElementByXPath("//UIAStaticText[@name='FIND']").click();
			driver.findElementByXPath("//UIAButton[@name='Map']").click();
			
			String pixelHexCode = getPixelColor("//UIAPopover/UIAImage[3]", 10, 10);
			double matchScore = getImageMatchScore("//UIAPopover/UIAImage[3]", "courtyard_map_popover_screenshot.png", imagePath1);
			softAssert.assertEquals(matchScore, threshold , thresholdDelta, "Failed to find logo popup image with a score of: " + matchScore);		
			
			System.out.println(pixelHexCode);
			softAssert.assertEquals(pixelHexCode, "#78be20", "Find Hotel logo color incorrect");
					
		}
		
		@Step("View Hotel Details")
		public void viewHotelDetails() throws InterruptedException{
			driver.findElementByXPath("//UIAPopover/UIAButton").click();
			Thread.sleep(5000);
			softAssert.assertTrue(textCheckpoint("Hotel details", 30), "Hotel details not found");
			String pixelHexCode = getPixelColor("//AppiumAUT/UIAApplication[1]/UIAWindow[1]/UIATableView[2]/UIAScrollView[1]/UIAImage[1]", 100, 30);
			System.out.println(pixelHexCode);
			softAssert.assertEquals(pixelHexCode, "#78be20", "View Hotel Details logo color incorrect");
			
			double matchScore = getImageMatchScore("//AppiumAUT/UIAApplication[1]/UIAWindow[1]/UIATableView[2]/UIAScrollView[1]/UIAImage[1]", "courtyard_hotel_details_screenshot.png", imagePath2);
			softAssert.assertEquals(matchScore,  threshold, thresholdDelta, "Failed to find hotel details logo with a score of: " + matchScore);	
		}
		
		@Step("View Hotel on Map")
		public void viewHotelOnMap() {
			driver.findElementByXPath("//UIAButton[@name='Map' and @visible='true']").click();
			
			String pixelHexCode = getPixelColor("//UIAPopover/UIAImage[3]", 10, 10);
					
			double matchScore = getImageMatchScore("//UIAPopover/UIAImage[3]", "courtyard_map_popover_screenshot.png", imagePath1);
			softAssert.assertEquals(matchScore,  threshold, thresholdDelta, "Failed to find hotel logo on map with a score of: " + matchScore);
			System.out.println(pixelHexCode);
			softAssert.assertEquals(pixelHexCode, "#78be20", "View Hotel on Map logo color incorrect");
			driver.findElementByXPath("(//UIAButton[@name='Back' and @visible='true'])[1]").click();
		}
		
		@Step("Check Availability")
		public void checkAvailability() throws InterruptedException{
			driver.findElementByXPath("//UIAStaticText[@name='Check availability']").click();
			Thread.sleep(5000);
			softAssert.assertTrue(textCheckpoint("Check availability", 30), "Check availability not found");
			driver.findElementByXPath("(//UIAStaticText[contains(@value,'2015')])[2]").click();
			softAssert.assertTrue(textCheckpoint("Select dates", 30), "Select dates not found");
			
			driver.findElementByXPath("//UIACollectionCell[@name='25' and @visible='true']").click();
			driver.findElementByXPath("//UIACollectionCell[@name='29' and @visible='true']").click();
			
			softAssert.assertEquals(driver.findElementByXPath("//UIACollectionCell[@name='25' and @visible='true']").getAttribute("value"), "1", "Cell 25 not highlighted");
			softAssert.assertEquals(driver.findElementByXPath("//UIACollectionCell[@name='29' and @visible='true']").getAttribute("value"), "1", "Cell 29 not highlighted");
			driver.findElementByXPath("//UIAButton[@name='OK']").click();
		}
		
		@Step("Select a Room")
		public void selectARoom() {
			driver.findElementByXPath("//UIAStaticText[@name='FIND']").click();
		}
		
		
		private String getPixelColor(String objXpath, Integer sampleX, Integer sampleY){
			int imageX = Integer.parseInt(driver.findElementByXPath(objXpath).getAttribute("x"));
			int imageY = Integer.parseInt(driver.findElementByXPath(objXpath).getAttribute("y"));
			int imageWidth = Integer.parseInt(driver.findElementByXPath(objXpath).getAttribute("width"));
			int imageHeight = Integer.parseInt(driver.findElementByXPath(objXpath).getAttribute("height"));
			byte[] Screenshot = downloadHighResScreenshot(user, password, host); 
			saveImage(Screenshot);
			
			System.out.println(imageX + "," + imageY + "," + imageWidth + "," + imageHeight);
			
			Image croppedScreenshot = new Image(Screenshot);

			croppedScreenshot.crop(imageX, imageY, imageWidth, imageHeight);
			saveImage(croppedScreenshot.getByteArray());
			Color pixelColor = croppedScreenshot.getColor(sampleX, sampleY);
			saveImage(croppedScreenshot.getByteArray());
			
			croppedScreenshot.saveAs("C:\\output.png");
			
			String pixelHexCode = String.format("#%06x", pixelColor.getRGB() & 0x00FFFFFF);
			return pixelHexCode;
		}
		
		private double getImageMatchScore(String xpath, String fileName, String needlePath) {
			saveScreenclip(xpath, fileName);
			double match_TM_SQDIFF_NORMED = matchImages(fileName, needlePath, Imgproc.TM_SQDIFF_NORMED);
			double match_TM_CCOEFF_NORMED = matchImages(fileName, needlePath, Imgproc.TM_CCOEFF_NORMED);
			double match_TM_CCORR_NORMED = matchImages(fileName, needlePath, Imgproc.TM_CCORR_NORMED);
			
			double minScore;
			if(match_TM_SQDIFF_NORMED > match_TM_CCOEFF_NORMED) { minScore = match_TM_CCOEFF_NORMED; }
			else {minScore = match_TM_SQDIFF_NORMED;}
			
			if(minScore > match_TM_CCORR_NORMED) { minScore = match_TM_CCORR_NORMED;}
			
			return minScore;
		}
		
		private double matchImages(String fileName, String needlePath, int matchMethod) {
			//UIAPopover/UIAImage[3]
			
			// match the needle
			Mat img = Highgui.imread("target\\screenshots\\" + fileName);
	        Mat templ = Highgui.imread(needlePath);
      
	        // create the result matrix
	        int resultCols = img.cols() - templ.cols() + 1;
	        int resultRows = img.rows() - templ.rows() + 1;
	        Mat result = new Mat(resultRows, resultCols, CvType.CV_32FC1);

	        // do the Matching and Normalize
	        // Imgproc.TM_SQDIFF_NORMED Imgproc.TM_CCOEFF_NORMED Imgproc.TM_CCORR_NORMED

	        Imgproc.matchTemplate(img, templ, result, matchMethod);
	        //Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

	        // / Localizing the best match with minMaxLoc
	        MinMaxLocResult mmr = Core.minMaxLoc(result);

	        Point matchLoc;
	        double matchScore;
	        if (matchMethod == Imgproc.TM_SQDIFF || matchMethod == Imgproc.TM_SQDIFF_NORMED) {
	            matchLoc = mmr.minLoc;
	            matchScore = 1.0 - mmr.minVal;
	        } else {
	            matchLoc = mmr.maxLoc;
	            matchScore = mmr.maxVal;
	        }
	        
	        System.out.println(matchMethod + " match score = " + matchScore + " match location = " + matchLoc.toString());

	        // show me what you got
	        Core.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
	                matchLoc.y + templ.rows()), new Scalar(0, 255, 0));

	        // save the visualized detection.
	       // String matchPath = "target\\matchpath\\";
	        //System.out.println("Writing "+ matchPath);
	        //Highgui.imwrite(matchPath, img);
	        
	        return matchScore;
		}

}
