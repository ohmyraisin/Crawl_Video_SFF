package CrawData;

import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

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
	public void Crawl_01_페이지_수_확인 () throws InterruptedException, IOException {
			
		//페이지 네비게이션 클릭하여 마지막 페이지로 이동
		driver.findElement(By.xpath("//a[@class='btn nextL']")).click();
		Thread.sleep(2000);
		//페이지 장수 확인
		System.out.println(driver.getCurrentUrl());
		endPageNum = Integer.parseInt(driver.findElement(By.xpath("//div[@class='paging']/a[@class='selected']")).getText());
		System.out.println("Number of pages : "+endPageNum);

	
    }
	
	@Test
	public void Crawl_02_비디오_크롤링 () {
		//마지막페이지에서 첫번째페이지까지 역순으로 이동하며 모든 비디오 타이틀과 링크 videoList에 저장
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
				String country = findCountry(title); //title에 있는 정보로 국가명을 추출
				videoList.add(new Video(title, url, country));
				
			}
			
			currentURL = currentURL.replaceFirst("page="+currentPage, "page="+(currentPage-1));
			currentPage -= 1;
			
		}

	}
	
	@Test
	public void Crawl_03_국가명_정렬 () throws IOException {

		
		//video의 국가정보를 기준으로 정렬
		Collections.sort(videoList, new Comparator<Video>() {
			
			public int compare(Video o1, Video o2)
			{
				return o1.country.compareTo(o2.country);
			}
		});
		

	}
	
	@Test
	public void Crawl_04_엑셀_저장 () throws IOException {
	
		//videoList에 있는 정보를 엑셀에 저장
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
        
        //국가명 별로 시트를 생성하여 저장
        
        for(int i=0; i<videoList.size();i++) {
        	String  present=""; 
        	Video video = videoList.get(i);
        
        	//hashset에 국가명이 저장되지 않은(최초) 국가는 시트를 생성하고 내용을 저장
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

            // hashset에 국가명이 저장되어 있다면 내용만 저장  
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
        

 
        // excel 파일로 저장. result.xls 결과물 생성
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
		//타이틀에 있는 정보를 기준으로 국가명을 구분함
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