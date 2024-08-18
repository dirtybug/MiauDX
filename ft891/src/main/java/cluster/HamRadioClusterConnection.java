package cluster;



import org.apache.commons.net.telnet.TelnetClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class HamRadioClusterConnection extends Thread {
    private String host;
    private int port;
    private String callsign;
    private LogCallback logCallback;

    private TelnetClient telnetClient;
    private PrintWriter writer;
    private BufferedReader reader;

    public interface LogCallback {
        void onLog(String message);
    }

    public HamRadioClusterConnection(String host, int port, String callsign, LogCallback logCallback) {
        this.host = host;
        this.port = port;
        this.callsign = callsign;
        this.logCallback = logCallback;
        this.telnetClient = new TelnetClient();
    }

    @Override
    public void run() {
        try {
            connect();
            listenForSpots();
        } catch (Exception e) {
            logCallback.onLog("Error: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void connect() throws Exception {
        logCallback.onLog("Connecting to cluster at " + host + ":" + port + "...");
        telnetClient.connect(host, port);
        writer = new PrintWriter(telnetClient.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(telnetClient.getInputStream()));
        logCallback.onLog("Connected to cluster.");

        // Send callsign to identify
        writer.println(callsign);
        logCallback.onLog("Sent callsign: " + callsign);
    }

    private void listenForSpots() throws Exception {
        String line;
        while ((line = reader.readLine()) != null) {
            logCallback.onLog("Cluster: " + line);
            // Here, you can process spots or other data as needed
        }
    }

    public void disconnect() {
        try {
            if (telnetClient.isConnected()) {
                telnetClient.disconnect();
                logCallback.onLog("Disconnected from cluster.");
            }
        } catch (Exception e) {
            logCallback.onLog("Error during disconnect: " + e.getMessage());
        }
    }
}