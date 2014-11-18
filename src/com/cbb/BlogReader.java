package com.cbb;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;



public class BlogReader {
	//protected static String FileBlogspot = "listOfBlogFeed.txt";
	protected HashSet<BlogFeed> urls;
	BufferedReader fileBlogsIn;
	
	public BlogReader() throws IOException {
		urls = new HashSet<BlogFeed>();
		//fileBlogsIn = new BufferedReader(new FileReader(FileBlogspot));
		//readFiles();
	}
	
	private void getAsBlogFeed(HashSet<String> list,HashSet<BlogFeed> set) throws IOException {
		for (String url:list) {
			set.add(new BlogFeed(url));
		}
	}
	public String readBlogs(HashSet<String> list) throws MalformedURLException, IOException, Exception {
		StringBuffer result = new StringBuffer();
		getAsBlogFeed(list,urls);
		for (BlogFeed url : urls) {
			result.append(readBlog(url));
		}
		return result.toString();
	}
	public String readBlog(BlogFeed url) throws MalformedURLException, IOException, Exception {
		if (!url.getFeed().equals("")) {
			//System.out.println("Going to the Site = " + url.getUrl());
			return url.readBlog();
		}
		else {
			return "";
		}
	}
	public static void main(String[] args) {
		HashSet<String> list = new HashSet<String>();
		
		list.add("http://craftingwithjack.blogspot.com");
		list.add("http://www.birdscards.com");
		list.add("http://tincidesigns.blogspot.com");
		//list.add("http://makingtheworldcuter.blogspot.com&&&&http://makingtheworldcuter.blogspot.com/feeds/posts/default&&&&");
		list.add("http://digitaldesignessentials.com/blogs/news&&&&http://feeds.feedburner.com/DigitalDesignEssentials-Blog&&&&");
		list.add("http://www.scrappersworkshop.com/blog&&&&http://www.scrappersworkshop.com/feed&&&&");
		
		try {
			System.out.println(new BlogReader().readBlogs(list));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
