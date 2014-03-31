package smw;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.google.gson.Gson;

/**
 * Annotate Purple Semantic MediaWiki Content.
 * @author Ken Baclawski and the SAOCE team.
 */
public class Annotator {
    /** The multiple part form generator. */
    private MultiForm multiform;
    /** The URL endpoint for web service requests. */
    private String endpoint = "http://localhost/w/api.php";
    /** The unencoded edit token. */
    private String editToken = "";
    /** The unencoded username. Default is admin. */
    private String user = "admin";
    /** The unencoded password. Default is abcd. */
    private String password = "abcd";
    /** Whether to produce debugging output. */
    private boolean debugEnabled = true;

    /**
     * Construct an annotator.
     * @param user The username.
     * @param password The password.
     * @param endpoint The wiki web service endpoint.
     */
    public Annotator(String user, String password, String endpoint) {
        this.multiform = new MultiForm();
        if (user != null) this.user = user;
        if (password != null) this.password = password;
        if (endpoint != null) this.endpoint = endpoint;
    }

    /**
     * Login request. The session token is specified as a cookie.
     * @throws Exception if an error occurs.
     */
    public void login() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("action", "login");
        map.put("lgname", user);
        map.put("lgpassword", password);
        map.put("format", "txt");
        String[] result = multiform.sendForm(endpoint, "POST", new String[] { "action", "lgname", "lgpassword", "format" }, map);
        debug("Login response", result);
        if (checkValue("result", result, "Success")) {
            return;
        }
        if (checkValue("result", result, "NeedToken")) {
            String token = getValue("token", result);
            map.put("lgtoken", token);
            result = multiform.sendForm(endpoint, "POST", new String[] { "action", "lgname", "lgpassword", "lgtoken", "format" }, map);
            debug("Login confirmation response", result);
            if (checkValue("result", result, "Success")) {
                return;
            }
            throw new Exception("Login failed after attempt to confirm");
        }
        throw new Exception("Login produced unexpected result: " + getValue("result", result));
    }

    /**
     * Edit request.
     * @param title The title of the page to be updated.
     * @param content The content of the page to be updated.
     * @throws Exception if an error occurs.
     */
    public void edit(String title, String content) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("action", "edit");
        map.put("title", title);
        map.put("text", content);
        map.put("recreate", "");
        map.put("token", editToken);
        String[] result = multiform.sendForm(endpoint, "POST", new String[] { "action", "title", "text", "recreate", "token" }, map);
        debug("Edit response", result);
    }

    /**
     * Get an edit token.
     * @throws Exception if an error occurs.
     */
    public void getEditToken() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("action", "query");
        map.put("prop", "info");
        map.put("intoken", "edit");
        map.put("titles", "Main Page");
        map.put("format", "txt");
        String[] response = multiform.sendForm(endpoint, "GET", new String[] { "action", "prop", "intoken", "titles", "format" }, map);
        debug("Get edit token response", response);
        if (response == null) {
            throw new Exception("Unable to obtain a token for editing");
        }
        editToken = getValue("edittoken", response);
        if (editToken == null) {
            throw new Exception("Unable to obtain a token for editing");
        }
    }

    /**
     * Get the value of an attribute from a text response.
     * @param param The desired parameter without the brackets.
     * @param lines The response text
     * @return The value if there is one or null if not.
     */
    public String getValue(String param, String[] lines) {
        param = "[" + param + "] => ";
        for (String line : lines) {
            int i = line.indexOf(param);
            if (i >= 0) {
                return line.substring(i + param.length());
            }
        }
        return null;
    }
	
    /**
     * Purpose: Given a title of the webpage, returns the contents of the page 
     * @param String title
     * @return Content of the webpage: ArrayList<String>
     * @throws MalformedURLException, IOException
     * Usage: readPage("ConferenceCall 2012 04 05")
     */
    public List<String> readContent(String title) throws MalformedURLException, IOException{
	debug("Reading content of ", title);
	title = title.replaceAll("\\s+","_");
	String wikiEndpoint = endpoint.replaceAll("w/api.php", "w/index.php") + "?action=raw&title=" + title;
	debug("wiki endpoint", wikiEndpoint);
	URL url = new URL(endpoint);
	URLConnection connection = url.openConnection();
	BufferedReader reader  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	String line;
	ArrayList<String> content = new ArrayList<String>();
	while((line=reader.readLine())!=null) {
	    content.add(line);
	}
	reader.close();
	return content;
    }

    /**
     * Extract properties from the page 
     * (day of the meeting, time of the meeting, title of the meeting, etc.)
     * You can get following Properties : MeetingDate,MeetingTime,MeetingSubject 
     * @param lines - The response Text
     * @return Map containing property type and its value
     */
    public Map<String,String> getMeetingInformation(List<String> lines) {
    	Map<String,String> meetingProperties = new HashMap<String,String>();
    	StringBuilder builder = new StringBuilder();
    	for(String line:lines)
    		builder.append(line);
    	// Response text into String
    	String page = builder.toString();
    	
    	String meetingDate;
    	String time;
    	String subject;
    	
    	
    	String regexDay = "(Sunday|Monday|Tuesday|Wednesday|" +
				"Thursday|Friday|Saturday)";
		String regexMonth ="(January|February|March|April|May|June|July|" +
				"August|September|October|November|Decmber)";
		String regexDate ="([0-3]?[0-9])";
		String regexYear = "([1-2][0-9][0-9][0-9])";
		
		// Regex to extract Day of meeting from a page
		String regexDayOfMeeting =".*"+"Date: "+regexDay+", "+regexMonth+" "
		+regexDate+", "+regexYear+".*";
		Pattern pattern= Pattern.compile(regexDayOfMeeting);
		Matcher matcher = pattern.matcher(page);
		
		if(matcher.matches()){
			meetingDate=matcher.group(1)+" "+matcher.group(2)+" "+matcher.group(3)
					+" "+matcher.group(4);
		}
		else
			meetingDate="Meeting date not found";
		 
		
		String regexTime= "([0-9]:[0-5][0-9]|[0-1][0-9]:[0-5][0-9])";
		String regexTimeZone = "(GMT/)?UTC";
		// Regex to extract time of meeting
		String regexForTime = ".*?"+"Start Time:"+".*?"+regexTime+" "
				+regexTimeZone+".*?";
		pattern=Pattern.compile(regexForTime);
		matcher=pattern.matcher(page);
		
		if(matcher.matches())
		{
			time=matcher.group(1)+ " UTC";
		}
		else
			time="Time Not Found";

		// Regex to extract Meeting Title.
		String regexForSubject=".*"+"Subject: "+"([^-]*)"+"-"+".*";
		pattern=Pattern.compile(regexForSubject);
		matcher=pattern.matcher(page);
		
		if(matcher.matches())
		{
			subject = matcher.group(1);
		}
		else
			subject = "Subject not found";
    	
    	meetingProperties.put("MeetingDate",meetingDate);
    	meetingProperties.put("MeetingTime",time);
    	meetingProperties.put("MeetingSubject",subject);
    	return meetingProperties;
    }

    public List<String> addProperties(List<String> pageContent,Map<String, String> propertiesCalendar) {
	pageContent.add("\n");
	for (String key : propertiesCalendar.keySet()) {
	    String value = propertiesCalendar.get(key);
	    if (key.equalsIgnoreCase("Attendees")){
		String[] splited = value.split(" ");
		for (int i = 0 ; i< splited.length; ++i) {
		    pageContent.add("[[" + key + "::" + splited[i] + "]]");
		}
	    } else {
		pageContent.add("[[" + key + "::" + value + "]]");
	    }
	}
	return pageContent;
    }
	
    public String getCalenderJson(Map<String,String> properties) {
	Gson gson = new Gson();
	Map<String,String> jsonProperties = new HashMap<String,String>();
	jsonProperties.put("kind","calendar#event");
	String date = null;
	String startTime = null;
	String endTime = null;
	for (String key : properties.keySet()) {
	    String value = properties.get(key);
	    if (key.equalsIgnoreCase("Subject")) {
		jsonProperties.put("summary", value);
	    } else if (key.equalsIgnoreCase("Agenda")){
		continue;
	    }else if (key.equalsIgnoreCase("Date")){
		date = value;
	    }else if (key.equalsIgnoreCase("Start Time")){
		startTime = value;
	    }else if (key.equalsIgnoreCase("Duration")){
		endTime = value;
	    }else if (key.equalsIgnoreCase("Dial-in Number")){
		continue;
	    } else if (key.equalsIgnoreCase("Participant Access Code")){
		continue;
	    }
	}		
	if (date != null && startTime != null) {
	    jsonProperties.put("start", "{date:" + date + ",dateTime:" + startTime );
	}
	
	if (date != null && endTime != null) {
	    jsonProperties.put("end", "{date:" + date + ",dateTime:" + endTime);
	}
	
	return gson.toJson(jsonProperties);
    }
	
    /**
     * Check the value of an attribute from a text response.
     * @param param The desired attribute name without the brackets.
     * @param lines The response text.
     * @param value The value to be checked.
     * @return true if the attribute value is the same as the required one.
     */
    public boolean checkValue(String param, String[] lines, String value) {
        String actual = getValue(param, lines);
        return (actual != null) && (actual.equals(value));
    }

    /**
     * Debugging output for requests.
     * @param title A title for the debug output.
     * @param lines The output of a request.
     */
    private void debug(String title, String... lines) {
        if (debugEnabled) {
            System.out.println(title);
            for (String line : lines) {
                System.out.println(line);
            }       
            System.out.println("End of " + title);
        }
    }

    /**
     * Annotate specified pages of the wiki.
     * @param list of the titles of pages
     * @throws Exception if an error occurs.
     */
    public void annotate(List<String> titles) throws Exception {

        // Login

        login();

        // Get the edit token.

        getEditToken();

	debug("annotating " + titles.size() + " titles");
	if (titles.size() == 0) {
	    titles.add("ConferenceCall_2009_01_15");
	}

        // update on each title
        for(String title : titles){
	    // read the content of the page
	    List<String> contentList = readContent(title);
	    // extract meeting information from content
	    Map<String, String> propertiesCalendar = getMeetingInformation(contentList);
	    // adding properties to calender
	    getCalenderJson(propertiesCalendar);
	    List<String> modifiedContentList = addProperties(contentList, propertiesCalendar);
	    StringBuilder builder = new StringBuilder();
	    for (String line : modifiedContentList) {
		builder.append(line).append('\n');
	    }
	    String content = builder.toString();
	    int first = Math.min(content.length(), 80);
	    System.out.println("edit on " + title + " " + content.substring(1, first));
	    edit (title, content);
        }
    }
    
    /**
     * Queries the api for pages that need to be modified
     * @return
     * Return id and titles of pages to be modified as key value
     */
    public Map<String, String> pagesToModify(){
    	Map<String, String> map = new HashMap<String, String>();
        map.put("action", "query");
        map.put("list", "categorymembers");
        map.put("cmtitle", "Category:Meeting");
        map.put("cmsort", "timestamp");
        map.put("cmdir", "desc");
        map.put("format", "xml");
        try {
	    String[] response = multiform.sendForm(endpoint, "GET", new String[] { "action", "list", "cmtitle", "cmsort","cmdir","format" }, map);
	    debug("Get modified pages response", response);
	    if (response == null) {
		throw new Exception("Unable to get response for pages to be modified");
	    }
	    return getPages(response);
        } catch (Exception e) {
	    e.printStackTrace();
	}
  	return null;
  }

    /**
     * Parses the response for list of pages to be modified 
     * @param response 
     * Response as string array
     * @return
     * 
     */
    public Map<String, String> getPages(String[] response){
    	HashMap<String,String> pagesMap = new HashMap<String,String>();
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dBuilder;
	try {
	    dBuilder = dbFactory.newDocumentBuilder();
	    if(response.length < 1 ){
		throw new Exception("Modified pages response not in correct format");	
	    }
	    String xml  = response[0];
	    Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));  
	    doc.getDocumentElement().normalize();
	    NodeList nList = doc.getElementsByTagName("cm");
	    for (int temp = 0; temp < nList.getLength() && temp < 10; temp++) {
		Node nNode = nList.item(temp);
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		    Element eElement = (Element) nNode;
		    String pageId = eElement.getAttribute("pageid");
		    String title = eElement.getAttribute("title");
		    pagesMap.put(pageId, title);
		}
	    }
	    return pagesMap;
	} catch (Exception e) {
	    e.printStackTrace();
	}
    	return null;
    }
    
    /**
     * Purpose: Given a title of the webpage, returns the contents of the page 
     * @param String title
     * @return Content of the webpage: ArrayList<String>
     * @throws MalformedURLException, IOException
     * Usage: readPage("ConferenceCall 2012 04 05")
     */
    public ArrayList<String> readPage(String title) throws MalformedURLException, IOException{
	title = title.replaceAll("\\s+","%20");
	String endpoint = "http://ontolog-test.cim3.net/wiki/"+title+"?action=raw";
	URL url = new URL(endpoint);
	URLConnection connection = url.openConnection();
	BufferedReader reader  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	String line;
	ArrayList<String> content = new ArrayList<String>();
	
	while((line=reader.readLine())!=null) {
	    content.add(line);
	}
	reader.close();
	return content;
    }
    
    /**
     * Main program
     * @param args The command-line arguments.
     * <ol>
     * <li>The username.
     * <li>The password.
     * <li>The wiki web service 
     * </ol>
     * @throws Exception if an error occurs.
     */
    public static void main(String... args) throws Exception {
        if (args.length >= 3) {
            Annotator annotator = new Annotator(args[0], args[1], args[2]);
            Map<String, String> pages = annotator.pagesToModify();
            List<String> titles = new ArrayList<String>();
            for(Map.Entry<String, String> page : pages.entrySet()){
		System.out.println(page.getValue());
            	titles.add(page.getValue());
            }
            annotator.annotate(titles);
        }
    }
}
