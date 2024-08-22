package ft891;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {

	public static final String CONFIG_FILE = "transceiver_config.json";
	private JSONObject config;

	public ConfigManager() {
		loadConfig();
	}

	public void saveConfig(String port, int baudRate) {

		config.put("port", port);
		config.put("baudRate", baudRate);

		try (FileWriter file = new FileWriter(CONFIG_FILE)) {
			file.write(config.toString(4)); // Write JSON with indentation for readability
			file.flush();
			System.out.println("Configuration saved to " + CONFIG_FILE);
		} catch (IOException e) {
			System.err.println("Error saving configuration: " + e.getMessage());
		}
	}

	public void loadConfig() {
		try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
			JSONTokener tokener = new JSONTokener(fis);
			config = new JSONObject(tokener);
		} catch (Exception e) {
			System.err.println("No previous configuration found or error reading configuration: " + e.getMessage());
			config = null;
		}
	}

	public String getPort() {
		return config != null ? config.getString("port") : null;
	}

	public int getBaudRate() {
		return config != null ? config.getInt("baudRate") : -1;
	}

	public String getCluster() {
		return config != null ? config.getString("Cluster") : null;
	}

	public String getContry() {
		return config != null ? config.getString("Country") : "Portugal";
	}

	public int getClusterPort() {
		return config != null ? config.getInt("ClusterPort") : -1;
	}

	public String getCallsign() {
		return config != null ? config.getString("callsign") : null;
	}
}