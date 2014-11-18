package com.cbb;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.ParsingFeedException;
import com.sun.syndication.io.SyndFeedInput;
public class FeedReader {
	
		SyndFeed feed;
		String feedString;
		
		public FeedReader(String address) throws IllegalArgumentException, FeedException, IOException,java.lang.NoClassDefFoundError {
			feedString = address;
			URL feedUrl = new URL(feedString);
			//System.out.println("Feed URL = " + feedUrl);
            //SyndFeedInput input = new SyndFeedInput();
            //System.out.println("Before doing the input build in Feed Reader");
            ReadingThread readingT;
        	readingT = new ReadingThread(feedUrl);
        	//tag = new ReadTag(url);
        	readingT.start();
        	
        	try {
        		readingT.join(40000);
        	} catch (InterruptedException e) {
        		// TODO Auto-generated catch block
        		System.out.println("Reading Thread got interrupted");
        	}
        	//feed = readingT.getFeed();
        	
           // feed = input.build(new XmlReader(feedUrl));
            //System.out.println("After doing the input build in Feed Reader");

           
		}
		
		class ReadingThread extends Thread {
			URL url;
			ReadTag tag;
			
			ReadingThread(URL url){
				
				this.url = url;
			}
			public void getConnection(boolean redirect) throws IOException, IllegalArgumentException, FeedException {
				SyndFeedInput input = new SyndFeedInput();
				input.setXmlHealerOn(true);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			    conn.setDoOutput(true);
			    conn.setConnectTimeout(20000);
			    conn.setRequestProperty("User-Agent","browser");
			    //System.out.println("redirect value = " + redirect);
			    if (!redirect) {
			    	conn.setInstanceFollowRedirects(false);
			    }
			   
			    //OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			    
			    //writer.write(data);
			    //writer.flush();
			    
			    //StringBuffer answer = new StringBuffer();
			    BufferedReader reader;
		
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				
				try {
				feed = input.build(reader);
				}catch (ParsingFeedException e) {
					System.out.println("Problems parsing the Feed");
					e.printStackTrace();
					throw e; 
				} finally {
					reader.close();
					conn.disconnect();
				}
				
			}
			public void run() {
				try {
					getConnection(true);
				}catch (MalformedURLException e2) {
					System.out.println("This url does not exist: "+ url);
				}catch (IOException e1) {
					e1.printStackTrace();
					System.out.println("IO Exception -  " + url);
					System.out.println("trying with no redirect");
					try {
						getConnection(false);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("IO Exception2 -  " + url);
					} catch (FeedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					
					}catch (Exception e) {
						try {
							SyndFeedInput input = new SyndFeedInput();
							feed = input.build(new  InputStreamReader(url.openStream()) );
						} catch (IllegalArgumentException e11) {
							// TODO Auto-generated catch block
							e11.printStackTrace();
						} catch (FeedException e11) {
							// TODO Auto-generated catch block
							e11.printStackTrace();
						} catch (IOException e11) {
							// TODO Auto-generated catch block
							e11.printStackTrace();
						}
						//e.printStackTrace();
					} 
					
				
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (FeedException e) {
					e.printStackTrace();
				}catch (Exception e) {
					try {
						SyndFeedInput input = new SyndFeedInput();
						feed = input.build(new  InputStreamReader(url.openStream()) );
					} catch (IllegalArgumentException e1) {
						e1.printStackTrace();
					} catch (FeedException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					//e.printStackTrace();
				}
			}
			
		}
		
		public SyndFeed getFeed() {
			return feed;
		}
		
		public List<SyndEntry> getEntries() {
			//System.out.println("In Feed Reader - get Entries");
			if (feed != null) {
			return feed.getEntries();
			}
			else {
				return null;
			}
		}
		
	    public static void main(String[] args) {
	       
	       
	            try {
	            	//FeedReader fr = new FeedReader("http://bekahedesigns.com/blog/feed"); 
	            	//FeedReader fr = new FeedReader("http://www.jenreeddesigns.com/blog/feed/");
	            	//FeedReader fr = new FeedReader("http://growingupsharp.blogspot.com/feeds/posts/default");
	            	FeedReader fr = new FeedReader("http://bekahedesigns.com/blog/feed/");
	            	//System.out.println("Feed Type--->" + fr.getFeed().getFeedType());
	               //System.out.println("Feed Description--->" + fr.getFeed().getDescription());
	                //System.out.println(fr.getFeed().getImage());
	                List<SyndEntry> entries = fr.getEntries();
	                //System.out.println(entries);
	                int size = entries.size();
	                SyndEntry entry;
	               /* if (size > 3) {
	                	size = 3;
	                }*/
	                for (int i = 0; i<size; i++){
	                	entry = entries.get(i);
	                	//System.out.println("Entry -------->"+ i);
	                	//System.out.println(entry);
	                	//System.out.println("Feed Link--->" + entry.getLink());
	                	//System.out.println("Feed Title--->" + entry.getTitle());
	                	//System.out.println(entry.getPublishedDate());
	                	//System.out.println(entry.getDescription().getValue());
	                	if (!entry.getContents().isEmpty()){
	                		//System.out.println("Feed Description--->" +	entry.getContents().get(0));
	                	//System.out.println("Feed Description--->" + ((SyndContent) entry.getContents().get(0)).getValue());
	                	}
	                	//System.out.println(entry.getUri());
	                	//System.out.println(entry.getSource());
	                	
	                	//System.out.println(entry.getSource());
	                	//System.out.println(entry.getWireEntry());
	                }
	            }
	            catch (Exception ex) {
	                ex.printStackTrace();
	                System.out.println("ERROR: "+ex.getMessage());
	            }
	        
	    }

	    
	

}
