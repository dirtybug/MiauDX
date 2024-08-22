package repeater;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class RepeaterFinder implements Runnable {

	private String country;
	private JSONArray repeaters;
	private LogCallback logCallback;
	private DisplayRepeaters displayRepeaters;

	public interface LogCallback {
		void onLog(String message);
	}

	public interface DisplayRepeaters {
		void displayRepeaters(JSONArray repeaters);
	}

	public RepeaterFinder(String country, LogCallback logCallback, DisplayRepeaters displayRepeaters) {
		this.country = country;
		this.logCallback = logCallback;
		this.displayRepeaters = displayRepeaters;

		// Start the thread immediately upon instantiation
		new Thread(this).start();
	}

	@Override
	public void run() {
		findRepeatersByCountry();
	}

	// Central method to handle repeater searching by country
	private void findRepeatersByCountry() {
		logCallback.onLog("Searching for repeaters in " + country);

		// Fetch and display repeaters
		findAndDisplayRepeatersByCountry();
	}

	public JSONArray getRepeaterArray() {
		return repeaters;
	}

	// Fetch repeaters by country and display them
	private void findAndDisplayRepeatersByCountry() {
		try {
			// Use the provided country to fetch repeaters
			String apiUrl = String.format("https://www.repeaterbook.com/api/exportROW.php?country=%s", country);
			URL url = new URL(apiUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Log the JSON response for debugging
			logCallback.onLog("API Response: " + response.toString());

			JSONObject repeaterArray = new JSONObject(response.toString());

			logCallback.onLog(repeaterArray.length() + " repeaters found.");
			this.repeaters = repeaterArray.getJSONArray("results");
			this.displayRepeaters.displayRepeaters(repeaters);

		} catch (Exception e) {
			e.printStackTrace();
			logCallback.onLog("Error fetching repeater data: " + e.getMessage());
		}
	}
}
