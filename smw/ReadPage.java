import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


/**
 * @author akshayhathwar
 *
 */
public class ReadPage {
	
	/**
	 * Purpose: Given a title of the webpage, returns the contents of the page 
	 * @param String title
	 * @return Content of the webpage: ArrayList<String>
	 * @throws MalformedURLException, IOException
	 * Usage: readPage("ConferenceCall 2012 04 05")
	 */
	public static ArrayList<String> readPage(String title) throws MalformedURLException, IOException{
		title = title.replaceAll("\\s+","%20");
		String endpoint = "http://ontolog-test.cim3.net/wiki/"+title+"?"+"&action=raw";
		URL url = new URL(endpoint);
		URLConnection connection = url.openConnection();
		BufferedReader reader  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		ArrayList<String> content = new ArrayList<String>();

		while((line=reader.readLine())!=null)
			content.add(line);
		reader.close();
		return content;
	}
	
	
	public static void main(String[] args) throws Exception{
		try{
		ArrayList<String> content = readPage("ConferenceCall 2012 04 05");
		for(String l:content)
			System.out.println(l);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}
