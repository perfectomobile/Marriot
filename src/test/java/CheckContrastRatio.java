package test.java;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.testng.annotations.Test;

public class CheckContrastRatio extends TestBase{

	@Test
	private void checkColors() throws Exception{
		Set<Integer> colors = new HashSet<Integer>();
		saveScreenclip("//UIAButton[@name='Join now']", "join_now.png");
	    BufferedImage image = ImageIO.read(new File("target\\screenshots\\join_now.png"));    
	    int w = image.getWidth();
	    int h = image.getHeight();
	    
	    Color c;
	    
	    for(int y = 0; y < h; y++) {
	        for(int x = 0; x < w; x++) {
	            int pixel = image.getRGB(x, y);
	                       
	            //System.out.println(c.getBlue());
	            colors.add(pixel);
	        }
	    }
	    System.out.println("There are "+colors.size()+" colors");
	    
	    double maxColorBrightness = 0;
	    double minColorBrightness = 255;
	    double colorBrightness;
	    
	    int maxInt = 0;
	    int minInt = 0;
	    
	    for(Integer colorInt:colors) {
	    	c = new Color(colorInt, true);
	    		colorBrightness = ((c.getRed() * 299) + (c.getGreen() * 587) + (c.getBlue() * 114))/1000;
	    		System.out.println(colorBrightness);
	    		if (colorBrightness > maxColorBrightness) { 
	    			maxColorBrightness = colorBrightness; 
	    			maxInt = c.getRGB();
	    		}else if (colorBrightness < minColorBrightness) {
	    			minColorBrightness = colorBrightness;
	    			minInt = c.getRGB();
	    		}
	    		
	    }
		
	    String maxString = String.format("#%06x", maxInt & 0x00FFFFFF);
	    String minString = String.format("#%06x", minInt & 0x00FFFFFF);
	    System.out.println(String.format("#%06x", maxInt & 0x00FFFFFF));
	    System.out.println(String.format("#%06x", minInt & 0x00FFFFFF));
	  
	    String url = "http://juicystudio.com/services/luminositycontrastratio.php";

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);

		// add header
		post.setHeader("User-Agent", "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405");
		
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("background", minString));
		urlParameters.add(new BasicNameValuePair("foreground", maxString));
		urlParameters.add(new BasicNameValuePair("submit", "Calculate Luminosity Contrast Ratio"));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		HttpResponse response = client.execute(post);
		System.out.println("Response Code : " 
	                + response.getStatusLine().getStatusCode());

		
		
		BufferedReader rd = new BufferedReader(
		        new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
	    
		System.out.println(result.toString());
	    
		TagNode tagNode = new HtmlCleaner().clean(result.toString());
		org.w3c.dom.Document doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);
	
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		String str = (String) xpath.evaluate("//h2[@id='results']/following-sibling::p[1]", 
		                       doc, XPathConstants.STRING);
		str = str.replace("The contrast ratio is: ", "");
		
		
		
		String[] split = str.split(":");
		softAssert.assertTrue(Double.parseDouble(split[0]) >= 4.5, "Check contrast ratio");
		System.out.println(split[0]);
		
		
		System.out.println(str);
		
	    System.out.println("max: " + maxColorBrightness + ", " );
	    System.out.println("min: " + minColorBrightness); 
	    int max = (int) maxColorBrightness;
	    int min = (int) minColorBrightness;
	    ratio(max, min);
	    softAssert.assertAll();
	    testTearDown();
	}
	
	int gcd(int p, int q) {
	    if (q == 0) return p;
	    else return gcd(q, p % q);
	}

	void ratio(int a, int b) {
	   final int gcd = gcd(a,b);
	   System.out.println(a/gcd + " " + b/gcd);
	}
	
}
