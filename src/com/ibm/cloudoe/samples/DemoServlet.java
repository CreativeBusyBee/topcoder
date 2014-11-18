package com.ibm.cloudoe.samples;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import com.cbb.BlogReader;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;


@MultipartConfig
public class DemoServlet extends HttpServlet {
	private static Logger logger = Logger.getLogger(DemoServlet.class.getName());
	private static final long serialVersionUID = 1L;

	private String serviceName = "user_modeling";
	
	// If running locally complete the variables below with the information in VCAP_SERVICES
	private String baseURL = "https://gateway.watsonplatform.net/systemu/service/";
	private String username = "82f455f5-70a9-4188-87e5-e276af71a665";
	private String password = "OHlk5Gy0acYS";
		
	/**
	 * Forward the request to the index.jsp file
	 *
	 * @param req the req
	 * @param resp the resp
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("/index.jsp").forward(req, resp);
	}
	private HashSet<String> getBlogs(String list){
		HashSet<String> result = new HashSet<String>();
		String[] aList = list.split("\n");
		int max = aList.length;
		if (max > 10) {
			max = 10;
		}
		
		for (int i = 0; i < aList.length; i++) {
			result.add(aList[i]);
		}
		return result;
	}
	private String getProfile(String text,JSONObject cItem,final HttpServletRequest req,Executor executor ) throws URISyntaxException, ClientProtocolException, IOException {
		JSONObject contentItem = new JSONObject();
		
    	contentItem.put("userid", UUID.randomUUID().toString());
    	contentItem.put("id", UUID.randomUUID().toString());
    	contentItem.put("sourceid", "freetext");
    	contentItem.put("contenttype", "text/html");
    	contentItem.put("language", "en");
    	contentItem.put("content", text);
    	
    	JSONObject content = new JSONObject();
    	JSONArray contentItems = new JSONArray();
    	content.put("contentItems", contentItems);
    	contentItems.add(contentItem);
    	
    	
		URI profileURI = new URI(baseURL + "api/v2/profile").normalize();
		
		Request profileRequest = Request.Post(profileURI)
				.addHeader("Accept", "application/json")
				.bodyString(content.toString(), ContentType.APPLICATION_JSON);
		String profileString = executor.execute(profileRequest).handleResponse(new ResponseHandler<String>() {

			@Override
			public String handleResponse(HttpResponse r)
					throws ClientProtocolException, IOException {
				if (r.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					req.setAttribute("error", handleError(r));
					return null;
				}
				return EntityUtils.toString(r.getEntity());
			}
			
		});
		return profileString;
    	
    	
	}
	private String getViz(String profileString,Executor executor) throws IOException, URISyntaxException {
		
		URI vizURI = new URI(baseURL + String.format("api/v2/visualize?w=%d&h=%d&imgurl=%s", 900, 900, "images/app.png")).normalize();
		
		String viz = executor.execute(Request.Post(vizURI)
		    .bodyString(profileString, ContentType.APPLICATION_JSON))
		    .returnContent().asString();
		return viz;
	}
	/**
	 * Create and POST a request to the User Modeling service
	 * 
	 * @param req the Http Servlet request
	 * @param resp the Http Servlet response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		logger.info("doPost");

		req.setCharacterEncoding("UTF-8");
		// create the request
		//String text = req.getParameter("content");
		String blogsS = req.getParameter("content");
		//String blogsF = req.getParameter("contentF");
		HashSet<String> blogs = getBlogs(blogsS);
		//HashSet<String> blogF = getBlogs(blogsF);
		String text;
		String textF;
		//String user1 = req.getParameter("user1");
		//String user2 = req.getParameter("user2");
		try {
			text = new BlogReader().readBlogs(blogs);
			//textF = new BlogReader().readBlogs(blogF);
			
			// Display the user's input for them, so they can
			// see the text that was analyzed
			req.setAttribute("content", blogsS);
			// Display the user's input for them, so they can
			// see the text that was analyzed
			//req.setAttribute("contentF", blogsF);
			//req.setAttribute("user1", user1);
			//req.setAttribute("user2", user2);
			JSONObject contentItem = new JSONObject();
			Executor executor = Executor.newInstance().auth(username, password);
			String profileString = getProfile(text,contentItem,req,executor);
    	
			List<Map<String,String>> traits=null;
			List<Map<String,String>> traitsF=null;
			List<Map<String,String>> traitsR=null;
			if (profileString != null) {
				traits = formatTraits(profileString);
				String viz = getViz(profileString,executor);
				
	    		
    			// send visualization and traits back to the UI
	    		req.setAttribute("viz", viz);
				req.setAttribute("traits", traits);
			}
			/*
			contentItem = new JSONObject();
			
			String profileString2 = getProfile(textF,contentItem,req,executor);
			if (profileString != null) {
				traitsF = formatTraits(profileString2);
				String viz2 = getViz(profileString2,executor);
				
	    		
    			// send visualization and traits back to the UI
			
	    		req.setAttribute("vizF", viz2);
				req.setAttribute("traitsF", traitsF);
				if ((traits != null) && (traitsF != null)) {
					traitsR= analyseCompatibility(traits,traitsF);
					req.setAttribute("traitsR", traitsR);
					
				}
				
			}*/
			
		} catch (Exception e) {
			// Log something and return an error message
			logger.log(Level.SEVERE, "got error: "+e.getMessage(), e);
		}
    	
    	req.getRequestDispatcher("/index.jsp").forward(req, resp);
	}

	private List<Map<String,String>> analyseCompatibility(List<Map<String,String>> t1, List<Map<String,String>> t2) {
		t1 = (ArrayList<Map<String,String>>)t1;
		t2 = (ArrayList<Map<String,String>>)t2;
		List<Map<String,String>> t3 = new ArrayList<Map<String,String>>();
		Map<String,String> el;
		/*
		for (Map<String,String> m: t1) {
			for (String s: m.keySet()) {
				System.out.print("key=" + s);
				System.out.println(" - value= " + m.get(s));
			}
		} */
		
		for (int i = 0; i < t1.size(); i++) {
			
			
				String value1 = t1.get(i).get("value");
				System.out.println("id= " + t1.get(i).get("id"));
				System.out.println("Values = " + value1);
				
				if (value1 != null) {
					el = new HashMap<String,String>();
					t3.add(el);
					double iv1 = transformValue(value1);
					String value2 = t2.get(i).get("value");
					System.out.println(", "+ value2);
					if (value2 != null) {
						double iv2 = transformValue(value2);
						double max = Math.max(iv2, iv1);
						max = 0.2 * max;
						el.put("id", t1.get(i).get("id"));
						if (Math.abs(iv2-iv1) > max) {
							el.put("value", "Not Compatible");
						}else {
							el.put("value","Compatible");
						}
					}
				}
				
			
		}
		
		return t3;
	}
	private double transformValue(String v) {
		v = v.substring(0, v.length()-1);
		return Double.parseDouble(v);
		
	}
	/**
	 * Parse the profile data to a Json object and transform it to a map.
	 *
	 * @param profileJson the string that contains the profile data as json 
	 * @return the trait list based on the profileJson content
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private List<Map<String,String>> formatTraits(String profileJson) throws IOException {
		List<Map<String,String>> arr = new ArrayList<Map<String,String>>();
		JSONObject tree = JSONObject.parse(profileJson);
		formatTree((JSONObject)tree.get("tree"), 0, arr);
		return arr;
	}

	private static final Map<String,String> NAME_SUBST = new HashMap<String,String>();
	static {
		// Some of the trait names can be misunderstood by lay people,
		// make them a bit more approachable for the lay person 
		NAME_SUBST.put("Anger", "Fiery"); 
		NAME_SUBST.put("Anxiety", "Prone to worry"); 
		NAME_SUBST.put("Depression", "Melancholy"); 
		NAME_SUBST.put("Vulnerability", "Susceptible to stress"); 
		NAME_SUBST.put("Liberalism", "Authority-challenging"); 
		NAME_SUBST.put("Morality", "Uncompromising"); 
		NAME_SUBST.put("Friendliness", "Outgoing");
		NAME_SUBST.put("Neuroticism", "Emotional range");
	}
	/**
	 * Convert the node parameter into a map of key-values 
	 *
	 * @param node the trait JSONObject
	 * @param level the level to be use during iteration
	 * @param arr the trait list
	 */
	private void formatTree(JSONObject node, int level, List<Map<String,String>> arr) {	
		if (node == null) return;
		String subst = NAME_SUBST.get(node.get("name"));
		if (subst != null) node.put("name", subst);
		JSONArray children = (JSONArray)node.get("children");
		if (level > 0 && (children == null || level != 2)) {
			Map<String,String> obj = new HashMap<String,String>();
			obj.put("id", (String)node.get("name"));
			if (children != null) obj.put("title", "true");
			if (node.containsKey("percentage")) {
				double p = (Double)node.get("percentage");
				p = Math.floor(p * 100.0);
				obj.put("value", Double.toString(p) + "%");
			}
			arr.add(obj);
		}
		if (children != null && !"sbh".equals(node.get("id"))) {
			for (int i = 0; i < children.size(); i++) {
				formatTree((JSONObject)children.get(i), level + 1, arr);
			}
		}
		
	}

	@Override
	public void init() throws ServletException {
		super.init();
		processVCAP_Services();
	}

	private String handleError(HttpResponse r) throws IOException {
		String msg = r.getStatusLine().getReasonPhrase();
		HttpEntity entity = r.getEntity();
		if (entity != null) {
			Header contentTypeHeader = entity.getContentType();
			if (contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().startsWith("application/json")) {
				JSONObject errorObject = JSONObject.parse(EntityUtils.toString(entity));
				if (errorObject.containsKey("user_message")) {
					msg = (String)errorObject.get("user_message");
				}
			}
		}
		return msg;
	}
	
    /**
     * If exists, process the VCAP_SERVICES environment variable in order to get the 
     * username, password and baseURL
     */
	private void processVCAP_Services() {
    	logger.info("Processing VCAP_SERVICES");
        JSONObject sysEnv = getVcapServices();
        if (sysEnv == null) return;
        logger.info("Looking for: "+ serviceName );
        
		for (Object key : sysEnv.keySet()) {
			String keyString = (String) key;
			logger.info("found key: " + key);
			if (keyString.startsWith(serviceName)) {
				JSONArray services = (JSONArray)sysEnv.get(key);
				JSONObject service = (JSONObject)services.get(0);
				JSONObject credentials = (JSONObject)service.get("credentials");
				baseURL  = (String)credentials.get("url");
				username = (String)credentials.get("username");
				password = (String)credentials.get("password");
				logger.info("baseURL  = "+baseURL);
				logger.info("username = "+username);
				logger.info("password = "+password);
			} else {
				logger.info("Doesn't match /^"+serviceName+"/");
			}
        }
    }

    /**
     * Gets the <b>VCAP_SERVICES</b> environment variable and return it
     *  as a JSONObject.
     *
     * @return the VCAP_SERVICES as Json
     */
    private JSONObject getVcapServices() {
        String envServices = System.getenv("VCAP_SERVICES");
        if (envServices == null) return null;
        JSONObject sysEnv = null;
        try {
        	 sysEnv = JSONObject.parse(envServices);
        } catch (IOException e) {
        	// Do nothing, fall through to defaults
        	logger.log(Level.SEVERE, "Error parsing VCAP_SERVICES: "+e.getMessage(), e);
        }
        return sysEnv;
    }
    private void doOffline(String blogsS,String blogsF) {
 		HashSet<String> blogs = getBlogs(blogsS);
		HashSet<String> blogF = getBlogs(blogsF);
		String text;
		String textF;
		try {
			text = new BlogReader().readBlogs(blogs);
			textF = new BlogReader().readBlogs(blogF);
		JSONObject contentItem = new JSONObject();
    	contentItem.put("userid", UUID.randomUUID().toString());
    	contentItem.put("id", UUID.randomUUID().toString());
    	contentItem.put("sourceid", "freetext");
    	contentItem.put("contenttype", "text/html");
    	contentItem.put("language", "en");
    	contentItem.put("content", text);
    	
		JSONObject content = new JSONObject();
    	JSONArray contentItems = new JSONArray();
    	content.put("contentItems", contentItems);
    	contentItems.add(contentItem);
    	
    	
    		Executor executor = Executor.newInstance().auth(username, password);
    		URI profileURI = new URI(baseURL + "api/v2/profile").normalize();
    		
			Request profileRequest = Request.Post(profileURI)
					.addHeader("Accept", "application/json")
					.bodyString(content.toString(), ContentType.APPLICATION_JSON);
			String profileString = executor.execute(profileRequest).handleResponse(new ResponseHandler<String>() {

				@Override
				public String handleResponse(HttpResponse r)
						throws ClientProtocolException, IOException {
					if (r.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
						System.out.println("Error ");
						return null;
					}
					return EntityUtils.toString(r.getEntity());
				}
				
			});

			if (profileString != null) {
				List<Map<String,String>> traits = formatTraits(profileString);
				
	    		URI vizURI = new URI(baseURL + String.format("api/v2/visualize?w=%d&h=%d&imgurl=%s", 900, 900, "images/app.png")).normalize();
	    		
	    		String viz = executor.execute(Request.Post(vizURI)
				    .bodyString(profileString, ContentType.APPLICATION_JSON))
				    .returnContent().asString();
	    		
    			// send visualization and traits back to the UI
	    		//req.setAttribute("viz", viz);
				//req.setAttribute("traits", traits);
	    		System.out.println(traits);
			}

		} catch (Exception e) {
			// Log something and return an error message
			logger.log(Level.SEVERE, "got error: "+e.getMessage(), e);
		}
    }
    public static void main(String[] args) {
    	DemoServlet s = new DemoServlet();
    	String blogsS = "http://alybet774.blogspot.com&&&&http://alybet774.blogspot.com/feeds/posts/default&&&&\n"+
    			"http://digital-scrapbook-art.com/blog&&&&http://www.digital-scrapbook-art.com/blog/?feed=rss2&&&&";
		String blogsF = "http://www.scrappersworkshop.com/blog&&&&http://www.scrappersworkshop.com/feed&&&&\n"+
				"http://studioilustrado.blogspot.com&&&&http://studioilustrado.blogspot.com/feeds/posts/default&&&&";
    	try {
			s.init();
			s.doOffline(blogsS,blogsF);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
