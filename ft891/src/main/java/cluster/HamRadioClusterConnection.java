package cluster;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class HamRadioClusterConnection extends Thread {
    private String host;
    private int port;
    private String callsign;
    
    private FreqCallback freqCallback;

    private TelnetClient telnetClient;
    private PrintWriter writer;
    private BufferedReader reader;
private LogCallback logCallback;
    public interface LogCallback {
        void onLog(String message);
    }

    public interface FreqCallback {
        void onAdd(String frequency, String callSign, String location);
    }

    public HamRadioClusterConnection(String host, int port, String callsign, LogCallback logCallback, FreqCallback freqCallback) {
        this.host = host;
        this.port = port;
        this.callsign = callsign;
        this.logCallback = logCallback;
        this.freqCallback = freqCallback;
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
            // Extract the call sign and frequency
            extractSpotInfo(line);
        }
    }

    private void extractSpotInfo(String line) {
        try {
            // Example line: "DX de SV8SYK:    18100.0  N4ZR                                        2014Z"
            String[] parts = line.split("\\s+");
            if (parts.length >= 5 && parts[0].equals("DX") && parts[1].equals("de")) {
                String frequency = parts[3];
                String callSign = parts[2];
                String location = parts[4];
                freqCallback.onAdd(frequency, callSign,location );
            }
        } catch (Exception e) {
            logCallback.onLog("Failed to extract spot info: " + e.getMessage());
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