import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.xml.ws.http.HTTPException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Marvin on 29.01.15.
 */
public class Controller {
//    private final String API_KEY = ""; //API Key for testing purposes
    private final String USER_AGENT = "Mozilla/5.0";

    public Controller() {
        new AppFrame(this);
    }

    public String request(String apiKey, String itemURL) {
//        apiKey = "".equals(apiKey) ? API_KEY : apiKey; //Testing

        Pattern pattern = Pattern.compile("http://steamcommunity\\.com/id/(?<userID>\\w+?)/inventory/#730_2_(?<itemID>\\d+)");

        Matcher matcher = pattern.matcher(itemURL);

        JSONObject jsonResponse;
        try {
            if (!matcher.find())
                throw new IllegalStateException();

            String userApiUrl = "http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key=" + apiKey + "&vanityurl=" + matcher.group("userID");

            jsonResponse = (JSONObject) JSONValue.parse(getHTTPResponse(userApiUrl));

            String itemApiUrl = "http://api.steampowered.com/IEconItems_730/GetPlayerItems/v0001/?key=" + apiKey + "&SteamID=" + ((JSONObject) (jsonResponse.get("response"))).get("steamid");

            jsonResponse = (JSONObject) JSONValue.parse(getHTTPResponse(itemApiUrl));
        } catch (IOException e) {
            return "Error fetching data";
        } catch (IllegalStateException e) {
            return "Error parsing input";
        }


        JSONObject result = (JSONObject) jsonResponse.get("result");
        JSONArray items = (JSONArray) result.get("items");

        JSONObject item = getObjectFromArray(items, "id", matcher.group("itemID"));
        JSONArray wearValues = (JSONArray) item.get("attributes");

        JSONObject wear = getObjectFromArray(wearValues, "defindex", "8");

        return wear.get("float_value").toString();
    }

    private JSONObject getObjectFromArray(JSONArray array, String key, String value) {
        for (Object o : array.toArray()) {
            JSONObject item = (JSONObject) o;

            String id = item.get(key).toString();

            if (id.equals(value))
                return item;
        }
        return null;
    }

    private String getHTTPResponse(String URL) throws IOException {
        URL url = new URL(URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = connection.getResponseCode();

        if (responseCode != 200)
            throw new HTTPException(responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        return response.toString();
    }
}
