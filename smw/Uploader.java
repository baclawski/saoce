package smw;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Map;
import java.util.HashMap;

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

/**
 * Upload a category to Semantic MediaWiki.
 * @author Ken Baclawski
 */
public class Uploader {
    /** The category to be uploaded to a Semantic MediaWiki instance. */
    private Category category;
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
     * Construct an uploader.
     * @param user The username.
     * @param password The password.
     * @param endpoint The wiki web service endpoint.
     */
    public Uploader(String user, String password, String endpoint) {
        this.category = new Category();
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
    private void debug(String title, String[] lines) {
        if (debugEnabled) {
            System.out.println(title);
            for (String line : lines) {
                System.out.println(line);
            }       
            System.out.println("End of " + title);
        }
    }

    /**
     * Read the name, welcome and form introduction text.
     * @param file The file with introductory material.
     * @throws Exception if an error occurs.
     */
    private void readIntroduction(String file) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        Map<String, String> map = new HashMap<String, String>();
        String parameter = null;
        StringBuilder builder = new StringBuilder();
        for (;;) {
            String line = reader.readLine();
            if (line == null || line.startsWith("%")) {
                if (parameter != null) {
                    map.put(parameter, builder.toString());
                    builder = new StringBuilder();
                }
                parameter = line;
            } else {
                builder.append(line).append("\n");
            }
            if (line == null) break;
        }
        category.name = map.get("%name").trim();
        category.welcome = map.get("%welcome");
        category.formIntroduction = map.get("%form");
    }


    /**
     * Read the property metadata and construct the category and property objects.
     * @param file The file containing the property metadata.
     * @throws Exception if an error occurs.
     */
    public void readMetadata(String file) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        for (;;) {
            String line = reader.readLine();
            if (line == null) break;
            if (line.isEmpty()) continue;
           // category.addEntry(line);
        }
    }

    /**
     * Upload the category pages to the wiki.
     * @throws Exception if an error occurs.
     */
    public void upload() throws Exception {

        // Login

        login();

        // Get the edit token.

        getEditToken();

        StringBuilder builder;

        // Define the properties.
	/*
        for (Property property : category.getProperties()) {
            edit("Property:" + property.getName(), property.propertyPageContent());
            String commentName = property.getCommentName();
            if (commentName != null) {
                edit("Property:" + commentName, property.commentPropertyPageContent());
            }
        }
	*/
        // Define the template.

        builder = new StringBuilder();
        category.template(builder);
        edit("Template:" + category.getName(), builder.toString());

        // Define the form.

        builder = new StringBuilder();
        category.form(builder);
        edit("Form:" + category.getName(), builder.toString());

        // Define the category.

        builder = new StringBuilder();
        category.category(builder);
        edit("Category:" + category.getName(), builder.toString());

        // Define the welcome page for the category.

        builder = new StringBuilder();
        category.welcome(builder);
        edit(category.getName(), builder.toString());
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
        	System.out.println(e.getMessage());
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
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory
		.newInstance();
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
     * Main program
     * @param args The command-line arguments.
     * <ol>
     * <li>The username.
     * <li>The password.
     * <li>The wiki web service 
     * <li>Local file with the introductory material.
     * <li>Local file with the metadata.
     * </ol>
     * @throws Exception if an error occurs.
     */
    public static void main(String... args) throws Exception {
        if (args.length >= 5) {
            Uploader uploader = new Uploader(args[0], args[1], args[2]);
            uploader.readIntroduction(args[3]);
            uploader.readMetadata(args[4]);
            uploader.upload();
            uploader.pagesToModify();
        } else {
            System.out.println("Using default values for the arguments.");
            Uploader uploader = new Uploader(null, null, null);
            uploader.readMetadata("metadata.txt");
            uploader.upload();
            uploader.pagesToModify();
        }
    }
}
