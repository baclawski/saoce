/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calendarevent;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Rohith
 */
public class CalendarEvent {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

    /*       
        
        This has to be replaced with the json data coming from the getJsonData() method
        Expecting the Json will be in the format from the above methid.
        
    */
        String jsonString = "{\n"
                + " \"kind\": \"calendar#events\",\n"
                + " \"etag\": \"\\\"2DaeHpkENZGECFHdcr5l8tYxjD4/QElT1PHkP9d3G5VSndpdEMlSzKE\\\"\",\n"
                + " \"summary\": \"PushEvents\",\n"
                + " \"description\": \"Hackathon\",\n"
                + " \"updated\": \"2014-03-29T22:35:18.495Z\",\n"
                + " \"timeZone\": \"Asia/Calcutta\",\n"
                + " \"accessRole\": \"reader\",\n"
                + " \"items\": [\n"
                + "  {\n"
                + "   \"kind\": \"calendar#event\",\n"
                + "   \"etag\": \"\\\"2DaeHpkENZGECFHdcr5l8tYxjD4/MTM5NjEyNTQwNzcxMTAwMA\\\"\",\n"
                + "   \"id\": \"q28lprjb8ad3m17955lf1p9d48\",\n"
                + "   \"status\": \"confirmed\",\n"
                + "   \"htmlLink\": \"https://www.google.com/calendar/event?eid=cTI4bHByamI4YWQzbTE3OTU1bGYxcDlkNDggM3RvcjdvamZxaWhlamNqNjduOWw0dDhnMmNAZw\",\n"
                + "   \"created\": \"2014-03-29T20:36:47.000Z\",\n"
                + "   \"updated\": \"2014-03-29T20:36:47.711Z\",\n"
                + "   \"summary\": \"Test API\",\n"
                + "   \"creator\": {\n"
                + "    \"email\": \"vrohitrao@gmail.com\",\n"
                + "    \"displayName\": \"Rohith Vallu\"\n"
                + "   },\n"
                + "   \"organizer\": {\n"
                + "    \"email\": \"3tor7ojfqihejcj67n9l4t8g2c@group.calendar.google.com\",\n"
                + "    \"displayName\": \"PushEvents\",\n"
                + "    \"self\": true\n"
                + "   },\n"
                + "   \"start\": {\n"
                + "    \"dateTime\": \"2014-03-30T02:30:00+05:30\"\n"
                + "   },\n"
                + "   \"end\": {\n"
                + "    \"dateTime\": \"2014-03-30T03:30:00+05:30\"\n"
                + "   },\n"
                + "   \"iCalUID\": \"q28lprjb8ad3m17955lf1p9d48@google.com\",\n"
                + "   \"sequence\": 0\n"
                + "  },\n"
                + "  {\n"
                + "   \"kind\": \"calendar#event\",\n"
                + "   \"etag\": \"\\\"2DaeHpkENZGECFHdcr5l8tYxjD4/MTM5NjEzMjUzMjQxNzAwMA\\\"\",\n"
                + "   \"id\": \"jgpue3stuo3js5qlsodob84voo\",\n"
                + "   \"status\": \"confirmed\",\n"
                + "   \"htmlLink\": \"https://www.google.com/calendar/event?eid=amdwdWUzc3R1bzNqczVxbHNvZG9iODR2b28gM3RvcjdvamZxaWhlamNqNjduOWw0dDhnMmNAZw\",\n"
                + "   \"created\": \"2014-03-29T22:35:32.000Z\",\n"
                + "   \"updated\": \"2014-03-29T22:35:32.417Z\",\n"
                + "   \"summary\": \"Test Events\",\n"
                + "   \"description\": \"Hack!!\",\n"
                + "   \"location\": \"Northeastern University, Huntington Avenue, Boston, MA, United States\",\n"
                + "   \"creator\": {\n"
                + "    \"email\": \"vrohitrao@gmail.com\",\n"
                + "    \"displayName\": \"Rohith Vallu\"\n"
                + "   },\n"
                + "   \"organizer\": {\n"
                + "    \"email\": \"3tor7ojfqihejcj67n9l4t8g2c@group.calendar.google.com\",\n"
                + "    \"displayName\": \"PushEvents\",\n"
                + "    \"self\": true\n"
                + "   },\n"
                + "   \"start\": {\n"
                + "    \"dateTime\": \"2014-03-30T04:30:00+05:30\"\n"
                + "   },\n"
                + "   \"end\": {\n"
                + "    \"dateTime\": \"2014-03-30T19:30:00+05:30\"\n"
                + "   },\n"
                + "   \"visibility\": \"public\",\n"
                + "   \"iCalUID\": \"jgpue3stuo3js5qlsodob84voo@google.com\",\n"
                + "   \"sequence\": 0\n"
                + "  }\n"
                + " ]\n"
                + "}";

        Gson gson = new Gson();
        try {
            JSONObject jsonData = new JSONObject(jsonString);
            JSONArray jsonArray = jsonData.getJSONArray("items");
            JSONObject eventData;
            Event event = new Event();
            for (int i = 0; i < jsonArray.length(); i++) {

                System.out.println(jsonArray.get(i).toString());
                Items item = gson.fromJson(jsonArray.get(i).toString(), Items.class);

                event.setSummary(item.getSummary());
                event.setLocation(item.getLocation());

                /* Will be adding the attendees here
                 ArrayList<EventAttendee> attendees = new ArrayList<EventAttendee>();
                 attendees.add(new EventAttendee().setEmail("attendeeEmail"));
                 // ...
                 event.setAttendees(attendees);
                 */
                Date startDate = new Date();
                Date endDate = new Date(startDate.getTime() + 3600000);
                DateTime start = new DateTime(startDate, TimeZone.getDefault().getDefault().getTimeZone("UTC"));
                event.setStart(new EventDateTime().setDateTime(start));
                DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
                event.setEnd(new EventDateTime().setDateTime(end));
                HttpTransport transport = new NetHttpTransport();
                JsonFactory jsonFactory = new JacksonFactory();
                Calendar.Builder builder = new Calendar.Builder(transport, jsonFactory, null);
                String clientID = "937140966210.apps.googleusercontent.com";
                String redirectURL = "urn:ietf:wg:oauth:2.0:oob";
                String clientSecret = "qMFSb_cadYDG7uh3IDXWiMQY";
                ArrayList<String> scope = new ArrayList<String>();
                scope.add("https://www.googleapis.com/auth/calendar");

                String url = new GoogleAuthorizationCodeRequestUrl(clientID, redirectURL, scope).build();

                System.out.println("Go to the following link in your browser:");
                System.out.println(url);

                // Read the authorization code from the standard input stream.
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("What is the authorization code?");
                String code = in.readLine();

                GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(transport, jsonFactory, clientID, clientSecret, redirectURL,
                        code, redirectURL).execute();
                
                GoogleCredential credential = new GoogleCredential()
                .setFromTokenResponse(response);
                
                Calendar service = new Calendar.Builder(transport, jsonFactory,
                credential).build();
                
                
                Event createdEvent = service.events().insert(item.getSummary(), event).execute();

                System.out.println(createdEvent.getId());

            }

           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
