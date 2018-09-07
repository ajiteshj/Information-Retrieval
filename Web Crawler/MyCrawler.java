import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.io.Files;

import OldCode.UrlInfo;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

class State {
    ArrayList<Details> attemptUrls;
    ArrayList<Details> visitedUrls;
    ArrayList<Details> discoveredUrls;

    public State() {
        attemptUrls = new ArrayList<Details>();
        visitedUrls = new ArrayList<Details>();
        discoveredUrls = new ArrayList<Details>();
    }
}
class Details{
	
	public String url;
	public int httpStatusCode;
	public int sizeOfFile;
	public String UrlType;
	public ArrayList<String> outgoingUrl;
	public String hash;
    public String extension;
	
    public Details(){
    	//default constructor
    }
	public Details(String url , int statusCode){
		this.url = url;
		this.httpStatusCode = statusCode;
	}
	
	public Details(String url , String indicator){
		this.url = url;
		this.UrlType = indicator;
	}
	
	public Details(String url, int size , ArrayList<String> outgoingUrl , String contentType , String extention ){
		
		this.url = url;
		this.sizeOfFile = size;
		this.outgoingUrl = outgoingUrl;
		this.UrlType = contentType;
		this.hash = hashString(url);
        this.extension = extention;
	}

	 public static String hashString(String s) {
	        byte[] hash = null;
	        try {
	            MessageDigest md = MessageDigest.getInstance("MD5");
	            hash = md.digest(s.getBytes());

	        } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
	        StringBuilder sb = new StringBuilder();
	        for (int i = 0; i < hash.length; ++i) {
	            String hex = Integer.toHexString(hash[i]);
	            if (hex.length() == 1) {
	                sb.append(0);
	                sb.append(hex.charAt(hex.length() - 1));
	            } else {
	                sb.append(hex.substring(hex.length() - 2));
	            }
	        }
	        return sb.toString();
	    }
}
public class MyCrawler extends WebCrawler { 
	private final static Pattern FILTERS = Pattern.compile(
		      ".*(\\.(css|js|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v" + "|rm|smil|wmv|swf|wma|zip|rar|gz|php|iso|ico))$");
	
	 
	    State state;
	    
	    public MyCrawler(){
		
	    state = new State();
		
	}
	    
	  
	    private static File storageFolder;
	    
	    public static void configure(String storageFolderName) {
	        storageFolder = new File(storageFolderName);
	        if (!(storageFolder.exists())) {
	            storageFolder.mkdirs();
	        }
	    }
	    
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) { 
		String href = url.getURL().toLowerCase().replace(',', '-'); 
		String indicator = "N_OK";
		if(href.startsWith("http://www.nbcnews.com")){
			indicator  = "OK";
		}
		
		state.discoveredUrls.add(new Details(href, indicator));
        //return !FILTERS.matcher(href).matches() && indicator.equals("OK");
		
		return !FILTERS.matcher(href).matches() && href.startsWith("http://www.nbcnews.com/");
	}
	
	@Override public void visit(Page page) 
	{ 
		String url = page.getWebURL().getURL().replace(',', '-');
		String content = page.getContentType().split(";")[0];
		ArrayList<String> outgoing = new ArrayList<String>();
		
		 Details detail;
		System.out.println("URL: " + url); 
		System.out.println("Content" + content);
	    if(content.equals("text/html"))
	    {	
		     if (page.getParseData() instanceof HtmlParseData) 
		     { 
			     HtmlParseData htmlParseData = (HtmlParseData) page.getParseData(); 
			
			     Set<WebURL> links = htmlParseData.getOutgoingUrls();
		         for(WebURL link : links)
		         {
		    	    outgoing.add(link.getURL());
		         }
		           detail = new Details(url,page.getContentData().length,outgoing,"text/html",".html");
		           state.visitedUrls.add(detail);
		     }
		     else
		     {
			     detail = new Details(url,page.getContentData().length,outgoing,"text/html",".html");
		         state.visitedUrls.add(detail);
		     }
	    }
	    else if(content.equals("application/pdf"))
	    {
		   detail = new Details(url,page.getContentData().length,outgoing,"application/pdf",".pdf");
	       state.visitedUrls.add(detail);
	    }
	    else if (content.equals("application/msword")) 
	    { // doc
            detail = new Details(url, page.getContentData().length, outgoing, "application/msword", ".doc");
            state.visitedUrls.add(detail);
	    } 
	    else if(content.equals("image/gif"))
	    {
		   detail = new Details(url,page.getContentData().length,outgoing,"image/gif",".gif");
	       state.visitedUrls.add(detail);
	    }
	    else if(content.equals("image/jpeg") || content.equals("image/jpg"))
	    {
		   detail = new Details(url,page.getContentData().length,outgoing,"image/jpeg",".jpeg");
	       state.visitedUrls.add(detail);
	    }
	    else if(content.equals("image/bmp"))
	    {
		   detail = new Details(url,page.getContentData().length,outgoing,"image/bmp",".bmp");
	       state.visitedUrls.add(detail);
	    }
	    else if(content.equals("image/tiff"))
	    {
		   detail = new Details(url,page.getContentData().length,outgoing,"image/tiff",".tiff");
	       state.visitedUrls.add(detail);
	    }
	    else if(content.equals("image/png"))
	    {
		   detail = new Details(url,page.getContentData().length,outgoing,"image/png",".png");
	       state.visitedUrls.add(detail);
	    }
	    else if (content.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            detail = new Details(url, page.getContentData().length, outgoing, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx");
            state.visitedUrls.add(detail);
	    }  
	    else
	    {
		   detail = new Details(url,page.getContentData().length,outgoing,"unknown","");
	       state.visitedUrls.add(detail);
	    }
	    
	    if (!detail.extension.equals("")) {
            String filename = storageFolder.getAbsolutePath() + "/" + detail.hash + detail.extension;
            try {
                Files.write(page.getContentData(), new File(filename));
            } catch (IOException ioe) {
                System.out.println("Failed to write file: " + filename);
            }
        }
  }
	
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
        state.attemptUrls.add(new Details(webUrl.getURL(), statusCode));
    }
	
	public Object getMyLocalData() {
        return state;
    }
}
