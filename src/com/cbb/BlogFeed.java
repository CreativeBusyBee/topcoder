package com.cbb;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.net.URI;

import javax.imageio.ImageIO;

import org.apache.http.client.fluent.Request;
import org.xml.sax.SAXParseException;

import com.ibm.json.java.JSONObject;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class BlogFeed implements Comparable<BlogFeed>{
	private static String Separator = "&&&&";
	String url;
	String canonicalUrl;
	private static final MyDate today = new MyDate();
	 private static java.sql.Date fewDaysAgo = null;  
	private static int MaxPoints = 10;
	static String DefaultImage = "https://lh4.googleusercontent.com/-6DqQSSisO8c/Uc5O68WqQeI/AAAAAAAACBo/l0dqj6-TRnw/s280/imageNotFound.jpg";
	public static String[] ImageWords = {"folder", "prev", "freebie"};
	public static String year = "2009";
	public final static int MinimumYear = today.getToday().getYear() - 1; // should be 114 for 2104; /2014 - 1900 because of the way Java handle the year;
	public static String previousYear = "2011";
	public static int MaxPosts =12;
	private static int WaitTime = 10000;
	private static int WaitTimeImage = 30000;
	
	boolean mytrap = false;
	boolean noheight = true;
	boolean nowidth = true;
	boolean image = false;
	Date publishDate;
	//public static String SpecialSite = "http://www.polarfuchs-treasures.de/digitaldesigns";
	public static int ReasonableSize=50;
	public static int ReasonableSizeHtml=50;
	public static int MaxImages = 40;
	public int totalImages = 0;
	public boolean followerImage;
	public static String BogusPostId = "bogus";
	private int samePostId = 0;
	private String feed="";
	private String blogType ="";
	String extra = null;
	boolean gotEnd = false;
	String endOfBlogPosition = null;
	int blogPoints = 0;
	
	
	public boolean hasExtra() {
		if (extra == null) {
			return true;
		}
		return false;
	}
	public int getBlogPoints() {
		return blogPoints;
	}


	public void setBlogPoints(int blogPoints) {
		this.blogPoints = blogPoints;
	}

	
	
	
	

	public String getBlogType() {
		return blogType;
	}
	
	
	
	
	
	
	
	public String getCanonicalUrl() {
		return canonicalUrl;
	}
	public void setCanonicalUrl(String canonicalUrl) {
		this.canonicalUrl = canonicalUrl;
	}
	
	
	public String writeBlogWithPoints() {
		StringBuffer result = new StringBuffer();
		result.append(url);
		result.append(Separator);
		result.append(feed);
		result.append(Separator);
		result.append(blogPoints);
		return(result.toString());
	}
	public String getExtra() {
		return extra;
	}


	public void setExtra(String extra) {
		this.extra = extra;
	}


	public String printBlog() {
		StringBuffer sb = new StringBuffer();
		sb.append(blogType);
		sb.append(url);
		sb.append(Separator);
		sb.append(feed);
		if (extra != null) {
			sb.append(Separator);
			sb.append(extra);
		}
		return sb.toString();
	}
	
	public String printBlogToApprove() {
		StringBuffer sb = new StringBuffer();
		sb.append(blogType);
		sb.append(url);
		if (!feed.isEmpty()) {
			sb.append(Separator);
			sb.append(feed);
			if (extra != null) {
				sb.append(Separator);
				sb.append(extra);
			}
		}
		return sb.toString();
	}
	
	public void printBlogSpecialFeed(PrintWriter fileOut, PrintWriter fileNoFeed,PrintWriter fileOutHtml, PrintWriter fileNoFeedHtml) {
		StringBuffer sb = new StringBuffer();
		//sb.append(blogType);
		sb.append(url);
		sb.append(Separator);
		sb.append(feed);
		if (blogType.equals("@")) {
			sb.append(Separator);
			sb.append(extra);
			fileNoFeed.println(sb.toString());
			fileNoFeedHtml.println("<p><a target=\"_blank\" href=\"" + getUrl()+"\">" +getUrl() +"</a></p>" );

		}
		else {
			fileOut.println(sb.toString());
			fileOutHtml.println("<p><a target=\"_blank\" href=\"" + getUrl()+"\">" +getUrl() +"</a></p>" );
		}
		
	}
	public void setBlogType(String blogType) {
		this.blogType = blogType;
	}
	public String getFeed() {
		return feed;
	}
	public void setFeed(String feed) {
		this.feed = feed;
	}
	public BlogFeed(String url) throws IOException{
		initializeDate();
		this.url=CleanURL(url);
		this.canonicalUrl = calculateCanonicalUrl(this.url);
		findFeed(url);
	}
	
	private void findFeed(String urlS) throws IOException {
		String req = "http://ajax.googleapis.com/ajax/services/feed/lookup?v=1.0&q="+urlS;
		URL url = new URL(req);
		URLConnection connection = url.openConnection();
		connection.addRequestProperty("Referer", "http://cbb-top1.mybluemix.net/");
		
		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while((line = reader.readLine()) != null) {
			builder.append(line); 
		}
		//System.out.println(builder.toString());
		org.json.JSONObject json = new org.json.JSONObject(builder.toString());
		//System.out.println(json.get("responseData"));
		json =json.getJSONObject("responseData");
		System.out.println(json.get("url"));
		feed =(String)json.get("url"); 
	}
	
	private void initializeDate() {
		 if (fewDaysAgo == null){
		   // Get today as a Calendar  
		   Calendar todayC = Calendar.getInstance();  
		   // Subtract 1 day  
		   todayC.add(Calendar.DATE, -30);  
		   // Make an SQL Date out of that  
		   fewDaysAgo = new java.sql.Date(todayC.getTimeInMillis());  
		 }
	}
	public String CleanURL(String url){
		//sometimes the url will contain an extra / at the end. we will remove it.
		//System.out.println(url);
		if (url.length() > 0) {
		if (url.charAt(url.length()-1) == '/') {
			//System.out.println(url.length());
			return url.substring(0, url.length()-1);
		}
		//return url.toLowerCase();
		}
		return url;
	}
	
	public String calculateCanonicalUrl(String url) {
		int pathIndex = url.indexOf("//") + 2;
		//System.out.println("Url in canonical = " + url);
		//System.out.println("Path Index in canonical = " + pathIndex);
		if (pathIndex < 2) return "";
		if (url.substring(pathIndex, pathIndex + 4).equals("www.")) {
			String result = url.substring(0, pathIndex) + url.substring(pathIndex + 4);
			//return url.substring(0, pathIndex) + url.substring(pathIndex + 4);
			return result.toLowerCase();
		} else {
			return url.toLowerCase();
		}
		/*int pathIndex = feed.indexOf("//") + 2;
		if (feed.substring(pathIndex, pathIndex + 4).equals("www.")) {
			String result = feed.substring(0, pathIndex) + feed.substring(pathIndex + 4);
			//return url.substring(0, pathIndex) + url.substring(pathIndex + 4);
			return result.toLowerCase();
		} else {
			return feed.toLowerCase();
		}*/
		
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
		canonicalUrl = calculateCanonicalUrl(url);
	}
	public boolean hasFeed() {
		return !feed.isEmpty();
	}
	

	private String checkDate(String tagS,ReadTag tag) throws IOException {
		String textBetweenTag;
		// postmetadata is for wordpress; date is for blogger
		if ( (tagS.contains("date")) || (tagS.contains("postmetadata")) ){
			textBetweenTag = tag.inBetweenTag();
			if ( (textBetweenTag.contains(year)) || (textBetweenTag.contains(previousYear)) ){
				//System.out.println("Date :" + textBetweenTag + " ----> "+ url);

				return textBetweenTag;
			}
		}
		
		
	return null;
	}

	private boolean checkDivClass(String tag){
		return tag.contains("div class");
	}

	// here is what in blogspot: href="http://www.blogger.com/rsd.g?blogID=208672613989970342"
	private String  checkBlogId(String ts) {
		int index;
		String result;
		int index2;
		index = ts.indexOf("blogID");
		if (index >= 0){
			index2 = ts.indexOf("\"",index);
			if (index2 > 0){
				result = ts.substring(index + 7, index2);
				//System.out.println("Blog Id = " + result);
				return result;
			}
			
		}
		
		return null;
	}


	//the post Id in blogspot is like this <a name='343382887737814223'></a>
	private String checkPostId(String ts) {
		int index;
		String result;
		int index2;
		index = ts.indexOf("<a name=");
		//System.out.println("Post Id Index 1= " + index);
		if (index >= 0){
			index2 = ts.indexOf("'",index + 9);
			//System.out.println("Ts is = " + ts);
			//System.out.println("Post Id Index 2= " + index);
			if (index2 > 0){
				result = ts.substring(index + 9, index2);
				//System.out.println("Post Id = " + result);
				return result;
			}
			
		}
		index = ts.toLowerCase().indexOf("postid=");
		if (index >= 0){
			index2 = ts.indexOf("'",index);
			//System.out.println("Ts is = " + ts);
			//System.out.println("Post Id Index 2= " + index);
			if (index2 > 0){
				result = ts.substring(index + 7, index2);
				index2 = result.toLowerCase().indexOf("&ispopup=true");
				if (index2 > 0){
					// the isPopup true happens at the end of the post. So I will return something
					// to be recognized as different postid
					//System.out.println("has popup true");
					result = result.substring(0,index2);
					//System.out.println("Post Id = " + result);
					return result;
				}
				
			}
			
		}
		index = ts.toLowerCase().indexOf("post\"");
		
		//this is a wordpress blog
		if (index >=0){
			//System.out.println("Ts is = " + ts);
			//System.out.println("Post Id Index= " + index);
			index2 = ts.indexOf("post-",index);
			//System.out.println("Post Id Index2-1 = " + index2);
			if (index2 > 0){
				index = index2;
				index2 = ts.indexOf("\"",index);
				//System.out.println("Post Id Index2-2 = " + index2);
				if (index2 > 0){
					result = ts.substring(index+5,index2);
					return result;
				}
				
				
			}
		}
		index = ts.toLowerCase().indexOf("\"post-title");
		
		//this is a wordpress blog
		if (index >=0){
			//System.out.println("Ts is = " + ts);
			//System.out.println("Post Id Index= " + index);
			index2 = ts.indexOf("post-",index+6);
			//System.out.println("Post Id Index2-1 = " + index2);
			if (index2 > 0){
				index = index2;
				index2 = ts.indexOf("\"",index);
				//System.out.println("Post Id Index2-2 = " + index2);
				if (index2 > 0){
					result = ts.substring(index+5,index2);
					//System.out.println("Result is - PostId =" + result);
					return result;
				}
				
				
			}
		}
		
		return null;
	}
	
private ReferenceTag getReference	(int index, String content) {
	//String result = null;
	ReferenceTag rtag = new ReferenceTag();
	int indexEnd;
	int indexBegin;
	
	indexBegin = content.indexOf("<a",index);
	//System.out.println("In get reference-index0="+indexBegin);
	rtag.setIndexEnd(indexBegin + 3);
	if (indexBegin > 0) {
		indexEnd = content.indexOf("</a>",indexBegin);
		rtag.setIndexEnd(indexEnd+4);
		//System.out.println("In get reference-indexEnd="+indexEnd);
		if (indexEnd > 0) {
			rtag.setTag( content.substring(indexBegin,indexEnd+4));
			return rtag;
		}
	}
	
	return rtag;
}
	private boolean goodPotential(String content){
		//it is a good potential for this analysis if it has an img inside it
		int index = content.indexOf("<img");
		if (index > 0){
			return true;
			
		}
		return false;
	}
	private String getFileServerFromTag(String tag) {
		int index = tag.indexOf(">");
		if (index > 0){
			return tag.substring(0,index);
		}
		return tag;
	}
	private String getDot(String fileServer) {
		System.out.println("in get dot; file server argument= " + fileServer);
		int index = fileServer.indexOf("://");
		//System.out.println("Index-getDot= " + index );
		if (index > 0) {
			int index2 = fileServer.indexOf("/", index+3);
			//System.out.println("Index2-getDot= " + index2);
			if (index2 > 0) {
				int index3 = index2 - 1;
				//System.out.println("Index3-getDot= " + index2);
				while ( (fileServer.charAt(index3) != '.') && (index3 > 0)) {
					index3--;
				}
				//the . thing is between index3 and index2
				String result = fileServer.substring(index3+1, index2);
				System.out.println("getdot result = " + result);
				return result;
			}
		}
		return "";
	}
	private String getEndDomain(String a) throws URISyntaxException{
		URI uri = new URI(a);
		String host = uri.getHost();
				
		String[] split = host.split("\\.");
		if (split.length > 0) {
			return split[split.length -1];
		} else {return "";}
	}
	private boolean verifyInternational(HashSet<String>intSites, String ending){
		return !intSites.contains(ending);
	}
	
	
	
	



	private String checkFileServer (String tag,HashSet<String> fileServers,HashSet<String>intSites ){
		return checkFileServer(tag,fileServers,false,intSites);
	}
	private String cleanTag(String tag) {
		//StringBuffer result = new StringBuffer();\
		//System.out.println("Input tag = " + tag);
		int index,index2;
		index = tag.indexOf("href");
		if (index > 0) {
		// need to get to the actual link
			index = tag.indexOf("\"",index);
			//System.out.println("Index1 = " + index);
			if (index > 0) {
				//get to the end of the "
				index2 = tag.indexOf("\"",index+1);
				//System.out.println("Index2 = " + index2);
				//System.out.println("Ouput tag manipulated = " + tag.substring(index, index2));
				return tag.substring(index+1, index2);
			}
			else {
				index = tag.indexOf("'",index);
				//System.out.println("Index1 = " + index);
				if (index > 0) {
					//get to the end of the "
					index2 = tag.indexOf("'",index+1);
					//System.out.println("Index2 = " + index2);
					//System.out.println("Ouput tag manipulated = " + tag.substring(index+1, index2));
					if (index2 > 0) {
						return tag.substring(index+1, index2);
					}
				}
			}
		}
		
		return tag;
	}
	private String checkFileServer (String tag,HashSet<String> fileServers,boolean follow,HashSet<String>intSites){
		String result = null;
		Iterator it = fileServers.iterator();
		String fileServer="";
		String tagLowerCase = tag.toLowerCase();
		//System.out.println("Check File Server ---> tag is ---> "+ tag); 
		if (!follow) {
			if ( !tagLowerCase.contains("href")) {
				return null;
			}
		}
		if (tagLowerCase.contains("4shared.com/ref")) {
			return null;
		}
		if (tagLowerCase.contains("tutorial")) {
			return null;
		}
		/*if (url.indexOf("paisleycatscrapsfreebloglayouts.blogspot.com") > 0){
			if (tag.toLowerCase().indexOf("tinypic.com") > 0) {
				result = "tinypic.com";
			}
		}
		if (url.indexOf("http://pamkez.blogspot.com") > 0){
			if (tag.toLowerCase().indexOf("blogspot.com") > 0) {
				result = "blogspot.com";
			}
		}
		*/
		if (url.indexOf("backgroundfairy.com") > 0){
			if (tag.indexOf("blogspot.com") > 0) {
				result = "blogspot.com";
			}
		}
				
		/*if (url.indexOf("spookycornerdesigns.blogspot.com") > 0){
			if (tag.toLowerCase().indexOf("photobucket.com") > 0) {
				result = "photobucket.com";
			}
		}
		if (url.indexOf("bevsblogdesign.blogspot.com") > 0){
			if (tag.toLowerCase().indexOf("photobucket.com") > 0) {
				result = "photobucket.com";
			}
		}
		if (url.indexOf("decoratublog.blogspot.com") > 0){
			if (tag.toLowerCase().indexOf("blogspot.com") > 0) {
				result = "blogspot.com";
			}
		}
		
//		if (url.indexOf("meaganswordart.blogspot.com") > 0){
//			if (tag.toLowerCase().indexOf("blogspot.com") > 0) {
//				result = "blogspot.com";
//			}
//			
//		}
		if (url.indexOf("lovefromevelyn.blogspot.com") > 0){
			if (tag.toLowerCase().indexOf("blogspot.com") > 0) {
				result = "blogspot.com";
			}
		}
		if (url.indexOf("hookedoncountry.blogspot.com") > 0){
			if (tag.toLowerCase().indexOf("blogspot.com") > 0) {
				result = "blogspot.com";
			}
		}
		if (url.indexOf("hpihlajanmarjanblogipohjat.blogspot.com") > 0){
			if (tag.toLowerCase().indexOf("blogspot.com") > 0) {
				result = "blogspot.com";
			}
		}
		*/
		//System.out.println("In CheckFileServer - Tag is = "+ tag);
	
		
		if (tagLowerCase.indexOf("?digifree") > 0){
			result = "?digifree";
			//System.out.println("In DigiFree check");
		}
		if (tagLowerCase.indexOf("digifree?") > 0){
			result = "digifree?";
			//System.out.println("In DigiFree check");
		}
		if (tagLowerCase.indexOf("?cbh") > 0){
			result = "?cbh";
			//System.out.println("In DigiFree check");
		}
		if (tagLowerCase.indexOf("?freebie") > 0){
			result = "?freebie";
			//System.out.println("In DigiFree check");
		}
		
		int index;
		
		//we need to clean this tag. I am still getting tag with href's
		
		while ((result == null) && (it.hasNext())) {

			fileServer = ((String) it.next()).toLowerCase();
			// System.out.println("---->File Server is = " + fileServer);

			if ((index = tagLowerCase.indexOf(fileServer)) >= 0) {
				System.out.println("File Server from database is ============> "
								+ fileServer);
				// need to make sure that it does not get things like foobox.net
				// and confuse with box.net
				if ( !(  ( tagLowerCase.contains(".jpg")) || ( tagLowerCase.contains(".png")) || ( tagLowerCase.contains(".jpeg")) || ( tagLowerCase.contains(".gif")) )) {
					result = fileServer;
				}

			}
		}
		if (result == null) {
			tag = cleanTag(tag);
			String dot = getDot(tag);
			if (dot.length() == 0) {
				//this is the problem when cannot find :// Therefore it is not a good server
				return result;
			}
			boolean candidate = false;
			boolean smallEnd = false;
			if (dot.length() < 3) {
				smallEnd = verifyInternational(intSites,dot);
				System.out.println("After verify international. Small End=" + smallEnd);
			}
			if (smallEnd) {
				candidate = true;
				System.out.println("Faking bit.ly because potential link ends with .something that is length 2");
			}
			if (candidate) {
				return "bit.ly";
			}
		}
		//System.out.println("Result for checkFileServer = "+ result);
		/*if (tag.indexOf("post-body") > 0) {
			mytrap = true;
		}*/
		
		return result;
	}
	private String treatAllZip(String tag,String fileServer){
		int index = tag.toLowerCase().indexOf(".zip");
		if (index > 0) {
			//needs to see if end of the line of the part that contains the http ends with a .zip
			return (fileServer);
		}
		return null;
	}
	

	private String treat4Shared(String tag){
		int index = tag.toLowerCase().indexOf(".html");
		if (index > 0) {
			//needs to see if end of the line of the part that contains the http ends with a .html
			return ("4shared");
		}
		index = tag.indexOf(".zip");
		if (index > 0) {
			//needs to see if end of the line of the part that contains the http ends with a .html
			return ("4shared");
		}
		return null;
	}

	private String treatRapidShare(String tag){
		int index = tag.toLowerCase().indexOf(".html");
		if (index > 0) {
			//needs to see if end of the line of the part that contains the http ends with a .html
			return ("rapidshare");
		}
		index = tag.indexOf(".zip");
		if (index > 0) {
			//needs to see if end of the line of the part that contains the http ends with a .html
			return ("rapidshare");
		}
		return null;
	}

	private String treatMediafire(String tag){
		
		int index = tag.toLowerCase().indexOf(".html");
		if (index > 0) {
			//needs to see if end of the line of the part that contains the http ends with a .html
			return ("mediafire");
		}
		index = tag.toLowerCase().indexOf("/?");
		if (index > 0) {
			//needs to see if end of the line of the part that contains the http ends with a .html
			return ("mediafire");
		}
		return null;
	}



	private String getFileServerURL(String tag,String fileServer){
		//String result;
		//"http://www.mediafire.com/file/yynnm45wygy/AF-Vintage"
		StringBuffer sb = new StringBuffer(tag);
		int index1; 
		String result = null;
		//System.out.println("In get File Server URLtag = "+ tag);
		index1 = sb.indexOf("href");
		//System.out.println("Index 1 in GFSU = " + index1);
		index1 = index1 + 6;
//href=http://www.mediafire.com/file/yynnm45wygy/AF-Vintage QP 8.zip
		int index2 = sb.indexOf("\"",index1);
		//System.out.println("Index 2 in GFSU = " +index2);
		if (index2 > 0) {
			//System.out.println("Result in GetFileServer Url = " + result);
			result =  treatResultFileServer(sb,url,index1,index2);
		}
		else {
			//raspberry designs is in this case
			index2 = sb.indexOf("'",index1);
			//System.out.println(index2);
			if (index2 > 0){
				result =  treatResultFileServer(sb,url,index1,index2);
			}
			else {
				index2 = sb.indexOf(">",index1);
				//System.out.println(index2);
				if (index2 > 0){
					result =  treatResultFileServer(sb,url,index1,index2);
				}
				else {
					index2 = sb.indexOf(" ",index1);
					//System.out.println(index2);
					if (index2 > 0){
						result =  treatResultFileServer(sb,url,index1,index2);
					}
				}
				
			}
			
			
		}
		/* This happen in angelflightscraps. the format is:
		 * href=http://www.4shared.com/file/77419944/29b1ab5c/AF-Special_Delivery_QP_1.html>
		 */
		if (result == null) {
			index1 = sb.indexOf("href");
			index1 = index1 + 5;
			index2 = sb.indexOf(".html",index1);
			if (index2 > 0) {
				result =  treatResultFileServer(sb,url,index1,index2+5);
			}
		}
		return result;
	}

	private String treatResultFileServer(StringBuffer sb,String url,int index1,int index2) {
		String result = sb.substring(index1, index2);
		
		//fix url in case is relative
		if (result.contains("http")){
			return result;
		}
		else {
			if (result.startsWith("/")){
				return (url+result);
			}
			else {
				return (url+"/"+result);
			}
		}
		
	}
	private int transformStringToNumber(StringBuffer sb) throws NumberFormatException{
		//System.out.println("The height String is = "+ sb.toString());
		Integer result = new Integer(sb.toString());
		//System.out.println("The image height is = " + result.intValue());
		return result.intValue();
	}
	private int grabNumber(StringBuffer tag,int index) throws NumberFormatException{
		
		int i = index;
		i++;
		char ch;
		StringBuffer numberS = new StringBuffer();
		boolean foundNumber = false;
		boolean gotAllNumber = false;
		while ( (i < tag.length()) && !gotAllNumber ){
			ch = tag.charAt(i);
			//System.out.println("Character read is ---> "+ ch);
			if (ch >= '0' && ch <= '9') {
				// ch is a digit
				numberS.append(ch);
				foundNumber = true;
			}
			else {
				if (foundNumber){
					//got all the numbers
					
					gotAllNumber = true;
				}
			}
			i++;
		}
		return transformStringToNumber(numberS);
	}

	private boolean hasWidthOrHeight(String tag){
		StringBuffer workTag = new StringBuffer(tag.toLowerCase());
		String hint = "height";
		int index = workTag.indexOf(hint);
		if (index <= 0) {
			//try the next hint
			hint = "h=";
			index = workTag.indexOf(hint);
		}
		//System.out.println("Hint word is = " + hint);
		//System.out.println("index = "+ index);
		// int index2;

		noheight = true;
		nowidth = true;
		if (index >= 0) {

			noheight = false;
			return true;
		} 
		else {		
			// doing the same for width
			index = workTag.indexOf("width");
			//System.out.println("index = "+ index);			
			if (index >= 0) {
				nowidth = false;
				return true;
			} 
		}
		return false;
	}

	private void checkHeightWidth(String tag) {
		StringBuffer workTag = new StringBuffer(tag.toLowerCase());
		String hint = "height";
		int index = workTag.indexOf(hint);
		if (index <= 0) {
			//try the next hint
			hint = "h=";
			index = workTag.indexOf(hint);
		}
		//System.out.println("Hint word is = " + hint);
		//System.out.println("index = "+ index);
		// int index2;
		
			noheight = true;
			nowidth = true;
			if (index > 0) {
				
				noheight = false;
			} 
			else {		
				// doing the same for width
				index = workTag.indexOf("width");
				//System.out.println("index = "+ index);			
				if (index > 0) {
					nowidth = false;		
				} 
			}
	}
	class ReadingImageThread extends Thread {
		URL url ;
		BufferedImage bi;
		String imageUrl;
		ReadingImageThread(String imageUrl){
			this.imageUrl = imageUrl;
		}
		public void run() {
			try {
				//System.out.println("In the thread: Going to get Image +++++++++++++++++++++++");
				url = new URL(imageUrl);
				bi = ImageIO.read(url);
				//WebFile file = new WebFile(imageUrl);
				//bi = file.getImage();
				//System.out.println("Just finished getting Image");
			}catch (MalformedURLException e2) {
				System.out.println("This url does not exist: "+ url);
			}catch (IOException e1) {
				System.out.println("Could not reach " + url);
			
			}
		}
		BufferedImage getImage() {
			return bi;
		}
	}

	

	private boolean checkForBloggerFollower(String tag) {
		followerImage = false;
		if (tag.toLowerCase().contains("http://www.blogger.com/img")) {
			followerImage = true;
			return true;
		}
		return false;
	}
	

	class ReadingThread extends Thread {
		String url;
		HashSet<String> fileServers;
		ReadTag tag;
		boolean titleFreebie = false;
		ReadingThread(String url){
				
				this.url = url;
		}
			
		ReadingThread(String url,HashSet<String> fileServers){
			
			this.titleFreebie=titleFreebie;
			this.fileServers = fileServers;
		}
		ReadingThread(String url,boolean titleFreebie){
			this.titleFreebie=titleFreebie;
			this.url = url;
	}
		
	ReadingThread(String url,HashSet<String> fileServers,boolean titleFreebie){
		this.url = url;
		this.titleFreebie=titleFreebie;
		this.fileServers = fileServers;
	}
		public void run() {
			try {
				tag = new ReadTag(url,fileServers,titleFreebie);
				//System.out.println("ReadingThread - Tag = " + tag);
			}catch (MalformedURLException e2) {
				System.out.println("This url does not exist: "+ url);
			}catch (IOException e1) {
				System.out.println("Could not reach " + url);
			
			}catch(Exception e3){
				e3.printStackTrace();
			}
		}
		ReadTag getReadTag() {
			return tag;
		}
	}
	
	private String getBlogId(String blogPostId){
		//System.out.println("GetBlogId="+blogPostId);
		int index = blogPostId.indexOf("blog-");
		if (index > 0) {
			int index2 = blogPostId.indexOf('.',index);
			if (index2 > 0) {
			 return blogPostId.substring(index + 5, index2);
			}
		
		}
		return blogPostId;
	}
	
	private String getPostId(String blogPostId){
		int index = blogPostId.indexOf("post-");
		if (index > 0) {
		return blogPostId.substring(index + 5);
		}
		//for wordpress blogs
		index = blogPostId.indexOf("?p=");
		if (index > 0) {
			return blogPostId.substring(index + 3);
		}
		return blogPostId;
	}
	
	private String getEnd(String url) {
		 //Blog Position = http://mimigrounette.canalblog.com/archives/2009/05/02/10863507.html
		int index;
		int indexPrev;
		gotEnd = true;
		if (url != null) {
		index = url.indexOf("/");
		indexPrev = index;
		while (index > 0) {
			indexPrev = index;
			//System.out.println("Index = " + index);
			index = url.indexOf("/",indexPrev+1);
		}
		
		return url.substring(indexPrev);
		}
		else {
			return null;
		}
	
		
	}
	
	public String removeLast(String url){
		String answer = url.substring(0,url.length()-1);
		//System.out.println("Remove Last = " + answer);
		return answer;
	}
	
	private String getNewTagForFeedWithProblem(ReadTag tag, String title,String smallDescription) {
		String result = tag.getWholeFeed(title,smallDescription);
		
		//System.out.println("GetNewTagForFeedWithProblem-result.1 = "+ result);
		return result;
	}
	private String getNewTagForFeedWithProblem(ReadTag tag, String title) {
		String result = tag.getWholeFeed(title);
		
		//System.out.println("GetNewTagForFeedWithProblem-result .2= "+ result);
		return result;
	}
	
	
	
	
	public String readBlog() throws IOException,MalformedURLException,Exception{
		ReadTag tag;
		StringBuffer result = new StringBuffer();
		
		boolean feedProblem= false;
		String title="";
		String href="";
		String date="";
		String blogPostId;
		String blogId="";
		String postId="";
		//get the entries for the feed
		
		
		String newBlogPosition = null;
		Exception excep = null;
		boolean readMore = false;
		long time = System.currentTimeMillis();
		//System.out.println("Current time (mili)= "+ time);
		try {
			//Feed feedEntries = Blogger.getFeedEntries(myService, this);
			FeedReader feedEntries = new FeedReader(feed);


			List<SyndEntry> entries = feedEntries.getEntries();
			if (entries == null) {
				return null;
			}
			int tamanho = entries.size();
			//System.out.println("Number of posts= "+ tamanho);
			if (tamanho > MaxPosts){
				tamanho = MaxPosts;
				
			}
			

			//System.out.println("Actual Number of posts to be Search= "+ tamanho);
			
			
			 //System.out.println("------------------->Number of posts- feedEntry= "+ tamanho);
					
			
			SyndEntry entry;
			
			
			boolean treated = false;
			String firstHref = null;
			for (int i = 0; i < tamanho; i++) {
				readMore = false;
				//System.out.println("------> Post Number: " + i);
				entry = entries.get(i);
				//System.out.println(entry);
				title = entry.getTitle();
				///System.out.println("Find Freebies-Title1 ="+ title);
				

				boolean titleFreebie = false;
				String titleLower = title.toLowerCase();
				
				href = entry.getLink();
				//System.out.println("Href= "+ href);
				//System.out.println("Entry is= " + entry);
				
				if (href == null) {
					//System.out.println("Href is NULL");
					//System.out.println(entry.getLinks().get(0));
				}
								
				firstHref = href;
				
				
				//find the blogPoints using only the first post
				Date realDate = entry.getPublishedDate();
				//System.out.println("Real Date =" + realDate);
				if (realDate != null) {
					date = realDate.toString();
					//System.out.println("Date= "+ date);
				}
				else {
					//System.out.println("RealDate is null");
					continue;
				}
				
				
				if (today.getToday().compareTo(realDate) < 0){
					//System.out.println("The POST DATE IS HIGHER THAN TODAY");
					continue;
				}
				
				
				if ((href != null) && (href.endsWith("/"))) {
					href = removeLast(href);
				}
				


				blogPostId = entry.getUri();
				//System.out.println(blogPostId);
				//System.out.println("Trial = " + entry);;
				blogId = getBlogId(blogPostId);
				postId = getPostId(blogPostId);
				//System.out.println("Treated = " + treated);
				//System.out.println("Href = " + href + "   --- size = " + href.trim().length());

				
				if ( ( entry.getContents().isEmpty()) && ( (entry.getDescription() != null) && (entry.getDescription().getValue().isEmpty())) ){
//				if ( ( entry.getContents().isEmpty()) ){
					//System.out.println("Setting feedProblem true: Contents empty, description not null, but description content is empty");
					feedProblem = true;
				}
				try {
					
					if ( ((SyndContent)entry.getContents().get(0)).getMode() != null){
						feedProblem = true;
						//System.out.println("-------->Most likely a summary feed");
					}
				}catch (Exception e) {
					//System.out.println("Feed Entry has no getContents().get(0)");
				}
				if ((entry.getDescription() != null) ){
					//System.out.println("The Post is = "+ entry.getDescription().getValue());
					//System.out.println("The Post length is = "+ entry.getDescription().getValue().length());
					if (entry.getDescription().getValue().length() < 400) {
						feedProblem = true;
						//System.out.println("-------->Most likely a summary feed... Length < 400");
					}
				}
				
				
				
				//FEED WITH PROBLEMS ---------------------------------------------
				//trying to figure out the "Read More" issue
				//System.out.println("Content = " + ((SyndContent) entry.getContents().get(0)).getValue());
				if (!feedProblem) {
					if (!entry.getContents().isEmpty()){
						//System.out.println("BlogFeed1");
						//System.out.println(((SyndContent) entry.getContents().get(0)).getValue());
						
						//System.in.read();
						tag = new ReadTag(((SyndContent) entry.getContents().get(0)).getValue(),true,titleFreebie);
						//System.out.println("Tag is = " + tag);
						//System.out.println("Tag length is = " + tag.getContent().length());
						if ( (tag.getContent().contains("...<br/>"))) {
							feedProblem = true;
							readMore = true;
							//System.out.println("-------->Most likely a summary feed");
						}
						if ( (tag.getContent().contains("[...]"))) {
							feedProblem = true;
							readMore = true;
							//System.out.println("-------->Most likely a summary feed");
						}
						else if ( (tag.getContent().contains("#more"))) {
							feedProblem = true;
							readMore = true;
							//System.out.println("-------->Most likely a Read More type of feed - has #more");
						}
						else if ( (tag.getContent().toLowerCase().contains("read more"))) {
							//need to check if has the <a href around
							int index = tag.getContent().toLowerCase().indexOf("read more");
							if ( ((String)tag.getContent().subSequence(index -1 , index)).contains(">")) {
								feedProblem = true;
								readMore = true;
								//System.out.println("-------->Most likely a Read More type of feed - has Read More");
						
							}
							else {
								//System.out.println("BlogFeed2");
								//blogList = checkForFileServer( tag,fileServers,date,href,blogId,postId,terms,titleFreebie,intSites);
								result.append(tag.getContent());
								feedProblem=false;
							}
						}
						else {
							//System.out.println("BlogFeed3");
							//blogList = checkForFileServer( tag,fileServers,date,href,blogId,postId,terms,titleFreebie,intSites);
							result.append(tag.getContent());
							feedProblem=false;
							//bfi = checkForFileServer( tag,fileServers,date,href);
						}
					}
					else if (!entry.getDescription().getValue().isEmpty())  {
						//System.out.println("Description not empty");
						//System.out.println("Description: ----->" + entry.getDescription().getValue());
						//System.out.println("Description Length: ----->" + entry.getDescription().getValue().length());
						if ( (entry.getDescription().getValue().contains("...<br/>"))) {
							feedProblem = true;
							//System.out.println("-------->Most likely a summary feed: has ...<br/>");
						}
						if ( (entry.getDescription().getValue().length()<400)) {
							feedProblem = true;
							readMore = true;
							//System.out.println("-------->Most likely a summary feed- Description Length < 400");
						}
						
						if ( (url.contains("thats-serendipity.com")) ||
							 (url.contains("scraps.statchoo.com")) || 
							 (url.contains("freedomtobark.blogspot.com"))) {
							feedProblem=true;
						}
						
						else {
						feedProblem=false;
						tag = new ReadTag(entry.getDescription().getValue(),true,titleFreebie);
						//System.out.println("Tag from Description = " + tag);
						//blogList = checkForFileServer( tag,fileServers,date,href,blogId,postId,terms,titleFreebie,intSites);
						result.append(tag.getContent());
						}
					}
					else {
						//System.out.println("Problem with feed");
						feedProblem=true;
					}
				}
				
				//new code --- feed problems
				//System.out.println("------------------->Will deal with feeds with problems1");
				//System.out.println("FeedProblem= "+ feedProblem);
				//System.out.println("ReadMore= "+ readMore);
				if (readMore && title.length() > 0 ) {
					//System.out.println("Trying to find the real link");
					//What I could see it adds #more to the end of the href
					href = href + "#more";
				}
				
				if (feedProblem && title.length() > 0 ) {
					//System.out.println("Going to the blog itself1. Href = " + href);
					ReadingThread readingT;
					readingT = new ReadingThread(href,titleFreebie);
					//tag = new ReadTag(url);
					readingT.start();

					try {
						readingT.join(WaitTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						//System.out.println("Reading Thread got interrupted");
					}
					tag = readingT.getReadTag();
					//System.out.println("Tag= " + tag);
					
					if (tag != null ) {
						//System.out.println("Tag is not null. Description.1 ---> " + entry.getDescription());
						//System.out.println("Tag is not null. Description Length---> " + entry.getDescription().getValue().length());
						//When is a summary feed, the description is not null. However a read more feed not necessaryly
						if (entry.getDescription() != null) {
							String text = getNewTagForFeedWithProblem(tag,title,entry.getDescription().getValue());
							//System.out.println("Before going to getNewTagForFeedWithProblem =" + text);
							tag = new ReadTag(text,true,titleFreebie);
							
							//System.out.println("Tag from Feed with Problem Description 1= "+ tag);
							if (tag != null){
									//blogList = checkForFileServer(tag,fileServers,date,href,blogId,postId,terms,titleFreebie,intSites);
								result.append(tag.getContent());
							}
							else {
									//System.out.println("Could not get to "+ url + " in a reasonable time");
									return "";
								}
							tag.close();
								
						}
						else {
							//System.out.println("Tag to be dealt is= " + getNewTagForFeedWithProblem(tag,title));
							tag = new ReadTag(getNewTagForFeedWithProblem(tag,title),true,titleFreebie);
							//System.out.println("Tag from Feed with Problem= "+ tag);
							if (tag != null){
									//blogList = checkForFileServer(tag,fileServers,date,href,blogId,postId,terms,titleFreebie,intSites);
								result.append(tag.getContent());
							}
								else {
									//System.out.println("Could not get to "+ url + " in a reasonable time");
									return "";
								}
								tag.close();
								
						}
						 
					
					}
					
					
					

				}

				
				
				
				//checking if it found any freebie. If not, consider the title


			}
			//} catch (SAXParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//System.in.read();
		} catch (java.lang.IllegalStateException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
			excep = e;
			System.out.println("IllegalStateException");
			
		} catch (Exception e) {
			e.printStackTrace();
			feedProblem = true;
			excep = e;
			System.out.println("exception");
		}
		//System.out.println("NewBlogPosition = " + newBlogPosition);
		
		
		if (!feedProblem){
			blogType = "";
			extra = "";
		}
		else {
			if (excep != null){
				throw excep;
			}
		}
		
		long time2 = System.currentTimeMillis();
		//System.out.println("Current time (mili)= "+ time2);
		//System.out.println("\t===================>>>Total time in this blog = " + (time2 - time)/1000.);
		return result.toString();
	}
	
	
	
	
	
	public int compareTo(BlogFeed o) {
		if (this != o) {
		if (canonicalUrl.compareTo(o.canonicalUrl) == 0) {
			//System.out.println("These sites are the same: ");
			//System.out.println(url);
			//System.out.println(" -------- ");
			//System.out.println(o.url);
		}
		}
		return canonicalUrl.compareTo(o.canonicalUrl);
	}
	
//	public int compareTo(Object obj) {
//	    BlogFeed blog = (BlogFeed) obj;
//	    //System.out.println("comparing "+ url + " with -->" + blog.getUrl());
//	    int classComp = url.compareTo(blog.getUrl());
//	    if (classComp != 0) {
//	    	//add www to url
//	    	int index = url.indexOf("//");
//	    	String partOfUrl = url.substring(index+2);
//			String wwwUrl = "http://www."+partOfUrl;
//			classComp = wwwUrl.compareTo(blog.getUrl());
//	    }
//	    else {
//	    	 System.out.println("comparing "+ url + " with -->" + blog.getUrl());
//	    }
//	    return classComp;
//	    
//	    
//	  }
	
	
//	public int hashCode() { 
//	    int hash = 1;
////
//	    hash = hash * 31 
//	                + (url == null ? 0 : url.hashCode());
//	   
//	    return hash;
//	  }


	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((canonicalUrl == null) ? 0 : canonicalUrl.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlogFeed other = (BlogFeed) obj;
		if (canonicalUrl == null) {
			if (other.canonicalUrl != null)
				return false;
		} else if (!canonicalUrl.equals(other.canonicalUrl))
			return false;
		if (this != other) {
		if (canonicalUrl.compareTo(other.canonicalUrl) == 0) {
			//System.out.println("These sites are the same: ");
			//System.out.println(url);
			//System.out.println(" -------- ");
			//System.out.println(other.url);
			//System.out.println("-----> Feed: " + feed);
		}
		}
		return true;
	}


	



	

	public static void main(String[] args) throws FileNotFoundException,
	IOException,Exception {
		// TODO Auto-generated method stub

		HashSet<BlogFeed>removeUrls = new HashSet<BlogFeed>();
		BufferedReader lineOfText = new BufferedReader(new InputStreamReader(
				System.in));
		
		
			
			
			BlogFeed blog = new BlogFeed("http://lugarencantadodaneli.blogspot.com");
			blog = new BlogFeed("http://digitaldesignessentials.com/blogs/news");
			blog = new BlogFeed("http://mom2aprincess.wordpress.com");
			blog = new BlogFeed("http://moncrapamoi.canalblog.com");
			//blog = new BlogFeed("http://pricelessmomentsbymelia.typepad.com","http://pricelessmomentsbymelia.typepad.com/priceless_moments_by_meli/atom.xml", "");
			String text= blog.readBlog();
			System.out.println("------------------------");
			System.out.println(text);
			/*blog = new BlogFeed("http://myemmadoodle.blogspot.com",
					"http://feeds2.feedburner.com/DesignsBySarah", "");
			bfis = blog.findFreebies("4shared", myService);
			for (BlogFeedFreebieInfo bfi2: bfis) {
			System.out.println("HtML for Image: " + bfi2.getImageHtml());
			System.out.println("HtML for Site: " +
			bfi2.getFileServerHtml());
			System.out.println("======================================");
			}*/
		

	}
}


