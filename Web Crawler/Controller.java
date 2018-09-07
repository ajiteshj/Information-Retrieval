import java.io.FileWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;


public class Controller {

	private static final int numberOfCrawlers = 7;
	private static final int maxDepth = 16;
	private static final String storageFolder = "/tmp/crawl";
	private static final int maxPages = 20000;
	//private static final int politenessValue = 2500;
	private static final String name = "Vani Kohli";
	private static final String id = "9391342298";
	private static final String site = "nbcnews.com";
	
	public static void main(String[] args) throws Exception { 
	
		CrawlConfig config = new CrawlConfig(); 
		config.setCrawlStorageFolder(storageFolder);
		config.setMaxDepthOfCrawling(maxDepth);
		config.setMaxPagesToFetch(maxPages);
		//config.setPolitenessDelay(politenessValue);
		config.setResumableCrawling(true);
		config.setIncludeBinaryContentInCrawling(true);
		
		PageFetcher pageFetcher = new PageFetcher(config); 
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig(); 
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher); 
		
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		
		controller.addSeed("http://www.nbcnews.com/");
		
		MyCrawler.configure(storageFolder + "/files");
		
		controller.start(MyCrawler.class, numberOfCrawlers);
		
		State crawl = new State();
		List<Object> crawlData = controller.getCrawlersLocalData();
		
	      for(Object data : crawlData){
			
			State myCrawl = (State) data;
			
			crawl.attemptUrls.addAll(myCrawl.attemptUrls);
			crawl.visitedUrls.addAll(myCrawl.visitedUrls);
			crawl.discoveredUrls.addAll(myCrawl.discoveredUrls);
		}
		
