package com.cbb;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.MalformedURLException;


	/**
	 * Get a web file.
	 */
	public final class WebFile { 
	    // Saved response.
	    private java.util.Map<String,java.util.List<String>> responseHeader = null;
	    private java.net.URL responseURL = null;
	    private int responseCode = -1;
	    private String MIMEtype  = null;
	    private String charset   = null;
	    private Object content   = null;
	 
	    public WebFile(String urlString )
        throws java.net.MalformedURLException, java.io.IOException {
	    	
	    	this(new java.net.URL( urlString ));
	    }
	    /** Open a web file. */
	    public WebFile( java.net.URL url )
	        throws java.net.MalformedURLException, java.io.IOException {
	        // Open a URL connection.
	        
	        final java.net.URLConnection uconn = url.openConnection( );
	        if ( !(uconn instanceof java.net.HttpURLConnection) )
	            throw new java.lang.IllegalArgumentException(
	                "URL protocol must be HTTP." );
	        final java.net.HttpURLConnection conn =
	            (java.net.HttpURLConnection)uconn;
	 
	        // Set up a request.
	        conn.setConnectTimeout( 10000 );    // 10 sec
	        conn.setReadTimeout( 20000 );       // 10 sec
	       
	        //conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
	        
	        // Send the request.
	        conn.connect( );
	 
	        // Get the response.
//	        responseHeader    = conn.getHeaderFields( );
	//        responseCode      = conn.getResponseCode( );
//	        responseURL       = conn.getURL( );
	        final int length  = conn.getContentLength( );
	        final String type = conn.getContentType( );
	        //System.out.println(responseHeader);
	        //System.out.println(responseCode);
	        //System.out.println(responseURL);
	        
	        //System.out.println(type);
	        if ( type != null ) {
	            final String[] parts = type.split( ";" );
	            //System.out.println("Parts = " + parts);
	            MIMEtype = parts[0].trim( );
	            //System.out.println("MIMEType = "+ MIMEtype);
	            for ( int i = 1; i < parts.length && charset == null; i++ ) {
	                final String t  = parts[i].trim( );
	                final int index = t.toLowerCase( ).indexOf( "charset=" );
	                if ( index != -1 )
	                    charset = t.substring( index+8 );
	            }
	            //System.out.println("Charset = " + charset);
	            if (charset == null) {
	            	charset = "UTF-8";
	            }
	        }
	 
	        // Get the content.
	        //final java.io.InputStream stream = conn.getErrorStream( );
	        final java.io.InputStream stream = conn.getInputStream();
	        //System.out.println("Stream = " + stream);
	        //System.out.println("Content = " + conn.getContent( ));
	        if ( stream != null )
	            content = readStream( length, stream );
	        else if ( (content = conn.getContent( )) != null &&
	            content instanceof java.io.InputStream ) {
	            content = readStream( length, (java.io.InputStream)content );
	            //System.out.println("Content = " + content);
	            
	        }
	        conn.disconnect( );
	    }
	 
	    /** Read stream bytes and transcode. */
	    private Object readStream( int length, java.io.InputStream stream )
	        throws java.io.IOException {
	    	//System.out.println("Read Stream - Length= " + length);
	    	//System.out.println("Read Stream - Available= " + stream.available());
	        final int buflen = Math.max( 1024, Math.max( length, stream.available() ) );
	        byte[] buf   = new byte[buflen];
	        byte[] bytes = null;
	 
	        for ( int nRead = stream.read(buf); nRead != -1; nRead = stream.read(buf) ) {
	            if ( bytes == null ) {
	                bytes = buf;
	                buf   = new byte[buflen];
	                continue;
	            }
	            final byte[] newBytes = new byte[ bytes.length + nRead ];
	            System.arraycopy( bytes, 0, newBytes, 0, bytes.length );
	            System.arraycopy( buf, 0, newBytes, bytes.length, nRead );
	            bytes = newBytes;
	        }
	 
	        if ( charset == null )
	            return bytes;
	        try {
	            return new String( bytes, charset );
	        }
	        catch ( java.io.UnsupportedEncodingException e ) { }
	        return bytes;
	    }
	 
	    /** Get the content. */
	    public Object getContent( ) {
	        return content;
	    }
	 
	    /** Get the response code. */
	    public int getResponseCode( ) {
	        return responseCode;
	    }
	 
	    /** Get the response header. */
	    public java.util.Map<String,java.util.List<String>> getHeaderFields( ) {
	        return responseHeader;
	    }
	 
	    /** Get the URL of the received page. */
	    public java.net.URL getURL( ) {
	        return responseURL;
	    }
	 
	    /** Get the MIME type. */
	    public String getMIMEType( ) {
	        return MIMEtype;
	    }
	   
	    
	    public String getWebSite() {
	    	String MIME    = getMIMEType( );
	    	Object content = getContent( );
	    	if ( MIME.contains( "text" ) && content instanceof String )
	    	{
	    	    String html = (String)content;
	    	   return html;
	    	}
	    	
	    	return null;
	    }
	    
	    public java.applet.AudioClip getAudio() {
	    	String MIME    = getMIMEType( );
	    	Object content = getContent( );
	    	if ( MIME.startsWith( "audio" ) && content instanceof java.applet.AudioClip )
	    	{
	    	    java.applet.AudioClip audio = (java.applet.AudioClip)content;
	    	    return audio;
	    	}
	    	return null;
	    }
	    
	    static public void main (String[] args) {
	    	WebFile file;
	    //	String myFeed = "http://blogsearch.google.com/ping?name=Creative%20Busy%20Hands%20Scrapbooking%20Freebies%20Search&url=http://cbhscrapbookfreebiessearch.blogspot.com&changesURL=http://feeds2.feedburner.com/CompleteCBHFreebiesSearch";
	    //	String myFeed = "http://blogsearch.google.com/ping?name=Creative+Busy+Hands&url=http%3A%2F%2Fcbhscrapbookfreebiessearch.blogspot.com%2F&changesURL=http%3A%2F%2Ffeeds2.feedburner.com%2FCompleteCBHFreebiesSearch";
			
	    	String myFeed =  "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.geojsonp";

	    	System.out.println("My Feed = ");
	    	System.out.println(myFeed);
	    	System.out.println("-------------");
	    	try {
				file = new WebFile(myFeed);
			
	    	//System.out.println(file.getWebSite());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}

