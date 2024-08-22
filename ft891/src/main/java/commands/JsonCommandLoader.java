package commands;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class JsonCommandLoader {

	private String fileName;

	public JsonCommandLoader(String fileName) {
		this.fileName = fileName;
	}

	public Map<String, Command> loadCommands() throws IOException {
		Path filePath = Paths.get(fileName);
		Map<String, Command> commandMap = new HashMap<>();

		// Check if the file exists
		if (!Files.exists(filePath)) {
			// If the file does not exist, create a new one with an empty command structure
			createEmptyCommandsFile(filePath);
			System.out.println("Created new file: " + fileName);
		}

		// Load the JSON from the file
		try (InputStream is = Files.newInputStream(filePath)) {
			JSONTokener tokener = new JSONTokener(is);
			JSONObject root = new JSONObject(tokener);

			JSONArray commandsArray = root.getJSONArray("commands");

			for (int i = 0; i < commandsArray.length(); i++) {
				JSONObject cmdObj = commandsArray.getJSONObject(i);
				String name = cmdObj.getString("name");
				String command = cmdObj.getString("command");

				Command cmd = new Command(command, name);

				JSONArray paramsArray = cmdObj.getJSONArray("parameters");
				for (int j = 0; j < paramsArray.length(); j++) {
					JSONObject paramObj = paramsArray.getJSONObject(j);
					String paramName = paramObj.getString("name");
					int minValue = paramObj.getInt("min_value");
					int maxValue = paramObj.getInt("max_value");
					int requiredDigits = paramObj.getInt("required_digits");

					CommandParameter param = new CommandParameter(paramName, minValue, maxValue, requiredDigits);
					cmd.addParameter(param);
				}

				commandMap.put(name, cmd);
			}
		}

		return commandMap;
	}

	private void createEmptyCommandsFile(Path filePath) throws IOException {
		// Create an empty JSON structure
		JSONObject root = new JSONObject();
		root.put("commands", new JSONArray());

		// Write the empty JSON structure to the file
		try (OutputStream os = Files.newOutputStream(filePath)) {
			os.write(root.toString(4).getBytes()); // Pretty print JSON with an indent of 4 spaces
		}
	}
}
