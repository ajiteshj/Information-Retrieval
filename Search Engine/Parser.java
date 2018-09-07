import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ContentHandler;
import java.util.*;

import javax.lang.model.util.Elements;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xml.sax.SAXException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class Parser {

   public void parseAll() throws IOException,SAXException, TikaException {
	   

	   File path = new File("/home/ajitesj/Desktop/WSJ/WSJ");
	   
       File[] files = path.listFiles();
       for (int i = 0; i < files.length; i++){
           if (files[i].isFile()){ //this line weeds out other directories/folders
               System.out.println(String.valueOf(i)+files[i]);
               
       BufferedReader br = new BufferedReader(new FileReader(files[i]));
               try {
            	   
            	   BodyContentHandler handler = new BodyContentHandler(-1);
                   Metadata metadata = new Metadata();
                   //BufferedReader br = new BufferedReader(new FileReader(files[i]));
                   //FileInputStream inputstream = new FileInputStream(new File(files[i].getPath()));
                   FileInputStream inputstream = new FileInputStream(new File(files[i].getPath()));     		

                   ParseContext pcontext = new ParseContext();
                   
                   //Html parser 
                   HtmlParser htmlparser = new HtmlParser();
                   htmlparser.parse(inputstream, handler, metadata,pcontext);
                   StringBuilder sb = new StringBuilder();
                   StringBuilder sbNew = new StringBuilder();
                   String content = handler.toString();
                   content = content.replaceAll("\t+", " ");
                   content = content.replaceAll("\n+", " ");
                   content = content.replaceAll("\\s+", " ");
                   sb.append(content.trim());
                   
     
                   try(FileWriter fw = new FileWriter("/home/ajitesj/Desktop/bigtest.txt", true);
                           BufferedWriter bw = new BufferedWriter(fw);
                           PrintWriter out = new PrintWriter(bw))
                       {
                	   		//String x = sb.toString();
                           out.println(sb.toString());
                       }catch (IOException e) {
                           //exception handling left as an exercise for the reader
                       }
                    finally {
                     br.close();
                   }
                   //System.out.println("Contents of the document:" + sb.toString());
               }
               catch(IOException e) {
            	   
               }
           }
       }
   }
   public void removeSpaces() throws IOException {
	   FileReader fr = new FileReader("/home/ajitesj/Desktop/bigtest.txt"); 
		BufferedReader br = new BufferedReader(fr); 
		//FileWriter fw = new FileWriter("src\\outfile.txt"); 
		//String line;
		
       //BufferedReader br = new BufferedReader(new FileReader(files[i]));
       try {
           StringBuilder sb = new StringBuilder();
           String line = br.readLine();

           while (line != null) {
               if(line.trim().length()>0){
                   sb.append(line);
                   sb.append(System.lineSeparator());
               }

               line = br.readLine();
           }

           String everything = sb.toString();

           try(FileWriter fw1 = new FileWriter("/home/ajitesj/Desktop/big.txt", true);
               BufferedWriter bw = new BufferedWriter(fw1);
               PrintWriter out = new PrintWriter(bw))
           {
               out.println(everything);
           } catch (IOException e) {
               //exception handling left as an exercise for the reader
           }
       } finally {
           br.close();
       }

	   
   }
   
   public static void main(String Args[]) {
	   MyParser p1 = new MyParser();
	   try {
		   p1.parseAll();
//		   p1.removeSpaces();
	   }
	   catch(IOException e) {
		   
	   } catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (TikaException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   
   }
}
