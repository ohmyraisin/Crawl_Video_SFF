package setup;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class SetUp {
	
	
    //WebDriver
    public static WebDriver driver;
    
    //Properties
//    public static final String WEB_DRIVER_ID = "webdriver.chrome.driver";
//    public static final String WEB_DRIVER_PATH = "/Users/jeong-eunju/Desktop/chromedriver";
    
    public SetUp()
    {
    	File file = new File("");
		String currentPath = file.getAbsolutePath();
		String WebDriverId = "webdriver.chrome.driver";
		String WebDriverPath = currentPath+"/lib/chromedriver_v88";
    	
		System.setProperty(WebDriverId, WebDriverPath);
		driver = new ChromeDriver();
		driver.get("http://program.tving.com/tvn/streetfoodfighter2/1/Vod/List?vod_type=2&order=");
		String actualTitle = driver.getTitle();
		String expectedTitle = "스트리트푸드파이터 시즌2";

		if(actualTitle.contains(expectedTitle)) {
			System.out.println("[My Log] We have StreetFoodFighter!");
		}else {
			System.out.println("[My Log] Fail to access...");
		}
    }

}
