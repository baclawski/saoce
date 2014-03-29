package smw;

import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * MIME Multipart Form.
 * @author Ken Baclawski
 * @cite http://petersnotes.blogspot.com/2012/03/sending-multipart-form-data-from-java.html
 */
@Copyright
public class MultiForm {
    /** The boundary between the parts of a multipart form. */
    private final String boundary = "--------------------" + Long.toString(System.currentTimeMillis(), 16);
    /** The HTML Cookie Mapping. */
    private Map<String, String> cookieMap = new HashMap<String, String>();
    /** User agent. */
    private static final String userAgent = "PSMWOntologyMapper/0.1";
    /** Newline string. */
    private static final String nl = "\r\n";
    /** Boundary prefix. */
    private static final String prefix = "--";

    /**
     * Get all cookies specified by an HTML connection.
     * @connection The connection.
     */
    private void getCookiesFromHeader(HttpURLConnection connection) {
        List<String> valueList = connection.getHeaderFields().get("Set-Cookie");
        if (valueList == null) {
            return;
        }
        for (String value : valueList) {
            String[] pairs = value.split("[;]");
            for (String pair : pairs) {
                String[] words = pair.trim().split("[=]");
                if (words.length == 2) {
                    cookieMap.put(words[0], words[1]);
                }
            }
        }
    }

    /**
     * Encode a string for use in a URL.
     * @param text The text to be encoded.
     * @return The encoded text.
     * @throws Exception if an error occurs.
     */
    public static String encode(String text) throws Exception {
	return URLEncoder.encode(text, "UTF-8");
    }

    /**
     * Put the current cookies in the header.
     * @connection The HTML connection.
     */
    private void putCookiesInHeader(HttpURLConnection connection) {
        String delim = "";
        StringBuilder builder = new StringBuilder();
        for (String key : cookieMap.keySet()) {
            String value = cookieMap.get(key);
            builder.append(delim).append(key).append('=').append(value);
            delim = "; ";
        }
        connection.addRequestProperty("Cookie", builder.toString());
	connection.addRequestProperty("User-Agent", userAgent);
    }

    /**
     * Send a form to a connection.
     * @param endpoint The HTTP endpoint.
     * @param method This must be GET or POST.
     * @param parameterList The parameters to be specified.
     * @param parameterMap Map from parameters to their values.
     * @return The returned document as an array of lines.
     * @throws Exception if an error occurs.
     */
    public String[] sendForm(String endpoint, String method, String[] parameterList, Map<String, String> parameterMap) throws Exception {

	// If the method is GET or if it is POST but the parameters are small, then encode in the URL.

	boolean useEncodedURL = true;
	if (method.equals("POST")) {
	    int size = endpoint.length();
	    for (String parameter : parameterList) {
		String value = parameterMap.get(parameter);
		if (parameter != null && value != null) {
		    size = size + parameter.length() + value.length() + 2;
		}
	    }
	    if (size >= 1000) {
		useEncodedURL = false;
	    }
	}

	// Perform the request.

	HttpURLConnection connection;
	if (useEncodedURL) {

	    // Specify request entirely in the URL.

	    URL url;
	    StringBuilder builder = new StringBuilder(endpoint);
	    String delimiter = "?";
	    for (String parameter : parameterList) {
		String value = parameterMap.get(parameter);
		if (parameter != null && value != null) {
		    builder.append(delimiter).append(encode(parameter)).append("=").append(encode(value));
		    delimiter = "&";
		}
	    }
	    url = new URL(builder.toString());
	    System.out.println("Request URL is " + url);
	    connection = (HttpURLConnection)url.openConnection();
	    connection.setDoInput(true); 
	    connection.setDoOutput(true); 
	    connection.setRequestMethod(method);
	    putCookiesInHeader(connection);
	} else {

	    // Specify request using a multipart form.

	    URL url = new URL(endpoint);
	    connection = (HttpURLConnection)url.openConnection();
	    connection.setDoInput(true); 
	    connection.setDoOutput(true); 
	    connection.setRequestMethod(method);
	    connection.setUseCaches(false); 
	    putCookiesInHeader(connection);
	    connection.setRequestProperty("Connection", "Keep-Alive"); 
	    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary); 

	    // Write the form.
	    
	    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
	    StringBuilder builder = new StringBuilder();
	    for (String param : parameterList) {
		String value = parameterMap.get(param);
		out.writeBytes(prefix + boundary + nl);
		out.writeBytes("Content-Disposition: form-data; name=\"" + param + "\"" + nl + nl);
		out.writeBytes(value + nl);
		builder.append(prefix + boundary + nl);
		builder.append("Content-Disposition: form-data; name=\"" + param + "\"" + nl + nl);
		builder.append(value + nl);
	    }
	    out.writeBytes(prefix + boundary + prefix + nl);
	    builder.append(prefix + boundary + prefix + nl);
	    out.flush();
	    out.close();
	    System.out.println("multpart form");
	    System.out.println(builder);
	    System.out.println("end of multpart form");
	}
	// Read the response data.

	getCookiesFromHeader(connection);
        BufferedReader result = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	List<String> lines = new ArrayList<String>();
	for (;;) {
	    String line = result.readLine();
	    if (line == null) break;
	    lines.add(line);
	}
	result.close();
	String[] array = new String[lines.size()];
	lines.toArray(array);
	return array;
    }
}
