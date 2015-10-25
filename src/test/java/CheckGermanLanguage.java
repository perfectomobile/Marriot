package test.java;

import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Step;

public class CheckGermanLanguage extends TestBase {

		@Test
		public void CheckLanguage() throws Exception {
			try{
				verifyLanguage();
				testTearDown();
			} catch (Exception e){
				System.out.println(e.getStackTrace());
			} finally {
				softAssert.assertAll();
			}
		}
		
		@Step("Verify Language - Booking Tab")
		public void verifyLanguage(){
			softAssert.assertTrue(checkDateFormat(driver.findElementByXPath("//UIAStaticText[@path='/0/0/2/2/0']").getAttribute("value")), "Check date format failed");
			softAssert.assertTrue(textCheckpoint("Hotel suchen", 30), "Hotel suchen not found");
			softAssert.assertTrue(textCheckpoint("Derzeitiger Standort", 30), "Derzeitiger Standort not found");
			softAssert.assertTrue(textCheckpoint("Gäste pro Zimmer", 30), "Gäste pro Zimmer not found");
			softAssert.assertTrue(textCheckpoint("Nutzen Sie Marriott Rewards Punkte", 30), "Nutzen Sie Marriott Rewards Punkte not found");
			softAssert.assertTrue(textCheckpoint("Was ist das?", 30), "Was ist das? not found");
			softAssert.assertTrue(textCheckpoint("Suche eingrenzen", 30), "Suche eingrenzen not found");
			softAssert.assertTrue(textCheckpoint("Alle Marken", 30), "Alle Marken not found");
			softAssert.assertTrue(textCheckpoint("Sonderpreise", 30), "Sonderpreise not found");
			softAssert.assertTrue(textCheckpoint("Suchen", 30), "Suchen not found");
			softAssert.assertTrue(textCheckpoint("Kostenpflichtig buchen", 30), "Kostenpflichtig buchen not found");
			softAssert.assertTrue(textCheckpoint("Jetzt Mitglied werden", 30), "Jetzt Mitglied werden not found");
			softAssert.assertTrue(textCheckpoint("Einloggen", 30), "Einloggen not found");
			softAssert.assertTrue(textCheckpoint("In der Nähe", 30), "In der Nähe not found");
			takeScreenshot();		
		}	

		private boolean checkDateFormat(String dateString) {
			boolean checkFormat;
			if (dateString.matches("([0-3]{1})([0-9]{1}).([0-1]{1})([0-9]{1}).([0-9]{4})([ \\–]+)([0-3]{1})([0-9]{1}).([0-1]{1})([0-9]{1}).([0-9]{4})")) {
				checkFormat=true;
			    }
			else{  checkFormat=false; }
			return checkFormat;
		}	
}