		FetchCsv(crawl);
		VisitCsv(crawl);
		saveUrlsCsv(crawl);
		outputFile(crawl);
	}

	

	private static void saveUrlsCsv(State crawl) throws Exception {
		
		String file = storageFolder + "/urls_NBC_News.csv";
		FileWriter writer = new FileWriter(file);
		writer.append("Url, Indicator\n");
		for(Details detail : crawl.discoveredUrls){
			writer.append(detail.url + "," + detail.UrlType + "\n");
		}
		writer.flush();
		writer.close();
		
	}

	private static void VisitCsv(State crawl) throws Exception {
		String file = storageFolder + "/visit_NBC_News.csv";
		FileWriter writer = new FileWriter(file);
		writer.append("Url, Size , # Outgoing_Links, Content_Type\n");
		for(Details detail : crawl.visitedUrls){
			if (detail.UrlType != "unknown") {
			writer.append(detail.url + "," + detail.sizeOfFile + "," + detail.outgoingUrl.size() + "," + detail.UrlType + "\n");
		}
		}
		writer.flush();
		writer.close();
		
	}

	private static void FetchCsv(State crawl) throws Exception{
		String file = storageFolder + "/fetch_NBC_News.csv";
		FileWriter writer = new FileWriter(file);
		writer.append("Url, HttpStatus\n");
		for(Details detail : crawl.attemptUrls){
			writer.append(detail.url + "," + detail.httpStatusCode + "\n");
		}
		writer.flush();
		writer.close();
		
	}
	
	private static void outputFile(State crawl) throws Exception {
		
		String file = storageFolder + "/CrawlReport_NBCNews.txt";
		FileWriter writer = new FileWriter(file);
		
		writer.append("Name: " + name + "\n");
		writer.append("USC ID: " + id + "\n");
		writer.append("News site crawled: " + site + "\n");
		writer.append("\n");
		
		writer.append("Fetch Statistics\n");
		writer.append("================\n");
		writer.append("# fetches attempted:" + crawl.attemptUrls.size() + "\n");
		writer.append("# fetches succeeded:" + crawl.visitedUrls.size() + "\n");
		
		int fetchAbortCount =0;
		int fetchFailedCount =0;
		
		for (Details detail : crawl.attemptUrls) {
            if (detail.httpStatusCode >= 300 && detail.httpStatusCode < 400) {
            	fetchAbortCount++;
            } 
            else if (detail.httpStatusCode != 200) {
                fetchFailedCount++;
            }
        }
		
		writer.append("# fetches aborted:" + fetchAbortCount + "\n");
		writer.append("# fetches failed:" + fetchFailedCount + "\n");
		writer.append("\n");
		
		writer.append("Outgoing URLs:\n");
		writer.append("==============\n");
		
		writer.append("Total URLs extracted: " + crawl.discoveredUrls.size() + "\n");
		
		HashSet<String> set = new HashSet<String>();
		int uniqueUrlCount =0;
		int withinNewsSiteCount =0;
		int outsideNewsSiteCount =0;
		
		for(Details detail : crawl.discoveredUrls){
			if(!(set.contains(detail.url))){
				set.add(detail.url);
				uniqueUrlCount++;
			
			   if (detail.UrlType.equals("OK"))
			   {
                   withinNewsSiteCount++;
               } 
			   else if(detail.UrlType.equals("N_OK")) 
			   {
                    outsideNewsSiteCount++;
			   }
				
			}
		}
			   
		writer.append("# Unique URLs extracted: " + uniqueUrlCount + "\n");
		writer.append("# Unique URLs within News Site: " + withinNewsSiteCount + "\n");
		writer.append("# Unique URLs outside News Site: " + outsideNewsSiteCount + "\n");
		
		writer.append("\n");
		writer.append("Status Codes:\n");
		writer.append("==============\n");
	
		HashMap<Integer, Integer> map = new HashMap<>();
		
		for(Details detail : crawl.attemptUrls){
			if(map.containsKey(detail.httpStatusCode)){
				map.put(detail.httpStatusCode, map.get(detail.httpStatusCode)+1);
			}
			else
			{
				map.put(detail.httpStatusCode, 1);
			}
		}
		
		HashMap<Integer, String> statusDescription = new HashMap<>();
		
		statusDescription.put(200, "OK");
		statusDescription.put(204, "No Content");
		statusDescription.put(301, "Moved Permanently");
		statusDescription.put(302, "Found");
		statusDescription.put(400, "Bad Request");
		statusDescription.put(401, "Unauthorized");
		statusDescription.put(403, "Forbidden");
		statusDescription.put(404, "Not Found");
		statusDescription.put(406, "Null");
		statusDescription.put(405, "Method Not Allowed");
        statusDescription.put(500, "Internal Server Error");
        statusDescription.put(502, "Null");
        
        for(Integer key : map.keySet()){
        	
        	writer.append("" + key + " " +statusDescription.get(key)+ ": " + map.get(key) + "\n");
        }
        writer.append("\n");
		
        writer.append("File Size:\n");
		writer.append("===========\n");
		
		int oneK =0;
		int tenK =0;
		int hundredK =0;
		int oneM = 0;
		int other =0;
		
		for(Details detail : crawl.visitedUrls){
			
			if(detail.sizeOfFile < 1024){
				oneK++;
			}
		   else if(detail.sizeOfFile < 10240){
			   tenK++;
		   }
		   else if(detail.sizeOfFile < 102400){
			   hundredK++;
		   }
		   else if(detail.sizeOfFile < 1024 * 1024){
			   oneM++;
		   }
		   else{
			   other++;
		   }
		}
		    writer.append("< 1KB: " + oneK + "\n");
	        writer.append("1KB ~ <10KB: " + tenK + "\n");
	        writer.append("10KB ~ <100KB: " + hundredK + "\n");
	        writer.append("100KB ~ <1MB: " + oneM + "\n");
	        writer.append(">= 1MB: " + other + "\n");
	        writer.append("\n");
	        
	        writer.append("Content Types:\n");
			writer.append("==============\n");
			
			HashMap<String, Integer> map1 = new HashMap<>();
			
			for(Details detail : crawl.visitedUrls){
				
				if (detail.UrlType.equals("unknown")) {
	                continue;
	            }
				if(map1.containsKey(detail.UrlType)){
					map1.put(detail.UrlType, map1.get(detail.UrlType)+1);
				}
				else{
					map1.put(detail.UrlType, 1);
				}
			}
			
			
			for(String key : map1.keySet()){
				
				writer.append(""+ key + ": " + map1.get(key) + "\n ");
			}
			 
			writer.append("\n");
			
			writer.flush();
			writer.close();

	}
}
