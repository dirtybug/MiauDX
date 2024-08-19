package ft891;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConnectionLogWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextArea connectionLogArea;
    private JTable spotTable;
    private DefaultTableModel spotTableModel;
    private ConnectionLogWindowListener listener;
    private Timer cleanupTimer;
    public interface ConnectionLogWindowListener {
        void onFrequencySelected(String frequency);
    }
    public ConnectionLogWindow(ConnectionLogWindowListener listener) {
        super("Connection Log and Spot Data");
        this.listener = listener;
        setLayout(new BorderLayout());

        // Create the table for frequency, call sign, and timestamp
        String[] columnNames = {"Timestamp", "Frequency", "Call Sign","Location"};
        spotTableModel = new DefaultTableModel(columnNames, 0);
        spotTable = new JTable(spotTableModel);
        JScrollPane tableScrollPane = new JScrollPane(spotTable);

        // Create the log area
        connectionLogArea = new JTextArea();
        connectionLogArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(connectionLogArea);

        // Split the window into two halves
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, logScrollPane);
        splitPane.setDividerLocation(150); // Set initial divider position
        add(splitPane, BorderLayout.CENTER);

        setSize(600, 400);
        setVisible(true);

        // Start a timer to clean up old entries every minute
        cleanupTimer = new Timer(60000, e -> removeOldEntries());
        cleanupTimer.start();

        // Add mouse listener for double-click
        spotTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                    int row = spotTable.getSelectedRow();
                    if (row != -1) {
                        String frequency = (String) spotTableModel.getValueAt(row, 2);

                        // Convert the frequency string (e.g., "144174.0") to an integer value
                        double freqDouble = Double.parseDouble(frequency);
                        // Multiply by 1000 to shift the decimal point, converting "144174.0" to "144174000"
                        long freqLong = (long) (freqDouble * 1000);
                        // Format the result as an 11-character string with leading zeros, e.g., "01441740000"
                        
                        listener.onFrequencySelected(String.format("%09d", freqLong));
                    }
                
            }
        });
    }

    public void logMessage(String message) {
        connectionLogArea.append(message + "\n");
    }

    public void addSpot(String frequency, String callSign, String location) {
        // Get the current timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // Check if the call sign already exists in the table
        boolean updated = false;
        for (int i = 0; i < spotTableModel.getRowCount(); i++) {
            if (spotTableModel.getValueAt(i, 2).equals(callSign)) {
                // Update the existing entry with new frequency and timestamp
                spotTableModel.setValueAt(timestamp, i, 0);
                spotTableModel.setValueAt(frequency, i, 1);
                spotTableModel.setValueAt(location, i, 2);
                updated = true;
                break;
            }
        }
        // If the call sign was not found, add a new row
        if (!updated) {
            spotTableModel.addRow(new Object[]{timestamp, frequency, callSign});
        }
    }


    private void removeOldEntries() {
        // Get the current time
        LocalDateTime now = LocalDateTime.now();
        for (int i = spotTableModel.getRowCount() - 1; i >= 0; i--) {
            String timestampString = (String) spotTableModel.getValueAt(i, 0);
            LocalDateTime entryTime = LocalDateTime.parse(timestampString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (entryTime.isBefore(now.minusMinutes(10))) {
                spotTableModel.removeRow(i);
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        cleanupTimer.stop(); // Stop the timer when the window is closed
    }
}

