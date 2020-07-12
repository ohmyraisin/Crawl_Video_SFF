package CrawData;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import setup.SetUp;

public class pageNavigation extends SetUp {
	
	@Test
	public void PageNavi () throws InterruptedException, IOException {
		
		driver.findElement(By.xpath("//a[@class='btn next']")).click();
		int currentNum = Integer.parseInt(driver.findElement(By.xpath("//div[@class='paging']/a[@class='selected']")).getText());
		assertTrue(currentNum==2, "페이지 네비게이션 테스트 - 다음 페이지로 이동 실패");
		
		driver.quit();
	}

}
