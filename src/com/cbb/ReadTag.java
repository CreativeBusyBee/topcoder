package com.cbb;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;

import com.sun.syndication.feed.synd.SyndContentImpl;

public class ReadTag {
	protected URL myURL = null;
	protected char[] inrdr = null;
	protected String tag;
	protected int i = 0;
	protected int length = 0;
	protected String content;
	protected HashSet<String> fileServers;
	protected boolean freebieTitle = false; 
	//construct a ReadTag froma URL String
	public ReadTag(String myURLString) 
		throws IOException, MalformedURLException {
		
		this(new URL(myURLString),null,false);
	}
	public ReadTag(URL theURL) 
	throws IOException, MalformedURLException {
	this(theURL,null,false);
}
	
	public ReadTag(String myURLString,HashSet<String> fileServers,boolean titleFreebie) 
	throws IOException, MalformedURLException {
	this(new URL(myURLString),fileServers,titleFreebie);
}

	
	public ReadTag(String contentFeed,boolean b,boolean titleFreebie){
		content = contentFeed;
		freebieTitle = titleFreebie;
		//System.out.println("Creating ReadTag-Title Freebie = "+ titleFreebie);
		//System.out.println("Content Feed = "+ content);
		if (content != null) {
		length = content.length();
		inrdr = content.toCharArray();
		}
		else {
			length = 0;
		}
	}
	
	public ReadTag(List contentFeed,boolean b){
		List<SyndContentImpl> list = contentFeed;
		
		//content = contentFeed;
		if (list.size() > 0){
		content = list.get(0).getValue();
		//System.out.println("Content Feed = "+ content);
		
		length = content.length();
		inrdr = content.toCharArray();
		
		
		}
		else {
			length = 0;
		}
	}
	//the boolean is here for nothing really...
	public ReadTag(String contentFeed,boolean b){
		content = contentFeed;
		//System.out.println("Content Feed = "+ content);
		if (content != null) {
		length = content.length();
		inrdr = content.toCharArray();
		}
		else {
			length = 0;
		}
	}
	public ReadTag(URL theURL,HashSet<String> fileServers,boolean titleFreebie) throws IOException {
		myURL = theURL;
		freebieTitle = titleFreebie;
		this.fileServers = fileServers;
		//System.out.println("FileServers= "+ fileServers);
		WebFile file = new WebFile( theURL );
		content = file.getWebSite();
		//System.out.println("Content of site = " + content);
		if (content != null) {
		length = content.length();
		inrdr = content.toCharArray();
		}
		else {
			length = 0;
		}
		
	}
	
	public String getContent() {
		return content;
	}
	public String nextLine()  {
			StringBuffer line  = new StringBuffer();
			boolean first = false;
			//System.out.println("i = "+ i);
			while ( (i<length) ) {
				first = true;
				if (inrdr[i] != '\n') {
					line.append(inrdr[i]);
				}
				if (inrdr[i] == '\n') {
					//System.out.println("In break");
					i++;
					break;
				}
				
				
				i++;
			}
			
			
			//System.out.println("Line = "+ line);
			if (first){
				return line.toString();
			}
			else {
				return null;
			}
	}
		
	public void startLines() {
		i = 0;
	}
	public int countLines() {
		int lines = 0;
		startLines();
		String next = nextLine();
		
		while (next != null){
			lines++;
			next = nextLine();
		}
		return lines;
	}
	
	public String nextTag() {
		
	   while ( (i<length) ) {
		
			char thisChar = inrdr[i];
			if (thisChar == '<'){
				
				String tag = readTag();
				return tag;
			}
			i++;
		}
		return null;
	}
	
	
	public String nextTagSpecial() {
		int j = i;
		
		   while ( (j<length) ) {
			
				char thisChar = inrdr[j];
				if (thisChar == '<'){
					
					String tag = readTagSpecial();
					return tag;
				}
				j++;
			}
			return null;
		}

