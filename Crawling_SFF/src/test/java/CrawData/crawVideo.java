package CrawData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;
import org.testng.xml.SuiteXmlParser;

import dto.Video;
import setup.SetUp;

public class crawVideo extends SetUp{

	
	static int endPageNum;
	
	
	LinkedList<Video> videoList;
	
	@Test
	public void Crawl_01 () throws InterruptedException, IOException {
			
		//페이지 네비게이션 클릭하여 마지막 페이지로 이동
		driver.findElement(By.xpath("//a[@class='btn nextL']")).click();
		Thread.sleep(2000);
		//마지막 페이지 URL에서 페이지 네비게이션 
		System.out.println(driver.getCurrentUrl());
		endPageNum = Integer.parseInt(driver.findElement(By.xpath("//div[@class='paging']/a[@class='selected']")).getText());
		System.out.println("Number of pages : "+endPageNum);

		
		videoList = new LinkedList<Video>();
		String currentURL = driver.getCurrentUrl();
		
		int currentPage = endPageNum;
		
		while(currentPage!=0) {
			
			driver.get(currentURL);
			
			List<WebElement> list = new ArrayList<WebElement>();	
			list = (List<WebElement>) driver.findElements(By.xpath("//ul[@class='videolist']/li/a[2]"));
						
			for(int j = 0; j<list.size(); j++) {
				
				String url = list.get(j).getAttribute("href");
				String title = list.get(j).getText();
				String country = findCountry(title);
				videoList.add(new Video(title, url, country));
				
			}
			
			currentURL = currentURL.replaceFirst("page="+currentPage, "page="+(currentPage-1));
			currentPage -= 1;
			
		}

				
		Collections.sort(videoList, new Comparator<Video>() {
			
			public int compare(Video o1, Video o2)
			{
				return o1.country.compareTo(o2.country);
			}
		});
		
		
		createResult();

		driver.quit();
    }


	
	public void createResult () throws IOException {
		
        Workbook work = new HSSFWorkbook(); // Excel 2007 이전 버전
        
        Row row = null;
        Cell cell = null;
 
        HashSet<String> conList = new  HashSet<String>();
        Sheet sheet=null;
        int rowNum = 0;
        
        for(int i=0; i<videoList.size();i++) {
        	String  present=""; 
        	Video video = videoList.get(i);
        
        	if(conList.contains(video.country)==false) {
        		conList.add(video.country);
                sheet = work.createSheet(video.country);
        		
        		row = sheet.createRow(0);
                cell = row.createCell(0);
                cell.setCellValue("클립영상 타이틀");
                
                cell = row.createCell(1);
                cell.setCellValue("클립영상 링크");
        		
        		rowNum = 1;
                row = sheet.createRow(rowNum);
                
                // 첫 번째 줄에 Cell 설정하기-------------
                cell = row.createCell(0);
                cell.setCellValue(video.title);
                
                cell = row.createCell(1);
                cell.setCellValue(video.url);
                
                rowNum++;

                
        	}else if(conList.contains(video.country)) {
                row = sheet.createRow(rowNum);
                
                // Cell 설정하기-------------
                cell = row.createCell(0);
                cell.setCellValue(video.title);
                
                cell = row.createCell(1);
                cell.setCellValue(video.url);
                rowNum++;

        	}
        }
        

 
        // excel 파일 저장
        try {
        	File file = new File("");
    		String currentPath = file.getAbsolutePath();    
            File xlsFile = new File(currentPath+"/result_excel/result.xls");
            FileOutputStream fileOut = new FileOutputStream(xlsFile);
            work.write(fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
	
	public String findCountry (String title) {
		
		if(title.contains("베트남") || title.contains("하노이") ) {
			return "베트남";
		}else if(title.contains("멕시코")) {
			return "멕시코";
		}else if(title.contains("터키") || title.contains("이스탄불")) {
			return "터키";
		}else if(title.contains("시안") || title.contains("섬서성")) {
			return "중국(시안)";			
		}else if(title.contains("미국") || title.contains("뉴욕") || title.contains("뉴요")) {
			return "미국";
		}else if(title.contains("타이베이") || title.contains("타이완")) {
			return "대만";
		}else if(title.contains("우한") || title.contains("후베이")) {
			return "중국(우한)";
		}else if(title.contains("시칠리아") || title.contains("부치리아") || title.contains("팔레르모")) {
			return "이태리(시칠리아)";
		}else if(title.contains("연변") || title.contains("연길")) {
			return "중국(연변)";
		}else if(title.contains("페낭") || title.contains("말레이시아")) {
			return "말레이시아(페낭)";
		}else {
			return "기타";

		}

		
	}
	
}