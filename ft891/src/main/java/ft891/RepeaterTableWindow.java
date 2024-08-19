package ft891;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.json.JSONArray;
import org.json.JSONObject;

import repeater.RepeaterFinder;

import java.awt.*;

public class RepeaterTableWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable repeaterTable;
    private DefaultTableModel repeaterTableModel;
    private JTextArea logArea;
    private JLabel locationLabel;
    public RepeaterTableWindow() {

        super("Nearby Repeaters");

        String country = "Switzerland"; // or "Portugal"

        setLayout(new BorderLayout());

        // Create the log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setPreferredSize(new Dimension(600, 100));

        // Create the table for displaying repeaters
        String[] columnNames = {"Name", "Frequency", "Input Freq", "PL", "TSQ", "Repeater Mode", "Latitude", "Longitude"};
        repeaterTableModel = new DefaultTableModel(columnNames, 0);
        repeaterTable = new JTable(repeaterTableModel);
        JScrollPane tableScrollPane = new JScrollPane(repeaterTable);

        // Create a label to display the current location
        locationLabel = new JLabel("Current Location: Unknown");
        locationLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Add the components to the window
        add(locationLabel, BorderLayout.NORTH);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, logScrollPane);
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);

        new RepeaterFinder(
                country,
                message -> this.logMessage(message),
                repeaters -> this.displayRepeaters(repeaters)
        );

        setSize(600, 400);
        setVisible(true);
    }

    public void logMessage(String message) {
        logArea.append(message + "\n");
    }

    public void displayRepeaters(JSONArray repeaters) {

        repeaterTableModel.setRowCount(0); // Clear the existing rows
        for (int i = 0; i < repeaters.length(); i++) {
            JSONObject repeaterJson = repeaters.getJSONObject(i);

            String name = repeaterJson.optString("Callsign", "Unknown");
            String frequency = repeaterJson.optString("Frequency", "Unknown");
            String offset = repeaterJson.optString("Input Freq", "Unknown");
            String pl = repeaterJson.optString("PL", "Unknown");  // PL value
            String tsq = repeaterJson.optString("TSQ", "Unknown");  // TSQ value
            double latitude = repeaterJson.optDouble("Lat", 0.0);
            double longitude = repeaterJson.optDouble("Long", 0.0);

            // Determine the repeater modes set to "Yes"
            StringBuilder modeBuilder = new StringBuilder();
            if ("Yes".equalsIgnoreCase(repeaterJson.optString("FM Analog", "No"))) {
                modeBuilder.append("FM Analog, ");
            }
            if ("Yes".equalsIgnoreCase(repeaterJson.optString("DMR", "No"))) {
                modeBuilder.append("DMR, ");
            }
            if ("Yes".equalsIgnoreCase(repeaterJson.optString("D-Star", "No"))) {
                modeBuilder.append("D-Star, ");
            }
            if ("Yes".equalsIgnoreCase(repeaterJson.optString("NXDN", "No"))) {
                modeBuilder.append("NXDN, ");
            }
            if ("Yes".equalsIgnoreCase(repeaterJson.optString("APCO P-25", "No"))) {
                modeBuilder.append("APCO P-25, ");
            }
            if ("Yes".equalsIgnoreCase(repeaterJson.optString("M17", "No"))) {
                modeBuilder.append("M17, ");
            }
            if ("Yes".equalsIgnoreCase(repeaterJson.optString("Tetra", "No"))) {
                modeBuilder.append("Tetra, ");
            }
            if ("Yes".equalsIgnoreCase(repeaterJson.optString("System Fusion", "No"))) {
                modeBuilder.append("System Fusion, ");
            }

            // Remove trailing comma and space
            String modes = modeBuilder.length() > 0 ? modeBuilder.substring(0, modeBuilder.length() - 2) : "None";

            repeaterTableModel.addRow(new Object[]{
                    name,
                    frequency,
                    offset,
                    pl,  // Add the PL value to the table row
                    tsq, // Add the TSQ value to the table row
                    modes,
                    latitude,
                    longitude
            });
        }
    }

    public void setCurrentLocation(double latitude, double longitude) {
        locationLabel.setText(String.format("Current Location: Latitude: %f, Longitude: %f", latitude, longitude));
    }
}