	public String inBetweenTag() throws IOException {
		StringBuffer theTag = new StringBuffer("");
	
		while ((i<length)  && (inrdr[i] != '<'))  {
			
			theTag.append(inrdr[i]);
			i++;
		}
	
		
		return theTag.toString().trim();
		
	}
	private String getFromTitle(String title) {
		//System.out.println("FileServers = " + fileServers);
		//System.out.println("ReadTag-getFromTitle-Title = "+ title);
		content = HtmlManipulator.replaceHtmlEntities(content);
		//System.out.println("readTag-GetfromTitle-Content= "+ content);
		//System.out.println("TestOnly=" + content.indexOf("bit.ly"));
		
		//will check if there is even a chance to have a match
		boolean goodToGo = false;
		int indexfs=0;
		if (fileServers != null){
			
			for (String fileServer: fileServers){
				//System.out.println("FileServer passed to ReadTag = " + fileServer);
				indexfs = content.indexOf(fileServer);
				if (indexfs > 0){
					//System.out.println("ReadTag-getFromTitle-indexfs="+indexfs);
					//System.out.println("File Server found for ReadTag purposes="+fileServer);
					// dealing with the porblem that css contains .zippy
					if (fileServer.contains(".")) {
						//check that there is a character before the .
						//System.out.println("Character before the . is = " + content.charAt(indexfs-1));
						if (content.charAt(indexfs-1) != ' ') {
							goodToGo = true;
							break;
						}
						
					}
					else {
						goodToGo = true;
						break;
					}
					
				}
			}
		}
		//System.out.println("ReadTag-getFromTitle-GoodToGo="+goodToGo);
		//System.out.println("and titleFreebie variable is = " + freebieTitle);
		if (!goodToGo){
			if (!freebieTitle) {
				return "";
			}
			else {
				goodToGo = true;
				//System.out.println("ReadTag-getFromTitle-GoodToGo - will continue because of freebie in title");
			}
		}
		int index0 = content.indexOf("<body>");
		if (index0 < 0) {
			index0 = content.indexOf("<body");
		}
		//System.out.println("Index0 ="+index0);
		int index = content.indexOf(title,index0);
		//int index3;
		int index2;
		//int index4;
		//System.out.println("Index ="+index);
		
		if (index > 0 ) {
			index2 = content.indexOf("</body",index);
			if (index2 > 0) {
				return content.substring(index,index2);
			}
			else {
				return "";
			}
		}
		return "";
	}
	
	private String getFromDescription (String smallDescription) {
		content = HtmlManipulator.replaceHtmlEntities(content);
		//System.out.println("Content in get From Description = " + content);
		int index0 = content.indexOf("<body>");
		//System.out.println("Index0 ="+index0);
		int index;
		int newEnd = smallDescription.length();
		if (newEnd >50) {
			newEnd = 50;
		}
		String smallerDesc = smallDescription.substring(0, newEnd);
		int index2;
		//System.out.println("------------------------------ReadTag - getFromDescription=" + smallerDesc);
		
		index = content.indexOf(smallerDesc,index0);
		//System.out.println("Index ="+index);
		if (index > 0) {
			index2 = content.indexOf("<div",index);
			//System.out.println("Index2 ="+index2);
			if (index2 > 0) {
				return content.substring(index, index2);
			}
		}
		else {
			//try the div main
			
				index = content.indexOf(smallerDesc);
				//System.out.println("Index ="+index);
				if (index > 0) {
					index2 = content.indexOf("<div",index);
					//System.out.println("Index2 ="+index2);
					if (index2 > 0) {
						return content.substring(index, index2);
					}
				
				
			}
			else {
			//just return the stuff after <body...
				if (index0>0){
					return content.substring(index0);
				}
			}
		}
		return "";
	}
	
	public String getWholeFeed(String title,String smallDescription) {
		

		//System.out.println("Title=" + title);
		String result = getFromTitle(title);
		//System.out.println("ReadTag-getWholeFeed-Just from title="+ result.isEmpty());
		if (result.isEmpty()) {
			result = getFromDescription(smallDescription);
			//System.out.println("ReadTag-getWholeFeed-Just from description="+ result);
			/*if (result.isEmpty()){
				result = getFromDescription(HtmlManipulator.quoteHtml(smallDescription));
			}*/
		}
		
		return result;
		
		
	}
public String getWholeFeed(String title) {
		

		//System.out.println("Title=" + title);
		String result = getFromTitle(title);
		//System.out.println("ReadTag-getWholeFeed="+ result);
		
		return result;
		
		
	}
	
	protected String readTag()  {
		StringBuffer theTag = new StringBuffer("<");
		i++;
		while ( (i<length) && (inrdr[i] != '>')) {
		
			theTag.append(inrdr[i]);
			i++;
		}
		theTag.append(">");
		i++;
		return theTag.toString();
	}
	
	protected String readTagSpecial()  {
		int j = i;
		StringBuffer theTag = new StringBuffer("<");
		j++;
		while ( (j<length) && (inrdr[j] != '>')) {
		
			theTag.append(inrdr[j]);
			j++;
		}
		theTag.append(">");
		j++;
		return theTag.toString();
	}
	
	public void close()  {
		
	}
	
	public String toString() {
		return "ReadTag[" + content + "]";
	}
	
	
	public static void main(String[] args)
	throws MalformedURLException, IOException {
	
	
		ReadTag rt = new ReadTag("http://www.digiapedesigns.com/blog/chevron-papers-freebie" );
		String tag;
		
		while ( ( (tag = rt.nextTag()) != null)){
			System.out.println("Tag = " + tag);
			
		}
		System.out.println("Size = " + rt.length);
		System.out.println("Lines = "+ rt.countLines());
		//String text = HtmlManipulator.replaceHtmlEntities(rt.content);
		//int result = text.indexOf("Hey Everyone");
		//System.out.println("Contents After Hey everyone: " + text.substring(result));
		rt.close();
	
} 
}
